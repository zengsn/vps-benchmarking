package com.kang.estimate.core.shiro;

import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.module.management.dao.UserMapper;
import com.kang.estimate.module.management.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;

/**
 * @author 叶兆康
 */
@Component
public class ShiroKit {

    @Autowired
    private UserMapper userMapper;

    public Integer getId(){
        Subject subject = SecurityUtils.getSubject();
        String userName=(String) subject.getPrincipal();
        User user=userMapper.selectByName(userName);
        if(user==null){
            throw new BussinessException(EmBussinessError.NOT_EXIST);
        }
        return user.getId();
    }

    public String getRoleName(){
        Subject subject = SecurityUtils.getSubject();
        String userName=(String) subject.getPrincipal();
        String roleName=userMapper.getRoleName(userName);
        if(roleName==null){
            throw new BussinessException(EmBussinessError.NOT_EXIST);
        }
        return roleName;
    };
}
