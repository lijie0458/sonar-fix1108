package com.dogfood.aa20240808.processV2.system.service;

import com.dogfood.aa20240808.processV2.system.domain.structure.ProcDef;

import java.util.List;
import java.util.Map;

public interface SystemProcessDefinitionService {
    /**
     * 获取获取流程定义列表
     * @param search
     * @return
     */
    List<ProcDef> getProcessDefinitionList(String search);

    /**
     * 获取流程定义
     * @param processDefUniqueKey
     * @return
     */
    ProcDef getProcessDefinition(String processDefUniqueKey);

    /**
     * 启动流程定义
     * @param procDefKey
     * @return
     */
    String launchProcess(String procDefKey, Map<String, Object> data);

    /**
     * 获取流程定义表单
     * @param processDefinitionId
     * @param taskDefName
     * @return
     */
    String getProcessDefinitionForm(String processDefinitionId, String taskDefName);
}
