package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.util.List;

public class CurrNode {
    public String currNodeTitle;
    public List<String> currNodeParticipants;
    public String taskId;

    public CurrNode() {
    }

    public CurrNode(String currNodeTitle, List<String> currNodeParticipants) {
        this.currNodeTitle = currNodeTitle;
        this.currNodeParticipants = currNodeParticipants;
    }


    public CurrNode(String currNodeTitle, List<String> currNodeParticipants,String taskId) {
        this.currNodeTitle = currNodeTitle;
        this.currNodeParticipants = currNodeParticipants;
        this.taskId = taskId;
    }

    public String getCurrNodeTitle() {
        return currNodeTitle;
    }

    public void setCurrNodeTitle(String currNodeTitle) {
        this.currNodeTitle = currNodeTitle;
    }

    public List<String> getCurrNodeParticipants() {
        return currNodeParticipants;
    }

    public void setCurrNodeParticipants(List<String> currNodeParticipants) {
        this.currNodeParticipants = currNodeParticipants;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
