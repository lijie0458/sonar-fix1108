package com.dogfood.aa20240808.processV2.system.engine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dogfood.aa20240808.domain.enumeration.BaseEnum;
import com.dogfood.aa20240808.processV2.system.engine.service.inter.ConditionServiceInterface;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.delegate.DelegateExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessFlowConditionService extends BaseProcessEngineService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessFlowConditionService.class);

    @Resource
    private RepositoryService repositoryService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String CONDITION_SERVICE_NAME = "%sVersion%s%sConditionService";

    /**
     * 解析流条件定义格式，通过计算得到流条件结果
     * 由于流程引擎的表达式特点，只能返回Boolean封装类型，而不是boolean基本类型
     *
     * @param execution 执行对象
     * @return true:流转 false:不流转
     */
    public Boolean compute(DelegateExecution execution, String conditionFlowKey) {
        LOGGER.info("ProcessFlowConditionService.compute: execution={}, currentFlow={}", execution, execution.getCurrentActivityId());
        BpmnModel bpmnModel = repositoryService.getBpmnModel(execution.getProcessDefinitionId());
        if (bpmnModel != null) {
            String flowConditionMapStr = bpmnModel.getMainProcess().getFlowElement(conditionFlowKey).getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_FLOW_CONDITION_MAP);
            if (StringUtils.isNotBlank(flowConditionMapStr)) {
                try {
                    Map<String, Object> flowConditionMap = objectMapper.readValue(flowConditionMapStr, Map.class);
                    String type = (String) flowConditionMap.get("type");
                    if ("logic".equalsIgnoreCase(type)) {
                        return computeLogicCondition(execution, bpmnModel, conditionFlowKey);
                    } else {
                        return computeSimpleCondition(execution, bpmnModel, flowConditionMap);
                    }
                } catch (JsonProcessingException e) {
                    LOGGER.error("getFlowConditionMap error", e);
                    return Boolean.FALSE;
                }
            }
        }
        return Boolean.TRUE;
    }

    private Boolean computeLogicCondition(DelegateExecution execution, BpmnModel bpmnModel, String conditionFlowKey) {
        if (bpmnModel != null) {
            String version = bpmnModel.getMainProcess().getAttributeValue(ProcessConstant.EXTENSION_NAMESPACE, ProcessConstant.KEY_NASL_VERSION);
            try {
                String conditionServiceName = String.format(CONDITION_SERVICE_NAME, bpmnModel.getMainProcess().getId(), version, conditionFlowKey);
                Optional<ConditionServiceInterface> conditionServiceOptional = getProcessSpringServiceBean(conditionServiceName, ConditionServiceInterface.class);
                if (conditionServiceOptional.isPresent()) {
                    LOGGER.info("Begin to execute flow condition logic: {}", conditionServiceName);
                    ConditionServiceInterface conditionService = conditionServiceOptional.get();
                    Map<String, Object> variables = execution.getVariables();
                    // 添加局部临时变量，此处只有上一个节点信息
                    addTemporaryVariablesForPreviousNode((Map<String, Object>) variables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM), bpmnModel, conditionFlowKey);
                    Boolean result = conditionService.compute(variables);
                    execution.setVariable(ProcessConstant.KEY_VARIABLE_CUSTOM, variables.get(ProcessConstant.KEY_VARIABLE_CUSTOM));
                    LOGGER.info("End to execute flow condition logic: {}", conditionServiceName);
                    return result;
                } else {
                    LOGGER.info("There is no need to execute flow condition logic: {}", conditionServiceName);
                    return Boolean.TRUE;
                }
            } catch (Exception e) {
                LOGGER.error("Execute flow condition logic error", e);
            }
        }
        return Boolean.FALSE;
    }

    private Boolean computeSimpleCondition(DelegateExecution execution, BpmnModel bpmnModel, Map<String, Object> flowConditionMap) {
        if (flowConditionMap == null || flowConditionMap.isEmpty()) {
            return Boolean.TRUE;
        }
        String quantifier = (String) flowConditionMap.get("quantifier");
        List<Map<String, Object>> items = (List<Map<String, Object>>) flowConditionMap.get("items");
        if (CollectionUtils.isEmpty(items)) {
            return Boolean.TRUE;
        }
        Map<String, Object> processVariables = execution.getVariables();
        addTemporaryVariablesForPreviousNode((Map<String, Object>) processVariables.get(ProcessConstant.KEY_VARIABLE_PROCESS_SYSTEM), bpmnModel, execution.getCurrentActivityId());
        if (ProcessConstant.VALUE_FLOW_CONDITION_V2_QUANTIFIER_ALL.equalsIgnoreCase(quantifier)) {
            return items.stream().allMatch(item -> computeItem(processVariables, item));
        } else if (ProcessConstant.VALUE_FLOW_CONDITION_V2_QUANTIFIER_ANY.equalsIgnoreCase(quantifier)) {
            return items.stream().anyMatch(item -> computeItem(processVariables, item));
        }
        return Boolean.FALSE;
    }


    private boolean computeItem(Map<String, Object> processVariables, Map<String, Object> item) {
        String left = (String) item.get("left");
        String operator = (String) item.get("operator");
        List<String> right = (List<String>) item.get("right");
        LOGGER.info("ProcessFlowConditionService.computeItem: left={}, operator={}, right={}", left, operator, right);
        Optional<Object> propertyValueOptional = getPropertyValue(processVariables, left);
        if (propertyValueOptional.isPresent() && CollectionUtils.isNotEmpty(right)) {
            Object propertyValue = propertyValueOptional.get();
            if (propertyValue.getClass().equals(String.class)) {
                return computeItemForSingle((String) propertyValue, right, operator);
            } else if (propertyValue.getClass().equals(Integer.class)) {
                return computeItemForSingle((Integer) propertyValue, right.stream().map(Integer::valueOf).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(Double.class)) {
                return computeItemForSingle((Double) propertyValue, right.stream().map(Double::valueOf).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(BigDecimal.class)) {
                return computeItemForSingle((BigDecimal) propertyValue, right.stream().map(BigDecimal::new).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(Boolean.class)) {
                return computeItemForSingle((Boolean) propertyValue, right.stream().map(Boolean::valueOf).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(Long.class)) {
                return computeItemForSingle((Long) propertyValue, right.stream().map(Long::valueOf).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(Float.class)) {
                return computeItemForSingle((Float) propertyValue, right.stream().map(Float::valueOf).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(Short.class)) {
                return computeItemForSingle((Short) propertyValue, right.stream().map(Short::valueOf).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(Byte.class)) {
                return computeItemForSingle((Byte) propertyValue, right.stream().map(Byte::valueOf).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(ZonedDateTime.class)) {
                return computeItemForSingle((ZonedDateTime) propertyValue, right.stream().map(ZonedDateTime::parse).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(LocalDate.class)) {
                return computeItemForSingle((LocalDate) propertyValue, right.stream().map(LocalDate::parse).collect(Collectors.toList()), operator);
            } else if (propertyValue.getClass().equals(LocalTime.class)) {
                return computeItemForSingle((LocalTime) propertyValue, right.stream().map(LocalTime::parse).collect(Collectors.toList()), operator);
            } else if (propertyValue instanceof BaseEnum) {
                return computeItemForEnum((BaseEnum)propertyValue, right, operator);
            } else if (propertyValue instanceof List) {
                return computeItemForList((List) propertyValue, right, operator);
            }
            return false;
        }
        return Boolean.FALSE;
    }

    private boolean computeItemForEnum(BaseEnum propertyValue, List<String> values, String operator) {
        switch (operator) {
            case "==":
                return values.stream().allMatch(value -> StringUtils.equalsIgnoreCase(value, propertyValue.getCode().toString()));
            case "!=":
                return values.stream().noneMatch(value -> StringUtils.equalsIgnoreCase(value, propertyValue.getCode().toString()));
            default:
                return false;
        }
    }

    private boolean computeItemForList(List<Object> propertyValue, List<String> values, String operator) {
        switch (operator) {
            case "in":
                return new HashSet<>(values).containsAll(propertyValue.stream().map(Objects::toString).collect(Collectors.toSet()));
            case "contains":
                return propertyValue.stream().map(Objects::toString).collect(Collectors.toSet()).containsAll(values);
            default:
                return false;
        }
    }

    private <T extends Comparable<T>> boolean computeItemForSingle(T propertyValue, List<T> values, String operator) {
        switch (operator) {
            case "==":
                return values.stream().allMatch(propertyValue::equals);
            case "!=":
                return values.stream().noneMatch(propertyValue::equals);
            case ">":
                return values.stream().allMatch(value -> propertyValue.compareTo(value) > 0);
            case "<":
                return values.stream().allMatch(value -> propertyValue.compareTo(value) < 0);
            case ">=":
                return values.stream().allMatch(value -> propertyValue.compareTo(value) >= 0);
            case "<=":
                return values.stream().allMatch(value -> propertyValue.compareTo(value) <= 0);
            case "in":
                return values.stream().anyMatch(propertyValue::equals);
            default:
                return false;
        }
    }

    private Optional<Object> getPropertyValue(Map<String, Object> processVariables, String keys) {
        return MapUtils.isEmpty(processVariables) ? Optional.empty() : getValueFromVariable(processVariables, keys);
    }

    private Optional<Object> getValueFromVariable(Map<String, Object> variables, String propertyKey) {
        List<String> propertyKeyList = Arrays.asList(StringUtils.split(propertyKey, "."));
        for (int i = 0; i < propertyKeyList.size(); i++) {
            String key = propertyKeyList.get(i);
            if (i == propertyKeyList.size() - 1) {
                return Optional.ofNullable(variables.get(key));
            } else {
                Object value = variables.get(key);
                if (value instanceof Map) {
                    variables = (Map<String, Object>) value;
                } else {
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }
}
