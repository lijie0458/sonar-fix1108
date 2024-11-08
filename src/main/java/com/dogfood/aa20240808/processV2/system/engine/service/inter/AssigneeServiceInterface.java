package com.dogfood.aa20240808.processV2.system.engine.service.inter;

import java.util.List;
import java.util.Map;

public interface AssigneeServiceInterface {
    List<String> getAssigneeList(Map<String, Object> variables) throws Exception;
}