package com.dogfood.aa20240808.processV2.system.aspect;

public enum ProcessRecordAction {
    LAUNCH("launch"),
    APPROVE("approve"),
    REJECT("reject"),
    SUBMIT("submit"),
    REASSIGN("reassign"),
    REVERT("revert"),
    WITHDRAW("withdraw"),
    TERMINATE("terminate"),
    ADDSIGN("addSign"),
    CC("cc"),
    END("end");

    private String value;

    ProcessRecordAction(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }
}
