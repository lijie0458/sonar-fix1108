package com.dogfood.aa20240808.processV2.system.service;

import com.netease.lowcode.auth.domain.LCAPUser;
import com.dogfood.aa20240808.processV2.system.domain.structure.*;
import com.dogfood.aa20240808.processV2.system.util.PageOf;
import org.apache.commons.lang3.tuple.Pair;
import org.flowable.bpmn.model.UserTask;
import org.flowable.task.api.history.HistoricTaskInstance;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;


public interface SystemTaskInstanceService {

    public PageOf<HistoricTaskInstance> getHistoryTaskInstanceList(String taskId, String userName, String processInstanceId, List<String> processInstanceIdIn,
                                                                   String processDefUniqueKey, String taskDefName, String category, ZonedDateTime createTimeBefore,
                                                                   ZonedDateTime createTimeAfter, ZonedDateTime endTimeBefore, ZonedDateTime endTimeAfter,
                                                                   Long page, Long size, String sort, String order, Boolean finished);

    public PageOf<HistoricTaskInstance> getRunTaskInstanceList(String taskId, String userName, String processInstanceId, List<String> processInstanceIdIn,
                                               String processDefUniqueKey, String taskDefName, String category, ZonedDateTime createTimeBefore,
                                               ZonedDateTime createTimeAfter, ZonedDateTime endTimeBefore, ZonedDateTime endTimeAfter,
                                               Long page, Long size, String sort, String order, Boolean latest);

    PageOf<HistoricTaskInstance> getTaskInstanceList(String taskId, String userName, String processInstanceId, List<String> processInstanceIdIn,
                                                        String processDefUniqueKey, String taskDefName, String category, ZonedDateTime createTimeBefore,
                                                        ZonedDateTime createTimeAfter, ZonedDateTime endTimeBefore, ZonedDateTime endTimeAfter,
                                                        Long page, Long size, String sort, String order, Boolean finished);

    public TaskInst getTaskInstance(String taskId);

    public String getTaskDestinationUrl(String taskId, String frontendKey);

    public void reassignTask(String taskId, String userForReassign);

    public void approveTask(String procDefKey, String taskId, Map<String, Object> data, String comment);

    public void rejectTask(String procDefKey, String taskId, Map<String, Object> data, String comment);

    public void submitTask(String procDefKey, String taskId, Map<String, Object> data);

    public Map<String, Object> getTaskInstanceForm(String processInstanceId);

    public String getTaskFormDef(String taskId);

    /**
     * 根据taskId获取task所在node的处理人，包括node已处理和未处理task
     * 注意：
     * 1. 或签只会产生一个task，candidate user会有多个。转派和加签都是处理的candidate user
     * 2. 会签会导致同一个node一次性并行分裂出多个task，会签会与转派、加签正交。转派：转移task candidate user；加签：新添加一个execution task
     * 3. 依次审批也会导致一个node分裂出多个task，但task会依次产生，不会一次性产生，区别于会签。加签：依次产生的task，加签实际上添加了task candidate user
     * @param taskId
     * @return
     */
    public List<String> getNodeProcessUsersByTask(String taskId);

    public List<String> getCandidateUsers(String taskId);

    public void revertTask(String taskId, String targetTaskDefinitionName);

    public Pair<HistoricTaskInstance, List<HistoricTaskInstance>> getCanWithdrawTask(String taskId);

    public void withdrawTask(String taskId);

    public List<LCAPUser> getTransferTargetUserList(String taskId);

    public List<LCAPUser> getUserList();

    public List<UserTask> getRejectableTaskList(String taskId);

    public void addSignTask(String taskId, String userForAddSign);

    void addSignTask(String taskId, String userForAddSign,String policyForAddSign);

    void viewCCTask(String taskId);

    List<CustomTask> getCustomTaskList(CustomTaskQuery query);

    List<CustomTask> selectCustomRunTaskList(CustomTaskQuery query);

    Long count(CustomTaskQuery query);

    Long countRun(CustomTaskQuery query);

    List<CustomTask> getCCTaskList(CCTaskQuery query);

    Long countCCTask(CCTaskQuery query);

    /**
     * 获取操作权限
     * @param taskId
     * @return
     */
    List<TaskOperationPermission> getTaskOperationPermissions(String taskId);

    /**
     * 流程图组件
     * @param taskId
     * @return
     */
    ProcInstGraph getProcInstGraph(String taskId);


    List<DataPropertyPermission> getDataPropertyPermissions(String taskId);

    List<FormDataPropertyPermission> getFormDataPropertyPermissions(String taskId);

    /**
     * 获取流程实例参数
     * @param taskId
     * @return
     */
    Map<String, Object> getProcVariable(String taskId);
}
