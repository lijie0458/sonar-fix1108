package com.dogfood.aa20240808.processV2.system.service.logic;

import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_0AD1F758234DAD21B49B8A003301836E;
import com.dogfood.aa20240808.domain.structure.anonymous.AnonymousStructure_489B441317D82681CAB7C33AB2650B61;
import com.dogfood.aa20240808.processV2.system.domain.structure.ProcInstInfo;
import com.dogfood.aa20240808.processV2.system.domain.structure.InstanceQuery;
import java.time.ZonedDateTime;

public interface SystemProcessInstanceSearchLogicService {

    /**
     * 查询流程信息组件详情
     * @param procInstId
     * @return
     */
    ProcInstInfo getProcInstInfoByInstId(String procInstId);

    /**
     * 获取流程实例审批记录
     * @param procInstId
     * @param page
     * @param size
     * @return
     */
    AnonymousStructure_0AD1F758234DAD21B49B8A003301836E getProcInstRecordsByInstId(String procInstId, Long page, Long size);

    AnonymousStructure_489B441317D82681CAB7C33AB2650B61 getProcessInstanceList(String processInstanceId, String startBy, ZonedDateTime startTimeBefore, ZonedDateTime startTimeAfter,Boolean finished, String processDefName,
                                                                                 Long page, Long size);
}
