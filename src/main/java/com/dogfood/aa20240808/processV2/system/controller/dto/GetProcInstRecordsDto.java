package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;

public class GetProcInstRecordsDto implements Serializable {
    public String taskId;
    public Long page;
    public Long size;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}