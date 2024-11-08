package com.dogfood.aa20240808.domain.structure.apis.codewave2024; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ApiReturnOfTest2Structure {

    public Long code;

    public String msg;

    public ResultStructure result;

    public Boolean success;

    public Long getCode() {
        return code;
    } 

    public void setCode(Long code) {
        this.code = code; 
    } 

    public String getMsg() {
        return msg;
    } 

    public void setMsg(String msg) {
        this.msg = msg; 
    } 

    public ResultStructure getResult() {
        return result;
    } 

    public void setResult(ResultStructure result) {
        this.result = result; 
    } 

    public Boolean getSuccess() {
        return success;
    } 

    public void setSuccess(Boolean success) {
        this.success = success; 
    } 

}
