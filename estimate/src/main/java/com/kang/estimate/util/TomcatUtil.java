package com.kang.estimate.util;

import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.module.management.entity.Server;
import java.util.*;

/**
 * @author 叶兆康
 */
public class TomcatUtil {

    private Server server;
    private String prefix;
    private String pid;

    private TomcatUtil(){}

    public TomcatUtil(Server server){
        this.server=server;

    }

    public boolean getConf(){
        String BIN_PATH=findBinPath();
        String JAVA_HOME=Ftp.getFtpUtil(server).execCommad(Const.JAVA_HOME).toString().replaceAll("\n","");
        this.prefix=JAVA_HOME+";cd "+BIN_PATH+";";
        return true;
    }

    public String findWebappsPath(){
        try {
            StringBuilder path = Ftp.getFtpUtil(this.server).execCommad(Const.FIND_WEBAPPS);
            Ftp.release();
            if (path.length()==0){
                throw new BussinessException(EmBussinessError.PATH_NOT_FOUND);
            }
            if(path.indexOf(".")==0){
                path=path.replace(0,1,"/usr");
            }
            return Common.replaceBlank(path.toString());
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof  BussinessException){
                throw e;
            }
            throw new BussinessException(EmBussinessError.UNKNOW_ERROR);
        }
    }

    public String findBinPath(){
        try {
            StringBuilder commadResult = Ftp.getFtpUtil(this.server).execCommad(Const.FIND_BIN);
            Ftp.release();
            String[] paths=commadResult.toString().split("\n");
            for(String path:paths){
                if(path.contains("tomcat")){
                    if(path.indexOf(".")==0){
                        path=path.replaceFirst(".","/usr");
                    }
                    return Common.replaceBlank(path);
                }
            }
            throw new BussinessException(EmBussinessError.PATH_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            if(e instanceof  BussinessException){
                throw e;
            }
            throw new BussinessException(EmBussinessError.UNKNOW_ERROR);
        }
    }

    public void stop(){
        getConf();
        String fullCommad= this.prefix+Const.STOP_TOMCAT;
        System.out.println(Ftp.getFtpUtil(this.server).execCommad(fullCommad));
    }

    public void start(){
        getConf();
        String fullCommad= this.prefix+Const.START_TOMCAT;
        Ftp.getFtpUtil(this.server).execCommad(fullCommad);
    }

    public void kill(){
        String fullCommad= "kill -9 "+this.pid;
        Ftp.getFtpUtil(this.server).execCommad(fullCommad);
    }

    public boolean isStarted(){
        String[] infos=Ftp.getFtpUtil(this.server).execCommad(Const.CHECK_TOMCAT).toString().split("\n");
        if(infos.length==1&&infos[0].equals("")){
            this.pid=null;
            return false;
        }else {
            this.pid=infos[0];
            return true;}
    }

    public List<Map<String,String>> listAll(){
        String fullCommand="cd "+findWebappsPath()+Const.LIST_SUFFIX;
        StringBuilder raw=Ftp.getFtpUtil(this.server).execCommad(fullCommand);
        List<Map<String,String>> returnList=new ArrayList<>();
        List<String> items=Common.ftpOutPutToRow(raw);
        Iterator<String> iterator=items.iterator();
        while (iterator.hasNext()){
            String item=iterator.next();
            if(item.equals(" /")){
                continue;
            }
            Map<String,String> map=new HashMap<>(2);
            String [] split=item.split("/");
            map.put("date",split[0]);
            map.put("fileName",split[1]);
            returnList.add(map);
        }
        return returnList;
    }

    public boolean deleteFile(String path){
        if(path.equals("")||path.isEmpty()){
            return false;
        }
        String fullCommand=Const.STRONG_DELETE+path;
        Ftp.getFtpUtil(this.server).execCommad(fullCommand);
        return true;
    }
}
