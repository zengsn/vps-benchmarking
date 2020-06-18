package com.kang.estimate.module.pressure.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.kang.estimate.module.pressure.entity.ArgumentEntity;
import com.kang.estimate.module.pressure.entity.HeaderEntity;
import com.kang.estimate.module.pressure.entity.PressurePlanEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 叶兆康
 */
@Mapper
public interface PressurePlanMapper extends BaseMapper<PressurePlanEntity> {

    List<PressurePlanEntity> obtainPressurePlanList(@Param("userId") Integer userId);

    List<HeaderEntity> obtainHeaders(@Param("planId") Integer planId);

    List<ArgumentEntity> obtainArguments(@Param("planId") Integer planId);
}
