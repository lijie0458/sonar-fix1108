package com.dogfood.aa20240808.processV2.system.service.logic;

import com.netease.lowcode.auth.domain.LCAPUser;
import com.dogfood.aa20240808.domain.structure.anonymous.*;
import com.dogfood.aa20240808.processV2.system.domain.structure.*;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface SystemTaskInstanceSearchLogicService {

    /**
     * 查询我的待办
     * @param procDefKey
     * @param procInstStartTimeAfter
     * @param procInstStartTimeBefore
     * @param procInstInitiator
     * @param page
     * @param size
     * @param search
     * @return
     */
    AnonymousStructure_69DC2791E68E885605E69282096D7B98 getMyPendingTasks(String procDefKey, ZonedDateTime procInstStartTimeAfter, ZonedDateTime procInstStartTimeBefore,
                                                                          String procInstInitiator, Long page, Long size, String search);
    /**
     * 查询所有任务
     * @param procDefKey
     * @param procInstId
     * @param procInstInitiator
     * @param candidateUser
     * @param page
     * @param size
     * @return
     */
    AnonymousStructure_CE03ADE6642D2FE6022E57E8CC76B1D4 getTaskInstanceList(String procDefKey, String procInstId, String procInstInitiator, String candidateUser,Boolean finished, Long page, Long size);

    /**
     * 查询我的已办
     * @param procDefKey
     * @param procInstStartTimeAfter
     * @param procInstStartTimeBefore
     * @param procInstInitiator
     * @param page
     * @param size
     * @param search
     * @return
     */
    AnonymousStructure_11075684741EE4AE47B8541DE90A626D getMyCompletedTasks(String procDefKey, ZonedDateTime procInstStartTimeAfter, ZonedDateTime procInstStartTimeBefore,
                                                                            String procInstInitiator, Long page, Long size, String search);

    /**
     * 查询我的发起
     * @param procDefKey
     * @param procInstStartTimeAfter
     * @param procInstStartTimeBefore
     * @param procInstInitiator
     * @param page
     * @param size
     * @param search
     * @return
     */
    AnonymousStructure_5C7123AB13F7FFBC7A1BACBB189EA455 getMyInitiatedTasks(String procDefKey, ZonedDateTime procInstStartTimeAfter, ZonedDateTime procInstStartTimeBefore,
                                                                            String procInstInitiator, Long page, Long size, String search);

    /**
     * 查询我的抄送
     * @param procDefKey
     * @param procInstStartTimeAfter
     * @param procInstStartTimeBefore
     * @param procInstInitiator
     * @param viewed
     * @param page
     * @param size
     * @param search
     * @return
     */
    AnonymousStructure_20D310FF999ED65913EC896ACFD7CF3B getMyCCTasks(String procDefKey, ZonedDateTime procInstStartTimeAfter, ZonedDateTime procInstStartTimeBefore,
                                                                     String procInstInitiator, Boolean viewed, Long page, Long size, String search);

    /**
     * 查询任务的跳转页面链接
     * @param taskId
     * @return
     */
    String getTaskDestinationUrl(String taskId);

    /**
     * 查询流程定义名称列表
     * @param search
     * @return
     */
    List<ProcDef> getProcDefInfos(String search);

    /**
     * 查询流程实例发起人列表
     * @return
     */
    List<LCAPUser> getProcInstInitiators();

    /**
     * 查询流程信息组件详情
     * @param taskId
     * @return
     */
    ProcInstInfo getProcInstInfo(String taskId);

    /**
     * 获取操作权限
     * @param taskId
     * @return
     */
    List<TaskOperationPermission> getTaskOperationPermissions(String taskId);

    /**
     * 获取任务表单字段权限
     * @param taskId
     * @return
     */
    List<DataPropertyPermission> getDataPropertyPermissions(String taskId);

    /**
     * 新获取任务表单字段权限
     * @param taskId
     * @return
     */
    List<FormDataPropertyPermission> getFormDataPropertyPermissions(String taskId);

    /**
     * 后端逻辑获取表单数据
     * @param taskId
     * @return
     */
    <T> T getTaskData(String taskId, String procDefKey, Class <T> clazz);

    /**
     * 前端逻辑获取表单数据
     * @param taskId
     * @return
     */
    Map<String, Object> getTaskDataByFronted(String taskId, String procDefKey);

    /**
     * 获取流程实例审批记录
     * @param taskId
     * @param page
     * @param size
     * @return
     */
    AnonymousStructure_0AD1F758234DAD21B49B8A003301836E getProcInstRecords(String taskId, Long page, Long size);



    /**
     * 查询转派目标用户列表
     * @param taskId
     * @return
     */
    List<LCAPUser> getUsersForReassign(String taskId);

    /**
     * 查询加签目标用户列表
     * @param taskId
     * @return
     */
    List<LCAPUser> getUsersForAddSign(String taskId);

        /**
         * 查询任务详情
         * @param taskId
         * @return
         */
    TaskInst getTaskInfo(String taskId);

    /**
     * 查添加流程审批记录
     * @param curNode
     * @param userName
     * @param recordCreatedTime
     * @param action
     * @param message
     * @param taskId
     */
    void addTaskComment(String curNode,String userName,Date recordCreatedTime,String action,String message,String taskId);

    ProcInstGraph getProcInstGraph(String taskId);

    /**
     * 表单定义
     * @param taskId
     * @return
     */
    String getTaskFormDef(String taskId);

    Map<String, Object> getProcVariable(String taskId);
}
