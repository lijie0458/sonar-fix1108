package com.dogfood.aa20240808.domain.structure.connector.RabbitMQ; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class RabbitMQExchangeDeclareStructure {

    public String exchange;

    public String exchangeType;

    public Boolean durable;

    public Boolean autoDelete;

    public Boolean internal1;

    public String getExchange() {
        return exchange;
    } 

    public void setExchange(String exchange) {
        this.exchange = exchange; 
    } 

    public String getExchangeType() {
        return exchangeType;
    } 

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType; 
    } 

    public Boolean getDurable() {
        return durable;
    } 

    public void setDurable(Boolean durable) {
        this.durable = durable; 
    } 

    public Boolean getAutoDelete() {
        return autoDelete;
    } 

    public void setAutoDelete(Boolean autoDelete) {
        this.autoDelete = autoDelete; 
    } 

    public Boolean getInternal1() {
        return internal1;
    } 

    public void setInternal1(Boolean internal1) {
        this.internal1 = internal1; 
    } 

}
