package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.io.Serializable;


public class TaskDef implements Serializable {
    public String name;
    public String title;
    public String description;
    public String processDefUniqueKey;
    public String emptyAssignee;
    public boolean skipEnabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProcessDefUniqueKey() {
        return processDefUniqueKey;
    }

    public void setProcessDefUniqueKey(String processDefUniqueKey) {
        this.processDefUniqueKey = processDefUniqueKey;
    }

    public String getEmptyAssignee() {
        return emptyAssignee;
    }

    public void setEmptyAssignee(String emptyAssignee) {
        this.emptyAssignee = emptyAssignee;
    }

    public boolean isSkipEnabled() {
        return skipEnabled;
    }

    public void setSkipEnabled(boolean skipEnabled) {
        this.skipEnabled = skipEnabled;
    }
}
