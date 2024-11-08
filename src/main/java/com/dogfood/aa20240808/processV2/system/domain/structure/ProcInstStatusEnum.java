package com.dogfood.aa20240808.processV2.system.domain.structure;

import com.fasterxml.jackson.annotation.JsonValue;
import com.dogfood.aa20240808.domain.enumeration.BaseEnum;

public enum ProcInstStatusEnum implements BaseEnum<ProcInstStatusEnum, String> {
    Approved("Approved", "审批结束"),
    Approving("Approving", "审批中"),
            ;
    public final String code;
    public final String desc;

    ProcInstStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @JsonValue
    public String getJsonValue(){
        return this.code;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDesc() {
        return desc;
    }
}
