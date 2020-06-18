package com.kang.estimate.module.monitor.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.core.redis.RedisUtil;
import com.kang.estimate.module.management.entity.Server;
import com.kang.estimate.module.management.service.ManagementService;
import com.kang.estimate.module.monitor.entity.Stats;
import com.kang.estimate.module.monitor.service.MonitorService;
import com.kang.estimate.util.Common;
import com.kang.estimate.util.Const;
import com.kang.estimate.util.Ftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 叶兆康
 */
@Service
public class MonitorServiceImpl implements MonitorService {

    @Value("${ADD_TO_CROND_SHELL}")
    String ADD_TO_CROND_SHELL;

    @Value("${TIMER_SHELL}")
    String TIMER_SHELL;

    @Value("${DETECT_SHELL}")
    String DETECT_SHELL;

    @Autowired
    private ManagementService managementService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public boolean isInstall(String host) {
        Server server=managementService.serverFullDetail(host);
        StringBuilder rawOut=Ftp.getFtpUtil(server).execCommad("find "+Const.CLIENT_SHELL_PATH);
        List<String> shell=Common.ftpOutPutToRow(rawOut);
        if(shell.size()==1 && shell.get(0).equals("")){
            // 监控脚本目录不存在
            return false;
        }
        List<String> logList=Common.ftpOutPutToRow(Ftp.getFtpUtil(server).execCommad(Const.IS_LOG));
        Ftp.release();
//        if(logList.size()==0){
//            // 查找不出日志，证明脚本未被安装或启动
//            return false;
//        }
        return true;
    }

    @Override
    public boolean installMonitor(String host) {
        Server server=managementService.serverFullDetail(host);
        // 创建目录
        Ftp.getFtpUtil(server).execCommad(Const.MKDIR_FOR_DETECT);
        // 上传脚本
        Ftp.getFtpUtil(server).upload(ADD_TO_CROND_SHELL,Const.CLIENT_SHELL_PATH);
        Ftp.getFtpUtil(server).upload(TIMER_SHELL,Const.CLIENT_SHELL_PATH);
        Ftp.getFtpUtil(server).upload(DETECT_SHELL,Const.CLIENT_SHELL_PATH);
        // 执行定时命令，重启crontab
        Ftp.getFtpUtil(server).execCommad(Const.INIT_DETECT);
        Ftp.release();
        return true;
    }

    @Override
    public boolean uninstallMonitor(String host) {
        Server server=managementService.serverFullDetail(host);
        // 删除脚本
        Ftp.getFtpUtil(server).execCommad(Const.STRONG_DELETE+Const.CLIENT_SHELL_PATH);
        // 撤销定时命令
        Ftp.getFtpUtil(server).execCommad(Const.UNINSTALL_MONITOR);
        Ftp.release();
        return true;
    }

    @Override
    public List<List<Object>> updateStats(String host) {
        // 获取当前日期生成key
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate=dateFormater.format(new Date());
        String currentTime=timeFormater.format(new Date());
        String log= Const.STATS_LOG+currentDate+Const.SUFFIX;
        // 从Redis中查出当前日期中最新记录
        String key=host+"-"+log;
        Server server=managementService.serverFullDetail(host);
        Ftp ftp=Ftp.getFtpUtil(server);
        StringBuilder result;
        if(redisUtil.hasKey(key)){
            // Redis中存在当天日志
            Stats latestStat= JSONObject.parseObject(redisUtil.lGetIndex(key,-1).toString(),Stats.class);
            String latestTime=latestStat.getTime();
            long timeDiff= 0;
            timeDiff = Common.calTime(currentTime,latestTime);
            if(timeDiff>60){
                // 时间差大于一小时,先清除缓存，再同步当前时间前一小时开始的数据
                redisUtil.del(key);
                String pre=Const.UPDATE_PREFIX+log+Const.UPDATE_AFTER_SUFFIX+Common.getBeforeByHourTime(1);
                result=ftp.execCommad(pre+"*\"");
            }else if(timeDiff<=60){
                // 时间差小于一小时,同步上一时间后所有数据
                result=ftp.execCommad(Const.UPDATE_PREFIX+log+Const.UPDATE_AFTER_SUFFIX+latestTime+"\"");
            }else {
                throw new BussinessException(EmBussinessError.UNKNOW_ERROR);
            }
        }else {
            // Redis中不存在当天日志
            String getFirstCommad=Const.UPDATE_PREFIX+log+Const.UPDATE_AFTER_SUFFIX+currentDate+Const.UPDATE_GET_FIRST_SUFFIX;
            List<Stats> firstStats=Common.convertToStats(ftp.execCommad(getFirstCommad));
            // 获得日志中第一条数据的时间
            if(firstStats.size()!=0){
                String latestTime=firstStats.get(0).getTime();
                long timeDiff= 0;
                timeDiff = Common.calTime(currentTime,latestTime);
                if(timeDiff>60){
                    // 时间差大于一小时,先清除缓存，再同步当前时间前一小时开始的数据
                    String fullCommad=Const.UPDATE_PREFIX+log+Const.UPDATE_AFTER_SUFFIX+Common.getBeforeByHourTime(1)+"*\"";
                    result=Ftp.getFtpUtil(server).execCommad(fullCommad);
                }
                else if(timeDiff<=60){
                    // 时间差小于一小时,同步所有数据
                    String fullCommad=Const.UPDATE_PREFIX+log;
                    result=Ftp.getFtpUtil(server).execCommad(fullCommad);
                }
                else {
                    throw new BussinessException(EmBussinessError.UNKNOW_ERROR);
                }
            }else{
                result=Ftp.getFtpUtil(server).execCommad(Const.UPDATE_PREFIX+log);
            }
        }
        // 存入Redis
        List<Stats> statsList=Common.convertToStats(result);
        for(Stats item:statsList){
            String jsonStr= JSON.toJSONString(item);
            redisUtil.lSet(key,jsonStr,3600);
        }
        List<List<Object>> returnVal=readStats(key);
        Ftp.release();
        return returnVal;
    }

    @Override
    public List<List<Object>> readStats(String key) {
        // 初始化list
        List<Object> time=new ArrayList<>();
        time.add(Const.TIME);
        List<Object> cpuUsage=new ArrayList<>();
        cpuUsage.add(Const.CPU_USAGE);
        List<Object> memoryUsage=new ArrayList<>();
        memoryUsage.add(Const.MEMORY_USAGE);
        List<Object> diskUsage=new ArrayList<>();
        diskUsage.add(Const.DISK_USAGE);
        List<Stats> statsList= JSONObject.parseArray(redisUtil.lGet(key,0,-1).toString(),Stats.class);
        for(Stats stats:statsList){
            time.add(stats.getTime());
            cpuUsage.add(new BigDecimal(stats.getCpuUsage()));
            memoryUsage.add(Common.convertMemoryUsage(stats.getMemoryUsage()));
            diskUsage.add(new BigDecimal(stats.getDiskUsage()));
        }
        List<List<Object>> returnVal=new ArrayList<>();
        returnVal.add(time);
        returnVal.add(cpuUsage);
        returnVal.add(memoryUsage);
        returnVal.add(diskUsage);
        return returnVal;
    }

}
