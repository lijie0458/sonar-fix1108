package com.dogfood.aa20240808.processV2.system.service;

import com.dogfood.aa20240808.processV2.system.domain.structure.*;

import java.util.List;


public interface SystemTaskDefinitionService {

    public List<TaskOperationPermission> taskOptPermission(String processDefinitionId, String taskDefName);

    public List<FormDataPropertyPermission> fieldPermissionDetail(String processDefinitionId, String taskDefName);

}
