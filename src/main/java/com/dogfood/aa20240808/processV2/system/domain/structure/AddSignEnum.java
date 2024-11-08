package com.dogfood.aa20240808.processV2.system.domain.structure;

public enum AddSignEnum {
    PREVIOUS_ADD_SIGN_FROM("previousAddSignFrom", "发起前加签"),
    PREVIOUS("PreviousNode", "前加签"),
    CURRENT("CurrentNode", "当前加签"),
    SUBSEQUENT("NextNode", "后加签"),
    SUBSEQUENT_ADD_SIGN_FROM("subsequentAddSignFrom", "发起后加签");

    private final String value;
    private final String nodeName;

    AddSignEnum(String value, String nodeName) {
        this.value = value;
        this.nodeName = nodeName;
    }

    public String getValue() {
        return value;
    }

    public String getNodeName() {
        return nodeName;
    }
}
