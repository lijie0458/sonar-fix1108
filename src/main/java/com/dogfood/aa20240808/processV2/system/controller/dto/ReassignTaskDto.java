package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;

public class ReassignTaskDto implements Serializable {
    public String procDefKey;
    public String taskId;
    public String userForReassign;

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

    public String getUserForReassign() {
        return userForReassign;
    }

    public void setUserForReassign(String userForReassign) {
        this.userForReassign = userForReassign;
    }
}