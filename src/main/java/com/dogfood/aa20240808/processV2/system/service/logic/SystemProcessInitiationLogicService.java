package com.dogfood.aa20240808.processV2.system.service.logic;

public interface SystemProcessInitiationLogicService {

    /**
     * 启动流程
     * @param data
     * @param procDefKey
     * @return
     */
    String launchProcess(Object data, String procDefKey);


}
