package com.dogfood.aa20240808.processV2.system.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.domain.structure.*;
import com.dogfood.aa20240808.processV2.system.service.SystemTaskInstanceService;
import com.dogfood.aa20240808.processV2.system.service.SystemProcessInstanceService;
import com.dogfood.aa20240808.processV2.system.util.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.common.engine.api.query.QueryProperty;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.impl.ProcessInstanceQueryProperty;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.engine.TaskService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class SystemProcessInstanceServiceImpl implements SystemProcessInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(SystemProcessInstanceServiceImpl.class);

    private static final Map<String, QueryProperty> allowedSortProperties = new HashMap<>();
    static {
        allowedSortProperties.put("processInstanceId", ProcessInstanceQueryProperty.PROCESS_INSTANCE_ID);
        allowedSortProperties.put("processDefUniqueKey", ProcessInstanceQueryProperty.PROCESS_DEFINITION_KEY);
        allowedSortProperties.put("startTime", ProcessInstanceQueryProperty.PROCESS_START_TIME);
    }

    @Autowired
    HistoryService historyService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Resource
    SystemTaskInstanceService systemTaskInstanceService;

    protected ObjectMapper mapper = new ObjectMapper();

    private static final Long INTEGER_MAX_VALUE = 2147483647L;

    @Override
    public PageOf<HistoricProcessInstance> getProcessInstancePageOf(String processInstanceId, String startBy, Boolean finished, String processDefUniqueKey,
                                                                    ZonedDateTime startTimeBefore, ZonedDateTime startTimeAfter, ZonedDateTime endTimeBefore,
                                                                    ZonedDateTime endTimeAfter, Long page, Long size, String sort, String order, String search) {
        Integer pageInt = Math.toIntExact(page);
        Integer sizeInt = Math.toIntExact(size);
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery();
        if (StringUtils.isNotBlank(processInstanceId)) {
            query.processInstanceId(processInstanceId);
        }
        if (StringUtils.isNotBlank(startBy)) {
            query.startedBy(startBy);
        }
        if (ObjectUtils.isNotEmpty(finished)) {
            if (finished) {
                query.finished();
            } else {
                query.unfinished();
            }
        }
        if (StringUtils.isNotBlank(processDefUniqueKey)) {
            query.processDefinitionKey(processDefUniqueKey);
        }
        if (ObjectUtils.isNotEmpty(startTimeBefore)) {
            query.startedBefore(SystemProcessUtil.ZonedDateTimeToDate(startTimeBefore));
        }
        if (ObjectUtils.isNotEmpty(startTimeAfter)) {
            query.startedAfter(SystemProcessUtil.ZonedDateTimeToDate(startTimeAfter));
        }
        if (ObjectUtils.isNotEmpty(endTimeBefore)) {
            query.finishedBefore(SystemProcessUtil.ZonedDateTimeToDate(endTimeBefore));
        }
        if (ObjectUtils.isNotEmpty(endTimeAfter)) {
            query.finishedAfter(SystemProcessUtil.ZonedDateTimeToDate(endTimeAfter));
        }
        if (ObjectUtils.isNotEmpty(search)) {
            query.processDefinitionName(search);
        }
        Pageable pageable = SystemProcessPageUtil.createPageable(pageInt, sizeInt, sort, order);
        return SystemProcessPageUtil.paginateQueryV2(pageable, query, "startTime", allowedSortProperties);
    }

    @Override
    public List<ProcInst> getProcessInstanceList(String processInstanceId, String startBy, Boolean finished, String processDefUniqueKey,
                                                 ZonedDateTime startTimeBefore, ZonedDateTime startTimeAfter, ZonedDateTime endTimeBefore,
                                                 ZonedDateTime endTimeAfter, Long page, Long size, String sort, String order, String search) {

        List<ProcInst> lcapProcessInstanceList = getProcessInstancePageOf(processInstanceId, startBy, finished, processDefUniqueKey,
                                            startTimeBefore, startTimeAfter, endTimeBefore, endTimeAfter, page, size, sort, order, search)
                .getContent().stream()
                .map(this::toLCAPProcessInstance)
                .collect(Collectors.toList());
        logger.info("Process instance list: {}", lcapProcessInstanceList);
        return lcapProcessInstanceList;
    }

    @Override
    public ProcInst getProcessInstance(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = processInstExists(processInstanceId);
        ProcInst lcapProcessInstance = toLCAPProcessInstance(historicProcessInstance);
        logger.info("Process instance: {}", lcapProcessInstance);
        return lcapProcessInstance;
    }

    private ProcInst toLCAPProcessInstance(HistoricProcessInstance historicProcessInstance) {
        if (ObjectUtils.isEmpty(historicProcessInstance)) return null;
        ProcInst lcapProcessInstance = new ProcInst();
        lcapProcessInstance.setProcessInstanceId(historicProcessInstance.getId());
        lcapProcessInstance.setTitle(historicProcessInstance.getName());
        lcapProcessInstance.setDescription(historicProcessInstance.getDescription());
        lcapProcessInstance.setStartBy(historicProcessInstance.getStartUserId());
        lcapProcessInstance.setStartTime(SystemProcessUtil.DateToZonedDateTime(historicProcessInstance.getStartTime()));
        lcapProcessInstance.setEndTime(SystemProcessUtil.DateToZonedDateTime(historicProcessInstance.getEndTime()));
        lcapProcessInstance.setFinished(historicProcessInstance.getEndTime() != null);
        lcapProcessInstance.setProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());
        lcapProcessInstance.setProcessDefUniqueKey(historicProcessInstance.getProcessDefinitionKey());
        return lcapProcessInstance;
    }

    @Override
    public void endProcessInstance(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = processInstExists(processInstanceId);
        if (historicProcessInstance.getEndTime() != null) {
            throw new ProcessException("Process has ended!");
        }
        runtimeService.deleteProcessInstance(processInstanceId, "terminate process instance");
        logger.info("End process instance: {} success", processInstanceId);
    }

    @Override
    public ProcInstGraph getProcInstGraph(String processInstanceId){
        HistoricProcessInstance historicProcessInstance = processInstExists(processInstanceId);

        ProcInstGraph displayNode = new ProcInstGraph();
        displayNode.setProcInstId(processInstanceId);

        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        if (!bpmnModel.getLocationMap().isEmpty()) {
            List<String> currentActivityInstances = processInstanceRunning(processInstanceId) ? runtimeService.getActiveActivityIds(processInstanceId): new LinkedList<>();

            GraphicInfo diagramInfo = new GraphicInfo();
            Set<String> currentElements = new HashSet<>(currentActivityInstances);
            SystemProcessUtil.processProcessElements(bpmnModel, displayNode, diagramInfo, currentElements);
            handleComletedInfo(displayNode, processInstanceId, historicProcessInstance);

            displayNode.setDiagramBeginX(diagramInfo.getX());
            displayNode.setDiagramBeginY(diagramInfo.getY());
            displayNode.setDiagramWidth(diagramInfo.getWidth());
            displayNode.setDiagramHeight(diagramInfo.getHeight());
        }
        logger.info("Process instance display: {}", displayNode);
        return displayNode;
    }

    @Override
    public Map<String, Object> getProcessInstanceParams(String processInstanceId) {
        return (Map<String, Object>) historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM)
                .singleResult()
                .getValue();
    }

    private void handleComletedInfo(ProcInstGraph displayNode, String processInstanceId, HistoricProcessInstance processInstance) {
        List<ProcInstGraph.Element> elements = displayNode.getElements();
        List<ProcInstGraph.Flow> flows = displayNode.getFlows();
        AtomicReference<Date> lastRejectTime = new AtomicReference<>(new Date(0));
        Map<String, Date> multiApprovalUpgradeTime = new HashMap<>();
        String lastProcessDefinitionId = processInstance.getProcessDefinitionId();
        historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .processDefinitionId(lastProcessDefinitionId)
                .orderByHistoricTaskInstanceEndTime().asc()
                .list()
                .stream()
                .filter(historicTaskInstance -> historicTaskInstance.getEndTime() != null && historicTaskInstance.getDeleteReason() != null)
                .forEach(historicTaskInstance -> {
                    if ((historicTaskInstance.getDeleteReason().contains("Change activity to") || historicTaskInstance.getDeleteReason().contains("Change parent activity to")) &&
                            !historicTaskInstance.getDeleteReason().contains(historicTaskInstance.getTaskDefinitionKey())) {
                        lastRejectTime.set(historicTaskInstance.getEndTime());
                    } else if (historicTaskInstance.getDeleteReason().contains(historicTaskInstance.getTaskDefinitionKey())) {
                        multiApprovalUpgradeTime.put(historicTaskInstance.getTaskDefinitionKey(), historicTaskInstance.getEndTime());
                    }
                });
        Map<String, String>  completedActivityInstances = getCompletedActivityInstancesByProcessInstanceId(processInstanceId, lastRejectTime, lastProcessDefinitionId);
        for (ProcInstGraph.Element elementNode : elements) {
            if (elementNode.getType().equals(ProcessConstant.TASK_CATEGORY_INITIATE_TASK) || elementNode.getType().equals(ProcessConstant.TASK_CATEGORY_APPROVAL_TASK)
                    || elementNode.getType().equals(ProcessConstant.TASK_CATEGORY_SUBMIT_TASK) || elementNode.getType().equals(ProcessConstant.TASK_CATEGORY_CC_TASK)) {
                List<ProcInstGraph.CompleteInfo> completeInfos = new ArrayList<>();
                if (elementNode.isCompleted() || elementNode.isCurrent() || completedActivityInstances.containsKey(elementNode.getName())) {
                    historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .processDefinitionId(lastProcessDefinitionId)
                            .taskDefinitionKey(elementNode.getName()).orderByHistoricTaskInstanceEndTime().asc()
                            .list()
                            .stream()
                            .filter(historicTaskInstance -> historicTaskInstance.getEndTime() != null && historicTaskInstance.getDeleteReason() == null
                                    && historicTaskInstance.getEndTime().compareTo(lastRejectTime.get()) > 0)
                            .forEach(historicTaskInstance -> {
                                if (multiApprovalUpgradeTime.containsKey(historicTaskInstance.getTaskDefinitionKey())) {
                                    if (multiApprovalUpgradeTime.get(historicTaskInstance.getTaskDefinitionKey()).compareTo(historicTaskInstance.getEndTime()) > 0) {
                                        return;
                                    }
                                }
                                ProcInstGraph.CompleteInfo completeInfo = new ProcInstGraph.CompleteInfo();
                                String assignee = historicTaskInstance.getAssignee();
                                String completedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(historicTaskInstance.getEndTime());
                                handleInitiateTask(elementNode, completeInfo, assignee, processInstance);
                                completeInfo.setCompleteTime(completedTime);
                                completeInfo.setCompleted(true);
                                Set<String> candidates = new HashSet<>();
                                historyService.getHistoricIdentityLinksForTask(historicTaskInstance.getId())
                                        .stream().filter(historicIdentityLink -> historicIdentityLink.getUserId() != null)
                                        .forEach(historicIdentityLink -> {
                                            candidates.add(historicIdentityLink.getUserId());
                                        });
                                completeInfo.setAddSignTag(historicTaskInstance.getScopeType());
                                completeInfo.setCandidates(new ArrayList<>(candidates));
                                completeInfos.add(completeInfo);
                                elementNode.setCompleted(true);
                            });
                }
                runtimeService.createActivityInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .processDefinitionId(lastProcessDefinitionId)
                        .activityId(elementNode.getName())
                        .list()
                        .stream().filter(activityInstance -> activityInstance.getEndTime() == null)
                        .forEach(activityInstance -> {
                            ProcInstGraph.CompleteInfo completeInfo = new ProcInstGraph.CompleteInfo();
                            String assignee = activityInstance.getAssignee();
                            handleInitiateTask(elementNode, completeInfo, assignee, processInstance);
                            completeInfo.setCompleteTime("");
                            completeInfo.setCompleted(false);
                            Set<String> candidates = new HashSet<>();
                            Task taskEntity = taskService.createTaskQuery().taskId(activityInstance.getTaskId()).singleResult();
                            List<Task> tasks = taskService.getSubTasks(activityInstance.getTaskId());
                            if (taskEntity != null && AddSignEnum.PREVIOUS_ADD_SIGN_FROM.getValue().equals(taskEntity.getScopeType()) && !tasks.isEmpty()){
                                return;
                            }else {
                                historyService.getHistoricIdentityLinksForTask(activityInstance.getTaskId())
                                        .stream().filter(historicIdentityLink -> historicIdentityLink.getUserId() != null)
                                        .forEach(historicIdentityLink -> {
                                            candidates.add(historicIdentityLink.getUserId());
                                        });
                            }
                            if (taskEntity != null){
                                completeInfo.setAddSignTag(taskEntity.getScopeType());
                            }
                            completeInfo.setCandidates(new ArrayList<>(candidates));
                            completedActivityInstances.remove(elementNode.getName());
                            completeInfos.add(completeInfo);
                            elementNode.setCompleted(false);
                        });
                elementNode.setCompleteInfos(completeInfos);
            } else {
                Set<String> completedElements = new HashSet<>(completedActivityInstances.keySet());
                if (completedElements != null) {
                    elementNode.setCompleted(completedElements.contains(elementNode.getName()));
                }
            }
        }

        for (ProcInstGraph.Element elementNode : elements) {
            //处理流的完成情况
            elementNode.getOutcomingFlows().forEach(flowNode -> {
                flows.forEach(flow -> {
                    if (flow.getName().equals(flowNode) && (
                            completedActivityInstances.containsKey(flowNode) || completedActivityInstances.containsKey(flowNode + "_DefaultFlow"))) {
                        flow.setCompleted(elementNode.isCompleted());
                    }
                });
            });
        }
    }

    protected Map<String, String> getCompletedActivityInstancesByProcessInstanceId(String processInstanceId, AtomicReference<Date> lastRejectTime, String lastProcessDefinitionId) {
        Map<String, String> completedActivityInstances = new HashMap<>();
        historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .processDefinitionId(lastProcessDefinitionId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list()
                .stream()
                .filter(historicActivity -> historicActivity.getActivityType().equals("startEvent") || historicActivity.getActivityType() != null && historicActivity.getEndTime() != null && (historicActivity.getDeleteReason() == null || historicActivity.getDeleteReason().equals("MI_END"))
                        && historicActivity.getEndTime().compareTo(lastRejectTime.get()) > 0)
                .forEach(historicActivity -> completedActivityInstances.put(historicActivity.getActivityId(), historicActivity.getTaskId()));
        return completedActivityInstances;
    }

    private void handleInitiateTask(ProcInstGraph.Element elementNode, ProcInstGraph.CompleteInfo completeInfo, String assignee, HistoricProcessInstance processInstance) {
        //处理发起任务的assignee参数
        if (elementNode.getType() != null && elementNode.getType().equals("InitiateTask")) {
            completeInfo.setAssignee(processInstance.getStartUserId());
        } else {
            completeInfo.setAssignee(assignee);
        }

    }

    private boolean processInstanceRunning(String processInstanceId){
        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .count() > 0;
    }

    private HistoricProcessInstance processInstExists(String processInstanceId) {
        if (StringUtils.isBlank(processInstanceId)) {
            throw new ProcessException("The processInstanceId cannot be empty!");
        }
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        if (processInstance == null) {
            throw new ProcessException("Process does not exist!");
        }
        return processInstance;
    }

    @Override
    public List<CurrNode> getCurrentNodeList(String processInstanceId) {
        PageOf<HistoricTaskInstance> taskPageOf = systemTaskInstanceService.getRunTaskInstanceList(null, null,
                processInstanceId, null, null, null,
                null, null, null, null, null, 1L, INTEGER_MAX_VALUE,
                null, null, null);
        List<HistoricTaskInstance> historicTaskInstances = taskPageOf.getContent();
        if (CollectionUtil.isEmpty(historicTaskInstances)) {
            CurrNode currNode = new CurrNode();
            currNode.setCurrNodeTitle("结束");
            return Lists.newArrayList(currNode);
        }
        List<CurrNode> currentNodeList = new LinkedList<>();
        Map<String, List<HistoricTaskInstance>> taskGroup = historicTaskInstances.stream().collect(Collectors.groupingBy(HistoricTaskInstance::getTaskDefinitionKey));
        taskGroup.forEach((taskDefKey, taskInstances) -> {
            HistoricTaskInstance historicTaskInstance = taskInstances.get(0);
            TaskInst taskInstance = systemTaskInstanceService.getTaskInstance(historicTaskInstance.getId());
            if(taskInstances.size() == 1) {
                currentNodeList.add(new CurrNode(taskInstance.getTitle(), taskInstance.getCandidateUsers(),taskInstance.getTaskId()));
            } else {
                List<String> curNodeParticipants = new ArrayList<>();
                taskInstances.stream().forEach(item -> curNodeParticipants.addAll(systemTaskInstanceService.getCandidateUsers(item.getId())));
                List<String> curNodeDistinctParticipants = curNodeParticipants.stream().distinct().collect(Collectors.toList());
                currentNodeList.add(new CurrNode(taskInstance.taskTitle, curNodeDistinctParticipants,taskInstance.getTaskId()));
            }
        });
        return currentNodeList;
    }

}
