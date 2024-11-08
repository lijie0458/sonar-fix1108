package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.io.Serializable;
import java.util.Date;
public class TaskCommentReq implements Serializable {

    //流程节点
    public String curNode;
    //处理人
    public String userName;
    // 处理时间
    public Date recordCreatedTime;
    // 审批操作
    public String action;
    //审批意见
    public String message;
    // 流程任务id
    public String taskId;

    public String getCurNode() {
        return curNode;
    }

    public void setCurNode(String curNode) {
        this.curNode = curNode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getRecordCreatedTime() {
        return recordCreatedTime;
    }

    public void setRecordCreatedTime(Date recordCreatedTime) {
        this.recordCreatedTime = recordCreatedTime;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
