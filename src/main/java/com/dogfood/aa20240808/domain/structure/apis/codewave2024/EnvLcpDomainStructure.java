package com.dogfood.aa20240808.domain.structure.apis.codewave2024; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class EnvLcpDomainStructure {

    public DevStructure dev;

    public OnlineStructure online;

    public DevStructure getDev() {
        return dev;
    } 

    public void setDev(DevStructure dev) {
        this.dev = dev; 
    } 

    public OnlineStructure getOnline() {
        return online;
    } 

    public void setOnline(OnlineStructure online) {
        this.online = online; 
    } 

}
