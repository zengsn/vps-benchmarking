package com.kang.estimate.core.error;

/**
 * @author 叶兆康
 */
public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrMsg(String errMsg);
}
