package com.dogfood.aa20240808.repository.process;

import com.dogfood.aa20240808.processV2.system.domain.structure.CustomTask;
import com.dogfood.aa20240808.processV2.system.domain.structure.CustomTaskQuery;
import com.dogfood.aa20240808.processV2.system.domain.structure.CCTaskQuery;

import java.util.List;

public interface CustomTaskMapper {
    List<CustomTask> selectCustomTaskList(CustomTaskQuery query);
    List<CustomTask> selectCustomRunTaskList(CustomTaskQuery query);
    Long count(CustomTaskQuery query);
    Long countRun(CustomTaskQuery query);
    List<CustomTask> selectCCTaskList(CCTaskQuery query);
    Long countCCTask(CCTaskQuery query);
}
