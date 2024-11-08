package com.dogfood.aa20240808.domain.structure.connector.RabbitMQ; 

import com.fasterxml.jackson.annotation.JsonAutoDetect; 
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility; 

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,getterVisibility = JsonAutoDetect.Visibility.NONE)
public class RabbitMQPropertiesStructure {

    public String contentType;

    public String contentEncoding;

    public Long deliveryMode;

    public Long priority;

    public String correlationId;

    public String replyTo;

    public String expiration;

    public String messageId;

    public String type;

    public String userId;

    public String appId;

    public String clusterId;

    public String getContentType() {
        return contentType;
    } 

    public void setContentType(String contentType) {
        this.contentType = contentType; 
    } 

    public String getContentEncoding() {
        return contentEncoding;
    } 

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding; 
    } 

    public Long getDeliveryMode() {
        return deliveryMode;
    } 

    public void setDeliveryMode(Long deliveryMode) {
        this.deliveryMode = deliveryMode; 
    } 

    public Long getPriority() {
        return priority;
    } 

    public void setPriority(Long priority) {
        this.priority = priority; 
    } 

    public String getCorrelationId() {
        return correlationId;
    } 

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId; 
    } 

    public String getReplyTo() {
        return replyTo;
    } 

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo; 
    } 

    public String getExpiration() {
        return expiration;
    } 

    public void setExpiration(String expiration) {
        this.expiration = expiration; 
    } 

    public String getMessageId() {
        return messageId;
    } 

    public void setMessageId(String messageId) {
        this.messageId = messageId; 
    } 

    public String getType() {
        return type;
    } 

    public void setType(String type) {
        this.type = type; 
    } 

    public String getUserId() {
        return userId;
    } 

    public void setUserId(String userId) {
        this.userId = userId; 
    } 

    public String getAppId() {
        return appId;
    } 

    public void setAppId(String appId) {
        this.appId = appId; 
    } 

    public String getClusterId() {
        return clusterId;
    } 

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId; 
    } 

}
