package com.dogfood.aa20240808.processV2.system.engine.custom.behavior;

import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import com.dogfood.aa20240808.processV2.system.domain.structure.TaskOperationPermission;
import com.dogfood.aa20240808.processV2.system.domain.structure.MoveToAfterRejected;
import com.dogfood.aa20240808.processV2.system.domain.structure.AddSignEnum;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Task;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventDispatcher;
import org.flowable.common.engine.impl.calendar.BusinessCalendar;
import org.flowable.common.engine.impl.calendar.DueDateBusinessCalendar;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.common.engine.impl.logging.LoggingSessionConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.engine.DynamicBpmnConstants;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.engine.impl.bpmn.helper.DynamicPropertyUtil;
import org.flowable.engine.impl.bpmn.helper.SkipExpressionUtil;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.context.BpmnOverrideContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.BpmnLoggingSessionUtil;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.TaskHelper;
import org.flowable.engine.interceptor.CreateUserTaskAfterContext;
import org.flowable.engine.interceptor.CreateUserTaskBeforeContext;
import org.flowable.engine.interceptor.MigrationContext;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.event.impl.FlowableTaskEventBuilder;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryImpl;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CustomUserTaskActivityBehavior extends UserTaskActivityBehavior {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserTaskActivityBehavior.class);

    protected ObjectMapper mapper = new ObjectMapper();


    public CustomUserTaskActivityBehavior(UserTask userTask) {
        super(userTask);
    }

    @Override
    public void execute(DelegateExecution execution, MigrationContext migrationContext) {
        CommandContext commandContext = CommandContextUtil.getCommandContext();
        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
        TaskService taskService = processEngineConfiguration.getTaskServiceConfiguration().getTaskService();

        TaskEntity task = taskService.createTask();
        task.setExecutionId(execution.getId());
        task.setTaskDefinitionKey(userTask.getId());
        task.setPropagatedStageInstanceId(execution.getPropagatedStageInstanceId());

        String activeTaskName = null;
        String activeTaskDescription = null;
        String activeTaskDueDate = null;
        String activeTaskPriority = null;
        String activeTaskCategory = null;
        String activeTaskFormKey = null;
        String activeTaskSkipExpression = null;
        String activeTaskAssignee = null;
        String activeTaskOwner = null;
        String activeTaskIdVariableName = null;
        List<String> activeTaskCandidateUsers = null;
        List<String> activeTaskCandidateGroups = null;

        ExpressionManager expressionManager = processEngineConfiguration.getExpressionManager();

        if (processEngineConfiguration.isEnableProcessDefinitionInfoCache()) {
            ObjectNode taskElementProperties = BpmnOverrideContext.getBpmnOverrideElementProperties(userTask.getId(), execution.getProcessDefinitionId());
            activeTaskName = DynamicPropertyUtil.getActiveValue(userTask.getName(), DynamicBpmnConstants.USER_TASK_NAME, taskElementProperties);
            activeTaskDescription = DynamicPropertyUtil.getActiveValue(userTask.getDocumentation(), DynamicBpmnConstants.USER_TASK_DESCRIPTION, taskElementProperties);
            activeTaskDueDate = DynamicPropertyUtil.getActiveValue(userTask.getDueDate(), DynamicBpmnConstants.USER_TASK_DUEDATE, taskElementProperties);
            activeTaskPriority = DynamicPropertyUtil.getActiveValue(userTask.getPriority(), DynamicBpmnConstants.USER_TASK_PRIORITY, taskElementProperties);
            activeTaskCategory = DynamicPropertyUtil.getActiveValue(userTask.getCategory(), DynamicBpmnConstants.USER_TASK_CATEGORY, taskElementProperties);
            activeTaskFormKey = DynamicPropertyUtil.getActiveValue(userTask.getFormKey(), DynamicBpmnConstants.USER_TASK_FORM_KEY, taskElementProperties);
            activeTaskSkipExpression = DynamicPropertyUtil.getActiveValue(userTask.getSkipExpression(), DynamicBpmnConstants.TASK_SKIP_EXPRESSION, taskElementProperties);
            activeTaskAssignee = getAssigneeValue(userTask, migrationContext, taskElementProperties);
            activeTaskOwner = DynamicPropertyUtil.getActiveValue(userTask.getOwner(), DynamicBpmnConstants.USER_TASK_OWNER, taskElementProperties);
            activeTaskCandidateUsers = getActiveValueList(userTask.getCandidateUsers(), DynamicBpmnConstants.USER_TASK_CANDIDATE_USERS, taskElementProperties);
            activeTaskCandidateGroups = getActiveValueList(userTask.getCandidateGroups(), DynamicBpmnConstants.USER_TASK_CANDIDATE_GROUPS, taskElementProperties);
            activeTaskIdVariableName = DynamicPropertyUtil.getActiveValue(userTask.getTaskIdVariableName(), DynamicBpmnConstants.USER_TASK_TASK_ID_VARIABLE_NAME, taskElementProperties);
        } else {
            activeTaskName = userTask.getName();
            activeTaskDescription = userTask.getDocumentation();
            activeTaskDueDate = userTask.getDueDate();
            activeTaskPriority = userTask.getPriority();
            activeTaskCategory = userTask.getCategory();
            activeTaskFormKey = userTask.getFormKey();
            activeTaskSkipExpression = userTask.getSkipExpression();
            activeTaskAssignee = getAssigneeValue(userTask, migrationContext, null);
            activeTaskOwner = userTask.getOwner();
            activeTaskCandidateUsers = userTask.getCandidateUsers();
            activeTaskCandidateGroups = userTask.getCandidateGroups();
            activeTaskIdVariableName = userTask.getTaskIdVariableName();
        }

        CreateUserTaskBeforeContext beforeContext = new CreateUserTaskBeforeContext(userTask, execution, activeTaskName, activeTaskDescription, activeTaskDueDate,
                activeTaskPriority, activeTaskCategory, activeTaskFormKey, activeTaskSkipExpression, activeTaskAssignee, activeTaskOwner,
                activeTaskCandidateUsers, activeTaskCandidateGroups);

        if (processEngineConfiguration.getCreateUserTaskInterceptor() != null) {
            processEngineConfiguration.getCreateUserTaskInterceptor().beforeCreateUserTask(beforeContext);
        }

        if (StringUtils.isNotEmpty(beforeContext.getName())) {
            String name = null;
            try {
                Object nameValue = expressionManager.createExpression(beforeContext.getName()).getValue(execution);
                if (nameValue != null) {
                    name = nameValue.toString();
                }
            } catch (FlowableException e) {
                name = beforeContext.getName();
                LOGGER.warn("property not found in task name expression {}", e.getMessage());
            }
            task.setName(name);
        }

        if (StringUtils.isNotEmpty(beforeContext.getDescription())) {
            String description = null;
            try {
                Object descriptionValue = expressionManager.createExpression(beforeContext.getDescription()).getValue(execution);
                if (descriptionValue != null) {
                    description = descriptionValue.toString();
                }
            } catch (FlowableException e) {
                description = beforeContext.getDescription();
                LOGGER.warn("property not found in task description expression {}", e.getMessage());
            }
            task.setDescription(description);
        }

        if (StringUtils.isNotEmpty(beforeContext.getDueDate())) {
            Object dueDate = expressionManager.createExpression(beforeContext.getDueDate()).getValue(execution);
            if (dueDate != null) {
                if (dueDate instanceof Date) {
                    task.setDueDate((Date) dueDate);
                } else if (dueDate instanceof String) {
                    String businessCalendarName = null;
                    if (StringUtils.isNotEmpty(userTask.getBusinessCalendarName())) {
                        businessCalendarName = expressionManager.createExpression(userTask.getBusinessCalendarName()).getValue(execution).toString();
                    } else {
                        businessCalendarName = DueDateBusinessCalendar.NAME;
                    }

                    BusinessCalendar businessCalendar = processEngineConfiguration.getBusinessCalendarManager()
                            .getBusinessCalendar(businessCalendarName);
                    task.setDueDate(businessCalendar.resolveDuedate((String) dueDate));

                } else {
                    throw new FlowableIllegalArgumentException("Due date expression does not resolve to a Date or Date string: " + activeTaskDueDate);
                }
            }
        }

        if (StringUtils.isNotEmpty(beforeContext.getPriority())) {
            final Object priority = expressionManager.createExpression(beforeContext.getPriority()).getValue(execution);
            if (priority != null) {
                if (priority instanceof String) {
                    try {
                        task.setPriority(Integer.valueOf((String) priority));
                    } catch (NumberFormatException e) {
                        throw new FlowableIllegalArgumentException("Priority does not resolve to a number: " + priority, e);
                    }
                } else if (priority instanceof Number) {
                    task.setPriority(((Number) priority).intValue());
                } else {
                    throw new FlowableIllegalArgumentException("Priority expression does not resolve to a number: " + activeTaskPriority);
                }
            }
        }

        if (StringUtils.isNotEmpty(beforeContext.getCategory())) {
            String category = null;
            try {
                Object categoryValue = expressionManager.createExpression(beforeContext.getCategory()).getValue(execution);
                if (categoryValue != null) {
                    category = categoryValue.toString();
                }
            }  catch (FlowableException e) {
                category = beforeContext.getCategory();
                LOGGER.warn("property not found in task category expression {}", e.getMessage());
            }
            task.setCategory(category);
        }

        if (StringUtils.isNotEmpty(beforeContext.getFormKey())) {
            String formKey = null;
            try {
                Object formKeyValue = expressionManager.createExpression(beforeContext.getFormKey()).getValue(execution);
                if (formKeyValue != null) {
                    formKey = formKeyValue.toString();
                }
            } catch (FlowableException e) {
                formKey = beforeContext.getFormKey();
                LOGGER.warn("property not found in task formKey expression {}", e.getMessage());
            }
            task.setFormKey(formKey);
        }

        boolean skipUserTask = SkipExpressionUtil.isSkipExpressionEnabled(beforeContext.getSkipExpression(), userTask.getId(), execution, commandContext)
                && SkipExpressionUtil.shouldSkipFlowElement(beforeContext.getSkipExpression(), userTask.getId(), execution, commandContext);

        TaskHelper.insertTask(task, (ExecutionEntity) execution, !skipUserTask, (!skipUserTask && processEngineConfiguration.isEnableEntityLinks()));

        // Handling assignments need to be done after the task is inserted, to have an id
        if (!skipUserTask) {
            if (processEngineConfiguration.isLoggingSessionEnabled()) {
                BpmnLoggingSessionUtil.addLoggingData(LoggingSessionConstants.TYPE_USER_TASK_CREATE, "User task '" +
                        task.getName() + "' created", task, execution);
            }

            handleAssignments(taskService, beforeContext.getAssignee(), beforeContext.getOwner(), beforeContext.getCandidateUsers(),
                    beforeContext.getCandidateGroups(), task, expressionManager, execution, processEngineConfiguration);

            if (processEngineConfiguration.getCreateUserTaskInterceptor() != null) {
                CreateUserTaskAfterContext afterContext = new CreateUserTaskAfterContext(userTask, task, execution);
                processEngineConfiguration.getCreateUserTaskInterceptor().afterCreateUserTask(afterContext);
            }

            if (processEngineConfiguration.isModeV2()) {
                //新形态流程系统变量，设置指定节点系统变量,设置任务基础属性，如任务ID、任务创建时间
                Object processSystemVariableObject = execution.getVariable("processSystem");
                if (processSystemVariableObject instanceof Map) {
                    Map<String, Object> processSystemVariables = (Map<String, Object>) processSystemVariableObject;
                    Map<String, Object> nodeVar = new HashMap<>();

                    //普通节点时，即非多实例任务
                    if (!userTask.hasMultiInstanceLoopCharacteristics()) {
                        nodeVar.put("nodeTitle", userTask.getAttributeValue(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE, "elementTitle"));
                        nodeVar.put("nodeDesc", userTask.getAttributeValue(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE, "elementDescription"));
                        nodeVar.put("nodeParticipants", task.getCandidates().stream().map(IdentityLinkInfo::getUserId).filter(StringUtils::isNotEmpty).collect(Collectors.toList()));
                        nodeVar.put("nodeAssignees", Collections.singletonList(task.getAssignee()));
                        nodeVar.put("nodeStartTime", ZonedDateTime.now());
                    } else {
                        // 直接使用多实例Behavior设置的nodeVar，无需重新new
                        nodeVar = (Map<String, Object>) processSystemVariables.getOrDefault(userTask.getId(), new HashMap<>());
                    }

                    nodeVar.put("taskId", task.getId());
                    nodeVar.put("taskStartTime", ZonedDateTime.ofInstant(task.getCreateTime().toInstant(), ZoneId.systemDefault()));
                    nodeVar.put("taskParticipants", task.getCandidates().stream().map(IdentityLinkInfo::getUserId).filter(StringUtils::isNotEmpty).collect(Collectors.toList()));
                    nodeVar.put("taskCompleteUser", StringUtils.isNotBlank(task.getAssignee()) ? Collections.singletonList(task.getAssignee()) : task.getCandidates().stream().map(IdentityLinkInfo::getUserId).filter(StringUtils::isNotEmpty).collect(Collectors.toList()));
                    nodeVar.put("taskEndTime", null);
                    nodeVar.put("taskOutcome", null);

                    processSystemVariables.put(userTask.getId(), nodeVar);
                    execution.setVariable("processSystem", processSystemVariables);
                }
            }

            processEngineConfiguration.getListenerNotificationHelper().executeTaskListeners(task, TaskListener.EVENTNAME_CREATE);

            // All properties set, now firing 'create' events
            FlowableEventDispatcher eventDispatcher = processEngineConfiguration.getTaskServiceConfiguration().getEventDispatcher();
            if (eventDispatcher != null  && eventDispatcher.isEnabled()) {
                eventDispatcher.dispatchEvent(FlowableTaskEventBuilder.createEntityEvent(FlowableEngineEventType.TASK_CREATED, task),
                        processEngineConfiguration.getEngineCfgKey());
            }

            if (StringUtils.isNotEmpty(activeTaskIdVariableName)) {
                Expression expression = expressionManager.createExpression(userTask.getTaskIdVariableName());
                String idVariableName = (String) expression.getValue(execution);
                if (StringUtils.isNotEmpty(idVariableName)) {
                    execution.setVariable(idVariableName, task.getId());
                }
            }

            //新增代码，用于判断InitiateTask是否自动完成任务
            if (ProcessConstant.TASK_CATEGORY_INITIATE_TASK.equalsIgnoreCase(task.getCategory())) {
                String initiateStatusKey = task.getTaskDefinitionKey() + "_InitiateStatus";
                Object value = execution.getVariable(initiateStatusKey);
                execution.setVariable(initiateStatusKey, true);
                if (value == null) {
                    processEngineConfiguration.getTaskService().setAssignee(task.getId(), Authentication.getAuthenticatedUserId());
                    processEngineConfiguration.getTaskService().complete(task.getId());
                }
            } else if (ProcessConstant.TASK_CATEGORY_CC_TASK.equalsIgnoreCase(task.getCategory())) {
                processEngineConfiguration.getTaskService().complete(task.getId());
            }
        } else {
            TaskHelper.deleteTask(task, null, false, false, false); // false: no events fired for skipped user task
            leave(execution);
        }

    }

    @Override
    public void leave(DelegateExecution execution) {
        Task task = (Task) execution.getCurrentFlowElement();
        Object processSystemVariableObject = execution.getVariable("processSystem");
        Map<String, Object> nodeVar = new HashMap<>();
        if (processSystemVariableObject instanceof Map) {
            Map<String, Object> processSystemVariables = (Map<String, Object>) processSystemVariableObject;
            nodeVar = (Map<String, Object>)processSystemVariables.getOrDefault(userTask.getId(), new HashMap<>());
        }
        //此处只处理多实例任务，而非多实例任务已在父类中处理
        if (task.hasMultiInstanceLoopCharacteristics() && execution.hasVariable("loopCounter")) {
            Map<String, Object> processSystemVariables = (Map<String, Object>) processSystemVariableObject;

            List<String> nodeAssignees = (List<String>) nodeVar.get("nodeAssignees");

            Object taskCompleteUserObject = nodeVar.get("taskCompleteUser");
            if (taskCompleteUserObject instanceof List) {
                nodeAssignees.addAll((List<String>) taskCompleteUserObject);
            }
            nodeVar.put("nodeAssignees", nodeAssignees);

            processSystemVariables.put(userTask.getId(), nodeVar);
            execution.setVariable("processSystem", processSystemVariables);
        }else{
            if ("Rejected".equals(nodeVar.get(ProcessConstant.TASK_OUTCOME))){
                String moveTo = getMoveTo(execution);
                if (!moveTo.equals(MoveToAfterRejected.NEXT_NODE.getValue())){
                    ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
                    RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
                    RepositoryService repositoryService = processEngineConfiguration.getRepositoryService();
                    List<String> waitExecutions = new ArrayList<>();
                    List<Execution> executions = runtimeService.createExecutionQuery()
                            .processInstanceId(execution.getProcessInstanceId())
                            .list();
                    Iterator<Execution> iterator = executions.iterator();
                    while (iterator.hasNext()) {
                        Execution exe = iterator.next();
                        if (exe.getParentId() ==null || !exe.getParentId().equals(execution.getProcessInstanceId())) {
                            iterator.remove();
                        }else{
                            waitExecutions.add(exe.getId());
                        }
                    }
                    String startAct = null;
                    String endAct = null;
                    String moveNode = getMoveTo(execution);
                    BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
                    Collection<FlowElement> flowElements = bpmnModel.getMainProcess().getFlowElements();
                    for (FlowElement flowElement : flowElements) {
                        if (flowElement instanceof StartEvent) {
                            StartEvent startEvent = (StartEvent) flowElement;
                            startAct =  startEvent.getId();
                        }else if (flowElement instanceof EndEvent) {
                            EndEvent endEvent = (EndEvent) flowElement;
                            endAct =  endEvent.getId();
                        }
                    }
                    // 拒绝
                    if (MoveToAfterRejected.START.getValue().equals(moveNode)){
                        runtimeService.createChangeActivityStateBuilder().processInstanceId(execution.getProcessInstanceId())
                                .moveExecutionsToSingleActivityId(waitExecutions,startAct)
                                .changeState();
                        return;
                    }else if (MoveToAfterRejected.END.getValue().equals(moveNode)){
                        runtimeService.createChangeActivityStateBuilder().processInstanceId(execution.getProcessInstanceId())
                                .moveExecutionsToSingleActivityId(waitExecutions,endAct)
                                .changeState();
                        return;
                    }
                }
            }
            HistoricTaskInstanceQueryImpl historicTaskInstanceQuery = new HistoricTaskInstanceQueryImpl();
            historicTaskInstanceQuery.executionId(execution.getId()).taskDefinitionKey(execution.getCurrentActivityId());
            List<HistoricTaskInstance> taskInstances = CommandContextUtil.getHistoricTaskService().findHistoricTaskInstancesByQueryCriteria(historicTaskInstanceQuery);

            String scopeType = (String) execution.getVariableLocal("executionScopeType");
            if (StringUtils.isNotBlank(scopeType) && scopeType.equals(AddSignEnum.SUBSEQUENT_ADD_SIGN_FROM.getValue())){
                List<ExecutionEntity> list = CommandContextUtil.getExecutionEntityManager().findChildExecutionsByParentExecutionId(execution.getId());
                CommandContextUtil.getActivityInstanceEntityManager().recordActivityEnd((ExecutionEntity) execution, null);
                for (ExecutionEntity executionEntity:list){
                    CommandContextUtil.getActivityInstanceEntityManager().recordActivityStart(executionEntity);
                }
                return;
            }

            if (AddSignEnum.PREVIOUS.getValue().equals(scopeType)){
                CommandContextUtil.getActivityInstanceEntityManager().recordActivityEnd((ExecutionEntity) execution, null);
                CommandContextUtil.getExecutionEntityManager().deleteExecutionAndRelatedData((ExecutionEntity)execution,"child addSign execution delete",true);
                return;
            } else if (AddSignEnum.SUBSEQUENT.getValue().equals(scopeType)){
                CommandContextUtil.getActivityInstanceEntityManager().recordActivityEnd((ExecutionEntity) execution, null);
                CommandContextUtil.getExecutionEntityManager().deleteExecutionAndRelatedData((ExecutionEntity)execution,"child addSign execution delete",true);
                super.leave(execution.getParent());
                return;
            }
        }
        super.leave(execution);
    }

    private String getMoveTo(DelegateExecution execution) {
        String moveTo = "nextNode";
        String operatePermission = execution.getCurrentFlowElement().getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_OPERATION_PERMISSION);
        List<TaskOperationPermission> taskOptPermissionList;
        try {
            mapper.registerModule(new JavaTimeModule());
            taskOptPermissionList = mapper.readValue(operatePermission, new TypeReference<List<TaskOperationPermission>>() {
            });
            TaskOperationPermission taskOptPermission = taskOptPermissionList.stream()
                    .filter(permission -> StringUtils.equals(permission.getName(), "reject") && permission.getOperationEnabled())
                    .findFirst().get();
            if (StringUtils.isNotBlank(taskOptPermission.getMoveTo())) {
                moveTo = taskOptPermission.getMoveTo();
            }
        } catch (JsonProcessingException e) {
            LOGGER.error("getMoveTo error", e);
        }
        return moveTo;

    }
}
