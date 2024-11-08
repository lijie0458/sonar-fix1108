package com.dogfood.aa20240808.processV2.system.domain.structure;

public enum MoveToAfterRejected {
    START("start", "开始节点"),
    END("end", "结束节点"),
    NEXT_NODE("nextNode", "下一节点");

    private final String value;
    private final String nodeName;

    MoveToAfterRejected(String value, String nodeName) {
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
