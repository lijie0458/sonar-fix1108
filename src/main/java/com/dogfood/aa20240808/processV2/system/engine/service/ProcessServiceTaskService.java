package com.dogfood.aa20240808.processV2.system.engine.service;

import com.dogfood.aa20240808.processV2.system.engine.service.inter.ServiceTaskServiceInterface;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Optional;

@Component
public class ProcessServiceTaskService extends BaseProcessEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceTaskService.class);

    private static final String SERVICE_TASK_SERVICE_NAME = "%sVersion%s%sService";

    @Resource
    private RepositoryService repositoryService;

    public void execute(DelegateExecution execution) {
        String serviceTaskDefinitionKey = execution.getCurrentActivityId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
        if (bpmnModel != null) {
            String version = bpmnModel.getMainProcess().getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_NASL_VERSION);
            try {
                String serviceTaskServiceName = String.format(SERVICE_TASK_SERVICE_NAME, bpmnModel.getMainProcess().getId(), version, serviceTaskDefinitionKey);
                Optional<ServiceTaskServiceInterface> serviceTaskServiceOptional = getProcessSpringServiceBean(serviceTaskServiceName, ServiceTaskServiceInterface.class);
                if (serviceTaskServiceOptional.isPresent()) {
                    LOGGER.info("Begin to execute service logic: {}", serviceTaskServiceName);
                    ServiceTaskServiceInterface serviceTaskServiceInterface = serviceTaskServiceOptional.get();
                    Map<String, Object> variables = execution.getVariables();
                    // 添加局部临时变量，此处只有上一个节点信息
                    addTemporaryVariablesForPreviousNode((Map<String, Object>) variables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM), bpmnModel, serviceTaskDefinitionKey);
                    serviceTaskServiceInterface.execute(variables);
                    execution.setVariable(ProcessConstant.KEY_VARIABLE_CUSTOM, variables.get(ProcessConstant.KEY_VARIABLE_CUSTOM));
                    execution.setVariable(ProcessConstant.KEY_PROCESS_VARIABLE, variables.get(ProcessConstant.KEY_PROCESS_VARIABLE));
                    LOGGER.info("End to execute service logic: {}", serviceTaskServiceName);
                } else {
                    LOGGER.info("There is no need to execute service logic: {}", serviceTaskServiceName);
                }
            } catch (Exception e) {
                LOGGER.error("Execute service logic error", e);
            }
        }

    }
}