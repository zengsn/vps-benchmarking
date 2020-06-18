package com.kang.estimate.core.shiro;

import com.kang.estimate.core.error.BussinessException;
import com.kang.estimate.core.error.EmBussinessError;
import com.kang.estimate.module.management.dao.UserMapper;
import com.kang.estimate.module.management.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * @author 叶兆康
 */
public class MyRealm extends AuthorizingRealm {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShiroKit shiroKit;

    /**
     * 权限验证
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> role=new HashSet<>(1);
        String roleName=shiroKit.getRoleName();
        if(roleName.isEmpty()){
            throw new BussinessException(EmBussinessError.NO_ROLE);
        }
        role.add(roleName);
        authorizationInfo.setRoles(role);
        return authorizationInfo;
    }

    /**
     * 登陆认证
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token=(UsernamePasswordToken)authenticationToken;
        Map<String,Object> param=new HashMap<>(1);
        param.put("userName",token.getUsername());
        List<User> users=userMapper.selectByMap(param);
        if(users.size()!=1){
            throw new BussinessException(EmBussinessError.NOT_EXIST);
        }
        return new SimpleAuthenticationInfo(token.getPrincipal(), users.get(0).getPassword(), getName());
    }
}