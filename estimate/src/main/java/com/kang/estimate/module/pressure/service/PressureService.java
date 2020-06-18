package com.kang.estimate.module.pressure.service;

import com.kang.estimate.module.management.entity.PageParam;
import com.kang.estimate.module.pressure.entity.PressureParams;
import com.kang.estimate.module.pressure.entity.PressurePlanEntity;

import java.util.List;
import java.util.Map;

/**
 * @author 叶兆康
 */
public interface PressureService {

    String initPressureTest(Integer planId);

    Map<String,Object> obtainPressureTestResult(PageParam pageParam);

    List<PressurePlanEntity> obtainPressurePlan();

    boolean savePlan(PressureParams pressureParams);

    boolean deletePlan(Integer planId);

    boolean editPlan(PressureParams pressureParams);

    PressureParams planDetail(Integer planId);

}
