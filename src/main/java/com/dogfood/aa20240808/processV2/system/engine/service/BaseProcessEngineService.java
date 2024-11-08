package com.dogfood.aa20240808.processV2.system.engine.service;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.cfg.SpringBeanFactoryProxyMap;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.util.CommandContextUtil;

import java.util.*;
import java.util.stream.Collectors;


public class BaseProcessEngineService {

    private static final List<String> VARIABLE_NAME_LIST = Arrays.asList("nodeTitle", "nodeDesc", "nodeParticipants", "nodeAssignees", "nodeStartTime", "nodeEndTime", "nodeOutcome",
            "taskCompleteUser", "taskId", "taskStartTime", "taskEndTime", "taskOutcome");

    protected void addTemporaryVariablesForCurrentNode(Map<String, Object> processSystemVariables, BpmnModel bpmnModel, String currentNodeKey) {
        FlowElement currentFlowElement = bpmnModel.getMainProcess().getFlowElement(currentNodeKey);
        if (currentFlowElement instanceof UserTask) {
            Map<String, Object> preNodeVar = new HashMap<>();
            Map<String, Object> fixedNodeVar = (Map<String, Object>) processSystemVariables.getOrDefault(currentNodeKey, new HashMap<>());
            VARIABLE_NAME_LIST.forEach(variableName ->
                    preNodeVar.put(variableName, fixedNodeVar.get(variableName))
            );
            processSystemVariables.put("curr", preNodeVar);
        }
    }

    /**
     * 添加临时变量,如上一个节点的信息
     *
     * @param processSystemVariables
     * @param bpmnModel
     * @param currentNodeKey
     */
    protected void addTemporaryVariablesForPreviousNode(Map<String, Object> processSystemVariables, BpmnModel bpmnModel, String currentNodeKey) {
        // 处理上一个节点的信息
        List<FlowElement> previousFlowElementList = bpmnModel.getMainProcess().getFlowElements().stream()
                .filter(flowElement -> flowElement instanceof SequenceFlow && ((SequenceFlow) flowElement).getTargetFlowElement().getId().equals(currentNodeKey))
                .map(flowElement -> ((SequenceFlow) flowElement).getSourceFlowElement())
                .collect(Collectors.toList());
        // 前一个节点只能唯一，不允许出现多个，否则就不存在多个节点
        if (previousFlowElementList.size() == 1) {
            FlowElement previousFlowElement = previousFlowElementList.get(0);
            // 且该组件只能是任务节点
            if (previousFlowElement instanceof UserTask) {
                String previousNodeKey = previousFlowElement.getId();
                Map<String, Object> preNodeVar = new HashMap<>();
                Map<String, Object> fixedNodeVar = (Map<String, Object>) processSystemVariables.getOrDefault(previousNodeKey, new HashMap<>());
                VARIABLE_NAME_LIST.forEach(variableName ->
                        preNodeVar.put(variableName, fixedNodeVar.get(variableName))
                );
                processSystemVariables.put("pre", preNodeVar);
            }
        }
    }

    protected <T> Optional<T> getProcessSpringServiceBean(String serviceName, Class<T> clazz) {
        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
        Map<Object, Object> beans = processEngineConfiguration.getBeans();
        if (beans instanceof SpringBeanFactoryProxyMap) {
            SpringBeanFactoryProxyMap springBeanFactoryProxyMap = (SpringBeanFactoryProxyMap) beans;
            if (springBeanFactoryProxyMap.containsKey(serviceName)) {
                Object value = springBeanFactoryProxyMap.get(serviceName);
                if (clazz.isInstance(value)) {
                    return Optional.of((T) value);
                }
            }
        }
        return Optional.empty();
    }
}
