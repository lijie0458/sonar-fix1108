package com.dogfood.aa20240808.domain.structure.connector.RabbitMQ; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import com.netease.cloud.extension.model.RabbitMQProperties; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class ReturnModelStructure {

    public Long code;

    public String msg;

    public String exchange;

    public String queueName;

    public RabbitMQProperties properties;

    public String content;

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

    public String getExchange() {
        return exchange;
    } 

    public void setExchange(String exchange) {
        this.exchange = exchange; 
    } 

    public String getQueueName() {
        return queueName;
    } 

    public void setQueueName(String queueName) {
        this.queueName = queueName; 
    } 

    public RabbitMQProperties getProperties() {
        return properties;
    } 

    public void setProperties(RabbitMQProperties properties) {
        this.properties = properties; 
    } 

    public String getContent() {
        return content;
    } 

    public void setContent(String content) {
        this.content = content; 
    } 

}
