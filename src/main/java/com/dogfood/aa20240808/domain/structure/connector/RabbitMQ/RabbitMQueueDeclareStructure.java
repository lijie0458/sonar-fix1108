package com.dogfood.aa20240808.domain.structure.connector.RabbitMQ; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class RabbitMQueueDeclareStructure {

    public String queueName;

    public Boolean durable;

    public Boolean exclusive;

    public Boolean autoDelete;

    public String getQueueName() {
        return queueName;
    } 

    public void setQueueName(String queueName) {
        this.queueName = queueName; 
    } 

    public Boolean getDurable() {
        return durable;
    } 

    public void setDurable(Boolean durable) {
        this.durable = durable; 
    } 

    public Boolean getExclusive() {
        return exclusive;
    } 

    public void setExclusive(Boolean exclusive) {
        this.exclusive = exclusive; 
    } 

    public Boolean getAutoDelete() {
        return autoDelete;
    } 

    public void setAutoDelete(Boolean autoDelete) {
        this.autoDelete = autoDelete; 
    } 

}
