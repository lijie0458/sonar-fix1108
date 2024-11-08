package com.dogfood.aa20240808.processV2.system.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.dogfood.aa20240808.processV2.system.aspect.ProcessRecordAction;
import com.dogfood.aa20240808.processV2.system.command.AddProcessRecordCmd;
import com.dogfood.aa20240808.processV2.system.domain.structure.TaskInst;
import com.dogfood.aa20240808.processV2.system.domain.structure.TaskOperationPermission;
import com.dogfood.aa20240808.processV2.system.event.ProcessRecordEvent;
import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.service.SystemProcessRecordService;
import com.dogfood.aa20240808.processV2.system.service.SystemTaskInstanceService;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.persistence.entity.CommentEntityImpl;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SystemProcessRecordServiceImpl implements SystemProcessRecordService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SystemProcessRecordServiceImpl.class);
    private static final String SAVE_COMMENT_LOG = "Consume operation event and save process record to database."
            + " processRecord:{} actionType:{}";

    private TaskService taskService;

    @Autowired
    HistoryService historyService;

    @Autowired
    RepositoryService repositoryService;

    private ManagementService managementService;

    protected ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private SystemTaskInstanceService systemTaskInstanceService;

    public SystemProcessRecordServiceImpl(TaskService taskService,
                                          EventBus eventBus,
                                          ManagementService managementService){
        this.taskService = taskService;
        this.managementService = managementService;
        eventBus.register(this);
    }

    @Override
    public List<Comment> getProcessInstanceRecord(String processInstanceId) {
        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId, AddProcessRecordCmd.COMMENT_TYPE);
        if (comments == null){
            comments = new ArrayList<>();
        }
        LOGGER.info("Get process instance record. processInstanceId:{} comments:{}", processInstanceId, comments);
        return comments.stream()
                .sorted((c1, c2) -> c2.getTime().compareTo(c1.getTime()) == 0 ? c2.getId().compareTo(c1.getId()) : c2.getTime().compareTo(c1.getTime()))
                .sorted(Comparator.comparingInt(comment -> {
                    String action = ((CommentEntityImpl) comment).getAction();
                    if (ProcessRecordAction.END.value().equalsIgnoreCase(action)) {
                        return -1;
                    } else if (ProcessRecordAction.LAUNCH.value().equalsIgnoreCase(action)) {
                        return 1;
                    } else {
                        return 0;
                    }
                }))
                .collect(Collectors.toList());
    }

    @Subscribe
    @AllowConcurrentEvents
    public void createProcessRecord(ProcessRecordEvent event){
        addProcessRecord(event);
    }

    @Override
    public void addProcessRecord(ProcessRecordEvent event){
        String processInstanceId = null;
        Map<String, String> operationPermission = new HashMap<>();
        String taskId = null;
        if (event.getAction() != null && event.getAction().equals(ProcessRecordAction.LAUNCH.value())){
            processInstanceId = event.getProcessInstanceId();
            operationPermission.put("nodeOperation", event.getAction());
            operationPermission.put("nodeOperationDisplayText", "启动");
            taskId = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .list()
                    .get(0)
                    .getId();
            Date launchDate = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId)
                    .singleResult().getStartTime();
            event.setRecordCreatedTime(launchDate);
        } else if (event.getAction() != null && event.getAction().equals(ProcessRecordAction.CC.value())) {
            processInstanceId = event.getProcessInstanceId();
            taskId = event.getTaskId();
            operationPermission.put("comment", event.getMessage());
            operationPermission.put("nodeOperation", event.getAction());
            operationPermission.put("nodeOperationDisplayText", "抄送");
        } else if (event.getAction() != null && event.getAction().equals(ProcessRecordAction.END.value())) {
            processInstanceId = event.getProcessInstanceId();
            operationPermission.put("comment", event.getMessage());
            operationPermission.put("nodeOperation", event.getAction());
            operationPermission.put("nodeOperationDisplayText", "结束");
        } else {
            TaskInst taskInstance = systemTaskInstanceService.getTaskInstance(event.getTaskId());
            processInstanceId = taskInstance.getProcessInstanceId();
            taskId = event.getTaskId();
            operationPermission = getOperationPermission(taskId, event.getAction());
            operationPermission.put("comment", event.getMessage());
        }

        Comment comment = managementService.executeCommand(
                new AddProcessRecordCmd(
                        processInstanceId,
                        taskId,
                        event.getUserName(),
                        event.getRecordCreatedTime(),
                        event.getAction(),
                        mapToString(operationPermission),
                        event.getFullMessage()));
        LOGGER.info(SAVE_COMMENT_LOG, comment.getId(), event.getAction());
    }

    public Map<String, String> getOperationPermission(String taskId, String operation) {
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(taskInstance.getProcessDefinitionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
        String operatePermission = bpmnModel.getMainProcess().getFlowElement(taskInstance.getTaskDefinitionKey()).getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_OPERATION_PERMISSION);
        if (StringUtils.isEmpty(operatePermission)) {
            LOGGER.error("The operatePermission of " + operation + "TaskInstance does not allowed!");
            throw new ProcessException("The operatePermission of " + operation + "TaskInstance does not allowed!");
        }
        List<TaskOperationPermission> taskOptPermissionList;
        try {
            mapper.registerModule(new JavaTimeModule());
            taskOptPermissionList = mapper.readValue(operatePermission, new TypeReference<List<TaskOperationPermission>>() {
            });
        } catch (JsonProcessingException e) {
            throw new ProcessException("operatePermission is invalid!");
        }
        if (CollectionUtils.isEmpty(taskOptPermissionList)) {
            LOGGER.error("The operatePermission of " + operation + "TaskInstance does not allowed!");
            throw new ProcessException("The operatePermission of " + operation + "TaskInstance does not allowed!");
        }
        TaskOperationPermission taskOptPermission = taskOptPermissionList.stream()
                .filter(permission -> StringUtils.equals(permission.getName(), operation) && permission.getOperationEnabled())
                .findAny().orElseThrow(() -> new ProcessException("The operatePermission of " + operation + "TaskInstance does not allowed!"));
        Map<String, String> taskOptPermissionMap = new HashMap<>();
        taskOptPermissionMap.put("nodeOperation", taskOptPermission.getName());
        taskOptPermissionMap.put("nodeOperationDisplayText", taskOptPermission.getDisplayText());
        return taskOptPermissionMap;
    }

    public String mapToString(Map<String, String> map) {
        try {
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new ProcessException("map to string error!");
        }
    }

}
