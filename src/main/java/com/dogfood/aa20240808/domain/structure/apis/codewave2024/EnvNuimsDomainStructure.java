package com.dogfood.aa20240808.domain.structure.apis.codewave2024; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class EnvNuimsDomainStructure {

    public String dev;

    public String online;

    public String getDev() {
        return dev;
    } 

    public void setDev(String dev) {
        this.dev = dev; 
    } 

    public String getOnline() {
        return online;
    } 

    public void setOnline(String online) {
        this.online = online; 
    } 

}
