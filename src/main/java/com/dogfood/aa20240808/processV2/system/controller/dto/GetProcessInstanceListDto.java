package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class GetProcessInstanceListDto implements Serializable {
    public String processInstanceId;
    public String startBy;
    public Boolean finished;
    public String processDefName;
    public ZonedDateTime startTimeBefore;
    public ZonedDateTime startTimeAfter;
    public ZonedDateTime endTimeBefore;
    public ZonedDateTime endTimeAfter;
    public Long page = 1L;
    public Long size = 10L;
    public String sort;
    public String order;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getStartBy() {
        return startBy;
    }

    public void setStartBy(String startBy) {
        this.startBy = startBy;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }

    public String getProcessDefName() {
        return processDefName;
    }

    public void setProcessDefName(String processDefName) {
        this.processDefName = processDefName;
    }

    public ZonedDateTime getStartTimeBefore() {
        return startTimeBefore;
    }

    public void setStartTimeBefore(ZonedDateTime startTimeBefore) {
        this.startTimeBefore = startTimeBefore;
    }

    public ZonedDateTime getStartTimeAfter() {
        return startTimeAfter;
    }

    public void setStartTimeAfter(ZonedDateTime startTimeAfter) {
        this.startTimeAfter = startTimeAfter;
    }

    public ZonedDateTime getEndTimeBefore() {
        return endTimeBefore;
    }

    public void setEndTimeBefore(ZonedDateTime endTimeBefore) {
        this.endTimeBefore = endTimeBefore;
    }

    public ZonedDateTime getEndTimeAfter() {
        return endTimeAfter;
    }

    public void setEndTimeAfter(ZonedDateTime endTimeAfter) {
        this.endTimeAfter = endTimeAfter;
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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
