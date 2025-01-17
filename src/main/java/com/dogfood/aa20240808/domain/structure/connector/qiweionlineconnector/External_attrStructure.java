package com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class External_attrStructure {

    public com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.Web2Structure web;
    public String name;
    public com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.Text3Structure text;
    public Long type;
    public com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.MiniprogramStructure miniprogram;
    public com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.Web2Structure getWeb() {
        return web;
    } 

    public void setWeb(com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.Web2Structure web) {
        this.web = web; 
    } 

    public String getName() {
        return name;
    } 

    public void setName(String name) {
        this.name = name; 
    } 

    public com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.Text3Structure getText() {
        return text;
    } 

    public void setText(com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.Text3Structure text) {
        this.text = text; 
    } 

    public Long getType() {
        return type;
    } 

    public void setType(Long type) {
        this.type = type; 
    } 

    public com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.MiniprogramStructure getMiniprogram() {
        return miniprogram;
    } 

    public void setMiniprogram(com.dogfood.aa20240808.domain.structure.connector.qiweionlineconnector.MiniprogramStructure miniprogram) {
        this.miniprogram = miniprogram; 
    } 


}
