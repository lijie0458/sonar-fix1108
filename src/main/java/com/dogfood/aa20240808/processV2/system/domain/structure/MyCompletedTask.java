package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.time.ZonedDateTime;
import java.util.List;

public class MyCompletedTask {
    public String taskId;
    public String procInstTitle;
    public String procDefTitle;
    public String procInstInitiator;
    public ZonedDateTime procInstStartTime;
    public List<CurrNode> procInstCurrNodes;
    // 新增任务标题
    public String taskTitle;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    public List<CurrNode> getProcInstCurrNodes() {
        return procInstCurrNodes;
    }

    public void setProcInstCurrNodes(List<CurrNode> procInstCurrNodes) {
        this.procInstCurrNodes = procInstCurrNodes;
    }

    public ZonedDateTime getProcInstStartTime() {
        return procInstStartTime;
    }

    public void setProcInstStartTime(ZonedDateTime procInstStartTime) {
        this.procInstStartTime = procInstStartTime;
    }

    public String getProcInstInitiator() {
        return procInstInitiator;
    }

    public void setProcInstInitiator(String procInstInitiator) {
        this.procInstInitiator = procInstInitiator;
    }


    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
}
