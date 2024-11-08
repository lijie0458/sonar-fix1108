package com.dogfood.aa20240808.processV2.system.command;

import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.service.HistoricVariableService;
import org.flowable.variable.service.impl.HistoricVariableInstanceQueryImpl;
import org.flowable.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.flowable.variable.service.impl.types.BooleanType;

import java.util.Date;


public class ViewCCTaskCmd implements Command<Void> {

    private String taskId;

    public ViewCCTaskCmd(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        HistoricTaskInstance historicTaskInstance = CommandContextUtil.getHistoricTaskService().getHistoricTask(taskId);
        if (historicTaskInstance == null) {
            throw new ProcessException("The taskId cannot be empty!");
        }

        HistoricVariableInstanceQueryImpl query = new HistoricVariableInstanceQueryImpl(CommandContextUtil.getCommandContext(), CommandContextUtil.getProcessEngineConfiguration().getVariableServiceConfiguration());
        query.taskId(taskId);
        query.variableName(ProcessConstant.KEY_VARIABLE_CC_TASK_VIEWED_PREFIX + Authentication.getAuthenticatedUserId());
        HistoricVariableInstance historicVariableInstance = query.singleResult();

        if (historicVariableInstance != null) {
            // 表示已查阅过
            return null;
        }

        HistoricVariableService historicVariableService = CommandContextUtil.getHistoricVariableService();
        HistoricVariableInstanceEntity variableInstanceEntity = historicVariableService.createHistoricVariableInstance();
        variableInstanceEntity.setVariableType(new BooleanType());
        variableInstanceEntity.setTaskId(taskId);
        variableInstanceEntity.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());
        variableInstanceEntity.setExecutionId(historicTaskInstance.getExecutionId());
        variableInstanceEntity.setName(ProcessConstant.KEY_VARIABLE_CC_TASK_VIEWED_PREFIX + Authentication.getAuthenticatedUserId());
        variableInstanceEntity.setLongValue(1L);
        Date createTime = CommandContextUtil.getProcessEngineConfiguration().getClock().getCurrentTime();
        variableInstanceEntity.setCreateTime(createTime);
        variableInstanceEntity.setLastUpdatedTime(createTime);
        historicVariableService.insertHistoricVariableInstance(variableInstanceEntity);

        return null;
    }
}
