package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.time.ZonedDateTime;

public class CustomTaskQuery {
    public String userName;
    public String procDefKey;
    public String procInstInitiator;
    public ZonedDateTime procInstStartTimeBefore;
    public ZonedDateTime procInstStartTimeAfter;
    public Integer offset;
    public Boolean finished;
    public Long page;
    public Long size;
    public String search;
    public String sort;



}
