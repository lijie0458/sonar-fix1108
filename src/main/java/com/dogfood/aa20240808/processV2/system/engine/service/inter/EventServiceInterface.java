package com.dogfood.aa20240808.processV2.system.engine.service.inter;

import java.util.Map;

public interface EventServiceInterface {
    void trigger(Map<String, Object> variables) throws Exception;
}