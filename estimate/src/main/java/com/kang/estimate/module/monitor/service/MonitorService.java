package com.kang.estimate.module.monitor.service;

import java.util.List;

/**
 * @author 叶兆康
 */
public interface MonitorService{

    /**
     * 是否已安装脚本
     * @param host
     * @return
     */
    boolean isInstall(String host);

    /**
     * 安装监控脚本
     * @param host
     * @return
     */
    boolean installMonitor(String host);

    /**
     * 卸载监控脚本
     * @param host
     * @return
     */
    boolean uninstallMonitor(String host);

    /**
     * 同步监控数据
     * @param host
     * @return
     */
    List<List<Object>> updateStats(String host);

    /**
     * 从缓存中读取当天的监控数据
     * @param host
     * @return
     */
    List<List<Object>> readStats(String host);
}
