package com.kang.estimate.module.deploy.service.impl;

import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.core.shiro.ShiroKit;
import com.kang.estimate.module.deploy.dao.FileMapper;
import com.kang.estimate.module.deploy.entity.FileEntity;
import com.kang.estimate.module.deploy.service.DeployService;
import com.kang.estimate.module.management.dao.ServerMapper;
import com.kang.estimate.module.management.entity.Server;
import com.kang.estimate.module.management.service.ManagementService;
import com.kang.estimate.util.Common;
import com.kang.estimate.util.Const;
import com.kang.estimate.util.Ftp;
import com.kang.estimate.util.TomcatUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 叶兆康
 */
@Service
public class DeployServiceImpl implements DeployService {

    @Value("${UPLOAD_PATH}")
    String UPLOAD_PATH;

    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private ShiroKit shiroKit;
    @Autowired
    private ManagementService managementService;

    @Override
    public String findWebappsPath(String host){
        Server server=managementService.serverFullDetail(host);
        TomcatUtil tomcatUtil=new TomcatUtil(server);
        return tomcatUtil.findWebappsPath();
    }

    @Override
    public String findBinPath(String host){
        Server server=managementService.serverFullDetail(host);
        TomcatUtil tomcatUtil=new TomcatUtil(server);
        return tomcatUtil.findBinPath();
    }

    @Override
    public List<FileEntity> selectFileList(){
        return fileMapper.selectFileList(shiroKit.getId());
    }

    @Override
    public String uploadFile(MultipartFile file){
        String fileName = file.getOriginalFilename();
        String filePath = UPLOAD_PATH;
        String path=filePath+fileName;
        File dest = new File(path);
        try {
            file.transferTo(dest);
            // 文件路径和大小记录到数据库
            FileEntity fileEntity=fileMapper.selectByPath(path,shiroKit.getId());
            if(fileEntity!=null){
                fileEntity.setPath(filePath);
                fileEntity.setFileName(fileName);
                fileEntity.setTotalSize(file.getSize());
                fileEntity.setUpdateBy(shiroKit.getId());
                fileEntity.setUpdateTime(new Date());
                fileMapper.updateById(fileEntity);
            }else{
                fileEntity=new FileEntity();
                fileEntity.setPath(filePath);
                fileEntity.setFileName(fileName);
                fileEntity.setTotalSize(file.getSize());
                fileEntity.setCreateBy(shiroKit.getId());
                fileEntity.setCreateTime(new Date());
                fileMapper.insert(fileEntity);
            }
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            throw new BussinessException(EmBussinessError.UNKNOW_ERROR);
        }
    }

    @Override
    public boolean deployApp(String host,String src,String dst){
        FileEntity fileEntity=fileMapper.selectByPath(src,shiroKit.getId());
        if(fileEntity!=null&&fileEntity.getTotalSize()!=null){
            new Thread(()->{
                Ftp.getFtpUtil(managementService.serverFullDetail(host)).
                        upload(src,dst,fileEntity.getTotalSize(),shiroKit.getId()+src);
                Ftp.release();
            }).start();
            return true;
        }
        throw new BussinessException(EmBussinessError.NOT_EXIST);
    }

    @Override
    public BigDecimal uploadProgress(String src){
        String key=shiroKit.getId()+src;
        if(Const.UPLOAD_PERCENTAGE.containsKey(key)){
            BigDecimal percentage=Const.UPLOAD_PERCENTAGE.get(key);
            if(percentage.compareTo(new BigDecimal(100))==0){
                Const.UPLOAD_PERCENTAGE.remove(key);
            }
            return percentage;
        }
        throw new BussinessException(EmBussinessError.NOT_EXIST);
    }

    @Override
    public boolean restartTomcat(String host) throws InterruptedException {
        Server server=managementService.serverFullDetail(host);
        TomcatUtil tomcatUtil=new TomcatUtil(server);
        tomcatUtil.stop();
        if(tomcatUtil.isStarted()==true){
            tomcatUtil.kill();
        }
        tomcatUtil.start();
        Thread.sleep(3000);
        if(tomcatUtil.isStarted()==true){
            Ftp.release();
            return true;
        }else {
            Ftp.release();
            return false;
        }
    }

    @Override
    public List<Map<String,String>> listAll(String host) {
        Server server=managementService.serverFullDetail(host);
        TomcatUtil tomcatUtil=new TomcatUtil(server);
        List<Map<String,String>> returnVal=tomcatUtil.listAll();
        Ftp.release();
        return returnVal;
    }

    @Override
    public boolean delete(String host,String path){
        Server server=managementService.serverFullDetail(host);
        TomcatUtil tomcatUtil=new TomcatUtil(server);
        tomcatUtil.deleteFile(path);
        Ftp.release();
        return true;
    }

}
