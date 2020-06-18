package com.kang.estimate.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 叶兆康
 */
public class Const {
    // linux部署语句
    public static String FIND_WEBAPPS="cd /usr;find -name 'webapps' -type d";
    public static String FIND_BIN="cd /usr;find -name 'bin' -type d";
    public static String HOST_CONF="cat /proc/cpuinfo | grep -E \"cpu cores|model name\";cat /proc/meminfo | grep MemTotal";
    public static String JAVA_HOME="grep \"export JAVA_HOME\" /etc/profile";
    public static String STOP_TOMCAT="./shutdown.sh";
    public static String START_TOMCAT="./startup.sh";
    public static String CHECK_TOMCAT="ps -ef | grep tomcat | grep -v grep | awk '{print $2}'";
    public static String LIST_SUFFIX=";ls -lht --time-style=long-iso|awk '{print $6\" \"$7\"/\"$8}'";
    public static String STRONG_DELETE="rm -rf ";

    // linux监控日志语句
    public static String CPU_CORES="cpu cores";
    public static String MODEL_NAME="model name";
    public static String MEM_TOTAL="MemTotal";
    public static String STATS_LOG="stats.";
    public static String SUFFIX=".log";
    public static String UPDATE_PREFIX="cat /usr/local/estimate/log/";
    public static String UPDATE_AFTER_SUFFIX=" |grep -A 9999999 \"";
    public static String UPDATE_GET_FIRST_SUFFIX="\"|head -1";
    public static String TIME="Time";
    public static String CPU_USAGE="CPU Usage";
    public static String MEMORY_USAGE="MEMORY Usage";
    public static String DISK_USAGE="DISK Usage";

    // 主服务器监控程序路径
    public static String MONITOR_BASE="/usr/local/estimateBase";
    // 创建客户端监控程序文件夹
    public static String MKDIR_FOR_DETECT="cd /usr/local;mkdir estimate;cd estimate;mkdir shell;mkdir log";
    // 客户端脚本文件夹
    public static String CLIENT_SHELL_PATH="/usr/local/estimate/shell";
    // 启动定时任务
    public static String INIT_DETECT="cd /usr/local/estimate/shell;sh add_to_crond.sh;sleep 3;systemctl restart crond";
    // 是否有日志
    public static String IS_LOG="ls /usr/local/estimate/log |grep stats*";
    // 卸载定时任务
    public static String UNINSTALL_MONITOR="sed -i '/timer.sh/d' /var/spool/cron/root ";

    // 进度条
    public static Map<String, BigDecimal> UPLOAD_PERCENTAGE=new HashMap<>();

    // KB转G
    public static BigDecimal KB2G=new BigDecimal("1048576");
}
