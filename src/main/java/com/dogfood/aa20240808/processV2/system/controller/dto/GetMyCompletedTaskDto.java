package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class GetMyCompletedTaskDto implements Serializable {
    public String procDefKey;
    public ZonedDateTime procInstStartTimeAfter;
    public ZonedDateTime procInstStartTimeBefore;
    public String procInstInitiator;
    public Long page;
    public Long size;
    public String search;

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public ZonedDateTime getProcInstStartTimeAfter() {
        return procInstStartTimeAfter;
    }

    public void setProcInstStartTimeAfter(ZonedDateTime procInstStartTimeAfter) {
        this.procInstStartTimeAfter = procInstStartTimeAfter;
    }

    public ZonedDateTime getProcInstStartTimeBefore() {
        return procInstStartTimeBefore;
    }

    public void setProcInstStartTimeBefore(ZonedDateTime procInstStartTimeBefore) {
        this.procInstStartTimeBefore = procInstStartTimeBefore;
    }

    public String getProcInstInitiator() {
        return procInstInitiator;
    }

    public void setProcInstInitiator(String procInstInitiator) {
        this.procInstInitiator = procInstInitiator;
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

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
