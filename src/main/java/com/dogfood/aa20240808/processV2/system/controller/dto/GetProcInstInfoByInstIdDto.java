package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;

public class GetProcInstInfoByInstIdDto implements Serializable {
    public String procInstId;

    public String getProcInstId() {
        return procInstId;
    }

    public void setProcInstId(String procInstId) {
        this.procInstId = procInstId;
    }
}