package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;

public class AddSignTaskDto implements Serializable {
    public String taskId;
    public String userForAddSign;
    public String policyForAddSign;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserForAddSign() {
        return userForAddSign;
    }

    public void setUserForAddSign(String userForAddSign) {
        this.userForAddSign = userForAddSign;
    }

    public String getPolicyForAddSign() {
        return policyForAddSign;
    }

    public void setPolicyForAddSign(String policyForAddSign) {
        this.policyForAddSign = policyForAddSign;
    }
}
