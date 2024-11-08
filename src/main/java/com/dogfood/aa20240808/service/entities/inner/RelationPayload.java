package com.dogfood.aa20240808.service.entities.inner;

import com.dogfood.aa20240808.repository.ReferenceHandleMapper;

public class RelationPayload {
    private String beRefProperty;
    private Class<? extends ReferenceHandleMapper> refEntityMapperClass;
    private String refProperty;
    private String delRule;

    public RelationPayload(String beRefProperty, Class<? extends ReferenceHandleMapper> refEntityMapperClass, String refProperty, String delRule) {
        this.beRefProperty = beRefProperty;
        this.refEntityMapperClass = refEntityMapperClass;
        this.refProperty = refProperty;
        this.delRule = delRule;
    }

    public String getBeRefProperty() {
        return beRefProperty;
    }

    public Class<? extends ReferenceHandleMapper> getRefEntityMapperClass() {
        return refEntityMapperClass;
    }

    public String getRefProperty() {
        return refProperty;
    }

    public String getDelRule() {
        return delRule;
    }


}
