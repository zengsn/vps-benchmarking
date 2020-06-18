package com.kang.estimate.module.management.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.plugins.Page;
import com.kang.estimate.core.base.controller.BaseController;
import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.core.response.CommonReturnType;
import com.kang.estimate.module.management.entity.PageParam;
import com.kang.estimate.module.management.entity.Server;
import com.kang.estimate.module.management.entity.User;
import com.kang.estimate.module.management.service.ManagementService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 叶兆康
 */
@RestController
public class ManagementController extends BaseController {

    @Autowired
    private ManagementService managementService;

    /**
     * 注册
     * @param user
     * @return
     */
    @PostMapping("/user")
    public CommonReturnType user(@RequestBody User user){
        return CommonReturnType.create(managementService.addUser(user.getUserName(),user.getPassword()));
    }

    /**
     * 登陆
     * @param user
     * @return
     */
    @PostMapping("/login")
    public CommonReturnType login(@RequestBody User user){
        UsernamePasswordToken token=new UsernamePasswordToken(user.getUserName(),user.getPassword());
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);
        return CommonReturnType.create(subject.getSession().getId());
    }

    /**
     * 登陆
     * @return
     */
    @GetMapping("/login")
    public CommonReturnType reLogin(){
        return CommonReturnType.create("relogin","fail");
    }

    @GetMapping("/logout")
    public CommonReturnType logout(){
        Subject subject=SecurityUtils.getSubject();
        subject.logout();
        return CommonReturnType.create(null);
    }

    /**
     * 添加服务器
     * @param server
     * @return
     */
    @PostMapping("/server")
    public CommonReturnType addServer(@RequestBody Server server){
        return CommonReturnType.create(managementService.addServer(server));
    }

    /**
     * 删除服务器
     * @param param
     * @return
     */
    @DeleteMapping("/server")
    public CommonReturnType deleteServer(@RequestBody String param){
        Integer serverId=Integer.parseInt(JSON.parseObject(param).get("serverId").toString());
        return CommonReturnType.create(managementService.deleteServer(serverId));
    }

    /**
     * 查询服务器信息
     * @param serverId
     * @return
     */
    @GetMapping("/server/{serverId}")
    public CommonReturnType serverDetail(@PathVariable Integer serverId){
        return CommonReturnType.create(managementService.serverDetail(serverId));
    }

    /**
     * 修改服务器信息
     * @param server
     * @return
     */
    @PutMapping("/server")
    public CommonReturnType editServer(@RequestBody Server server){
        return CommonReturnType.create(managementService.editServer(server));
    }

    /**
     * 获取服务器下拉框数据
     * @return
     */
    @GetMapping("/server/list")
    public CommonReturnType serverList(){
        return CommonReturnType.create(managementService.selectServerList());
    }

    /**
     * 服务器列表
     * @param pageParam
     * @return
     */
    @PostMapping("/server/list")
    public CommonReturnType serverList(@RequestBody PageParam pageParam){
        Page<Server> page=new Page<>(pageParam.getPn(),pageParam.getSize());
        return CommonReturnType.create(managementService.selectServerList(page));
    }

    /**
     * 测试连接
     * @param serverId
     * @return
     */
    @GetMapping("/test/{serverId}")
    public CommonReturnType testConnection(@PathVariable Integer serverId) throws Exception {
        if(!managementService.testConnection(serverId)){
            throw new BussinessException(EmBussinessError.CONNECT_REFUSED);
        }
        return CommonReturnType.create(null);
    }
}
