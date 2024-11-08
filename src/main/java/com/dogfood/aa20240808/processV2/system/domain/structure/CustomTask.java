package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.time.ZonedDateTime;

public class CustomTask {
    public String id;
    public String taskId;
    public String processInstanceId;
    public String taskTitle;
    public String procInstTitle;
    public String procDefTitle;
    public String procInstInitiator;
    public ZonedDateTime procInstStartTime;
    public String candidators;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getProcInstTitle() {
        return procInstTitle;
    }

    public void setProcInstTitle(String procInstTitle) {
        this.procInstTitle = procInstTitle;
    }

    public String getProcDefTitle() {
        return procDefTitle;
    }

    public void setProcDefTitle(String procDefTitle) {
        this.procDefTitle = procDefTitle;
    }

    public String getProcInstInitiator() {
        return procInstInitiator;
    }

    public void setProcInstInitiator(String procInstInitiator) {
        this.procInstInitiator = procInstInitiator;
    }

    public ZonedDateTime getProcInstStartTime() {
        return procInstStartTime;
    }

    public void setProcInstStartTime(ZonedDateTime procInstStartTime) {
        this.procInstStartTime = procInstStartTime;
    }
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getCandidators() {
        return candidators;
    }

    public void setCandidators(String candidators) {
        this.candidators = candidators;
    }
}
