package com.kang.estimate.core.error;

/**
 * @author 叶兆康
 */
public enum EmBussinessError implements CommonError {
    // 系统相关
    PARAMETER_VALIDATION_ERROR(10001,"参数不合法"),
    UNKNOW_ERROR(10002,"未知错误"),
    NOT_EXIST(10003,"请求的资源不存在"),
    NOT_AUTHORIZE(10004,"无权限"),
    PARAMETER_LACK(10005,"缺少足够参数"),

    // 权限认证相关
    ALREADY_REGISTERD(20001,"该用户名已注册"),
    WROING_PASSWORD(20003,"密码不正确"),
    NOT_LOGIN(20004,"未登录"),
    NO_ROLE(20005,"用户没有角色"),
    UNAUTHORIZED(20006,"您没有权限"),

    // 部署相关
    CONNECT_REFUSED(30001,"主机连接失败"),
    SESSION_CLOSE_ERROR(30002,"FTP关闭SESSION错误"),
    REPEAT_UPLOAD(30003,"上传资源重复"),
    PATH_NOT_FOUND(30004,"找不到指定路径"),
    TOMCAT_STOP_FAIL(30005,"Tomcat关闭失败"),

    // 监控相关
    TIME_FORMATE_ERROR(40001,"时间日期解析出错")
    ;

    private int errCode;
    private String errMsg;

    private EmBussinessError(int errCode,String errMsg){
        this.errCode=errCode;
        this.errMsg=errMsg;
    }

    @Override
    public int getErrCode() {
        return this.errCode;
    }

    @Override
    public String getErrMsg() {
        return this.errMsg;
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.errMsg=errMsg;
        return this;
    }
}
