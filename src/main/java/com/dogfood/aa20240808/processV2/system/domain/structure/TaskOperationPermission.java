package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.io.Serializable;

public class TaskOperationPermission implements Serializable {
    public String name;
    public Boolean operationEnabled;
    public String displayText;
    public Boolean commentRequired;
    public String moveTo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getOperationEnabled() {
        return operationEnabled;
    }

    public void setOperationEnabled(Boolean operationEnabled) {
        this.operationEnabled = operationEnabled;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Boolean getCommentRequired() {
        return commentRequired;
    }

    public void setCommentRequired(Boolean commentRequired) {
        this.commentRequired = commentRequired;
    }


    public String getMoveTo() {
        return moveTo;
    }

    public void setMoveTo(String moveTo) {
        this.moveTo = moveTo;
    }
}
