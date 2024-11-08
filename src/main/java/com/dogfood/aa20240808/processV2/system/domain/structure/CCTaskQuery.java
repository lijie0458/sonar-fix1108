package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.time.ZonedDateTime;

public class CCTaskQuery {
    public String userName;
    public String procDefKey;
    public String procInstInitiator;
    public ZonedDateTime procInstStartTimeBefore;
    public ZonedDateTime procInstStartTimeAfter;
    public Integer offset;
    public Boolean viewed;
    public String variableName;
    public Long page;
    public Long size;
    public String search;
    public String sort;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getProcInstInitiator() {
        return procInstInitiator;
    }

    public void setProcInstInitiator(String procInstInitiator) {
        this.procInstInitiator = procInstInitiator;
    }

    public ZonedDateTime getProcInstStartTimeBefore() {
        return procInstStartTimeBefore;
    }

    public void setProcInstStartTimeBefore(ZonedDateTime procInstStartTimeBefore) {
        this.procInstStartTimeBefore = procInstStartTimeBefore;
    }

    public ZonedDateTime getProcInstStartTimeAfter() {
        return procInstStartTimeAfter;
    }

    public void setProcInstStartTimeAfter(ZonedDateTime procInstStartTimeAfter) {
        this.procInstStartTimeAfter = procInstStartTimeAfter;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Boolean getViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
