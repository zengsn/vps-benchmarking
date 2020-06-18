package com.kang.estimate.util;

import com.jcraft.jsch.*;
import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.module.management.entity.Server;

import java.io.InputStream;

/**
 * @author 叶兆康
 */
public class Ftp {

    private static ThreadLocal<Ftp> ftpLocal=new ThreadLocal<Ftp>();

    private Session session;

    private Ftp(String username, String host, int port, String password){
        JSch jSch = new JSch();
        try {
            session = jSch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.setTimeout(30000);
            session.connect();
        }catch (JSchException jsex){
            System.out.println(jsex.getMessage());
            throw new BussinessException(EmBussinessError.CONNECT_REFUSED);
        }
    }

    public boolean isSession(){
        return session!=null&&session.isConnected();
    }


    public static Ftp getFtpUtil(Server server){
        Ftp ftpUtil=ftpLocal.get();
        if(null == ftpUtil || !ftpUtil.isSession()){
            ftpLocal.set(new Ftp(server.getUserName(),server.getHost(),server.getPort(),server.getPassword()));
        }
        return ftpLocal.get();
    }

    public static void release(){
        if(ftpLocal.get()!=null){
            ftpLocal.get().closeSession();
            ftpLocal.set(null);
        }
    }

    private void closeSession(){
        if(isSession()){
            try{
                session.disconnect();
            }catch (Exception e){
                throw new BussinessException(EmBussinessError.SESSION_CLOSE_ERROR);
            }
        }
    }

    /**
     * 执行命令
     * @param commad
     * @return
     */
    public StringBuilder execCommad(String commad){
        StringBuilder executeResultString = new StringBuilder();
        try{
            Channel channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec) channel;
            channelExec.setCommand(commad);
            channelExec.setErrStream(System.err);
            channelExec.setInputStream(null);
            channelExec.setOutputStream(System.out);
            InputStream in = channelExec.getInputStream();
            channelExec.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0){
                        break;
                    }
                    executeResultString.append(new String(tmp, 0, i));
                }
                if (channelExec.isClosed()) {
                    System.out.println("exit code:" + channelExec.getExitStatus());
                    break;
                }
                Thread.sleep(1000);
            }
            channelExec.disconnect();
//            closeSession();
        }catch (Exception e){
            e.printStackTrace();
            throw new BussinessException(EmBussinessError.CONNECT_REFUSED);
        }
        return executeResultString;
    }

    /**
     * 普通上传
     * @param src
     * @param dst
     */
    public void upload(String src,String dst){
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.put(src, dst);
            channelSftp.quit();
            channel.disconnect();
//            closeSession();
        }catch (Exception e){
            e.printStackTrace();
            throw new BussinessException(EmBussinessError.UNKNOW_ERROR);
        }
    }

    /**
     * 带进度条的上传
     * @param src
     * @param dst
     * @param totalSize
     * @param key
     */
    public void upload(String src,String dst,long totalSize,String key){
        try {
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.put(src, dst,new UploadProgressMonitor(key,totalSize),ChannelSftp.OVERWRITE);
            channelSftp.quit();
            channel.disconnect();
//            closeSession();
        }catch (Exception e){
            e.printStackTrace();
            throw new BussinessException(EmBussinessError.UNKNOW_ERROR);
        }
    }

    public static void main(String[] args) {
        Server server=new Server();
        server.setHost("47.101.143.247");
        server.setUserName("root");
        server.setPort(22);
        server.setPassword("pokemonYZK98");
        Ftp ftp=Ftp.getFtpUtil(server);
    }
}
