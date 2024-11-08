package com.dogfood.aa20240808.domain.structure.apis.codewave2024; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class DocumentCenterStructure {

    public Boolean isLocalDeployment;

    public String deliveryMode;

    public String url;

    public Boolean getIsLocalDeployment() {
        return isLocalDeployment;
    } 

    public void setIsLocalDeployment(Boolean isLocalDeployment) {
        this.isLocalDeployment = isLocalDeployment; 
    } 

    public String getDeliveryMode() {
        return deliveryMode;
    } 

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode; 
    } 

    public String getUrl() {
        return url;
    } 

    public void setUrl(String url) {
        this.url = url; 
    } 

}
