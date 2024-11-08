package com.dogfood.aa20240808.processV2.system.service.impl;

import com.dogfood.aa20240808.context.UserContext;
import com.dogfood.aa20240808.processV2.system.engine.config.ProcessEngineStarter;
import com.dogfood.aa20240808.processV2.system.exception.ProcessException;
import com.dogfood.aa20240808.processV2.system.domain.structure.ProcDef;
import com.dogfood.aa20240808.processV2.system.service.SystemProcessDefinitionService;
import com.dogfood.aa20240808.processV2.system.util.ProcessConstant;
import com.dogfood.aa20240808.processV2.system.util.SystemProcessUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
public class SystemProcessDefinitionServiceImpl implements SystemProcessDefinitionService {

    private static final Logger logger = LoggerFactory.getLogger(SystemProcessDefinitionServiceImpl.class);

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    protected RuntimeService runtimeService;

    @Override
    public String launchProcess(String procDefKey, Map<String, Object> data) {
        if (StringUtils.isBlank(procDefKey)) {
            throw new ProcessException("The processDefUniqueKey cannot be empty!");
        }
        if (data == null) {
            logger.error("The data cannot be empty!");
            throw new ProcessException("The data cannot be empty!");
        }
        ProcessDefinition processDefinition = ProcessEngineStarter.allLatestProcessDefinitionMap.get(procDefKey)
                .get(ProcessEngineStarter.processNaslVersionMap.get(procDefKey));
        if(processDefinition == null){
            throw new ProcessException("The processDefUniqueKey does not exist!");
        }
        UserContext.UserInfo launchUserInfo = UserContext.getCurrentUserInfo();
        if (launchUserInfo != null) {
            Authentication.setAuthenticatedUserId(launchUserInfo.getUserName());
        }
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        processInstanceBuilder.processDefinitionId(processDefinition.getId());
        Map<String, Object> variables = new HashMap<>();
        variables.put(ProcessConstant.KEY_VARIABLE_FORM_DEFINITION, ProcessEngineStarter.formDefinitionMap.get(procDefKey));
        variables.put("_FLOWABLE_SKIP_EXPRESSION_ENABLED", true);
        variables.put(ProcessConstant.KEY_VARIABLE_CUSTOM, data);
        variables.put(ProcessConstant.KEY_PROCESS_VARIABLE,new HashMap<>());
        processInstanceBuilder.variables(variables);
        processInstanceBuilder.name(processDefinition.getName() + "-"  + UserContext.getCurrentUserInfo().getUserName() + "-" + LocalDate.now());
        ProcessInstance processInstance = processInstanceBuilder.start();
        String processInstanceId = processInstance.getProcessInstanceId();
        Authentication.setAuthenticatedUserId(null);
        logger.info("Process instance launched with id: {}", processInstanceId);
        return processInstanceId;
    }

    @Override
    public List<ProcDef> getProcessDefinitionList(String search) {
        List<ProcDef> lcapProcessDefinitionList = new LinkedList<>();
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        if (StringUtils.isNotBlank(search)) {
            query.processDefinitionNameLike("%" + search + "%");
        }
        List<ProcessDefinition> processDefinitionList = query.orderByProcessDefinitionKey()
                .latestVersion()
                .asc()
                .list();
        if (CollectionUtil.isNotEmpty(processDefinitionList)){
            processDefinitionList.forEach(processDefinition -> lcapProcessDefinitionList.add(toLCAPProcessDefinition(processDefinition)));
        }
        logger.info("Process definition list: {}", lcapProcessDefinitionList);
        return lcapProcessDefinitionList;
    }

    @Override
    public ProcDef getProcessDefinition(String processDefUniqueKey) {
        ProcessDefinition processDefinition = processDefExists(processDefUniqueKey);
        ProcDef lcapProcessDefinition = toLCAPProcessDefinition(processDefinition);
        logger.info("Process definition: {}", lcapProcessDefinition);
        return lcapProcessDefinition;
    }

    @Override
    public String getProcessDefinitionForm(String processDefinitionId, String taskDefName) {
        String frontendKey = SystemProcessUtil.getFrontendKey();
        ProcessDefinition processDefinition = processDefExists(processDefinitionId);
        try (InputStream definitionFormInputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinitionId.split(":")[0] + "." + taskDefName + "." + frontendKey + ".formDefinition")) {
            return IOUtils.toString(definitionFormInputStream, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ProcessException("Failed to read process definition form: " + e.getMessage());
        }
    }

    private ProcDef toLCAPProcessDefinition(ProcessDefinition processDefinition) {
        if (ObjectUtils.isEmpty(processDefinition)) return null;
        ProcDef lcapProcessDefinition = new ProcDef();
        lcapProcessDefinition.setProcDefKey(processDefinition.getKey());
        lcapProcessDefinition.setProcDefTitle(processDefinition.getName());
        lcapProcessDefinition.setProcDefDescription(processDefinition.getDescription());
        lcapProcessDefinition.setSuspended(processDefinition.isSuspended());
        return lcapProcessDefinition;
    }

    private ProcessDefinition processDefExists(String processDefinitionId) {
        if (StringUtils.isBlank(processDefinitionId)) {
            throw new ProcessException("The processDefinitionId cannot be empty!");
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        if(processDefinition == null){
            throw new ProcessException("The processDefinitionId does not exist!");
        }
        return processDefinition;
    }
}
