package com.kang.estimate.module.monitor.entity;

/**
 * @author 叶兆康
 */
public class Stats {

    private String time;
    private String CpuUsage;
    private String MemoryUsage;
    private String DiskUsage;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCpuUsage() {
        return CpuUsage;
    }

    public void setCpuUsage(String cpuUsage) {
        CpuUsage = cpuUsage;
    }

    public String getMemoryUsage() {
        return MemoryUsage;
    }

    public void setMemoryUsage(String memoryUsage) {
        MemoryUsage = memoryUsage;
    }

    public String getDiskUsage() {
        return DiskUsage;
    }

    public void setDiskUsage(String diskUsage) {
        DiskUsage = diskUsage;
    }
}
