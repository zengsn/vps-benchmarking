package com.kang.estimate.module.management.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.kang.estimate.module.management.entity.Server;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 叶兆康
 */
@Mapper
public interface ServerMapper extends BaseMapper<Server> {

    List<Server> selectServerList(Pagination page,@Param("userId") Integer userId);

    Server selectByHost(@Param("userId") Integer userId,@Param("host") String host);

    List<Server> selectServerListByUserId(@Param("userId") Integer userId);
}
