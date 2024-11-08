package com.dogfood.aa20240808.processV2.system.engine.custom.behavior;

import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import com.dogfood.aa20240808.processV2.system.domain.structure.TaskOperationPermission;
import com.dogfood.aa20240808.processV2.system.domain.structure.MoveToAfterRejected;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.bpmn.model.BpmnModel;

import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.flowable.engine.impl.bpmn.helper.ErrorPropagation;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.apache.commons.lang3.StringUtils;
import java.util.Iterator;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomParallelMultiInstanceBehavior extends ParallelMultiInstanceBehavior {
    private static final Logger logger = LoggerFactory.getLogger(CustomParallelMultiInstanceBehavior.class);
    protected ObjectMapper mapper = new ObjectMapper();
    public CustomParallelMultiInstanceBehavior(Activity activity, AbstractBpmnActivityBehavior originalActivityBehavior) {
        super(activity, originalActivityBehavior);
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        ExecutionEntity execution = (ExecutionEntity) delegateExecution;
        if (getLocalLoopVariable(execution, getCollectionElementIndexVariable()) == null) {
            Object processSystemVariableObject = execution.getVariable("processSystem");
            if (processSystemVariableObject instanceof Map) {
                FlowElement currentFlowElement = execution.getCurrentFlowElement();
                Map<String, Object> processSystemVariables = (Map<String, Object>) processSystemVariableObject;
                Map<String, Object> nodeVar = new HashMap<>();
                nodeVar.put("nodeTitle", currentFlowElement.getAttributeValue(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE, "elementTitle"));
                nodeVar.put("nodeDesc", currentFlowElement.getAttributeValue(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE, "elementDescription"));
                nodeVar.put("nodeStartTime", ZonedDateTime.now());

                // 任务经办人已在节点执行前，塞入到task_assignee_list_variable_%s变量中，直接使用即可
                String taskAssigneeListVariableName = String.format(ProcessConstant.TASK_ASSIGNEE_LIST_VARIABLE_NAME, currentFlowElement.getId());
                List<String> taskAssigneeList = (List<String>)execution.getVariable(taskAssigneeListVariableName);
                nodeVar.put("nodeParticipants", taskAssigneeList);
                nodeVar.put("nodeAssignees", new ArrayList<String>());

                Object nrOfApprovedInstances = 0;
                Object nrOfRejectedInstances = 0;
                nodeVar.put("nrOfApprovedInstances", nrOfApprovedInstances);
                nodeVar.put("nrOfRejectedInstances", nrOfRejectedInstances);

                processSystemVariables.put(currentFlowElement.getId(), nodeVar);
                execution.setVariable("processSystem", processSystemVariables);
            }

            int nrOfInstances = 0;

            try {
                nrOfInstances = createInstances(delegateExecution);
            } catch (BpmnError error) {
                ErrorPropagation.propagateError(error, execution);
            }

            if (nrOfInstances == 0) {
                cleanupMiRoot(execution);
            }

        } else {
            // for synchronous, history was created already in ContinueMultiInstanceOperation,
            // but that would lead to wrong timings for asynchronous which is why it's here
            if (activity.isAsynchronous()) {
                CommandContextUtil.getActivityInstanceEntityManager().recordActivityStart(execution);
            }
            innerActivityBehavior.execute(execution);
        }
    }

     @Override
     public void leave(DelegateExecution execution) {

         boolean zeroNrOfInstances = false;
         if (resolveNrOfInstances(execution) == 0) {
             // Empty collection, just leave.
             zeroNrOfInstances = true;
             super.leave(execution); // Plan the default leave
         }

         int loopCounter = getLoopVariable(execution, getCollectionElementIndexVariable());
         int nrOfInstances = getLoopVariable(execution, NUMBER_OF_INSTANCES);
         int nrOfCompletedInstances = getLoopVariable(execution, NUMBER_OF_COMPLETED_INSTANCES) + 1;
         int nrOfActiveInstances = getLoopVariable(execution, NUMBER_OF_ACTIVE_INSTANCES) - 1;

         DelegateExecution miRootExecution = getMultiInstanceRootExecution(execution);
         CommandContextUtil.getActivityInstanceEntityManager().recordActivityEnd((ExecutionEntity) execution, null);
         logLoopDetails(execution, "instance completed", loopCounter, nrOfCompletedInstances, nrOfActiveInstances, nrOfInstances);
         if (zeroNrOfInstances) {
             return;
         }
         ExecutionEntity executionEntity = (ExecutionEntity) execution;
         if (executionEntity.getParent() != null) {
             boolean isCompletionConditionSatisfied = completionConditionSatisfied(execution.getParent());
             if (nrOfCompletedInstances >= nrOfInstances || isCompletionConditionSatisfied) {
                 Map<String, Object> variables = execution.getVariables();
                 Map<String, Object> processSystem = (Map<String, Object>) variables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM);
                 FlowElement currentFlowElement = execution.getCurrentFlowElement();
                 Map<String, Object> nodeVar = (Map<String, Object>) processSystem.get(currentFlowElement.getId());
                 if ("Rejected".equals(nodeVar.get(ProcessConstant.NODE_OUTCOME))){
                     String moveTo = getMoveTo(execution);
                     if (!moveTo.equals(MoveToAfterRejected.NEXT_NODE.getValue())){
                         callActivityEndListeners(execution);
                         ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
                         RuntimeService runtimeService = processEngineConfiguration.getRuntimeService();
                         RepositoryService repositoryService = processEngineConfiguration.getRepositoryService();
                         List<String> waitExecutions = new ArrayList<>();
                         waitExecutions.add(miRootExecution.getId());
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
                         ExecutionEntity leavingExecution = null;
                         if (nrOfInstances > 0) {
                             leavingExecution = executionEntity.getParent();
                         } else {
                             CommandContextUtil.getActivityInstanceEntityManager().recordActivityEnd((ExecutionEntity) execution, null);
                             leavingExecution = executionEntity;
                         }
                         if (isCompletionConditionSatisfied) {
                             sendCompletedWithConditionEvent(leavingExecution);
                         }
                         else {
                             sendCompletedEvent(leavingExecution);
                         }
                         // 拒绝
                         if (MoveToAfterRejected.START.getValue().equals(moveTo)){
                             runtimeService.createChangeActivityStateBuilder().processInstanceId(execution.getProcessInstanceId())
                                     .moveExecutionsToSingleActivityId(waitExecutions,startAct)
                                     .changeState();
                             return;
                         }else if (MoveToAfterRejected.END.getValue().equals(moveTo)){
                             runtimeService.createChangeActivityStateBuilder().processInstanceId(execution.getProcessInstanceId())
                                     .moveExecutionsToSingleActivityId(waitExecutions,endAct)
                                     .changeState();
                             return;
                         }
                     }

                 }
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
             logger.error("getMoveTo error", e);
         }
         return moveTo;

     }
}
