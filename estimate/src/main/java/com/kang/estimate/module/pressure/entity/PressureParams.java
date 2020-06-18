package com.kang.estimate.module.pressure.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author 叶兆康
 */
public class PressureParams {
    private Integer id;
    /**
     * 识别号
     */
    @NotEmpty
    private String identifyCode;
    /**
     * 线程数量
     */
    @NotNull
    private Integer numThreads;
    /**
     * 启动时间
     */
    @NotNull
    private Integer rampUp;
    /**
     * 间隔时间
     */
    private Integer duration;
    /**
     * 延迟
     */
    private Integer delay;
    /**
     * 循环次数
     */
    @NotNull
    private Integer loop;
    /**
     * 协议
     */
    @NotEmpty
    private String protocol;
    /**
     * 域名
     */
    @NotEmpty
    private String domain;
    /**
     * 端口
     */
    private Integer port;
    /**
     * 路径
     */
    @NotEmpty
    private String path;
    /**
     * method
     */
    @NotEmpty
    private String method;
    /**
     * headers
     */
    private Map<String,String> headers;
    /**
     * 参数
     */
    private Map<String,Object> arguments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(String identifyCode) {
        this.identifyCode = identifyCode;
    }

    public Integer getNumThreads() {
        return numThreads;
    }

    public void setNumThreads(Integer numThreads) {
        this.numThreads = numThreads;
    }

    public Integer getRampUp() {
        return rampUp;
    }

    public void setRampUp(Integer rampUp) {
        this.rampUp = rampUp;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getLoop() {
        return loop;
    }

    public void setLoop(Integer loop) {
        this.loop = loop;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, Object> getArguments() {
        return arguments;
    }

    public void setArguments(Map<String, Object> arguments) {
        this.arguments = arguments;
    }
}
