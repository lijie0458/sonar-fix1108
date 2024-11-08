package com.dogfood.aa20240808.processV2.system.service.logic;

public interface SystemProcessHandlingLogicService {

    /**
     * 同意
     * @param data
     * @param procDefKey
     * @param taskId
     * @param comment
     * @return
     */
    void approveTask(Object data, String procDefKey, String taskId, String comment);

    /**
     * 拒绝
     * @param data
     * @param procDefKey
     * @param taskId
     * @param comment
     * @return
     */
    void rejectTask(Object data, String procDefKey, String taskId, String comment);

    /**
     * 提交
     * @param data
     * @param procDefKey
     * @param taskId
     * @return
     */
    void submitTask(Object data, String procDefKey, String taskId);

    /**
     * 转派
     * @param taskId
     * @param userForReassign
     * @return
     */
    void reassignTask(String taskId, String userForReassign);

    /**
     * 回退
     * @param taskId
     * @return
     */
    void revertTask(String taskId);

    /**
     * 撤回
     * @param taskId
     * @return
     */
    void withdrawTask(String taskId);

    /**
     * 终止
     * @param processId
     * @return
     */
    void terminateProcess(String processId);

    /**
     * 加签
     * @param taskId
     * @param userForAddSign
     * @return
     */
    void addSignTask(String taskId, String userForAddSign);

    /**
     * 加签，支持前加签和后加签
     * @param taskId
     * @param userForAddSign
     * @return
     */
    void addSignTask(String taskId, String userForAddSign,String policyForAddSign);

    /**
     * 查看抄送任务
     * @param taskId
     */
    void viewCCTask(String taskId);
}
