package com.dogfood.aa20240808.processV2.system.command;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.dynamic.DynamicUserTaskBuilder;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.context.Context;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

public class AddProcessTaskCmd implements Command<ExecutionEntity> {
    public static final String COMMENT_TYPE = "process_record";
    protected TaskEntity taskEntity;
    protected String userForAddSign;
    protected DynamicUserTaskBuilder dynamicUserTaskBuilder;
    protected BpmnModel bpmnModel;


    public AddProcessTaskCmd(TaskEntity taskEntity, String userForAddSign, DynamicUserTaskBuilder dynamicUserTaskBuilder, BpmnModel bpmnModel) {
        this.taskEntity = taskEntity;
        this.userForAddSign = userForAddSign;
        this.dynamicUserTaskBuilder = dynamicUserTaskBuilder;
        this.bpmnModel = bpmnModel;

    }

    @Override
    public ExecutionEntity execute(CommandContext commandContext) {

        ExecutionEntity parentExecution = CommandContextUtil.getExecutionEntityManager().findById(taskEntity.getExecutionId());
        ExecutionEntity execution = CommandContextUtil.getExecutionEntityManager().createChildExecution(parentExecution);
        FlowElement current = bpmnModel.getMainProcess().getFlowElement(taskEntity.getTaskDefinitionKey());
        execution.setCurrentFlowElement(current);
        CommandContextUtil.getActivityInstanceEntityManager().recordActivityStart(execution);
        Context.getAgenda().planContinueProcessOperation(execution);
        return execution;
    }
}
