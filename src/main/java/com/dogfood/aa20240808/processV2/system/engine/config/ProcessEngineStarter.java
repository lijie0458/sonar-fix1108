package com.dogfood.aa20240808.processV2.system.engine.config;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;


@Component
public class ProcessEngineStarter implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProcessEngineStarter.class);

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    ResourcePatternResolver resourcePatternResolver;

    // 当前所有流程正在生效的NASL版本
    public static Map<String, Integer> processNaslVersionMap = new HashMap<>();

    // 当前生效版本的流程对应的表单定义
    public static Map<String, Map<String, String>> formDefinitionMap = new HashMap<>();

    // 当前所有流程的每个NASL版本的最新版本（此处指Flowable版本）定义
    public static Map<String, Map<Integer, ProcessDefinition>> allLatestProcessDefinitionMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        processEngine.startExecutors();
        LOGGER.info("Process engine {} start success", processEngine.getName());
        autoDeployment(processEngine);
        LOGGER.info("Process auto deploy success");
    }

    public void autoDeployment(ProcessEngine processEngine) throws Exception {
        List<Resource> processResources = discoverDeploymentResources("classpath*:/process/definition/",
                Arrays.asList("**.bpmn20.xml", "**.bpmn", "**.nasl.version", "**.formDefinition", "**.nasl.status"), true);

        RepositoryService repositoryService = processEngine.getRepositoryService();

        Map<String, List<Resource>> deployResourceMap = processResources.stream().collect(
                Collectors.groupingBy(resource -> Objects.requireNonNull(resource.getFilename()).split("\\.")[0] )
        );

        processNaslVersionMap = deployResourceMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    try (InputStream inputStream = entry.getValue().stream()
                            .filter(resource -> Objects.requireNonNull(resource.getFilename()).equalsIgnoreCase(entry.getKey() + ".nasl.version"))
                            .findFirst().get().getInputStream()) {
                        return Integer.parseInt(StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        LOGGER.error("Failed to read nasl version for process {}", entry.getKey(), e);
                        return -1;
                    }
                }));

        formDefinitionMap = deployResourceMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                            .filter(resource -> Objects.requireNonNull(resource.getFilename()).endsWith(".formDefinition"))
                            .collect(Collectors.toMap(Resource::getFilename, resource -> {
                                        try (InputStream inputStream = resource.getInputStream()) {
                                            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                                        } catch (IOException e) {
                                            LOGGER.error("Failed to read form definition for process {}", entry.getKey(), e);
                                            return "";
                                        }
                                    }
                            ))
                ));

        Map<String, Map<Integer, ProcessDefinition>> allProcessNaslVersionMap = getAllLatestProcessNaslVersionMap(processEngine);

        for (Map.Entry<String, List<Resource>> entry : deployResourceMap.entrySet()) {
            Map<Integer, ProcessDefinition> naslVersionMap = allProcessNaslVersionMap.get(entry.getKey());
            int deployNaslVersion = processNaslVersionMap.get(entry.getKey());

            LOGGER.info("Begin to deploy process {} with version {}", entry.getKey(), deployNaslVersion);
            if (naslVersionMap == null || naslVersionMap.isEmpty() || !naslVersionMap.containsKey(deployNaslVersion)) {
                // 未发布过，或者该流程对应的版本也未发布过
                deployProcess(entry.getKey(), entry.getValue(), repositoryService);
                LOGGER.info("Deploy process {} with version {} success", entry.getKey(), deployNaslVersion);
            } else {
                // 之前发布过
                ProcessDefinition processDefinition = naslVersionMap.get(deployNaslVersion);
                String deploymentId = processDefinition.getDeploymentId();

                //当前只判断xml流程定义和内容是否发生了变化，其他表单定义暂时不做考虑
                String xmlFileName = processDefinition.getKey() + ".bpmn20.xml";
                String xmlContent = StreamUtils.copyToString(repositoryService.getResourceAsStream(deploymentId, xmlFileName), StandardCharsets.UTF_8);
                String deployXmlContent = StreamUtils.copyToString(entry.getValue().stream()
                        .filter(resource -> Objects.requireNonNull(resource.getFilename()).equalsIgnoreCase(xmlFileName))
                        .findFirst().get().getInputStream(), StandardCharsets.UTF_8);

                if (!xmlContent.equals(deployXmlContent)) {
                    // xml内容发生变化，需要重新部署
                    LOGGER.info("Process {} with version {} has been deployed, but xml content has changed, need to redeploy", entry.getKey(), deployNaslVersion);
                    deployProcess(entry.getKey(), entry.getValue(), repositoryService);
                    LOGGER.info("Redeploy process {} with version {} success", entry.getKey(), deployNaslVersion);
                } else {
                    // xml内容未发生变化，不需要重新部署
                    LOGGER.info("Process {} with version {} has been deployed, no need to redeployed", entry.getKey(), deployNaslVersion);
                }
            }
        }

        allLatestProcessDefinitionMap = getAllLatestProcessNaslVersionMap(processEngine);
    }


    private void deployProcess(String uniqueKey, List<Resource> resources, RepositoryService repositoryService) {
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name("Deployment_" + uniqueKey);
        resources.forEach(resource -> addResource(resource, deploymentBuilder));
        deploymentBuilder.deploy();
    }

    private void addResource(Resource resource, DeploymentBuilder deploymentBuilder) {
        if (resource != null && StringUtils.isNotBlank(resource.getFilename()) && !resource.getFilename().endsWith("formDefinition")){
            String resourceName = resource.getFilename();
            try (InputStream inputStream = resource.getInputStream()) {
                if (resourceName.endsWith(".bar") || resourceName.endsWith(".zip") || resourceName.endsWith(".jar")) {
                    try (ZipInputStream zipStream = new ZipInputStream(inputStream)) {
                        deploymentBuilder.addZipInputStream(zipStream);
                    }
                } else {
                    deploymentBuilder.addInputStream(resourceName, inputStream);
                }
            } catch (IOException ex) {
                throw new UncheckedIOException("Failed to read resource " + resource, ex);
            }
        }

    }

    private List<Resource> discoverDeploymentResources(String prefix, List<String> suffixes, boolean loadResources) throws IOException {
        if (loadResources) {
            List<Resource> result = new ArrayList<>();
            for (String suffix : suffixes) {
                String path = prefix + suffix;
                Resource[] resources = resourcePatternResolver.getResources(path);
                if (resources != null && resources.length > 0) {
                    Collections.addAll(result, resources);
                }
            }
            if (result.isEmpty()) {
                LOGGER.info("No deployment resources were found for autodeployment");
            }
            return removeDuplicatesResource(result);
        }
        return new ArrayList<>();
    }

    /**
     * 返回所有流程的每个NASL版本的最新版本（此处指Flowable版本）定义
     * @param processEngine
     * @return
     * @throws Exception
     */
    private Map<String, Map<Integer, ProcessDefinition>> getAllLatestProcessNaslVersionMap(ProcessEngine processEngine) throws Exception{
        Map<String, Map<Integer, ProcessDefinition>> allProcessNaslVersionMap = new HashMap<>();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Map<String,List<ProcessDefinition>> processDefinitionMap = repositoryService.createProcessDefinitionQuery().list().stream()
                .collect(Collectors.groupingBy(ProcessDefinition::getKey));

        for (Map.Entry<String, List<ProcessDefinition>> entry : processDefinitionMap.entrySet()) {
            if (entry.getKey().startsWith("Process_")) {
                Map<Integer, ProcessDefinition> naslVersionMap = new HashMap<>();
                for (ProcessDefinition processDefinition : entry.getValue()) {
                    if (repositoryService.getDeploymentResourceNames(processDefinition.getDeploymentId()).stream()
                            .anyMatch(resourceName -> StringUtils.equalsIgnoreCase(resourceName, entry.getKey() + ".nasl.version"))) {
                        Integer naslVersion = Integer.parseInt(StreamUtils.copyToString(repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), entry.getKey() + ".nasl.version"), StandardCharsets.UTF_8));
                        if (naslVersionMap.containsKey(naslVersion)) {
                            ProcessDefinition existProcessDefinition = naslVersionMap.get(naslVersion);
                            if (existProcessDefinition.getVersion() < processDefinition.getVersion()) {
                                naslVersionMap.put(naslVersion, processDefinition);
                            }
                        } else {
                            naslVersionMap.put(naslVersion, processDefinition);
                        }
                    }
                }
                allProcessNaslVersionMap.put(entry.getKey(), naslVersionMap);
            }
        }
        return allProcessNaslVersionMap;
    }

    private List<Resource> removeDuplicatesResource(List<Resource> oldResult) {
        Map<String, Resource> resourceMap = new LinkedHashMap<>();
        for (Resource resource : oldResult) {
            String filename = resource.getFilename();
            if (!resourceMap.containsKey(filename)){
                resourceMap.put(filename, resource);
            }
        }
        return new ArrayList<>(resourceMap.values());
    }
}
