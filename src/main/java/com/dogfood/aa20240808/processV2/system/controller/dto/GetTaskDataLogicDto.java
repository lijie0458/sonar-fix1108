package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;

public class GetTaskDataLogicDto implements Serializable {
    public String taskId;
    public String procDefKey;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }
}