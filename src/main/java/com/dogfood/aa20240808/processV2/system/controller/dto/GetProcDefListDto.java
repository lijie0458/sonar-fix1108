package com.dogfood.aa20240808.processV2.system.controller.dto;

import java.io.Serializable;

public class GetProcDefListDto implements Serializable {
    public String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
