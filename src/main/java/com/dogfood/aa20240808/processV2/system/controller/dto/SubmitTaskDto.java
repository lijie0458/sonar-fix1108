package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;
import java.util.Map;

public class SubmitTaskDto implements Serializable {
    public String procDefKey;
    public String taskId;
    public Map<String, Object> data;

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}