package com.kang.estimate.util;

import com.jcraft.jsch.SftpProgressMonitor;
import java.math.BigDecimal;
import static java.math.BigDecimal.ROUND_HALF_DOWN;

/**
 * @author 叶兆康
 */
public class UploadProgressMonitor implements SftpProgressMonitor {

    private String key;

    private long totalSize;

    private long transfered;


    public UploadProgressMonitor(String key, long totalSize){
        this.key=key;
        this.totalSize=totalSize;
    }

    @Override
    public void init(int op, String src, String dest, long max) {
        System.out.println("开始传输.");
    }

    /**
     * 当每次传输了一个数据块后，调用count方法，count方法的参数为这一次传输的数据块大小
     */
    @Override
    public boolean count(long count) {
        transfered=transfered+count;
        BigDecimal transferedBD=new BigDecimal(transfered);
        BigDecimal totalSizeBD=new BigDecimal(totalSize);
        BigDecimal percentage=transferedBD.divide(totalSizeBD,2,ROUND_HALF_DOWN).multiply(new BigDecimal(100));
        if(Const.UPLOAD_PERCENTAGE.containsKey(key)){
            Const.UPLOAD_PERCENTAGE.replace(key,percentage);
        }else {
            Const.UPLOAD_PERCENTAGE.put(key,percentage);
        }
        return true;
    }
    @Override
    public void end() {
        Const.UPLOAD_PERCENTAGE.put(key,new BigDecimal(100));
        System.out.println("结束传输.");
    }
}
