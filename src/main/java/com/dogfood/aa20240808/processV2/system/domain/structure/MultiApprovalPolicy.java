package com.dogfood.aa20240808.processV2.system.domain.structure;

public class MultiApprovalPolicy {
    public String concept;
    public Integer passPercent;

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public Integer getPassPercent() {
        return passPercent;
    }

    public void setPassPercent(Integer passPercent) {
        this.passPercent = passPercent;
    }
}
