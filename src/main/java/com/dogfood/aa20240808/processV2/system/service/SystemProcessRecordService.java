package com.dogfood.aa20240808.processV2.system.service;

import com.dogfood.aa20240808.processV2.system.event.ProcessRecordEvent;
import org.flowable.engine.task.Comment;

import java.util.List;

import java.util.Map;

public interface SystemProcessRecordService {

    List<Comment> getProcessInstanceRecord(String processInstanceId);

    Map<String, String> getOperationPermission(String taskId, String operation);

    void addProcessRecord(ProcessRecordEvent event);
}
