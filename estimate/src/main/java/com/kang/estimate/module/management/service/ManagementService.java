package com.kang.estimate.module.management.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.core.shiro.ShiroKit;
import com.kang.estimate.module.management.dao.ServerMapper;
import com.kang.estimate.module.management.dao.UserMapper;
import com.kang.estimate.module.management.entity.Server;
import com.kang.estimate.module.management.entity.User;
import com.kang.estimate.util.Common;
import com.kang.estimate.util.Const;
import com.kang.estimate.util.Ftp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * @author 叶兆康
 */
@Service
public class ManagementService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ServerMapper serverMapper;
    @Resource
    private ShiroKit shiroKit;

    /**
     * 添加用户
     * @param userName
     * @param password
     * @return
     */
    public boolean addUser(String userName,String password){
        User user=new User();
        user.setUserName(userName);
        user.setPassword(password);
        userMapper.insert(user);
        return true;
    }

    /**
     * 添加服务器记录
     * @param server
     * @return
     */
    public boolean addServer(Server server){
        Integer userId=shiroKit.getId();
        server.setCreateBy(userId);
        server.setCreateTime(new Date());
        server.setId(null);
        server.setStsCd(null);
        serverMapper.insert(server);
        return true;
    }

    /**
     * 删除服务器记录
     * @param serverId
     * @return
     */
    public boolean deleteServer(Integer serverId){
        serverMapper.deleteById(serverId);
        return true;
    }

    /**
     * 编辑服务器
     * @param server
     * @return
     */
    public boolean editServer(Server server){
        server.setUpdateBy(shiroKit.getId());
        server.setUpdateTime(new Date());
        if (server.getPassword().isEmpty()){
            server.setPassword(null);
        }
        serverMapper.updateById(server);
        return true;
    }

    /**
     * 获得无密码的服务器记录
     * @param serverId
     * @return
     */
    public Server serverDetail(Integer serverId){
        Server server=serverMapper.selectById(serverId);
        server.setPassword(null);
        return server;
    }

    public Server serverFullDetail(String host){
        Integer userId=shiroKit.getId();
        Server server=serverMapper.selectByHost(userId,host);
        if(server==null){
            throw new BussinessException(EmBussinessError.NOT_EXIST);
        }
        return server;
    }

    public List<Server> selectServerList(){
        Integer userId=shiroKit.getId();
        List<Server> serverList=serverMapper.selectServerListByUserId(userId);
        for(Server server:serverList){
            server.setPassword(null);
        }
        return serverList;
    }

    public Page<Server> selectServerList(Page<Server> page){
        List<Server> serverList=serverMapper.selectServerList(page,shiroKit.getId());
        for(Server server:serverList){
            server.setPassword(null);
        }
        return page.setRecords(serverList);
    }

    public Boolean testConnection(Integer serverId) throws Exception {
        Server server=serverMapper.selectById(serverId);
        if(Ftp.getFtpUtil(server).isSession()){
            StringBuilder commadResult=Ftp.getFtpUtil(server).execCommad(Const.HOST_CONF);
            Ftp.release();
            String[] confs=commadResult.toString().split("\n");
            for(String conf:confs){
                if(conf.startsWith(Const.CPU_CORES)){
                    server.setCpuCores(Common.replaceBlank(conf).split(":")[1]);
                }
                if(conf.startsWith(Const.MODEL_NAME)){
                    server.setModelName(Common.replaceBlank(conf).split(":")[1]);
                }
                if(conf.startsWith(Const.MEM_TOTAL)){
                    String  mem=Common.replaceBlank(conf).split(":")[1];
                    BigDecimal memBD=new BigDecimal(mem.replace("kB",""));
                    server.setMemTotal(memBD.divide(Const.KB2G,2, RoundingMode.HALF_UP).toString()+"G");
                }
            }
            serverMapper.updateById(server);
        };
        return true;
    }
}
