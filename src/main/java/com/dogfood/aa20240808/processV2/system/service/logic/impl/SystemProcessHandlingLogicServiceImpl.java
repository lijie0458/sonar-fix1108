package com.dogfood.aa20240808.processV2.system.service.logic.impl;

import com.dogfood.aa20240808.processV2.system.aspect.ProcessRecord;
import com.dogfood.aa20240808.processV2.system.aspect.ProcessRecordAction;
import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.service.SystemProcessInstanceService;
import com.dogfood.aa20240808.processV2.system.service.SystemTaskInstanceService;
import com.dogfood.aa20240808.processV2.system.service.logic.SystemProcessHandlingLogicService;
import com.dogfood.aa20240808.processV2.system.util.SystemProcessUtil;
import org.flowable.bpmn.model.UserTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemProcessHandlingLogicServiceImpl implements SystemProcessHandlingLogicService {

    @Autowired
    private SystemTaskInstanceService systemTaskInstanceService;

    @Autowired
    private SystemProcessInstanceService systemProcessInstanceService;

    private static final Long INTEGER_MAX_VALUE = 2147483647L;

    @Override
    public void approveTask(Object data, String procDefKey, String taskId, String comment) {
        systemTaskInstanceService.approveTask(procDefKey, taskId, SystemProcessUtil.dataToMap(data), comment);
    }

    @Override
    public void rejectTask(Object data, String procDefKey, String taskId, String comment) {
        systemTaskInstanceService.rejectTask(procDefKey, taskId, SystemProcessUtil.dataToMap(data), comment);
    }

    @Override
    public void submitTask(Object data, String procDefKey, String taskId) {
        systemTaskInstanceService.submitTask(procDefKey, taskId, SystemProcessUtil.dataToMap(data));
    }

    @Override
    @ProcessRecord(action = ProcessRecordAction.REASSIGN)
    public void reassignTask(String taskId, String userForReassign) {
        systemTaskInstanceService.reassignTask(taskId, userForReassign);
    }

    @Override
    @ProcessRecord(action = ProcessRecordAction.REVERT)
    public void revertTask(String taskId) {
        List<UserTask> userTaskList = systemTaskInstanceService.getRejectableTaskList(taskId);
        UserTask targetTask = new UserTask();
        if (userTaskList.size() > 1) {
            targetTask = userTaskList
                    .stream()
                    .filter(task -> task.getCategory() != null && !task.getCategory().equals("InitiateTask"))
                    .findFirst()
                    .orElseThrow(() -> new ProcessException("No target task can be reverted!"));
        } else if (userTaskList.size() == 1) {
            targetTask = userTaskList.get(0);
        } else {
            throw new ProcessException("No target task can be reverted!");
        }
        systemTaskInstanceService.revertTask(taskId, targetTask.getId());
    }

    @Override
    @ProcessRecord(action = ProcessRecordAction.WITHDRAW)
    public void withdrawTask(String taskId) {
        systemTaskInstanceService.withdrawTask(taskId);
    }

    @Override
    public void terminateProcess(String processId) {
        systemProcessInstanceService.endProcessInstance(processId);
    }

    @Override
    @ProcessRecord(action = ProcessRecordAction.ADDSIGN)
    public void addSignTask(String taskId, String userForAddSign) {
        systemTaskInstanceService.addSignTask(taskId, userForAddSign);
    }

    @Override
    @ProcessRecord(action = ProcessRecordAction.ADDSIGN)
    public void addSignTask(String taskId, String userForAddSign, String policyForAddSign) {
        systemTaskInstanceService.addSignTask(taskId, userForAddSign,policyForAddSign);
    }

    @Override
    public void viewCCTask(String taskId) {
        systemTaskInstanceService.viewCCTask(taskId);
    }
}
