package com.dogfood.aa20240808.processV2.system.engine.service;

import com.dogfood.aa20240808.processV2.system.engine.service.inter.AssigneeServiceInterface;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ProcessTaskAssigneeService extends BaseProcessEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessTaskAssigneeService.class);

    @Resource
    private RepositoryService repositoryService;

    @Resource
    RuntimeService runtimeService;

    private static final String TASK_ASSIGNEE_SERVICE_NAME = "%sVersion%s%sAssigneeService";

    /**
     * 解析经办人定义格式，通过计算得到经办人列表
     *
     * @param execution
     * @return
     */
    public List<String> getAssigneeList(DelegateExecution execution) {
        String currentUserTaskNodeKey = execution.getCurrentActivityId();
        List<String> assigneeList = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
        if (bpmnModel != null) {
            String version = bpmnModel.getMainProcess().getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_NASL_VERSION);
            try {
                String taskAssigneeServiceName = String.format(TASK_ASSIGNEE_SERVICE_NAME, bpmnModel.getMainProcess().getId(), version, currentUserTaskNodeKey);
                Optional<AssigneeServiceInterface> assigneeServiceOptional = getProcessSpringServiceBean(taskAssigneeServiceName, AssigneeServiceInterface.class);
                if (assigneeServiceOptional.isPresent()) {
                    LOGGER.info("Begin to execute task assignee logic: {}", taskAssigneeServiceName);
                    AssigneeServiceInterface assigneeService = assigneeServiceOptional.get();
                    Map<String, Object> variables = execution.getVariables();
                    // 添加局部临时变量，此处只有上一个节点信息
                    addTemporaryVariablesForPreviousNode((Map<String, Object>) variables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM), bpmnModel, currentUserTaskNodeKey);
                    assigneeList = assigneeService.getAssigneeList(variables);
                    execution.setVariable(ProcessConstant.KEY_VARIABLE_CUSTOM, variables.get(ProcessConstant.KEY_VARIABLE_CUSTOM));
                    execution.setVariable(ProcessConstant.KEY_PROCESS_VARIABLE, variables.get(ProcessConstant.KEY_PROCESS_VARIABLE));
                    LOGGER.info("End to execute task assignee logic: {}", taskAssigneeServiceName);
                } else {
                    LOGGER.info("There is no need to execute task assignee logic: {}", taskAssigneeServiceName);
                }
            } catch (Exception e) {
                LOGGER.error("Execute task assignee logic error", e);
            }
        }

        return assigneeList;
    }

    /**
     * 设置用户任务的经办人列表变量
     * @param execution
     */
    public void setAssigneeListVariableForUserTask(DelegateExecution execution) {
        List<String> assigneeList = getAssigneeList(execution);
        String taskAssigneeListVariableName = String.format(ProcessConstant.TASK_ASSIGNEE_LIST_VARIABLE_NAME, execution.getCurrentActivityId());
        execution.setVariable(taskAssigneeListVariableName, assigneeList);
    }

    /**
     * 获取用户任务的经办人列表变量值
     * @param execution
     * @return
     */
    public List<String> getAssigneeListVariableValue(DelegateExecution execution) {
        return getAssigneeListVariableValue(execution.getProcessInstanceId(), execution.getCurrentActivityId());
    }

    public List<String> getAssigneeListVariableValue(String processInstanceId, String taskDefinitionKey) {
        String taskAssigneeListVariableName = String.format(ProcessConstant.TASK_ASSIGNEE_LIST_VARIABLE_NAME, taskDefinitionKey);
        Object objectValue = runtimeService.getVariable(processInstanceId, taskAssigneeListVariableName);
        if (objectValue instanceof List) {
            return (List<String>) objectValue;
        } else {
            LOGGER.error("The assignee list variable value is not a list, the variable is: {}", taskAssigneeListVariableName);
            return Collections.emptyList();
        }
    }
}