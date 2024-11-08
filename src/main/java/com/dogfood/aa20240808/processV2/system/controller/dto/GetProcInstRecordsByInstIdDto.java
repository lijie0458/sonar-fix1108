package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;

public class GetProcInstRecordsByInstIdDto implements Serializable {
    public String procInstId;
    public Long page;
    public Long size;

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
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