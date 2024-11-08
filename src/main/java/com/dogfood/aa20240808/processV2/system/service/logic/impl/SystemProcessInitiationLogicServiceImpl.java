package com.dogfood.aa20240808.processV2.system.service.logic.impl;

import com.dogfood.aa20240808.processV2.system.aspect.ProcessRecord;
import com.dogfood.aa20240808.processV2.system.aspect.ProcessRecordAction;
import com.dogfood.aa20240808.processV2.system.service.*;
import com.dogfood.aa20240808.processV2.system.service.logic.SystemProcessInitiationLogicService;
import com.dogfood.aa20240808.processV2.system.util.SystemProcessUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemProcessInitiationLogicServiceImpl implements SystemProcessInitiationLogicService {

    private final Logger logger = LoggerFactory.getLogger(SystemProcessInitiationLogicServiceImpl.class);

    @Autowired
    private SystemProcessDefinitionService systemProcessDefinitionService;

    @Override
    @ProcessRecord(action = ProcessRecordAction.LAUNCH)
    public String launchProcess(Object data, String procDefKey) {
        return systemProcessDefinitionService.launchProcess(procDefKey, SystemProcessUtil.dataToMap(data));
    }

}
