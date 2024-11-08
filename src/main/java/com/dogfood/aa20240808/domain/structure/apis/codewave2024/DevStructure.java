package com.dogfood.aa20240808.domain.structure.apis.codewave2024; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class DevStructure {

    public String lcpDomain;

    public String getLcpDomain() {
        return lcpDomain;
    } 

    public void setLcpDomain(String lcpDomain) {
        this.lcpDomain = lcpDomain; 
    } 

}
