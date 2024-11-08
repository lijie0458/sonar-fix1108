package com.dogfood.aa20240808.processV2.system.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.dogfood.aa20240808.processV2.system.domain.structure.MultiApprovalPolicy;
import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProcessApprovalTaskPassService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessApprovalTaskPassService.class);

    protected ObjectMapper mapper = new ObjectMapper();

    public boolean getCompleted(DelegateExecution execution) {
        String userTaskDefinitionKey = execution.getCurrentFlowElement().getId();
        String policy = execution.getCurrentFlowElement().getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, "policy");
        LOGGER.info("The task policy is {}", policy);
        MultiApprovalPolicy multiApprovalPolicy;
        try {
            mapper.registerModule(new JavaTimeModule());
            multiApprovalPolicy = mapper.readValue(policy, MultiApprovalPolicy.class);
            LOGGER.info("The task policy is {}", policy);
        } catch (JsonProcessingException e) {
            throw new ProcessException("policy is invalid!");
        };
        Map<String, Object> variables = execution.getVariables();
        Map<String, Object> processSystem = (Map<String, Object>) variables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM);
        Integer nrOfInstances = (Integer) variables.get("nrOfInstances");
        Map<String, Object> nodeVar = (Map<String, Object>) processSystem.get(userTaskDefinitionKey);
        Integer nrOfApprovedInstances = (Integer) nodeVar.get(ProcessConstant.NR_OF_APPROVED_INSTANCES);
        Integer nrOfRejectedInstances = (Integer) nodeVar.get(ProcessConstant.NR_OF_REJECTED_INSTANCES);
        LOGGER.info("nrOfInstances: {}, nrOfApprovedInstances: {}, nrOfRejectedInstances: {}", nrOfInstances, nrOfApprovedInstances, nrOfRejectedInstances);

        boolean isPass = false;
        if (multiApprovalPolicy.getConcept().equals("CountersignPolicy")
            || multiApprovalPolicy.getConcept().equals("CountersignPolicyV2")) {
            // 通过会签比例判断是否通过
            float percent = multiApprovalPolicy.getPassPercent()/100f;
            float passPercent = nrOfApprovedInstances/(float) nrOfInstances;
            float failPercent = nrOfRejectedInstances/(float) nrOfInstances;
            if (passPercent >= percent) {
                nodeVar.put(ProcessConstant.NODE_OUTCOME, "Approved");
                processSystem.put(userTaskDefinitionKey, nodeVar);
                variables.put(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM, processSystem);
                execution.setVariables(variables);
                LOGGER.info("通过比例判断通过");
                isPass = true;
            } else if (failPercent > (1 - percent)) {
                nodeVar.put(ProcessConstant.NODE_OUTCOME, "Rejected");
                processSystem.put(userTaskDefinitionKey, nodeVar);
                variables.put(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM, processSystem);
                execution.setVariables(variables);
                LOGGER.info("通过比例判断不通过");
                isPass = true;
            }
            LOGGER.info("通过比例判断未完成");
        } else if (multiApprovalPolicy.getConcept().equals("SequentialPolicy")
            || multiApprovalPolicy.getConcept().equals("SequentialPolicyV2")) {
            //顺序审批是否通过
            if (nrOfRejectedInstances > 0) {
                nodeVar.put(ProcessConstant.NODE_OUTCOME, "Rejected");
                processSystem.put(userTaskDefinitionKey, nodeVar);
                variables.put(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM, processSystem);
                execution.setVariables(variables);
                LOGGER.info("顺序审批不通过");
                isPass = true;
            }
            if (nrOfApprovedInstances.equals(nrOfInstances)) {
                nodeVar.put(ProcessConstant.NODE_OUTCOME, "Approved");
                processSystem.put(userTaskDefinitionKey, nodeVar);
                variables.put(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM, processSystem);
                execution.setVariables(variables);
                LOGGER.info("顺序审批通过");
                isPass = true;
            }
            LOGGER.info("顺序审批未完成");
        }
        return isPass;
    }
}
