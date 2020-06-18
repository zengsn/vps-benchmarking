package com.kang.estimate.module.monitor.controller;

import com.kang.estimate.core.base.controller.BaseController;
import com.kang.estimate.core.response.CommonReturnType;
import com.kang.estimate.module.monitor.service.impl.MonitorServiceImpl;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 叶兆康
 */
@RestController
@RequestMapping("/monitor")
public class MonitorController extends BaseController {

    @Autowired
    private MonitorServiceImpl monitorService;

    @PostMapping("/isInstall")
    public CommonReturnType isInstall(@RequestParam String host){
        return CommonReturnType.create(monitorService.isInstall(host));
    }

    @RequiresRoles("USER")
    @PostMapping("/installMonitor")
    public CommonReturnType installMonitor(@RequestParam String host){
        return CommonReturnType.create(monitorService.installMonitor(host));
    }

    @RequiresRoles("USER")
    @PostMapping("/uninstallMonitor")
    public CommonReturnType uninstallMonitor(@RequestParam String host){
        return CommonReturnType.create(monitorService.uninstallMonitor(host));
    }

    @PostMapping("/updateStats")
    public CommonReturnType updateStats(@RequestParam String host){
        return CommonReturnType.create(monitorService.updateStats(host));
    }

}
