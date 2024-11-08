package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;

public class GetProcVariableDto implements Serializable {
    public String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}