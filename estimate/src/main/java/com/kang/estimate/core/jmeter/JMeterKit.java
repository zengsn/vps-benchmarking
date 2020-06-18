package com.kang.estimate.core.jmeter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import com.kang.estimate.module.pressure.entity.PressureParams;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.SummaryReport;
import org.apache.jorphan.collections.HashTree;

/**
 * @author 叶兆康
 */
public class JMeterKit {


    public static void createScript(PressureParams pressureParams){
        // 配置文件
        File jmeterProperties = new File(JMeterKit.class.getClassLoader().getResource("jmeter.properties").getPath());
        String saveservice_properties=TestPlanLauncher.class.getClassLoader().getResource("saveservice.properties").getPath();
        JMeterUtils.loadJMeterProperties(jmeterProperties.getPath());
        JMeterUtils.setProperty("saveservice_properties",saveservice_properties);
        JMeterUtils.setJMeterHome("");
        JMeterUtils.initLocale();

        HashTree testPlanTree = new HashTree();

        TestPlan testPlan = new TestPlan(pressureParams.getIdentifyCode());
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        testPlanTree.add(testPlan);

        HashTree threadGroupHashTree = testPlanTree.add(testPlan, createThreadGroup(pressureParams));
        threadGroupHashTree.add(createHTTPSamplerProxy(pressureParams),createHeaderManager(pressureParams));

        try {
            SaveService.saveTree(testPlanTree, new FileOutputStream("C:\\Users\\hasee\\Desktop\\"+pressureParams.getIdentifyCode()+".jmx"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建线程组
     *
     * @return
     */
    public static ThreadGroup createThreadGroup(PressureParams pressureParams) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Thread");
        threadGroup.setNumThreads(pressureParams.getNumThreads());
        threadGroup.setRampUp(pressureParams.getRampUp());
        if(pressureParams.getDuration()!=null){
            threadGroup.setScheduler(true);
            threadGroup.setDuration(pressureParams.getDuration());
        }
        if(pressureParams.getDelay()!=null){
            threadGroup.setDelay(pressureParams.getDelay());
        }
        threadGroup.setSamplerController(createLoopController(pressureParams.getLoop()));
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        return threadGroup;
    }

    /**
     * 创建循环控制器
     *
     * @return
     */
    public static LoopController createLoopController(int loop) {
        // Loop Controller
        LoopController loopController = new LoopController();
        //永久循环的情况下loops应设置为-1.
        loopController.setLoops(loop);
        loopController.setContinueForever(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();
        return loopController;
    }

    /**
     * 创建http采样器
     *
     * @return
     */
    public static HTTPSamplerProxy createHTTPSamplerProxy(PressureParams pressureParams) {
        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        httpSamplerProxy.setProtocol(pressureParams.getProtocol());
        httpSamplerProxy.setName("sampler");
        httpSamplerProxy.setDomain(pressureParams.getDomain());
        if(pressureParams.getPort()!=null){
            httpSamplerProxy.setPort(pressureParams.getPort());
        }
        httpSamplerProxy.setPath(pressureParams.getPath());
        httpSamplerProxy.setMethod(pressureParams.getMethod());
        httpSamplerProxy.setConnectTimeout("5000");
        httpSamplerProxy.setUseKeepAlive(true);
        httpSamplerProxy.setAutoRedirects(true);
        httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSamplerProxy.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        // 参数设置
        Map<String,Object> arguments=pressureParams.getArguments();
        if (arguments!=null){
            Iterator<Map.Entry<String,Object>> argumentsIt=arguments.entrySet().iterator();
            while (argumentsIt.hasNext()){
                Map.Entry<String,Object> entry=argumentsIt.next();
                httpSamplerProxy.addArgument(entry.getKey(),entry.getValue().toString());
            }
        }
        return httpSamplerProxy;
    }

    public static HeaderManager createHeaderManager(PressureParams pressureParams){
        HeaderManager manager = new HeaderManager();
        manager.setName("header");
        Map<String,String> headers=pressureParams.getHeaders();
        if (headers!=null){
            Iterator<Map.Entry<String,String>> headersIt=headers.entrySet().iterator();
            while (headersIt.hasNext()){
                Map.Entry<String,String> entry=headersIt.next();
                manager.add(new Header(entry.getKey(),entry.getValue()));
            }
        }
        manager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        manager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
        return manager;
    }

}

