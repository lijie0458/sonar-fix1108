package com.dogfood.aa20240808.domain.structure.connector.RabbitMQ; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 
import com.netease.cloud.extension.model.RabbitMQueueDeclare; 
import com.netease.cloud.extension.model.RabbitMQProperties; 
import com.netease.cloud.extension.model.RabbitMQExchangeDeclare; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class SendModelStructure {

    public RabbitMQExchangeDeclare exchangeDeclare;

    public RabbitMQueueDeclare declare;

    public RabbitMQProperties properties;

    public String msg;

    public RabbitMQExchangeDeclare getExchangeDeclare() {
        return exchangeDeclare;
    } 

    public void setExchangeDeclare(RabbitMQExchangeDeclare exchangeDeclare) {
        this.exchangeDeclare = exchangeDeclare; 
    } 

    public RabbitMQueueDeclare getDeclare() {
        return declare;
    } 

    public void setDeclare(RabbitMQueueDeclare declare) {
        this.declare = declare; 
    } 

    public RabbitMQProperties getProperties() {
        return properties;
    } 

    public void setProperties(RabbitMQProperties properties) {
        this.properties = properties; 
    } 

    public String getMsg() {
        return msg;
    } 

    public void setMsg(String msg) {
        this.msg = msg; 
    } 

}
