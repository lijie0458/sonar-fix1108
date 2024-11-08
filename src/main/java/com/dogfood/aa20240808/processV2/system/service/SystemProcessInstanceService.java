package com.dogfood.aa20240808.processV2.system.service;

import com.dogfood.aa20240808.processV2.system.domain.structure.*;
import com.dogfood.aa20240808.processV2.system.util.PageOf;
import org.flowable.engine.history.HistoricProcessInstance;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;


public interface SystemProcessInstanceService {

    public PageOf<HistoricProcessInstance> getProcessInstancePageOf(String processInstanceId, String startBy, Boolean finished, String processDefUniqueKey,
                                                                  ZonedDateTime procInstStartTimeBefore, ZonedDateTime procInstStartTimeAfter, ZonedDateTime endTimeBefore,
                                                                  ZonedDateTime endTimeAfter, Long page, Long size, String sort, String order, String search);

    public List<ProcInst> getProcessInstanceList(String processInstanceId, String startBy, Boolean finished, String processDefUniqueKey,
                                                 ZonedDateTime procInstStartTimeBefore, ZonedDateTime procInstStartTimeAfter, ZonedDateTime endTimeBefore,
                                                 ZonedDateTime endTimeAfter, Long page, Long size, String sort, String order, String search);


    public ProcInst getProcessInstance(String processInstanceId);

    public void endProcessInstance(String processInstanceId);

    public ProcInstGraph getProcInstGraph(String processInstanceId);

    public Map<String, Object> getProcessInstanceParams(String processInstanceId);

    /**
     * 获取当前实例运行中节点列表
     * @param processInstanceId
     * @return
     */
    public List<CurrNode> getCurrentNodeList(String processInstanceId);
}
