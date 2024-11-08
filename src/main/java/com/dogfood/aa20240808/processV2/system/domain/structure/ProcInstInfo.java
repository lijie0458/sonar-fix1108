package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.time.ZonedDateTime;
import java.util.List;

public class ProcInstInfo {
    public String procInstId;
    public String procInstInitiator;
    public ZonedDateTime procInstStartTime;
    public ZonedDateTime procInstEndTime;
    public ProcInstStatusEnum procInstStatus;
    public List<CurrNode> procInstCurrNodes;
    public String processDefinitionName;

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
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

    public ZonedDateTime getProcInstEndTime() {
        return procInstEndTime;
    }

    public void setProcInstEndTime(ZonedDateTime procInstEndTime) {
        this.procInstEndTime = procInstEndTime;
    }

    public ProcInstStatusEnum getProcInstStatus() {
        return procInstStatus;
    }

    public void setProcInstStatus(ProcInstStatusEnum procInstStatus) {
        this.procInstStatus = procInstStatus;
    }

    public List<CurrNode> getProcInstCurrNodes() {
        return procInstCurrNodes;
    }

    public void setProcInstCurrNodes(List<CurrNode> procInstCurrNodes) {
        this.procInstCurrNodes = procInstCurrNodes;
    }

    public String getProcessDefinitionName() {
        return processDefinitionName;
    }

    public void setProcessDefinitionName(String processDefinitionName) {
        this.processDefinitionName = processDefinitionName;
    }
}
