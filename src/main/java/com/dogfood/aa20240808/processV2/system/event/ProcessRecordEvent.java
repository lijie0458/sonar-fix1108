package com.dogfood.aa20240808.processV2.system.event;

import java.util.Date;

public class ProcessRecordEvent {

   private String processInstanceId;
   private String taskId;
   private String userName;
   private Date recordCreatedTime;
   private String action;
   private String message;
   private String fullMessage;

   public ProcessRecordEvent(String processInstanceId, String taskId, String userName, Date recordCreatedTime,
                             String message, String fullMessage){
      this.processInstanceId = processInstanceId;
      this.taskId = taskId;
      this.userName = userName;
      this.recordCreatedTime = recordCreatedTime;
      this.message = message;
      this.fullMessage = fullMessage;
   }

   public ProcessRecordEvent(){}

   public String getProcessInstanceId() {
      return processInstanceId;
   }

   public void setProcessInstanceId(String processInstanceId) {
      this.processInstanceId = processInstanceId;
   }

   public String getTaskId() {
      return taskId;
   }

   public void setTaskId(String taskId) {
      this.taskId = taskId;
   }

   public String getUserName() {
      return userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public Date getRecordCreatedTime() {
      return recordCreatedTime;
   }

   public void setRecordCreatedTime(Date recordCreatedTime) {
      this.recordCreatedTime = recordCreatedTime;
   }

   public String getAction() {
      return action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public String getFullMessage() {
      return fullMessage;
   }

   public void setFullMessage(String fullMessage) {
      this.fullMessage = fullMessage;
   }

   @Override
   public String toString() {
      return "CommentEvent{" + "userName='" + userName + '\'' + ", taskId='" + taskId + '\''
              + ", fullMessage='" + fullMessage + '\'' + ", message='" + message + '\'' + ", action='" + action + '\''
              + ", processInstanceId='" + processInstanceId + '\'' + ", recordCreatedTime=" + recordCreatedTime + '}';
   }
}
