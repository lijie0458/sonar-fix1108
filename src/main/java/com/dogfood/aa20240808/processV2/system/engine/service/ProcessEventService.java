package com.dogfood.aa20240808.processV2.system.engine.service;

import com.dogfood.aa20240808.processV2.system.aspect.ProcessRecordAction;
import com.dogfood.aa20240808.processV2.system.engine.service.inter.EventServiceInterface;
import com.dogfood.aa20240808.processV2.system.event.ProcessRecordEvent;
import com.dogfood.aa20240808.processV2.system.service.SystemProcessRecordService;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import com.dogfood.aa20240808.processV2.system.domain.structure.AddSignEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.engine.TaskService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class ProcessEventService extends BaseProcessEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessServiceTaskService.class);

    private static final String EVENT_LEVEL_PROCESS_SERVICE_NAME = "%sVersion%s%sEventService";

    private static final String EVENT_LEVEL_TASK_SERVICE_NAME = "%sVersion%s%s%sEventService";

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private TaskService taskService;

    @Resource
    private ProcessTaskAssigneeService processTaskAssigneeService;

    @Resource
    private SystemProcessRecordService systemProcessRecordService;

    public void triggerTaskCreateEvent(DelegateExecution execution, DelegateTask task) {
        triggerAddProcessRecordEventForCCTask(execution, task);
        triggerTaskEvent(execution, task, task.getTaskDefinitionKey(), "OnTaskCreate", "OnTasksCreate");
    }

    public void triggerTaskCloseEvent(DelegateExecution execution, DelegateTask task) {
        TaskEntity taskEntity =(TaskEntity) taskService.createTaskQuery().taskId(task.getId()).singleResult();
        if (AddSignEnum.PREVIOUS.getValue().equals(taskEntity.getScopeType())){
            return;
        }
        triggerTaskEvent(execution, task, task.getTaskDefinitionKey(), "OnTaskClose", "OnTasksClose");
    }

    /**
     * 触发任务事件,如任务创建、完成等，以及流程全局任务事件
     *
     * @param execution
     * @param delegateTask
     * @param userTaskDefinitionKey
     */
    public void triggerTaskEvent(DelegateExecution execution, DelegateTask delegateTask, String userTaskDefinitionKey, String eventName, String globalEventName) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
        if (bpmnModel != null) {
            String version = bpmnModel.getMainProcess().getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_NASL_VERSION);
            try {
                String eventServiceName = String.format(EVENT_LEVEL_TASK_SERVICE_NAME, bpmnModel.getMainProcess().getId(), version, userTaskDefinitionKey, eventName);
                Optional<EventServiceInterface> eventServiceOptional = getProcessSpringServiceBean(eventServiceName, EventServiceInterface.class);

                String globalEventServiceName = String.format(EVENT_LEVEL_PROCESS_SERVICE_NAME, bpmnModel.getMainProcess().getId(), version, globalEventName);
                Optional<EventServiceInterface> globalEventServiceOptional = getProcessSpringServiceBean(globalEventServiceName, EventServiceInterface.class);

                if (eventServiceOptional.isPresent() || globalEventServiceOptional.isPresent()) {
                    Map<String, Object> variables = execution.getVariables();
                    addTemporaryVariablesForCurrentNode((Map<String, Object>) variables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM), bpmnModel, userTaskDefinitionKey);
                    addTemporaryVariablesForPreviousNode((Map<String, Object>) variables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM), bpmnModel, userTaskDefinitionKey);

                    if (eventServiceOptional.isPresent()) {
                        LOGGER.info("Begin to execute task event logic: {}", eventServiceName);
                        EventServiceInterface eventServiceInterface = eventServiceOptional.get();
                        eventServiceInterface.trigger(variables);
                        execution.setVariable(ProcessConstant.KEY_VARIABLE_CUSTOM, variables.get(ProcessConstant.KEY_VARIABLE_CUSTOM));
                        execution.setVariable(ProcessConstant.KEY_PROCESS_VARIABLE, variables.get(ProcessConstant.KEY_PROCESS_VARIABLE));
                        LOGGER.info("End to execute task event logic: {}", eventServiceName);
                    } else {
                        LOGGER.info("There is no need to execute task event logic: {}", eventServiceName);
                    }

                    if (globalEventServiceOptional.isPresent()) {
                        LOGGER.info("Begin to execute global task event logic: {}", globalEventServiceName);
                        EventServiceInterface globalEventServiceInterface = globalEventServiceOptional.get();
                        globalEventServiceInterface.trigger(variables);
                        execution.setVariable(ProcessConstant.KEY_VARIABLE_CUSTOM, variables.get(ProcessConstant.KEY_VARIABLE_CUSTOM));
                        execution.setVariable(ProcessConstant.KEY_PROCESS_VARIABLE, variables.get(ProcessConstant.KEY_PROCESS_VARIABLE));
                        LOGGER.info("End to execute global task event logic: {}", globalEventServiceName);
                    } else {
                        LOGGER.info("There is no need to execute global task event logic: {}", globalEventServiceName);
                    }

                } else {
                    LOGGER.info("There is no need to execute task event logic: {}", eventServiceName);
                    LOGGER.info("There is no need to execute global task event logic: {}", globalEventServiceName);
                }
            } catch (Exception e) {
                LOGGER.error("Execute task event logic error", e);
            }

        }

    }

    public void triggerProcessStartEvent(DelegateExecution execution) {
        triggerProcessEvent(execution, "OnProcessStart");
    }

    public void triggerProcessEndEvent(DelegateExecution execution) {
        triggerAddProcessRecordEventForProcessEnd(execution);
        triggerProcessEvent(execution, "OnProcessEnd");
    }

    /**
     * 触发流程事件，如流程启动、结束等
     *
     * @param execution
     * @param eventName
     */
    public void triggerProcessEvent(DelegateExecution execution, String eventName) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
        if (bpmnModel != null) {
            String version = bpmnModel.getMainProcess().getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_NASL_VERSION);
            try {
                String eventServiceName = String.format(EVENT_LEVEL_PROCESS_SERVICE_NAME, bpmnModel.getMainProcess().getId(), version, eventName);
                Optional<EventServiceInterface> eventServiceOptional = getProcessSpringServiceBean(eventServiceName, EventServiceInterface.class);
                if (eventServiceOptional.isPresent()) {
                    LOGGER.info("Begin to execute process event logic: {}", eventServiceName);
                    EventServiceInterface eventServiceInterface = eventServiceOptional.get();
                    Map<String, Object> variables = execution.getVariables();
                    eventServiceInterface.trigger(variables);
                    execution.setVariable(ProcessConstant.KEY_VARIABLE_CUSTOM, variables.get(ProcessConstant.KEY_VARIABLE_CUSTOM));
                    execution.setVariable(ProcessConstant.KEY_PROCESS_VARIABLE, variables.get(ProcessConstant.KEY_PROCESS_VARIABLE));
                    LOGGER.info("End to execute process event logic: {}", eventServiceName);
                } else {
                    LOGGER.info("There is no need to execute process event logic: {}", eventServiceName);
                }
            } catch (Exception e) {
                LOGGER.error("Execute process event logic error", e);
            }
        }
    }

    /**
     * 流程节点启动事件，暂未对外开发，
     * 目前只用于内部经办人变量的设置，以便在流程节点开始时，便以确定任务节点的经办人列表，防止二次修改，保证幂等性
     * @param execution
     */
    public void triggerNodeStartEvent(DelegateExecution execution) {
        if (!execution.hasVariable("loopCounter")) {
            processTaskAssigneeService.setAssigneeListVariableForUserTask(execution);
        }
    }


    /**
     * 为抄送任务节点创建流程记录
     * @param execution
     * @param task
     */
    public void triggerAddProcessRecordEventForCCTask(DelegateExecution execution, DelegateTask task){
        if (ProcessConstant.TASK_CATEGORY_CC_TASK.equalsIgnoreCase(task.getCategory())) {

            ProcessRecordEvent processRecordEvent = new ProcessRecordEvent();
            processRecordEvent.setTaskId(task.getId());
            processRecordEvent.setProcessInstanceId(execution.getProcessInstanceId());
            processRecordEvent.setUserName(ProcessConstant.SYSTEM_USER);
            processRecordEvent.setRecordCreatedTime(new Date());
            List<String> candidateList = task.getCandidates().stream()
                    .filter(identityLink -> identityLink.getType().equalsIgnoreCase(ProcessConstant.INDENTITY_LINK_TYPE_CANDIDATE))
                    .map(IdentityLinkInfo::getUserId)
                    .collect(Collectors.toList());

            processRecordEvent.setAction(ProcessRecordAction.CC.value());
            String comment = CollectionUtils.isEmpty(candidateList) ? null : "抄送给" + StringUtils.join(candidateList, ",");

            processRecordEvent.setMessage(comment);
            processRecordEvent.setFullMessage(null);
            systemProcessRecordService.addProcessRecord(processRecordEvent);
        }
    }

    /**
     * 流程结束增加审批记录
     * @param execution
     */
    public void triggerAddProcessRecordEventForProcessEnd(DelegateExecution execution){
        ProcessRecordEvent processRecordEvent = new ProcessRecordEvent();
        processRecordEvent.setProcessInstanceId(execution.getProcessInstanceId());
        processRecordEvent.setUserName(ProcessConstant.SYSTEM_USER);
        processRecordEvent.setRecordCreatedTime(new Date());
        processRecordEvent.setAction(ProcessRecordAction.END.value());
        processRecordEvent.setMessage(null);
        processRecordEvent.setFullMessage(null);
        systemProcessRecordService.addProcessRecord(processRecordEvent);
    }
}

