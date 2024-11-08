package com.dogfood.aa20240808.processV2.system.domain.structure;

import java.io.Serializable;

public class ProcDef implements Serializable {
    public String procDefKey;
    public String procDefTitle;
    public String procDefDescription;
    public boolean suspended;

    public String getProcDefKey() {
        return procDefKey;
    }

    public void setProcDefKey(String procDefKey) {
        this.procDefKey = procDefKey;
    }

    public String getProcDefTitle() {
        return procDefTitle;
    }

    public void setProcDefTitle(String procDefTitle) {
        this.procDefTitle = procDefTitle;
    }

    public String getProcDefDescription() {
        return procDefDescription;
    }

    public void setProcDefDescription(String procDefDescription) {
        this.procDefDescription = procDefDescription;
    }

    public boolean isSuspended() {
        return suspended;
    }

    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }
}
