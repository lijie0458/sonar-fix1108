package com.dogfood.aa20240808.processV2.system.engine.service.inter;

import java.util.Map;

public interface ServiceTaskServiceInterface {
    void execute(Map<String, Object> variables) throws Exception;
}