package com.dogfood.aa20240808.processV2.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.netease.lowcode.auth.domain.LCAPUser;
import com.dogfood.aa20240808.context.UserContext;
import com.dogfood.aa20240808.processV2.system.command.*;
import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.domain.structure.*;
import com.dogfood.aa20240808.processV2.system.service.*;
import com.dogfood.aa20240808.processV2.system.engine.service.ProcessTaskAssigneeService;
import com.dogfood.aa20240808.processV2.system.util.*;
import com.dogfood.aa20240808.service.UserInfoService;
import com.dogfood.aa20240808.web.dto.UserListReqDTO;
import com.dogfood.aa20240808.web.dto.UserListResDTO;
import com.dogfood.aa20240808.repository.process.CustomTaskMapper;
import com.dogfood.aa20240808.repository.process.ProcessFormMapper;
import com.dogfood.aa20240808.util.SnowflakeIdWorker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.query.QueryProperty;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.impl.persistence.entity.CommentEntityImpl;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.task.service.impl.HistoricTaskInstanceQueryProperty;
import org.flowable.task.service.impl.TaskQueryProperty;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SystemTaskInstanceServiceImpl implements SystemTaskInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(SystemTaskInstanceServiceImpl.class);

    private static final HashMap<String, QueryProperty> historyAllowedSortProperties = new HashMap<>();
    private static final HashMap<String, QueryProperty> runtimeAllowedSortProperties = new HashMap<>();

    private static final String AUTO = "auto";
    private static final String AUTOINCREMENT = "autoIncrement";
    private static final String MANUAL = "manual";

    static {
        runtimeAllowedSortProperties.put("taskId", TaskQueryProperty.TASK_ID);
        runtimeAllowedSortProperties.put("title", TaskQueryProperty.NAME);
        runtimeAllowedSortProperties.put("description", TaskQueryProperty.DESCRIPTION);
        runtimeAllowedSortProperties.put("processInstanceId", TaskQueryProperty.PROCESS_INSTANCE_ID);
        runtimeAllowedSortProperties.put("createTime", TaskQueryProperty.CREATE_TIME);
        runtimeAllowedSortProperties.put("completeTime", TaskQueryProperty.DUE_DATE);
        runtimeAllowedSortProperties.put("taskDefName", TaskQueryProperty.TASK_DEFINITION_KEY);

        historyAllowedSortProperties.put("taskId", HistoricTaskInstanceQueryProperty.HISTORIC_TASK_INSTANCE_ID);
        historyAllowedSortProperties.put("title", HistoricTaskInstanceQueryProperty.TASK_NAME);
        historyAllowedSortProperties.put("description", HistoricTaskInstanceQueryProperty.TASK_DESCRIPTION);
        historyAllowedSortProperties.put("processInstanceId", HistoricTaskInstanceQueryProperty.PROCESS_INSTANCE_ID);
        historyAllowedSortProperties.put("createTime", HistoricTaskInstanceQueryProperty.START);
        historyAllowedSortProperties.put("completeTime", HistoricTaskInstanceQueryProperty.END);
        historyAllowedSortProperties.put("taskDefName", HistoricTaskInstanceQueryProperty.TASK_DEFINITION_KEY);
    }

    private static final String FIELD_PERMISSION_EDITABLE = "editable";
    private static final String FIELD_PERMISSION_READONLY = "readOnly";
    private static final String IDENTITY_TYPE_CANDIDATE = "candidate";
    private static final String IDENTITY_TYPE_ASSIGNEE = "assignee";
    private static final String COUNTER_SIGN_LOOP_COUNTER = "loopCounter";

    private static final String OPERATION_PERMISSION_WITHDRAW = "withdraw";
    private static final String OPERATION_PERMISSION_APPROVE = "approve";
    private static final String OPERATION_PERMISSION_REJECT = "reject";

    @Autowired
    private ProcessFormMapper processFormMapper;
    @Autowired
    HistoryService historyService;

    @Autowired
    TaskService taskService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Resource
    private UserInfoService userInfoService;

    @Autowired
    private SystemProcessRecordService systemProcessRecordService;

    @Autowired
    private SystemProcessInstanceService systemProcessInstanceService;

    @Autowired
    private ProcessTaskAssigneeService processTaskAssigneeService;

    @Resource
    private SystemProcessDefinitionService systemProcessDefinitionService;

    @Resource
    private SystemTaskDefinitionService systemTaskDefinitionService;

    protected ObjectMapper mapper = new ObjectMapper();

    @Resource
    private CustomTaskMapper customTaskMapper;

    @Autowired
    private Environment environment;

    @Autowired
    private ManagementService managementService;

    @Override
    public PageOf<HistoricTaskInstance> getHistoryTaskInstanceList(String taskId, String userName, String processInstanceId,
                                                                   List<String> processInstanceIdIn, String processDefUniqueKey,
                                                                   String taskDefName, String category, ZonedDateTime createTimeBefore,
                                                                   ZonedDateTime createTimeAfter, ZonedDateTime endTimeBefore,
                                                                   ZonedDateTime endTimeAfter, Long page, Long size, String sort,
                                                                   String order, Boolean finished) {
        Integer pageInt = Math.toIntExact(page);
        Integer sizeInt = Math.toIntExact(size);
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
        if (StringUtils.isNotBlank(taskId)) {
            query.taskId(taskId);
        }
        query.taskWithoutDeleteReason();
        if (ObjectUtils.isNotEmpty(finished)) {
            if (finished) {
                query.finished();
            } else {
                query.unfinished();
            }
        }
        if (StringUtils.isNotBlank(processInstanceId)) {
            query.processInstanceId(processInstanceId);
        }
        if (CollectionUtils.isNotEmpty(processInstanceIdIn)) {
            query.processInstanceIdIn(processInstanceIdIn);
        }
        if (StringUtils.isNotBlank(processDefUniqueKey)) {
            query.processDefinitionKey(processDefUniqueKey);
        }
        if (StringUtils.isNotBlank(taskDefName)) {
            query.taskDefinitionKey(taskDefName);
        }
        if (ObjectUtils.isNotEmpty(createTimeBefore)) {
            query.taskCreatedBefore(SystemProcessUtil.ZonedDateTimeToDate(createTimeBefore));
        }
        if (ObjectUtils.isNotEmpty(createTimeAfter)) {
            query.taskCreatedAfter(SystemProcessUtil.ZonedDateTimeToDate(createTimeAfter));
        }
        if (ObjectUtils.isNotEmpty(endTimeBefore)) {
            query.taskCompletedBefore(SystemProcessUtil.ZonedDateTimeToDate(endTimeBefore));
        }
        if (ObjectUtils.isNotEmpty(endTimeAfter)) {
            query.taskCompletedAfter(SystemProcessUtil.ZonedDateTimeToDate(endTimeAfter));
        }
        if (StringUtils.isNotBlank(userName)) {
            query.or();
            query.taskCandidateUser(userName);
            query.taskAssignee(userName);
            query.endOr();
        }
        if (StringUtils.isNotBlank(category)) {
            query.taskCategory(category);
        }
        Pageable pageable = SystemProcessPageUtil.createPageable(pageInt, sizeInt, sort, order);

        return SystemProcessPageUtil.paginateQueryV2(pageable, query, "createTime", historyAllowedSortProperties);
    }

    @Override
    public PageOf<HistoricTaskInstance> getTaskInstanceList(String taskId, String userName, String processInstanceId,
                                                               List<String> processInstanceIdIn, String processDefUniqueKey,
                                                               String taskDefName, String category, ZonedDateTime createTimeBefore,
                                                               ZonedDateTime createTimeAfter, ZonedDateTime endTimeBefore,
                                                               ZonedDateTime endTimeAfter, Long page, Long size, String sort,
                                                               String order, Boolean finished) {
        Integer pageInt = Math.toIntExact(page);
        Integer sizeInt = Math.toIntExact(size);
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
        if (finished != null){
            if (finished){
                query.finished();
            }else{
                query.unfinished();
            }
        }
        if (StringUtils.isNotBlank(taskId)) {
            query.taskId(taskId);
        }
        if (StringUtils.isNotBlank(processInstanceId)) {
            query.processInstanceId(processInstanceId);
        }
        if (CollectionUtils.isNotEmpty(processInstanceIdIn)) {
            query.processInstanceIdIn(processInstanceIdIn);
        }
        if (StringUtils.isNotBlank(processDefUniqueKey)) {
            query.processDefinitionKey(processDefUniqueKey);
        }
        if (StringUtils.isNotBlank(taskDefName)) {
            query.taskDefinitionKey(taskDefName);
        }
        if (ObjectUtils.isNotEmpty(createTimeBefore)) {
            query.taskCreatedBefore(SystemProcessUtil.ZonedDateTimeToDate(createTimeBefore));
        }
        if (ObjectUtils.isNotEmpty(createTimeAfter)) {
            query.taskCreatedAfter(SystemProcessUtil.ZonedDateTimeToDate(createTimeAfter));
        }
        if (ObjectUtils.isNotEmpty(endTimeBefore)) {
            query.taskDueBefore(SystemProcessUtil.ZonedDateTimeToDate(endTimeBefore));
        }
        if (ObjectUtils.isNotEmpty(endTimeAfter)) {
            query.taskDueAfter(SystemProcessUtil.ZonedDateTimeToDate(endTimeAfter));
        }
        if (StringUtils.isNotBlank(userName)) {
            query.or();
            query.taskCandidateUser(userName);
            query.taskAssignee(userName);
            query.endOr();
        }
        if (StringUtils.isNotBlank(category)) {
            query.taskCategory(category);
        }
        if (StringUtils.isNotBlank(order)) {
            if ("desc".equals(sort)){
                query.orderBy(historyAllowedSortProperties.get(order)).desc();
            }else{
                query.orderBy(historyAllowedSortProperties.get(order)).asc();
            }

        }
        Pageable pageable = SystemProcessPageUtil.createPageable(pageInt, sizeInt, sort, order);

        return SystemProcessPageUtil.paginateQueryV2(pageable, query, "createTime", historyAllowedSortProperties);
    }

    @Override
    public PageOf<HistoricTaskInstance> getRunTaskInstanceList(String taskId, String userName, String processInstanceId,
                                                               List<String> processInstanceIdIn, String processDefUniqueKey,
                                                               String taskDefName, String category, ZonedDateTime createTimeBefore,
                                                               ZonedDateTime createTimeAfter, ZonedDateTime endTimeBefore,
                                                               ZonedDateTime endTimeAfter, Long page, Long size, String sort,
                                                               String order, Boolean latest) {
        Integer pageInt = Math.toIntExact(page);
        Integer sizeInt = Math.toIntExact(size);
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery();
        if (StringUtils.isNotBlank(taskId)) {
            query.taskId(taskId);
        }
        query.unfinished();
        if (StringUtils.isNotBlank(processInstanceId)) {
            query.processInstanceId(processInstanceId);
        }
        if (CollectionUtils.isNotEmpty(processInstanceIdIn)) {
            query.processInstanceIdIn(processInstanceIdIn);
        }
        if (StringUtils.isNotBlank(processDefUniqueKey)) {
            query.processDefinitionKey(processDefUniqueKey);
        }
        if (StringUtils.isNotBlank(taskDefName)) {
            query.taskDefinitionKey(taskDefName);
        }
        if (ObjectUtils.isNotEmpty(createTimeBefore)) {
            query.taskCreatedBefore(SystemProcessUtil.ZonedDateTimeToDate(createTimeBefore));
        }
        if (ObjectUtils.isNotEmpty(createTimeAfter)) {
            query.taskCreatedAfter(SystemProcessUtil.ZonedDateTimeToDate(createTimeAfter));
        }
        if (ObjectUtils.isNotEmpty(endTimeBefore)) {
            query.taskDueBefore(SystemProcessUtil.ZonedDateTimeToDate(endTimeBefore));
        }
        if (ObjectUtils.isNotEmpty(endTimeAfter)) {
            query.taskDueAfter(SystemProcessUtil.ZonedDateTimeToDate(endTimeAfter));
        }
        if (StringUtils.isNotBlank(userName)) {
            query.or();
            query.taskCandidateUser(userName);
            query.taskAssignee(userName);
            query.endOr();
        }
        if (StringUtils.isNotBlank(category)) {
            query.taskCategory(category);
        }
        if (ObjectUtils.isNotEmpty(latest) && latest) {
            query.orderBy(historyAllowedSortProperties.get("createTime")).desc();
        }
        Pageable pageable = SystemProcessPageUtil.createPageable(pageInt, sizeInt, sort, order);

        return SystemProcessPageUtil.paginateQueryV2(pageable, query, "createTime", historyAllowedSortProperties);
    }

    @Override
    public TaskInst getTaskInstance(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            logger.error("The task does not exist!");
            throw new ProcessException("The task does not exist!");
        }
        TaskInst taskInstance = toLCAPTaskInstance(historicTaskInstance);
        logger.info("The taskInstance is {}", taskInstance);
        return taskInstance;
    }

    @Override
    public void reassignTask(String taskId, String userForReassign) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo != null) {
            Authentication.setAuthenticatedUserId(userInfo.getUserName());
        }
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        if (taskInstance.getEndTime() != null) {
            logger.error("Task has ended!");
            throw new ProcessException("Task has ended!");
        }
        checkOperationPermission(taskInstance, "reassign", null);
        List<String> candidateUsers = getCandidateUsers(taskId);
        if(CollectionUtils.isEmpty(candidateUsers) || !candidateUsers.contains(userInfo.getUserName())) {
            logger.error("current login user {} is not task {} candidate users", userInfo.getUserName(), taskId);
            throw new ProcessException("you are not task candidate user, can't reassign");
        }

        Object processSystemVariableObject = runtimeService.getVariable(taskInstance.getProcessInstanceId(), "processSystem");
        if (processSystemVariableObject instanceof Map) {
            Map<String, Object> processSystemVariables = (Map<String, Object>) processSystemVariableObject;
            Map<String, Object> nodeVar = (Map<String, Object>) processSystemVariables.getOrDefault(taskInstance.getTaskDefinitionKey(), new HashMap<>());

            List<String> nodeParticipants = (List<String>) nodeVar.get("nodeParticipants");
            // nodeParticipants将userInfo.getUserName()替换为userForReassign
            nodeVar.put("nodeParticipants", nodeParticipants.stream()
                    .map(participant -> participant.equals(userInfo.getUserName()) ? userForReassign : participant)
                    .collect(Collectors.toList()));

            processSystemVariables.put(taskInstance.getTaskDefinitionKey(), nodeVar);
            taskService.setVariable(taskId, "processSystem", processSystemVariables);
        }

        taskService.addCandidateUser(taskId, userForReassign);
        taskService.deleteCandidateUser(taskId, userInfo.getUserName());
        Authentication.setAuthenticatedUserId(null);
        logger.info("The task {} is reassigned from {} to {}", taskId, userInfo.getUserName(), userForReassign);
    }

    @Override
    public String getTaskDestinationUrl(String taskId, String frontendKey) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        HistoricTaskInstance taskInstance= historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        String url;
        Map<String, Object> custom = new HashMap<>();
        if (taskInstance.getEndTime() != null) {
            custom = (Map<String, Object>)historyService.createHistoricVariableInstanceQuery().processInstanceId(taskInstance.getProcessInstanceId())
                            .variableName(ProcessConstant.KEY_VARIABLE_CUSTOM).singleResult().getValue();
            String destination = repositoryService.getBpmnModel(taskInstance.getProcessDefinitionId())
                    .getProcesses()
                    .get(0)
                    .getFlowElement(taskInstance.getTaskDefinitionKey())
                    .getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_DESTINATION_MAP);
            try {
                mapper.registerModule(new JavaTimeModule());
                Map<String, String> destinationMap = mapper.readValue(destination, new TypeReference<Map<String, String>>() {
                });
                url = destinationMap.get(frontendKey) + "?taskId=" + taskId;
            } catch (IOException e) {
                logger.error("The destination map is invalid!");
                throw new ProcessException("The destination map is invalid!");
            }
        } else {
            custom = (Map<String, Object>)runtimeService.getVariable(taskInstance.getProcessInstanceId(),ProcessConstant.KEY_VARIABLE_CUSTOM);
            url = taskService.getTaskDestinationUrl(taskId, frontendKey);
            url = url + "?taskId=" + taskId;

        }
        if (custom.size()>0&& custom.get("data") != null){
            Map<String, Object> data = (Map<String, Object>) custom.get("data");
            if (data != null && data.get("id") != null){
                url = url + "&dataId="+ data.get("id");
            }
        }
        logger.info("The task {} destination url is {}", taskId, url);
        return url;
    }

    @Override
    public void approveTask(String procDefKey, String taskId, Map<String, Object> data, String comment) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo != null) {
            Authentication.setAuthenticatedUserId(userInfo.getUserName());
        }
        HistoricTaskInstance taskInstance= historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        if (StringUtils.isBlank(procDefKey)){
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().
                    processInstanceId(taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId())
                    .singleResult();
            if (processInstance == null) {
                throw new ProcessException("Process does not exist!");
            }
            procDefKey = processInstance.getProcessDefinitionKey();
        }
        if (taskInstance.getCategory() != null && !taskInstance.getCategory().equals(ProcessConstant.TASK_CATEGORY_APPROVAL_TASK)) {
            logger.error("Task does not support approve!");
            throw new ProcessException("Task does not support approve!");
        }
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        if (taskInstance.getEndTime() != null) {
            logger.error("Task has ended!");
            throw new ProcessException("Task has ended!");
        }
        checkOperationPermission(taskInstance, "approve", comment);
        List<String> candidateUsers = getCandidateUsers(taskId);
        if (CollectionUtils.isEmpty(candidateUsers) && StringUtils.isEmpty(taskInstance.getAssignee())) {
            logger.error("Task has no candidate users!");
            throw new ProcessException("Task has no candidate users!");
        }
        logger.info("The current userInfo is {}", userInfo);
        if (!candidateUsers.contains(userInfo.getUserName()) && !taskInstance.getAssignee().equals(userInfo.getUserName())) {
            logger.error("You are not a candidate for the task!");
            throw new ProcessException("You are not a candidate for the task!");
        }
        // 过滤之前保存用户提交数据,避免影响审批业务
        try {
            saveComment(taskId,taskInstance.getProcessInstanceId(),userInfo.getUserName(), "approve",data, comment);
        }catch (Exception e){
            logger.error("save comment error {} {}",taskId,comment);
        }
        //表单字段过滤
        logger.info("The data is {}", data);
        if (data != null && !data.isEmpty()) {
            data = correctProcessCustomData(data);
            data = checkFieldPermission(taskInstance, Collections.singletonList(FIELD_PERMISSION_EDITABLE), data);
            logger.info("The data is {}", data);
        }


        Map<String, Object> processVariables = runtimeService.getVariables(taskInstance.getProcessInstanceId());
        Map<String, Object> processSystem = (Map<String, Object>) processVariables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM);
        logger.info("The processSystem is {}", processSystem);
        Map<String, Object> variableData = (Map<String, Object>) processVariables.get(ProcessConstant.KEY_VARIABLE_CUSTOM);
        editFormData(variableData, data, procDefKey);
        logger.info("The data is {}", data);
        //多人审批任务的处理
        Map<String, Object> nodeVar = (Map<String, Object>) processSystem.get(taskInstance.getTaskDefinitionKey());
        logger.info("The nodeVar is {}", nodeVar);
        logger.info("The taskInstance parent taskId is {}", taskInstance.getParentTaskId());
        Execution execution = runtimeService.createExecutionQuery()
                .executionId(taskInstance.getExecutionId())
                .processInstanceId(taskInstance.getProcessInstanceId())
                .singleResult();
        if (taskInstance.getCategory() != null && taskInstance.getCategory().equals(ProcessConstant.TASK_CATEGORY_APPROVAL_TASK) &&
            !execution.getParentId().equals(taskInstance.getProcessInstanceId())) {
            Integer nrOfApprovedInstances = (Integer) nodeVar.get(ProcessConstant.NR_OF_APPROVED_INSTANCES);
            if (nodeVar != null && nrOfApprovedInstances!= null){
                logger.info("The nrOfApprovedInstances is {}", nrOfApprovedInstances);
                nodeVar.put(ProcessConstant.NR_OF_APPROVED_INSTANCES, nrOfApprovedInstances + 1);
                logger.info("The nrOfApprovedInstances is {}", nrOfApprovedInstances);
            }
        }
        nodeVar.put(ProcessConstant.TASK_OUTCOME, "Approved");
        nodeVar.put(ProcessConstant.TASK_COMMENT, comment);

        processSystem.put(taskInstance.getTaskDefinitionKey(), nodeVar);
        processVariables.put(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM, processSystem);
        processVariables.put(ProcessConstant.KEY_VARIABLE_CUSTOM, variableData);
        runtimeService.setVariables(taskInstance.getExecutionId(),processVariables);
        taskService.setAssignee(taskId, userInfo.getUserName());
        taskService.complete(taskId, processVariables);
        String parentTaskId = taskInstance.getParentTaskId();
        //4.处理加签父任务
        if (StringUtils.isNotBlank(parentTaskId)) {
            long subTaskCount = taskService.getSubTasks(parentTaskId).size();
            if (subTaskCount ==0 ) {
                Task task = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
                //处理前后加签的任务
                if (task == null){
                    return;
                }
                //taskService.resolveTask(parentTaskId);
                if(AddSignEnum.PREVIOUS_ADD_SIGN_FROM.getValue().equals(task.getScopeType())){
                    taskService.addCandidateUser(parentTaskId, task.getOwner());
                }

            }
        }
        Authentication.setAuthenticatedUserId(null);
        logger.info("The task {} is approved by {}", taskId, userInfo.getUserName());
    }

    @Override
    public void rejectTask(String procDefKey, String taskId, Map<String, Object> data, String comment) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo != null) {
            Authentication.setAuthenticatedUserId(userInfo.getUserName());
        }
        HistoricTaskInstance taskInstance= historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        if (taskInstance.getCategory() != null && !taskInstance.getCategory().equals(ProcessConstant.TASK_CATEGORY_APPROVAL_TASK)) {
            logger.error("Task does not support reject!");
            throw new ProcessException("Task does not support reject!");
        }
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        if (taskInstance.getEndTime() != null) {
            logger.error("Task has ended!");
            throw new ProcessException("Task has ended!");
        }
        if (StringUtils.isBlank(procDefKey)){
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().
                    processInstanceId(taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId())
                    .singleResult();
            if (processInstance == null) {
                throw new ProcessException("Process does not exist!");
            }
            procDefKey = processInstance.getProcessDefinitionKey();
        }
        checkOperationPermission(taskInstance, "reject", comment);
        List<String> candidateUsers = getCandidateUsers(taskId);
        if (CollectionUtils.isEmpty(candidateUsers) && StringUtils.isEmpty(taskInstance.getAssignee())) {
            logger.error("Task has no candidate users!");
            throw new ProcessException("Task has no candidate users!");
        }
        if (!candidateUsers.contains(userInfo.getUserName()) && !taskInstance.getAssignee().equals(userInfo.getUserName())) {
            logger.error("You are not a candidate for the task!");
            throw new ProcessException("You are not a candidate for the task!");
        }
        // 过滤之前保存用户提交数据,避免审批业务
        try {
            saveComment(taskId,taskInstance.getProcessInstanceId(),userInfo.getUserName(), "reject",data, comment);
        }catch (Exception e){
            logger.error("save comment error {} {}",taskId,comment);
        }
        //表单字段过滤
        if (data != null && !data.isEmpty()) {
            data = correctProcessCustomData(data);
            if (data != null) {
                data = checkFieldPermission(taskInstance, Collections.singletonList(FIELD_PERMISSION_EDITABLE), data);
            }
        }
        Map<String, Object> processVariables = runtimeService.getVariables(taskInstance.getProcessInstanceId());
        Map<String, Object> processSystem = (Map<String, Object>) processVariables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM);
        logger.info("The processSystem is {}", processSystem);
        Map<String, Object> variableData = (Map<String, Object>) processVariables.get(ProcessConstant.KEY_VARIABLE_CUSTOM);
        editFormData(variableData, data, procDefKey);
        //多人审批任务的处理
        Map<String, Object> nodeVar = (Map<String, Object>) processSystem.get(taskInstance.getTaskDefinitionKey());
        Execution execution = runtimeService.createExecutionQuery()
                .executionId(taskInstance.getExecutionId())
                .processInstanceId(taskInstance.getProcessInstanceId())
                .singleResult();
        if (taskInstance.getCategory() != null && taskInstance.getCategory().equals(ProcessConstant.TASK_CATEGORY_APPROVAL_TASK) &&
            !execution.getParentId().equals(taskInstance.getProcessInstanceId())) {
            Integer nrOfRejectedInstances = (Integer) nodeVar.get(ProcessConstant.NR_OF_REJECTED_INSTANCES);
            if (nrOfRejectedInstances != null){
                nodeVar.put(ProcessConstant.NR_OF_REJECTED_INSTANCES, nrOfRejectedInstances + 1);
            }
        }
        nodeVar.put(ProcessConstant.TASK_OUTCOME, "Rejected");
        nodeVar.put(ProcessConstant.TASK_COMMENT, comment);

        processSystem.put(taskInstance.getTaskDefinitionKey(), nodeVar);
        processVariables.put(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM, processSystem);
        processVariables.put(ProcessConstant.KEY_VARIABLE_CUSTOM, variableData);
        taskService.setAssignee(taskId, userInfo.getUserName());
        taskService.complete(taskId, processVariables);
        String parentTaskId = taskInstance.getParentTaskId();
        //4.处理加签父任务
        if (StringUtils.isNotBlank(parentTaskId)) {
            long subTaskCount = taskService.getSubTasks(parentTaskId).size();
            if (subTaskCount ==0 ) {
                Task task = taskService.createTaskQuery().taskId(parentTaskId).singleResult();
                //处理前后加签的任务
                if (task == null){
                    return;
                }
                //taskService.resolveTask(parentTaskId);
                if(AddSignEnum.PREVIOUS_ADD_SIGN_FROM.getValue().equals(task.getScopeType())){
                    taskService.addCandidateUser(parentTaskId, task.getOwner());
                }

            }
        }
        Authentication.setAuthenticatedUserId(null);
        logger.info("The task {} is rejected by {}", taskId, userInfo.getUserName());
    }

    @Override
    public void submitTask(String procDefKey, String taskId, Map<String, Object> data) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo != null) {
            Authentication.setAuthenticatedUserId(userInfo.getUserName());
        }
        HistoricTaskInstance taskInstance= historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        if (taskInstance.getCategory() != null &&
                !taskInstance.getCategory().equals(ProcessConstant.TASK_CATEGORY_INITIATE_TASK) && !taskInstance.getCategory().equals(ProcessConstant.TASK_CATEGORY_SUBMIT_TASK)) {
            logger.error("Task does not support submit!");
            throw new ProcessException("Task does not support submit!");
        }
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        if (taskInstance.getEndTime() != null) {
            logger.error("Task has ended!");
            throw new ProcessException("Task has ended!");
        }
        if (StringUtils.isBlank(procDefKey)){
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().
                    processInstanceId(taskService.createTaskQuery().taskId(taskId).singleResult().getProcessInstanceId())
                    .singleResult();
            if (processInstance == null) {
                throw new ProcessException("Process does not exist!");
            }
            procDefKey = processInstance.getProcessDefinitionKey();
        }
        checkOperationPermission(taskInstance, "submit", null);
        List<String> candidateUsers = getCandidateUsers(taskId);
        if (CollectionUtils.isEmpty(candidateUsers)) {
            logger.error("Task has no candidate users!");
            throw new ProcessException("Task has no candidate users!");
        }
        if (!candidateUsers.contains(userInfo.getUserName())) {
            logger.error("You are not a candidate for the task!");
            throw new ProcessException("You are not a candidate for the task!");
        }
        // 过滤之前保存用户提交数据,避免审批业务
        try {
            saveComment(taskId,taskInstance.getProcessInstanceId(),userInfo.getUserName(), "submit",data, null);
        }catch (Exception e){
            logger.error("save comment error {} {}",taskId,null);
        }
        //表单字段过滤
        if (data != null && !data.isEmpty()) {
            data = correctProcessCustomData(data);
            if (data != null) {
                data = checkFieldPermission(taskInstance, Collections.singletonList(FIELD_PERMISSION_EDITABLE), data);
            }
        }
        Map<String, Object> processVariables = runtimeService.getVariables(taskInstance.getProcessInstanceId());
        Map<String, Object> processSystem = (Map<String, Object>) processVariables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM);
        logger.info("The processSystem is {}", processSystem);
        Map<String, Object> variableData = (Map<String, Object>) processVariables.get(ProcessConstant.KEY_VARIABLE_CUSTOM);
        editFormData(variableData, data, procDefKey);
        logger.info("The data is {}", data);
        processVariables.put(ProcessConstant.KEY_VARIABLE_CUSTOM, variableData);
        processVariables.put(taskInstance.getProcessDefinitionId().split(":")[0], processVariables);
        taskService.setAssignee(taskId, userInfo.getUserName());
        taskService.complete(taskId, processVariables);
        Authentication.setAuthenticatedUserId(null);
        logger.info("The task {} is submitted by {}", taskId, userInfo.getUserName());
    }

    @Override
    public Map<String, Object> getTaskInstanceForm(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        HistoricTaskInstance taskInstance= historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        //表单字段过滤
//        Map<String, Object> data = (Map<String, Object>) historyService.createHistoricVariableInstanceQuery()
//                .processInstanceId(taskInstance.getProcessInstanceId())
//                .variableName(ProcessConstant.KEY_VARIABLE_CUSTOM)
//                .singleResult()
//                .getValue();
        Map<String, Object> custom = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        if (taskInstance.getEndTime() != null) {
            if (StringUtils.isNoneBlank(taskInstance.getDeleteReason())) {
                //若任务非正常结束，那么该任务的快照将不存在，此时流程可能也已结束，所以干脆直接从
                custom = (Map<String, Object>) historyService.createHistoricVariableInstanceQuery()
                        .processInstanceId(taskInstance.getProcessInstanceId())
                        .variableName(ProcessConstant.KEY_VARIABLE_CUSTOM)
                        .singleResult()
                        .getValue();
                logger.info("Process instance custom: {}", custom);
            } else {
                List<Comment> commentList = systemProcessRecordService.getProcessInstanceRecord(taskInstance.getProcessInstanceId());
                if (CollectionUtils.isEmpty(commentList)) {
                    return new HashMap<>();
                }
                Optional<Comment> commentOptional = commentList.stream()
                        .filter(comment -> StringUtils.equals(taskId, comment.getTaskId()) && comment.getType().equals(AddProcessRecordCmd.COMMENT_TYPE))
                        .max(Comparator.comparing(Comment::getTime));

                if (commentOptional.isPresent()) {
                    try {
                        mapper.registerModule(new JavaTimeModule());
                        Comment commentEntity = commentOptional.get();
                        if (commentEntity.getFullMessage() == null) {
                            custom = (Map<String, Object>) historyService.createHistoricVariableInstanceQuery()
                                    .processInstanceId(taskInstance.getProcessInstanceId())
                                    .variableName(ProcessConstant.KEY_VARIABLE_CUSTOM)
                                    .singleResult()
                                    .getValue();
                            logger.info("Process instance custom: {}", custom);
                        } else {
                            custom = mapper.readValue(commentEntity.getFullMessage(), new TypeReference<Map<String, Object>>() {
                            });
                        }
                    } catch (JsonProcessingException e) {
                        throw new ProcessException("The data is not a valid json format!");
                    }
                } else {
                    //可能存在没有快照，故从历史变量中获取，做兜底处理
                    custom = (Map<String, Object>) historyService.createHistoricVariableInstanceQuery()
                            .processInstanceId(taskInstance.getProcessInstanceId())
                            .variableName(ProcessConstant.KEY_VARIABLE_CUSTOM)
                            .singleResult()
                            .getValue();
                    logger.info("Process instance custom: {}", custom);
                }
            }
        } else {
            custom = (Map<String, Object>) runtimeService.getVariable(taskInstance.getProcessInstanceId(), ProcessConstant.KEY_VARIABLE_CUSTOM);
            logger.info("Process instance custom: {}", custom);
        }
        logger.info("Process instance custom: {}", custom);
        checkFieldPermission(taskInstance, Arrays.asList(FIELD_PERMISSION_EDITABLE, FIELD_PERMISSION_READONLY), custom);
        logger.info("Process instance custom: {}", custom);
        return custom;
    }

    @Override
    public String getTaskFormDef(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        HistoricTaskInstance taskInstance= historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        Map<String, String> formDefinitionMap = null;
        if (taskInstance.getEndTime() == null) {
            VariableInstance variableInstance = runtimeService.getVariableInstance(taskInstance.getProcessInstanceId(), ProcessConstant.KEY_VARIABLE_FORM_DEFINITION);
            if (variableInstance != null && variableInstance.getValue() != null) {
                formDefinitionMap = (Map<String, String>) variableInstance.getValue();
            }
        } else {
            HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(taskInstance.getProcessInstanceId())
                    .variableName(ProcessConstant.KEY_VARIABLE_FORM_DEFINITION)
                    .singleResult();
            if (historicVariableInstance != null && historicVariableInstance.getValue() != null) {
                formDefinitionMap = (Map<String, String>) historicVariableInstance.getValue();
            }
        }
        if (formDefinitionMap != null) {
            String formDefinitionKey = taskInstance.getProcessDefinitionId().split(":")[0] + "." + taskInstance.getTaskDefinitionKey() + "." + SystemProcessUtil.getFrontendKey() + ".formDefinition";
            return formDefinitionMap.get(formDefinitionKey);
        } else {
            // 兼容老模式
            return systemProcessDefinitionService.getProcessDefinitionForm(taskInstance.getProcessDefinitionId(), taskInstance.getTaskDefinitionKey());
        }
    }

    @Override
    public List<String> getNodeProcessUsersByTask(String taskId) {
        // 1. 判断当前任务是否是会签节点任务
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        //区分或签、依次审批和会签
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(historicTaskInstance.getTaskDefinitionKey());
        UserTask userTask = (UserTask) flowElement;
        if (userTask.getLoopCharacteristics() == null) {
            //或签，注意：或签过程中可能会发生转派，加签
            //或签转派/转派：或签只会形成一个task，task candidateUsers会发生变化，所以转派和加签都是针对同一个task的candidateUsers处理
            //转派A->B：当前处理人A可以被B再次转派/加签回来A->B->A; 加签：当前处理人A不可以被B再次转派/加签
            List<HistoricIdentityLink> historicIdentityLinks = historyService.getHistoricIdentityLinksForTask(taskId);
            if (CollectionUtils.isEmpty(historicIdentityLinks)) {
                logger.error("taskId {} has no historic identity link record", taskId);
                return new ArrayList<>();
            }
            return historicIdentityLinks.stream().filter(item -> StringUtils.isNotBlank(item.getUserId()))
                    .filter(item -> IDENTITY_TYPE_CANDIDATE.equals(item.getType())).map(HistoricIdentityLink::getUserId).distinct().collect(Collectors.toList());
        } else if (!userTask.getLoopCharacteristics().isSequential()) {
            // 会签会导致同一个node一次性并行分裂出多个task，会签会与转派、加签正交。转派：转移task candidate user；加签：新添加一个execution task
            // 1. 获取当前任务执行
            Execution execution = runtimeService.createExecutionQuery().executionId(historicTaskInstance.getExecutionId()).processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();
            // 2. 获取当前任务执行的父执行下的所有子执行。注意：会签场景下，若其中一个执行执行完毕，db is_active=0
            List<Execution> childExecutions = runtimeService.createExecutionQuery().parentId(execution.getParentId()).processInstanceId(historicTaskInstance.getProcessInstanceId()).list();
            List<String> childExecutionIds = childExecutions.stream().map(it -> it.getId()).collect(Collectors.toList());
            // 3. 根据所有子执行获取所有的任务列表。
            List<HistoricTaskInstance> historicTaskInstances = new ArrayList<>();
            childExecutionIds.stream().forEach(item -> historicTaskInstances.add(historyService.createHistoricTaskInstanceQuery()
                    .executionId(item).processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult()));
            // 4. 根据任务列表获取所有的处理人，转派和加签会导致historicIdentityLink同一个task的多条记录
            //    初始状态下，会签在historicIdentityLink中 type=assignee，转派后新增type=candidate，加签后新增type=assignee
            List<HistoricIdentityLink> historicIdentityLinks = new ArrayList<>();
            for (int i = 0; i < historicTaskInstances.size(); i++) {
                HistoricTaskInstance taskInstance = historicTaskInstances.get(i);
                List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(taskInstance.getId());
                if (CollectionUtils.isEmpty(identityLinks)) {
                    logger.error("taskId {} has no identity links", taskInstance.getId());
                    continue;
                } else if (identityLinks.size() > 1) {
                    // 会签初始状态为 type=assignee，转派后新增type=candidate，所以此时task会有两条记录，需过滤掉type=assignee
                    identityLinks = identityLinks.stream().filter(item -> StringUtils.isNotBlank(item.getUserId()))
                            .filter(item -> IDENTITY_TYPE_CANDIDATE.equals(item.getType())).collect(Collectors.toList());
                }
                historicIdentityLinks.addAll(identityLinks);
            }
            return historicIdentityLinks.stream().filter(item -> StringUtils.isNotBlank(item.getUserId()))
                    .map(HistoricIdentityLink::getUserId).distinct().collect(Collectors.toList());
        } else {
            // 依次审批，执行任务会依次产生。注意：依次审批产生的任务可能会发生转派，加签，转派/加签都是处理的candidate user
            // 1. 依次审批场景下，所有依次产生的任务会共用同一个执行。获取该执行下的所有任务实例
            Execution execution = runtimeService.createExecutionQuery().executionId(historicTaskInstance.getExecutionId()).processInstanceId(historicTaskInstance.getProcessInstanceId()).singleResult();
            List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery()
                    .executionId(execution.getId()).processInstanceId(historicTaskInstance.getProcessInstanceId()).list();
            // 2. 会签依次审批场景下，当前产生任务A->B，当前处理人A可以被B再次转派/加签回来A->B->A，加签：当前处理人A不可以被B再次转派/加签
            // 2.1. A->B，一旦发生转派，则A不算当前节点处理人，需要隔离
            // 2.2. 会签初始任务产生时，identityLink type=assignee记录，转派/加签后，该记录依然存在，会新增 type=candidate记录，依据此满足2.1
            List<String> processUsers = new ArrayList<>();
            for (int i = 0; i < historicTaskInstances.size(); i++) {
                HistoricTaskInstance taskInstance = historicTaskInstances.get(i);
                List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(taskInstance.getId());
                if (CollectionUtils.isEmpty(identityLinks)) {
                    logger.error("taskId {} has no identity links", taskInstance.getId());
                    continue;
                } else if (identityLinks.size() > 1) {
                    // 会签初始状态为 type=assignee，转派后新增type=candidate，所以此时task会有两条记录，需过滤掉type=assignee
                    Map<String, List<HistoricIdentityLink>> usersIdentityLink = identityLinks.stream().filter(item -> StringUtils.isNotBlank(item.getUserId()))
                            .collect(Collectors.groupingBy(HistoricIdentityLink::getType));
                    List<String> candidates = usersIdentityLink.get(IDENTITY_TYPE_CANDIDATE).stream().map(HistoricIdentityLink::getUserId).collect(Collectors.toList());
                    processUsers.addAll(candidates);
                } else {
                    processUsers.add(identityLinks.get(0).getUserId());
                }
            }
            // 3. 获取流程节点原始定义的流程处理人
            String currentUserTaskNodeKey = historicTaskInstance.getTaskDefinitionKey();
            List<String> candidates = processTaskAssigneeService.getAssigneeListVariableValue(historicTaskInstance.getProcessInstanceId(), currentUserTaskNodeKey);
            // 获取当前依次审批节点的loopCounter，A->B->C，若A审批完成，B待审批，loopCounter+1，A不应该被转派。若A转派给D，则审批链路变为D->B->C，D可以再次转派给A
            List<String> excludeUsers = new ArrayList<>();
            Integer loopCounter = (Integer) runtimeService.getVariableLocal(execution.getId(), COUNTER_SIGN_LOOP_COUNTER);
            for(int i = 0; i < loopCounter; i++) {
                if(!processUsers.contains(candidates.get(i))) {
                    excludeUsers.add(candidates.get(i));
                }
            }
            candidates.addAll(processUsers);
            return candidates.stream().filter(item -> !excludeUsers.contains(item)).distinct().collect(Collectors.toList());
        }
    }

    @Override
    public List<String> getCandidateUsers(String taskId) {
        List<String> candidateUsers = new ArrayList<>();
        List<HistoricIdentityLink> identityLinks = historyService.getHistoricIdentityLinksForTask(taskId);
        if (CollectionUtils.isEmpty(identityLinks)) {
            return candidateUsers;
        }
        identityLinks = identityLinks.stream().filter(item -> StringUtils.isNotBlank(item.getUserId()))
                .filter(item -> IDENTITY_TYPE_CANDIDATE.equals(item.getType()) || IDENTITY_TYPE_ASSIGNEE.equals(item.getType())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(identityLinks)) {
            return candidateUsers;
        } else if (identityLinks.size() == 1) {
            candidateUsers.add(identityLinks.get(0).getUserId());
            return candidateUsers;
        }
        candidateUsers.addAll(identityLinks.stream().filter(item -> IDENTITY_TYPE_CANDIDATE.equals(item.getType()))
                .map(HistoricIdentityLink::getUserId).distinct().collect(Collectors.toList()));
        return candidateUsers;
    }

    @Override
    public void revertTask(String taskId, String targetTaskDefinitionName) {
        if (StringUtils.isBlank(taskId)) {
            throw new ProcessException("The taskId cannot be empty!");
        }
        if (StringUtils.isBlank(targetTaskDefinitionName)) {
            throw new ProcessException("The targetTaskDefinitionName cannot be empty!");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo != null) {
            Authentication.setAuthenticatedUserId(userInfo.getUserName());
        }
        HistoricTaskInstance taskInstance= historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance.getCategory() != null && !taskInstance.getCategory().equals("ApprovalTask")) {
            throw new ProcessException("Task does not support revert!");
        }
        if (taskInstance == null) {
            throw new ProcessException("Task does not exist!");
        }
        if (taskInstance.getEndTime() != null) {
            throw new ProcessException("Task has ended!");
        }
        checkOperationPermission(taskInstance, "revert", null);
        List<String> candidateUsers = historyService.getHistoricIdentityLinksForTask(taskId)
                .stream().filter(historicIdentityLink -> historicIdentityLink.getUserId() != null)
                .map(historicIdentityLink -> historicIdentityLink.getUserId())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(candidateUsers) && StringUtils.isEmpty(taskInstance.getAssignee())) {
            logger.error("Task has no candidate users!");
            throw new ProcessException("Task has no candidate users!");
        }
        if (!candidateUsers.contains(userInfo.getUserName()) && !taskInstance.getAssignee().equals(userInfo.getUserName())) {
            logger.error("You are not a candidate for the task!");
            throw new ProcessException("You are not a candidate for the task!");
        }

        taskService.rejectTask(taskId, targetTaskDefinitionName, null);
//        taskService.setAssignee(taskId, userInfo.getUserName());
        Authentication.setAuthenticatedUserId(null);
        logger.info("The task {} is reverted by {}", taskId, userInfo.getUserName());
    }

    @Override
    public Pair<HistoricTaskInstance, List<HistoricTaskInstance>> getCanWithdrawTask(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            return Pair.of(null, null);
        }
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            logger.error("The current task does not exist!");
            return Pair.of(null, null);
        }
        if (historicTaskInstance.getEndTime() == null) {
            logger.info("The current task has not been completed, cannot withdraw!");
            return Pair.of(null, null);
        }
        ProcInst procInst = systemProcessInstanceService.getProcessInstance(historicTaskInstance.getProcessInstanceId());
        if (procInst == null) {
            logger.error("The process instance does not exist!");
            return Pair.of(null, null);
        }
        if (procInst.getEndTime() != null) {
            logger.info("The process instance has ended, cannot withdraw!");
            return Pair.of(null, null);
        }
        if (StringUtils.isBlank(historicTaskInstance.getCategory()) || !historicTaskInstance.getCategory().equals("InitiateTask")) {
            logger.info("The task category is {}, don't support withdraw", historicTaskInstance.getCategory());
            return Pair.of(null, null);
        }
        checkOperationPermission(historicTaskInstance, "withdraw", null);
        //查找下一节点的taskId
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());
        FlowElement targetFlowElement = bpmnModel.getMainProcess().getFlowElement(historicTaskInstance.getTaskDefinitionKey());
        if (targetFlowElement == null) {
            logger.error("task definitionKey {}, FlowElement is null", historicTaskInstance.getTaskDefinitionKey());
            return Pair.of(null, null);
        }
        FlowNode targetFlowNode = (FlowNode) targetFlowElement;
        List<SequenceFlow> outgoingFlows = targetFlowNode.getOutgoingFlows();
        // 获取initiate task出口序列流只会有一条
        if (outgoingFlows == null || outgoingFlows.size() > 1) {
            logger.error("initiate task has no or multi outgoing flow");
            return Pair.of(null, null);
        }
        SequenceFlow sequenceFlow = outgoingFlows.get(0);
        // 获取initiate task出口序列流指向的下一节点
        FlowElement nextNode = bpmnModel.getFlowElement(sequenceFlow.getTargetRef());
        // 如果下个节点不是ApprovalTask节点，则报错
        if (!(nextNode instanceof UserTask && (((UserTask) nextNode).getCategory().equals("ApprovalTask") || ((UserTask) nextNode).getCategory().equals("SubmitTask")))) {
            logger.info("the next node is not ApprovalTask or SubmitTask, can't withdraw");
            return Pair.of(null, null);
        }
        // 对UserTask节点进行判断处理，同意、拒绝、转派、加签、回退动作发生时，不允许撤回；会签只要有一个任务被处理，就算动作发生
        List<Comment> commentList = systemProcessRecordService.getProcessInstanceRecord(historicTaskInstance.getProcessInstanceId());
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(historicTaskInstance.getProcessInstanceId())
                .taskDefinitionKey(nextNode.getId())
                .list();
        if (CollectionUtils.isEmpty(historicTaskInstanceList)) {
            logger.error("The next node task does not exist, cannot withdraw!");
            return Pair.of(null, null);
        }
        List<HistoricTaskInstance> historicTaskInstancesCanWithdraw = new ArrayList<>();
        for (HistoricTaskInstance historicTask : historicTaskInstanceList) {
            // 发起1->审批(未审批)—>撤回->发起2(提交)->审批(未审批)->撤回，此时我的发起应该只能有1条数据，我的已办有2条，同时只能撤回发起2的发起，
            // 如果传入了发起1的taskId，则该taskId的下一个节点task的endTime < 发起2及以后节点任务的endTime
            if (historicTask.getEndTime() != null && historicTask.getEndTime().compareTo(historicTaskInstance.getEndTime()) < 0) {
                continue;
            } else if (historicTask.getEndTime() != null) {
                logger.info("the next node task has been operated, can't withdraw");
                historicTaskInstancesCanWithdraw.clear();
                break;
            }
            // 举例：转派场景下，taskId不变，会更换人，但不允许撤回，这个时候就需要用到comment
            // 举例：加签场景下呢？
            if (commentList.stream()
                    .anyMatch(comment -> StringUtils.equals(comment.getTaskId(), historicTask.getId())
                            && comment.getType().equals(AddProcessRecordCmd.COMMENT_TYPE))) {
                logger.error("The task has been operated, cannot withdraw!");
                historicTaskInstancesCanWithdraw.clear();
                break;
            }
            historicTaskInstancesCanWithdraw.add(historicTask);
        }
        if(CollectionUtils.isEmpty(historicTaskInstancesCanWithdraw)) {
            return Pair.of(null, null);
        }
        if (StringUtils.isEmpty(historicTaskInstance.getAssignee()) || !historicTaskInstance.getAssignee().equals(UserContext.getCurrentUserInfo().getUserName())) {
            logger.error("Task has no complete user or current login user is not complete user");
            return Pair.of(null, null);
        }
        return Pair.of(historicTaskInstance, historicTaskInstancesCanWithdraw);
    }

    @Override
    public void withdrawTask(String taskId) {
        Pair<HistoricTaskInstance, List<HistoricTaskInstance>> canWithdrawTaskPair = getCanWithdrawTask(taskId);
        if (canWithdrawTaskPair == null || canWithdrawTaskPair.getKey() == null || canWithdrawTaskPair.getValue() == null) {
            logger.error("withdraw task {} error", taskId);
            throw new ProcessException("withdraw task error");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo != null) {
            Authentication.setAuthenticatedUserId(userInfo.getUserName());
        }
        // 会签节点会生成多个任务，只调用其中一个任务的撤回会把整个节点产生的所有任务给消除么？测试下来是会的，会把整个父执行给结束掉
        taskService.rejectTask(canWithdrawTaskPair.getValue().get(0).getId(), canWithdrawTaskPair.getKey().getTaskDefinitionKey(), null);
        Authentication.setAuthenticatedUserId(null);
        logger.info("The task {} is withdrawn by {}", taskId, UserContext.getCurrentUserInfo().getUserName());
    }

    @Override
    public List<LCAPUser> getTransferTargetUserList(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            logger.error("The taskId cannot be empty!");
            throw new ProcessException("The taskId cannot be empty!");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo == null) {
            logger.error("there is no login user");
            return new ArrayList<>();
        }
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (taskInstance == null) {
            logger.error("Task does not exist!");
            throw new ProcessException("Task does not exist!");
        }
        if (taskInstance.getEndTime() != null) {
            logger.error("Task {} has ended for get users for reassign", taskId);
            return new ArrayList<>();
        }
        try {
            checkOperationPermission(taskInstance, "reassign", null);
        } catch (ProcessException e) {
            logger.error("The user {} does not have permission to reassign the task {}", userInfo.getUserName(), taskId);
            return new ArrayList<>();
        }
        if(!getCandidateUsers(taskId).contains(userInfo.getUserName())) {
            logger.info("taskId {} candidate users don't contain current login user", taskId);
            return new ArrayList<>();
        }
        List<String> nodeProcessUsers = getNodeProcessUsersByTask(taskId);
        return getUserList().stream().filter(user -> !nodeProcessUsers.contains(user.getUserName())).collect(Collectors.toList());
    }

    @Override
    public List<LCAPUser> getUserList() {
        UserListReqDTO userListReqDTO = new UserListReqDTO();
        userListReqDTO.setLimit(Integer.MAX_VALUE);
        userListReqDTO.setOffset(0);
        List<UserListResDTO> userListResDTOList = userInfoService.getUserListFromAppOrNuims(userListReqDTO);
        List<LCAPUser> result = new ArrayList<>();
        userListResDTOList.forEach(userListResDTO -> {
            LCAPUser lcapUser = new LCAPUser();
            lcapUser.setUserName(userListResDTO.getUserName());
            result.add(lcapUser);
        });
        return result;
    }

    @Override
    public List<UserTask> getRejectableTaskList(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            throw new ProcessException("The taskId cannot be empty!");
        }
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().
                processInstanceId(historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult().getProcessInstanceId())
                .singleResult();
        if (processInstance == null) {
            throw new ProcessException("Process does not exist!");
        }
        return taskService.getRejectableTaskDefinitionList(taskId).stream()
                .filter(UserTask.class::isInstance)
                .map(UserTask.class::cast)
                .collect(Collectors.toList());
    }

    @Override
    public void addSignTask(String taskId, String userForAddSign) {
        if (StringUtils.isBlank(taskId)) {
            throw new ProcessException("The taskId cannot be empty!");
        }
        if (StringUtils.isBlank(userForAddSign)) {
            throw new ProcessException("The userForAddSign cannot be empty!");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo == null) {
            logger.error("there is no current login user");
            throw new ProcessException("there is no current login user");
        }
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            throw new ProcessException("Task does not exist!");
        }
        if (historicTaskInstance.getEndTime() != null) {
            throw new ProcessException("Task has ended!");
        }
        List<String> candidateUsers = getCandidateUsers(taskId);
        if(CollectionUtils.isEmpty(candidateUsers) || !candidateUsers.contains(userInfo.getUserName())) {
            logger.error("current login user {} is not task {} candidate user", userInfo.getUserName(), taskId);
            throw new ProcessException("you are not task candidate user, can't addSign");
        }
        checkOperationPermission(historicTaskInstance, "addSign", null);
        //区分或签、依次审批和会签
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(historicTaskInstance.getTaskDefinitionKey());
        if (historicTaskInstance.getCategory() != null && historicTaskInstance.getCategory().equals("ApprovalTask")) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                Authentication.setAuthenticatedUserId(userInfo.getUserName());
                if (userTask.getLoopCharacteristics() == null) {
                    //或签
                    taskService.addCandidateUser(taskId, userForAddSign);
                } else {
                    if (userTask.getLoopCharacteristics().isSequential()) {
//                        taskService.addCandidateUser(taskId, historicTaskInstance.getAssignee());
//                        taskService.setAssignee(taskId, null);
                        taskService.addCandidateUser(taskId, userForAddSign);
                    } else {
                        //会签
                        runtimeService.addMultiInstanceExecution(historicTaskInstance.getTaskDefinitionKey(), historicTaskInstance.getProcessInstanceId(), Collections.singletonMap("assignee", userForAddSign));
                    }
                }
                Authentication.setAuthenticatedUserId(null);
            }
        } else {
            throw new ProcessException("Task does not support addSign!");
        }

        Object processSystemVariableObject = runtimeService.getVariable(historicTaskInstance.getProcessInstanceId(), "processSystem");
        if (processSystemVariableObject instanceof Map) {
            Map<String, Object> processSystemVariables = (Map<String, Object>) processSystemVariableObject;
            Map<String, Object> nodeVar = (Map<String, Object>) processSystemVariables.getOrDefault(historicTaskInstance.getTaskDefinitionKey(), new HashMap<>());

            List<String> nodeParticipants = (List<String>) nodeVar.get("nodeParticipants");
            // 将userForAddSign加入到nodeParticipants即可

            nodeParticipants.add(userForAddSign);
            nodeVar.put("nodeParticipants", nodeParticipants);

            processSystemVariables.put(historicTaskInstance.getTaskDefinitionKey(), nodeVar);
            taskService.setVariable(taskId, "processSystem", processSystemVariables);
        }
    }

    @Override
    public void addSignTask(String taskId, String userForAddSign, String policyForAddSign) {
        if (StringUtils.isBlank(taskId)) {
            throw new ProcessException("The taskId cannot be empty!");
        }
        if (StringUtils.isBlank(userForAddSign)) {
            throw new ProcessException("The userForAddSign cannot be empty!");
        }
        if (StringUtils.isBlank(policyForAddSign)) {
            policyForAddSign = AddSignEnum.CURRENT.getValue();
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo == null) {
            logger.error("there is no current login user");
            throw new ProcessException("there is no current login user");
        }
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            throw new ProcessException("Task does not exist!");
        }
        if (historicTaskInstance.getEndTime() != null) {
            throw new ProcessException("Task has ended!");
        }
        List<String> candidateUsers = getCandidateUsers(taskId);
        if(CollectionUtils.isEmpty(candidateUsers) || !candidateUsers.contains(userInfo.getUserName())) {
            logger.error("current login user {} is not task {} candidate user", userInfo.getUserName(), taskId);
            throw new ProcessException("you are not task candidate user, can't addSign");
        }
        Object processSystemVariableObject = runtimeService.getVariable(historicTaskInstance.getProcessInstanceId(), "processSystem");
        if (processSystemVariableObject instanceof Map) {
            Map<String, Object> processSystemVariables = (Map<String, Object>) processSystemVariableObject;
            Map<String, Object> nodeVar = (Map<String, Object>) processSystemVariables.getOrDefault(historicTaskInstance.getTaskDefinitionKey(), new HashMap<>());

            List<String> nodeParticipants = (List<String>) nodeVar.get("nodeParticipants");
            // 将userForAddSign加入到nodeParticipants即可

            nodeParticipants.add(userForAddSign);
            nodeVar.put("nodeParticipants", nodeParticipants);

            processSystemVariables.put(historicTaskInstance.getTaskDefinitionKey(), nodeVar);
            taskService.setVariable(taskId, "processSystem", processSystemVariables);
        }
        //checkOperationPermission(historicTaskInstance, "addSign", null);
        //区分或签、依次审批和会签
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());
        FlowElement flowElement = bpmnModel.getMainProcess().getFlowElement(historicTaskInstance.getTaskDefinitionKey());
        if (historicTaskInstance.getCategory() != null && historicTaskInstance.getCategory().equals("ApprovalTask")) {
            if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask) flowElement;
                Authentication.setAuthenticatedUserId(userInfo.getUserName());
                if (userTask.getLoopCharacteristics() == null) {
                    if (policyForAddSign.equals(AddSignEnum.PREVIOUS.getValue()) || policyForAddSign.equals(AddSignEnum.SUBSEQUENT.getValue())){
                        // 采用任务方式
                        TaskEntityImpl taskEntity = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
                        //1.把当前的节点设置为空
                        if (taskEntity != null) {
                            //如果是加签再加签
                            String parentTaskId = taskEntity.getParentTaskId();
                            if (StringUtils.isBlank(parentTaskId)) {
                                taskEntity.setOwner(Authentication.getAuthenticatedUserId());
                                taskEntity.setAssignee(null);
                                taskEntity.setCountEnabled(true);
                                if (policyForAddSign.equals(AddSignEnum.PREVIOUS.getValue())) {
                                    taskEntity.setScopeType(AddSignEnum.PREVIOUS_ADD_SIGN_FROM.getValue());
                                } else if (policyForAddSign.equals(AddSignEnum.SUBSEQUENT.getValue())){
                                    taskEntity.setScopeType(AddSignEnum.SUBSEQUENT_ADD_SIGN_FROM.getValue());
                                }
                                //1.2 设置任务为空执行者
                                taskService.saveTask(taskEntity);
                                if (policyForAddSign.equals(AddSignEnum.SUBSEQUENT.getValue())){
                                    runtimeService.setVariableLocal(taskEntity.getExecutionId(),"executionScopeType",taskEntity.getScopeType());
                                    taskService.setAssignee(taskId,Authentication.getAuthenticatedUserId());
                                    taskService.complete(taskId);
                                }
                            }
                            //2.添加加签数据
                            createSignSubTasks(userForAddSign, taskEntity,policyForAddSign);
                        }
                    }else{
                        taskService.addCandidateUser(taskId, userForAddSign);
                    }

                } else {
                    if (userTask.getLoopCharacteristics().isSequential()) {
                        taskService.addCandidateUser(taskId, userForAddSign);
                    } else {
                        //会签
                        runtimeService.addMultiInstanceExecution(historicTaskInstance.getTaskDefinitionKey(), historicTaskInstance.getProcessInstanceId(), Collections.singletonMap("assignee", userForAddSign));

                    }
                }
                Authentication.setAuthenticatedUserId(null);
            }
        } else {
            throw new ProcessException("Task does not support addSign!");
        }
    }
    /**
     * 创建加签子任务
     *
     * @param userForAddSign     加签参数
     * @param taskEntity 父任务
     */
    private void createSignSubTasks(String userForAddSign, TaskEntity taskEntity,String policyForAddSign) {
        if (StringUtils.isNotEmpty(userForAddSign)) {
            String parentTaskId = taskEntity.getParentTaskId();
            if (StringUtils.isBlank(parentTaskId)) {
                parentTaskId = taskEntity.getId();
            }
            String finalParentTaskId = parentTaskId;
            //1.创建被加签人的任务列表
            TaskEntity task  = createSubTask(taskEntity, finalParentTaskId, userForAddSign);
            if (policyForAddSign.equals(AddSignEnum.PREVIOUS.getValue())) {
                //如果是候选人，需要删除运行时候选表种的数据。
                List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskEntity.getId());
                if (!identityLinks.isEmpty()){
                    for (IdentityLink identityLink : identityLinks){
                        taskService.deleteCandidateUser(taskEntity.getId(),identityLink.getUserId());
                    }
                }
            }

        }
    }
    private TaskEntity createSubTask(TaskEntity taskEntity, String parentTaskId, String userForAddSign) {
        TaskEntity task = (TaskEntity)taskService.newTask();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(taskEntity.getProcessDefinitionId());
        DynamicUserTaskBuilder dynamicUserTaskBuilder = new DynamicUserTaskBuilder();

        ExecutionEntity execution = managementService.executeCommand(new AddProcessTaskCmd(taskEntity, userForAddSign,dynamicUserTaskBuilder,bpmnModel));
        //execution.setParentId(runtimeService.createExecutionQuery().executionId(taskEntity.getExecutionId()).singleResult().getId());
        task = execution.getTasks().get(0);
        task.setExecutionId(execution.getId());
        if (taskEntity != null) {
            //1.生成子任务
            //execution.setCurrentFlowElement(currentElement);
            //task.setId(UUID.randomUUID().toString());
            task.setCategory(taskEntity.getCategory());
            task.setDescription(taskEntity.getDescription());
            task.setTenantId(taskEntity.getTenantId());
            task.setAssignee(null);
            if (taskEntity.getScopeType().equals(AddSignEnum.PREVIOUS_ADD_SIGN_FROM.getValue())){
                task.setName(taskEntity.getName()+"(前加签)");
                task.setScopeType(AddSignEnum.PREVIOUS.getValue());
                runtimeService.setVariableLocal(execution.getId(),"executionScopeType",AddSignEnum.PREVIOUS.getValue());
            } else if (taskEntity.getScopeType().equals(AddSignEnum.SUBSEQUENT_ADD_SIGN_FROM.getValue())) {
                task.setName(taskEntity.getName()+"(后加签)");
                task.setScopeType(AddSignEnum.SUBSEQUENT.getValue());
                runtimeService.setVariableLocal(execution.getId(),"executionScopeType",AddSignEnum.SUBSEQUENT.getValue());
            }else {
                task.setName(taskEntity.getName()+"(当前加签)");
            }
            task.setParentTaskId(parentTaskId);
            task.setProcessDefinitionId(taskEntity.getProcessDefinitionId());
            task.setProcessInstanceId(taskEntity.getProcessInstanceId());
            task.setTaskDefinitionKey(taskEntity.getTaskDefinitionKey());
            task.setTaskDefinitionId(taskEntity.getTaskDefinitionId());
            task.setPriority(taskEntity.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
            List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(task.getId());
            if (!identityLinks.isEmpty()){
                for (IdentityLink identityLink : identityLinks){
                    taskService.deleteCandidateUser(task.getId(),identityLink.getUserId());
                }
            }
            taskService.addCandidateUser(task.getId(), userForAddSign);
        }
        return task;
    }

    @Override
    public void viewCCTask(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            throw new ProcessException("The taskId cannot be empty!");
        }
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        if (userInfo == null) {
            logger.error("there is no current login user");
            throw new ProcessException("there is no current login user");
        }
        Authentication.setAuthenticatedUserId(userInfo.getUserName());

        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        if (historicTaskInstance == null) {
            throw new ProcessException("Task does not exist!");
        }
        if (!ProcessConstant.TASK_CATEGORY_CC_TASK.equalsIgnoreCase(historicTaskInstance.getCategory())) {
            throw new ProcessException("This task is not a CC task and does not support viewing!");
        }
        List<String> candidateUsers = getCandidateUsers(taskId);
        if(CollectionUtils.isEmpty(candidateUsers) || !candidateUsers.contains(userInfo.getUserName())) {
            logger.error("current login user {} is not task {} candidate user", userInfo.getUserName(), taskId);
            throw new ProcessException("you are not task candidate user, can't view");
        }

        managementService.executeCommand(new ViewCCTaskCmd(taskId));
        Authentication.setAuthenticatedUserId(null);
        logger.info("The task {} is viewed by {}", taskId, userInfo.getUserName());
    }

    private TaskInst toLCAPTaskInstance(HistoricTaskInstance historicTaskInstance) {
        if (ObjectUtils.isEmpty(historicTaskInstance)) return null;
        BpmnModel bpmnModel = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());
        String elementTitle = bpmnModel.getMainProcess().getFlowElement(historicTaskInstance.getTaskDefinitionKey())
                .getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_ELEMENT_TITLE);
        String elementDescription = bpmnModel.getMainProcess().getFlowElement(historicTaskInstance.getTaskDefinitionKey())
                .getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_ELEMENT_DESCRIPTION);
        TaskInst taskInstance = new TaskInst();
        FlowElement flowElement = bpmnModel.getFlowElement(historicTaskInstance.getTaskDefinitionKey());
        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;
            if (userTask.getLoopCharacteristics() == null) {
                //或签
                taskInstance.setApprovalPolicy("simple");
            } else {
                if (userTask.getLoopCharacteristics().isSequential()) {
                    taskInstance.setApprovalPolicy("sequence");
                } else {
                    taskInstance.setApprovalPolicy("parallel");
                }
            }
        }
        taskInstance.setTaskId(historicTaskInstance.getId());
        taskInstance.setTitle(elementTitle);
        taskInstance.setDescription(elementDescription);
        taskInstance.setTaskTitle(historicTaskInstance.getName());
        taskInstance.setTaskDescription(historicTaskInstance.getDescription());
        taskInstance.setCategory(historicTaskInstance.getCategory());
        taskInstance.setFinished(historicTaskInstance.getEndTime() != null);
        taskInstance.setCompleteBy(historicTaskInstance.getAssignee());
        taskInstance.setCreateTime(SystemProcessUtil.DateToZonedDateTime(historicTaskInstance.getCreateTime()));
        taskInstance.setCompleteTime(SystemProcessUtil.DateToZonedDateTime(historicTaskInstance.getEndTime()));
        taskInstance.setTaskDefName(historicTaskInstance.getTaskDefinitionKey());
        taskInstance.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
        taskInstance.setCandidateUsers(getCandidateUsers(historicTaskInstance.getId()));
        String processDefinitionId = historicTaskInstance.getProcessDefinitionId();
        taskInstance.setProcessDefinitionId(processDefinitionId);
        taskInstance.setProcessDefUniqueKey(processDefinitionId.split(":")[0]);
        taskInstance.setProcessDefName(bpmnModel.getMainProcess().getName());
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(taskInstance.getProcessInstanceId()).singleResult();
        taskInstance.setProcInstStatus(processInstance.getEndTime() != null ? ProcInstStatusEnum.Approved : ProcInstStatusEnum.Approving);
        return taskInstance;
    }

    public void checkOperationPermission(HistoricTaskInstance taskInstance, String operation, String comment) {
        if (ProcessConstant.TASK_CATEGORY_CC_TASK.equalsIgnoreCase(taskInstance.getCategory())) {
            logger.error("The task {} is a CC task, does not support operation!", taskInstance.getId());
            throw new ProcessException("The task is a CC task, does not support operation!");
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(taskInstance.getProcessDefinitionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        String operatePermission = bpmnModel.getMainProcess().getFlowElement(taskInstance.getTaskDefinitionKey()).getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_OPERATION_PERMISSION);
        if (StringUtils.isEmpty(operatePermission)) {
            logger.error("The operatePermission of " + operation + "TaskInstance does not allowed!");
            throw new ProcessException("The operatePermission of " + operation + "TaskInstance does not allowed!");
        }
        List<TaskOperationPermission> operationPermissionList;
        try {
            mapper.registerModule(new JavaTimeModule());
            operationPermissionList = mapper.readValue(operatePermission, new TypeReference<List<TaskOperationPermission>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ProcessException("operatePermission is invalid!");
        }
        if (CollectionUtils.isEmpty(operationPermissionList)) {
            logger.error("The operatePermission of " + operation + "TaskInstance does not allowed!");
            throw new ProcessException("The operatePermission of " + operation + "TaskInstance does not allowed!");
        }
        TaskOperationPermission lcapTaskDefOperationPermission = operationPermissionList.stream()
                .filter(permission -> StringUtils.equals(permission.getName(), operation) && permission.getOperationEnabled())
                .findAny().orElseThrow(() -> new ProcessException("The operatePermission of " + operation + "TaskInstance does not allowed!"));
        if (lcapTaskDefOperationPermission.getCommentRequired() && StringUtils.isEmpty(comment)) {
            throw new ProcessException("The comment of " + operation + "TaskInstance is required!");
        }
    }

    public Map<String, Object> checkFieldPermission(HistoricTaskInstance taskInstance, List<String> permissions, Map<String, Object> data) {
        if (MapUtils.isEmpty(data)) return new HashMap<>();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(taskInstance.getProcessDefinitionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        String fieldPermission = bpmnModel.getMainProcess().getFlowElement(taskInstance.getTaskDefinitionKey()).getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_FIELD_PERMISSION);
        if (StringUtils.isEmpty(fieldPermission)) data.clear();
        // 新的
        if (fieldPermission.contains("subFieldPermissions")){
            mapper.registerModule(new JavaTimeModule());
            List<FormDataPropertyPermission> fieldPermissionList = null;
            try {
                fieldPermissionList = mapper.readValue(fieldPermission, new TypeReference<List<FormDataPropertyPermission>>() {
                });
                if (fieldPermissionList.isEmpty()) data.clear();

                Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, Object> entry = iterator.next();
                    String key = entry.getKey();
                    for (FormDataPropertyPermission permission:fieldPermissionList){
                        if (key.equals(permission.getPropertyName())){
                            if (!permissions.contains(permission.getPermission())){
                                iterator.remove();
                                break;
                            }
                            // 主表单
                            if (key.equals("data")){
                                Map<String,Object> map = (Map<String,Object>)entry.getValue();
                                Iterator<Map.Entry<String, Object>> subIterator = map.entrySet().iterator();
                                while (subIterator.hasNext()) {
                                    Map.Entry<String, Object> subEntry = subIterator.next();
                                    String subKey = subEntry.getKey();
                                    if (permission.getSubFieldPermissions().stream()
                                            .noneMatch(lcapTaskDefFieldPermission ->
                                                    StringUtils.equals(lcapTaskDefFieldPermission.getPropertyName(), subKey)
                                                            && permissions.contains(lcapTaskDefFieldPermission.getPermission()))) {
                                        subIterator.remove();
                                    }
                                }
                            }else{
                                // 子表单
                                if (entry.getValue() != null){
                                    List<Map<String,Object>> list = (List<Map<String,Object>>)entry.getValue();
                                    for (Map<String,Object> map:list){
                                        Iterator<Map.Entry<String, Object>> subIterator = map.entrySet().iterator();
                                        while (subIterator.hasNext()) {
                                            Map.Entry<String, Object> subEntry = subIterator.next();
                                            String subKey = subEntry.getKey();
                                            if (permission.getSubFieldPermissions().stream()
                                                    .noneMatch(lcapTaskDefFieldPermission ->
                                                            StringUtils.equals(lcapTaskDefFieldPermission.getPropertyName(), subKey)
                                                                    && permissions.contains(lcapTaskDefFieldPermission.getPermission()))) {
                                                subIterator.remove();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }


        }else{
            // 旧的
            try {
                mapper.registerModule(new JavaTimeModule());
                List<DataPropertyPermission> fieldPermissionList = mapper.readValue(fieldPermission, new TypeReference<List<DataPropertyPermission>>() {
                });
                if (fieldPermissionList.isEmpty()) data.clear();
                Map<String,Object> mainFormData = (Map<String,Object>)data.get("data");
                Iterator<Map.Entry<String, Object>> iterator = mainFormData.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, Object> entry = iterator.next();
                    String key = entry.getKey();

                    if (fieldPermissionList.stream()
                            .noneMatch(lcapTaskDefFieldPermission ->
                                    StringUtils.equals(lcapTaskDefFieldPermission.getPropertyName(), key)
                                            && permissions.contains(lcapTaskDefFieldPermission.getPermission()))) {
                        iterator.remove();
                    }
                }
                data.put("data",mainFormData);
            } catch (JsonProcessingException e) {
                data.clear();
                logger.error("The fieldPermission is invalid!", e);
            }
        }

        return data;
    }

    private Map<String, Object> correctProcessCustomData(Map<String, Object> customDataMap) {
        if(customDataMap == null || customDataMap.isEmpty()) {
            return new HashMap<>();
        }
        for(Map.Entry<String, Object> entry : customDataMap.entrySet()) {
            if(entry.getValue() != null) {
                return customDataMap;
            }
        }
        return new HashMap<>();
    }

    @Override
    public List<CustomTask> getCustomTaskList(CustomTaskQuery query) {
        return customTaskMapper.selectCustomTaskList(query);
    }

    @Override
    public Long count(CustomTaskQuery query) {
        return customTaskMapper.count(query);
    }

    @Override
    public List<CustomTask> selectCustomRunTaskList(CustomTaskQuery query) {
        return customTaskMapper.selectCustomRunTaskList(query);
    }

    @Override
    public Long countRun(CustomTaskQuery query) {
        return customTaskMapper.countRun(query);
    }

    @Override
    public List<CustomTask> getCCTaskList(CCTaskQuery query) {
        return customTaskMapper.selectCCTaskList(query);
    }

    @Override
    public Long countCCTask(CCTaskQuery query) {
        return customTaskMapper.countCCTask(query);
    }

    private void editFormData(Map<String, Object> variableData,Map<String, Object> data,String procDefKey){

        try {
            if (data != null){
                Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry<String, Object> entry = iterator.next();
                    String key = entry.getKey();
                    if (entry.getValue() == null){
                        continue;
                    }
                    if (key.equals("data")){
                        // 1、如果是主表单，遍历流程内部的所有key，如果表单中存在此key，则替换value
                        Map<String,Object> mainData = (Map<String,Object>)variableData.get("data");
                        mainData.putAll((Map<String,Object>)data.get("data"));
                    }else{
                        // 2、如果是子表单，遍历流程内部子表单的每一条数据，如果表单中存在此条数据的id，如果表单中存在key，替换value
                        List<Map<String,Object>> oldList = (List<Map<String,Object>>)variableData.get(key);
                        List<Map<String,Object>> newList = (List<Map<String,Object>>)entry.getValue();
                        if (oldList == null){
                            oldList = new ArrayList<>();
                        }
                        String idGenerateMode = environment.getProperty("process."+procDefKey+".form."+key+".idGenerateMode");
                        String idType = environment.getProperty("process."+procDefKey+".form."+key+".idType");
                        String entityName = environment.getProperty("process."+procDefKey+".form."+key+".entityName");
                        // import LCAPPerResMappingService
                        for (Map<String,Object> newItem:newList){
                            if (newItem.get("id") == null){
                                if (idGenerateMode.equals(AUTOINCREMENT)){
                                    Class clazz = processFormMapper.getClass();
                                    Method m1 = clazz.getDeclaredMethod("insert"+entityName,Map.class);
                                    Method m2 = clazz.getDeclaredMethod("delete"+entityName, Long.class);
                                    m1.invoke(processFormMapper,newItem);
                                    oldList.add(newItem);
                                    m2.invoke(processFormMapper,Long.parseLong(newItem.get("id").toString()));
                                }else if (idGenerateMode.equals(AUTO)){
                                    if (idType.equals(Integer.class.getSimpleName())){
                                        newItem.put("id",SnowflakeIdWorker.getInstance().nextId());
                                    }else{
                                        newItem.put("id",UUID.randomUUID());
                                    }
                                }else if (idGenerateMode.equals(MANUAL)){
                                    // 用户自定义
                                    if (newItem.get("id") == null){
                                        logger.error("manual entity should have id");
                                        throw new ProcessException("");
                                    }
                                }
                                oldList.add(newItem);
                            }else{
                                if (oldList != null && oldList.size()>0){
                                    for (Map<String,Object> oldItem:oldList){
                                        if (oldItem.get("id").equals(newItem.get("id"))){
                                            oldItem.putAll(newItem);
                                        }

                                    }
                                }
                            }
                        }
                        List<Object> newIds = newList.stream()
                                .map(newMap -> newMap.get("id"))
                                .collect(Collectors.toList());

                        oldList.removeIf(map -> !newIds.contains(map.get("id")));
                        variableData.put(key,oldList);
                    }

                }
            }

        } catch (Exception e) {
            logger.error("editFormData error ",e);
            throw new RuntimeException(e);
        }
    }

    public Comment saveComment(String taskId,String processInstanceId,String userName,String action,Map<String,Object> variable,String comment) {
        Map<String, String> operationPermission = systemProcessRecordService.getOperationPermission(taskId,action);
        operationPermission.put("comment", comment);
        String opStr = null;
        String fullMessage = null;
        try {
            mapper.registerModule(new JavaTimeModule());
            opStr =  mapper.writeValueAsString(operationPermission);
            fullMessage = mapper.writeValueAsString(variable);
        } catch (JsonProcessingException e) {
            throw new ProcessException("map to string error!");
        }
        return managementService.executeCommand(
                new AddProcessRecordCmd(
                        processInstanceId,
                        taskId,
                        userName,
                        new Date(),
                        action,
                        opStr,
                        fullMessage));
    }

   @Override
    public List<TaskOperationPermission> getTaskOperationPermissions(String taskId) {
        TaskInst taskInstance = getTaskInstance(taskId);
        ProcInst procInst = systemProcessInstanceService.getProcessInstance(taskInstance.getProcessInstanceId());
        if (procInst.getFinished()) {
            return new ArrayList<>();
        }
        UserContext.UserInfo currentUserInfo = UserContext.getCurrentUserInfo();
        if (currentUserInfo == null) {
            return new ArrayList<>();
        }
        List<TaskOperationPermission> taskOperationPermissions = systemTaskDefinitionService.taskOptPermission(taskInstance.getProcessDefinitionId(), taskInstance.getTaskDefName());
        // 1. 判断当前任务是否结束，若当前任务已经结束
        if (taskInstance.getFinished()) {
            // 1.1. 判断当前任务完成人是否为当前登录用户，若不是，则所有按钮不展示
            if (!currentUserInfo.getUserName().equals(taskInstance.getCompleteBy())) {
                return new ArrayList<>();
            }
            // 1.2 判断节点操作权限上是否允许撤回，若允许撤回，则可以继续以下判断，否则，直接排除即可，无需做其他检查
            if (taskOperationPermissions.stream()
                    .anyMatch(lcapTaskDefOperationPermission -> StringUtils.equals(lcapTaskDefOperationPermission.getName(), OPERATION_PERMISSION_WITHDRAW)
                            && lcapTaskDefOperationPermission.getOperationEnabled())) {
                Pair<HistoricTaskInstance, List<HistoricTaskInstance>> canWithdrawTaskPair = getCanWithdrawTask(taskId);
                if (canWithdrawTaskPair == null || canWithdrawTaskPair.getKey() == null || canWithdrawTaskPair.getValue() == null || canWithdrawTaskPair.getValue().size() == 0) {
                    return new ArrayList<>();
                }
            }
            // 1.3. 当前节点是initiateTask节点，判断是否可以withdraw
            return taskOperationPermissions.stream()
                    .filter(lcapTaskDefOperationPermission -> lcapTaskDefOperationPermission.getName().equals(OPERATION_PERMISSION_WITHDRAW))
                    .collect(Collectors.toList());
        } else {
            // 2. 若当前任务未结束
            // 2.1. 判断当前任务候选人是否包含当前登录用户，若不是，则所有按钮不展示
            List<String> candidateUsers = taskInstance.getCandidateUsers();
            if (CollectionUtils.isEmpty(candidateUsers) || !candidateUsers.contains(currentUserInfo.getUserName())) {
                return new ArrayList<>();
            }
            Task taskEntity = taskService.createTaskQuery().taskId(taskInstance.getTaskId()).singleResult();
            if (StringUtils.isNotBlank(taskEntity.getScopeType())){
                if (taskEntity.getScopeType().equals(AddSignEnum.PREVIOUS.getValue()) || taskEntity.getScopeType().equals(AddSignEnum.SUBSEQUENT.getValue())) {
                    taskOperationPermissions = taskOperationPermissions.stream()
                            .filter(lcapTaskDefOperationPermission -> lcapTaskDefOperationPermission.getName().equals(OPERATION_PERMISSION_APPROVE)
                                    || lcapTaskDefOperationPermission.getName().equals(OPERATION_PERMISSION_REJECT))
                            .collect(Collectors.toList());
                }
            }
            // 2.2. 当前任务未完成，无法withdraw，过滤去除
            return taskOperationPermissions.stream()
                    .filter(lcapTaskDefOperationPermission -> !lcapTaskDefOperationPermission.getName().equals(OPERATION_PERMISSION_WITHDRAW))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public ProcInstGraph getProcInstGraph(String taskId) {
        TaskInst taskInstance = getTaskInstance(taskId);
        return systemProcessInstanceService.getProcInstGraph(taskInstance.getProcessInstanceId());
    }

    @Override
    public List<DataPropertyPermission> getDataPropertyPermissions(String taskId) {
        TaskInst taskInstance = getTaskInstance(taskId);
        List<FormDataPropertyPermission> formDataPropertyPermissions = systemTaskDefinitionService.fieldPermissionDetail(taskInstance.getProcessDefinitionId(), taskInstance.getTaskDefName());
        // 任务结束 || 查看他人任务：editable -> readOnly
        List<DataPropertyPermission> dataPropertyPermissions = new ArrayList<>();
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        List<String> candidateUsers = taskInstance.getCandidateUsers();

        for (FormDataPropertyPermission permission:formDataPropertyPermissions){
            if (permission.getPropertyName().equals("data") && permission.getSubFieldPermissions() != null){
                for (FormDataPropertyPermission formDataPropertyPermission:permission.getSubFieldPermissions()){
                    DataPropertyPermission dataPropertyPermission = new DataPropertyPermission();
                    dataPropertyPermission.setPermission(formDataPropertyPermission.getPermission());
                    dataPropertyPermission.setPropertyName(formDataPropertyPermission.getPropertyName());
                    dataPropertyPermissions.add(dataPropertyPermission);
                }
            }
        }
        if(taskInstance.getFinished() || CollectionUtils.isEmpty(candidateUsers) || !candidateUsers.contains(userInfo.getUserName())
                || ProcessConstant.TASK_CATEGORY_CC_TASK.equalsIgnoreCase(taskInstance.getCategory())) {
            dataPropertyPermissions.stream().forEach(item -> {
                if(FIELD_PERMISSION_EDITABLE.equals(item.permission)) {
                    item.setPermission(FIELD_PERMISSION_READONLY);
                }
            });
        }
        return dataPropertyPermissions;
    }

    @Override
    public List<FormDataPropertyPermission> getFormDataPropertyPermissions(String taskId) {
        TaskInst taskInstance = getTaskInstance(taskId);
        List<FormDataPropertyPermission> dataPropertyPermissions = systemTaskDefinitionService.fieldPermissionDetail(taskInstance.getProcessDefinitionId(), taskInstance.getTaskDefName());
        // 任务结束 || 查看他人任务：editable -> readOnly
        UserContext.UserInfo userInfo = UserContext.getCurrentUserInfo();
        List<String> candidateUsers = taskInstance.getCandidateUsers();
        if(taskInstance.getFinished() || CollectionUtils.isEmpty(candidateUsers) || !candidateUsers.contains(userInfo.getUserName())
                || ProcessConstant.TASK_CATEGORY_CC_TASK.equalsIgnoreCase(taskInstance.getCategory())) {
            dataPropertyPermissions.stream().forEach(item -> {
                if(FIELD_PERMISSION_EDITABLE.equals(item.permission)) {
                    item.setPermission(FIELD_PERMISSION_READONLY);
                    if (item.getSubFieldPermissions() != null && item.getSubFieldPermissions().size()>0){
                        item.getSubFieldPermissions().stream().forEach(subItem -> {
                            if (FIELD_PERMISSION_EDITABLE.equals(subItem.permission)) {
                                subItem.setPermission(FIELD_PERMISSION_READONLY);
                            }
                        });
                    }
                }
            });
        }
        return dataPropertyPermissions;
    }

    @Override
    public Map<String, Object> getProcVariable(String taskId) {
        TaskInst taskInstance = getTaskInstance(taskId);
        Map<String, Object> processInstanceParams = systemProcessInstanceService.getProcessInstanceParams(taskInstance.getProcessInstanceId());
        List<String> resultKeys = new ArrayList<>(Arrays.asList("procInstStartBy", "procInstId"));
        Map<String, Object> resultMap = new HashMap<>();
        resultKeys.stream().forEach(resultKey -> {
            resultMap.put(resultKey, processInstanceParams.get(resultKey));
        });
        return resultMap;
    }
}
