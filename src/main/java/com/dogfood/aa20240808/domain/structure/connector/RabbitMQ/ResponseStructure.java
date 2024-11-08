package com.dogfood.aa20240808.domain.structure.connector.RabbitMQ; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ResponseStructure {

    public Boolean success;

    public String msg;

    public String errors;

    public Boolean getSuccess() {
        return success;
    } 

    public void setSuccess(Boolean success) {
        this.success = success; 
    } 

    public String getMsg() {
        return msg;
    } 

    public void setMsg(String msg) {
        this.msg = msg; 
    } 

    public String getErrors() {
        return errors;
    } 

    public void setErrors(String errors) {
        this.errors = errors; 
    } 

}
