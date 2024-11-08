package com.dogfood.aa20240808.domain.entities; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; 
import com.dogfood.aa20240808.annotation.Label; 
import com.dogfood.aa20240808.config.DateTimeFormatConfiguration; 
import java.time.ZonedDateTime; 
import com.dogfood.aa20240808.domain.enumeration.OtherReturnStorageStateEnumEnum; 
import javax.validation.constraints.NotNull; 
import com.fasterxml.jackson.annotation.JsonFormat; 

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class OtherReturnStorageEntity {

    @Label("其他入库退回单号")
    @NotNull
    public String returnCode;

    @Label("创建时间")
    @JsonFormat(pattern = DateTimeFormatConfiguration.ZONED_DATETIME_FORMAT,timezone = DateTimeFormatConfiguration.DEFAULT_TIMEZONE)
    public ZonedDateTime createdTime;

    @Label("创建者")
    public String createdBy;

    @Label("更新时间")
    @JsonFormat(pattern = DateTimeFormatConfiguration.ZONED_DATETIME_FORMAT,timezone = DateTimeFormatConfiguration.DEFAULT_TIMEZONE)
    public ZonedDateTime updatedTime;

    @Label("更新者")
    public String updatedBy;

    @Label("状态")
    public OtherReturnStorageStateEnumEnum state;

    @Label("其他入库单号")
    public String otherInSCode;

    public String getReturnCode() {
        return returnCode;
    } 

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode; 
    } 

    public ZonedDateTime getCreatedTime() {
        return createdTime;
    } 

    public void setCreatedTime(ZonedDateTime createdTime) {
        this.createdTime = createdTime; 
    } 

    public String getCreatedBy() {
        return createdBy;
    } 

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy; 
    } 

    public ZonedDateTime getUpdatedTime() {
        return updatedTime;
    } 

    public void setUpdatedTime(ZonedDateTime updatedTime) {
        this.updatedTime = updatedTime; 
    } 

    public String getUpdatedBy() {
        return updatedBy;
    } 

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy; 
    } 

    public OtherReturnStorageStateEnumEnum getState() {
        return state;
    } 

    public void setState(OtherReturnStorageStateEnumEnum state) {
        this.state = state; 
    } 

    public String getOtherInSCode() {
        return otherInSCode;
    } 

    public void setOtherInSCode(String otherInSCode) {
        this.otherInSCode = otherInSCode; 
    } 

}
