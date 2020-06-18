package com.kang.estimate.core.jmeter;

import com.alibaba.fastjson.JSON;
import com.kang.estimate.core.redis.RedisUtil;
import com.kang.estimate.module.pressure.entity.Sample;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

/**
 * @author 叶兆康
 */
public class JmeterResultCollector extends ResultCollector {

    private RedisUtil redisUtil;

    private String identifyCode;

    public JmeterResultCollector(Summariser summer, String identifyCode, RedisUtil redisUtil) {
        super(summer);
        this.redisUtil=redisUtil;
        this.identifyCode=identifyCode;
        redisUtil.hset("FalseCount",identifyCode,0);
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        super.sampleOccurred(e);
        SampleResult result = e.getResult();
        Sample sample=new Sample();
        sample.setIdentifyCode(identifyCode);
        sample.setThreadName(result.getThreadName());
        sample.setResponseCode(result.getResponseCode());
        sample.setResponseData(new String(result.getResponseData()));
        sample.setSuccess(result.isSuccessful()?"true":"false");
        sample.setStartTime(result.getStartTime());
        sample.setEndTime(result.getEndTime());
        sample.setElapsedTime(result.getTime());
        sample.setIdleTime(result.getIdleTime());
        sample.setLatency(result.getLatency());
        sample.setActiveCount(Thread.activeCount());
        String jsonStr= JSON.toJSONString(sample);
        redisUtil.lSet(identifyCode,jsonStr);
        if(result.isSuccessful()==false){
            // 连接失败
            redisUtil.hincr("FalseCount",identifyCode,1);
        }
    }
}