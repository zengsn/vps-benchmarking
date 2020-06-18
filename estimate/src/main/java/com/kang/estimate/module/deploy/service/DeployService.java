package com.kang.estimate.module.deploy.service;

import com.kang.estimate.module.deploy.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 叶兆康
 */
public interface DeployService {

    /**
     * 搜索tomcat下webapps的路径
     * @param host
     * @return
     */
    String findWebappsPath(String host);

    /**
     * 搜索tomcat下bin路径
     * @param host
     * @return
     */
    String findBinPath(String host);

    /**
     * 已上传文件下拉框
     * @return
     */
    List<FileEntity> selectFileList();

    /**
     * 上传到服务器
     * @param file
     * @return
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传（部署）到目标服务器
     * @param host
     * @param src
     * @param dst
     * @return
     */
    boolean deployApp(String host,String src,String dst);

    /**
     * 进度条
     * @param src
     * @return
     */
    BigDecimal uploadProgress(String src);

    /**
     * 重启tomcat
     * @param host
     * @return
     * @throws InterruptedException
     */
    boolean restartTomcat(String host) throws InterruptedException;

    /**
     * 列出webapps下所有文件
     * @param host
     * @return
     */
    List<Map<String,String>> listAll(String host);

    /**
     * 强制删除文件/文件夹
     * @param host
     * @param path
     * @return
     */
    boolean delete(String host,String path);
}
