package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.time.ZonedDateTime;

public class ProcInstRecord {
    public String nodeTitle;
    public String userName;
    public ZonedDateTime recordCreatedTime;
    public String nodeOperationComment;
    public String nodeOperation;
    public String nodeOperationDisplayText;

    public String getNodeTitle() {
        return nodeTitle;
    }

    public void setNodeTitle(String nodeTitle) {
        this.nodeTitle = nodeTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ZonedDateTime getRecordCreatedTime() {
        return recordCreatedTime;
    }

    public void setRecordCreatedTime(ZonedDateTime recordCreatedTime) {
        this.recordCreatedTime = recordCreatedTime;
    }

    public String getNodeOperationComment() {
        return nodeOperationComment;
    }

    public void setNodeOperationComment(String nodeOperationComment) {
        this.nodeOperationComment = nodeOperationComment;
    }

    public String getNodeOperation() {
        return nodeOperation;
    }

    public void setNodeOperation(String nodeOperation) {
        this.nodeOperation = nodeOperation;
    }

    public String getNodeOperationDisplayText() {
        return nodeOperationDisplayText;
    }

    public void setNodeOperationDisplayText(String nodeOperationDisplayText) {
        this.nodeOperationDisplayText = nodeOperationDisplayText;
    }
}
