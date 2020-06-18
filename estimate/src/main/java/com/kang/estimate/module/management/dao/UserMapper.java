package com.kang.estimate.module.management.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.kang.estimate.module.management.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 叶兆康
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    User selectByName(@Param("userName") String userName);

    String getRoleName(@Param("userName") String userName);
}
