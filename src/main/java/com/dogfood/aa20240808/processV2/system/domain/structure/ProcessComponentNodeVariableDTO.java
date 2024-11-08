package com.dogfood.aa20240808.processV2.system.domain.structure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessComponentNodeVariableDTO {
    public String nodeTitle;
    public String nodeDesc;
    public List<String> nodeParticipants;
    public List<String> nodeAssignees;
    public ZonedDateTime nodeStartTime;
    public ZonedDateTime nodeEndTime;
    public String nodeOutcome;

    public List<String> taskCompleteUser;
    public String taskId;
    public ZonedDateTime taskStartTime;
    public ZonedDateTime taskEndTime;
    public String taskOutcome;
}