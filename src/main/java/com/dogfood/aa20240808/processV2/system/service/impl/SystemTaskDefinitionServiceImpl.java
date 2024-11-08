package com.dogfood.aa20240808.processV2.system.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.domain.structure.FormDataPropertyPermission;
import com.dogfood.aa20240808.processV2.system.domain.structure.TaskOperationPermission;
import com.dogfood.aa20240808.processV2.system.service.SystemTaskDefinitionService;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SystemTaskDefinitionServiceImpl implements SystemTaskDefinitionService {

    private static final Logger logger = LoggerFactory.getLogger(SystemTaskDefinitionServiceImpl.class);

    @Autowired
    RepositoryService repositoryService;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<TaskOperationPermission> taskOptPermission(String processDefinitionId, String taskDefName) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        String operatePermission = bpmnModel.getMainProcess().getFlowElement(taskDefName).getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_OPERATION_PERMISSION);
        if (StringUtils.isBlank(operatePermission)) {
            return new ArrayList<>();
        }
        try {
            objectMapper.registerModule(new JavaTimeModule());
            List<TaskOperationPermission> lcapTaskDefOperationPermissionList = objectMapper.readValue(operatePermission, new TypeReference<List<TaskOperationPermission>>() {});
            logger.info("OperatePermission is {}", lcapTaskDefOperationPermissionList);
            return lcapTaskDefOperationPermissionList;
        } catch (JsonProcessingException e) {
            throw new ProcessException("operatePermission is invalid!");
        }
    }

    @Override
    public List<FormDataPropertyPermission> fieldPermissionDetail(String processDefinitionId, String taskDefName) {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        String fieldPermission = bpmnModel.getMainProcess().getFlowElement(taskDefName).getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_FIELD_PERMISSION);
        try {
            objectMapper.registerModule(new JavaTimeModule());
            List<FormDataPropertyPermission> lcapTaskDefFieldPermissionList = objectMapper.readValue(fieldPermission, new TypeReference<List<FormDataPropertyPermission>>() {});
            logger.info("FieldPermission is {}", lcapTaskDefFieldPermissionList);
            return lcapTaskDefFieldPermissionList;
        } catch (JsonProcessingException e) {
            throw new ProcessException("fieldPermission is invalid!");
        }
    }
}
