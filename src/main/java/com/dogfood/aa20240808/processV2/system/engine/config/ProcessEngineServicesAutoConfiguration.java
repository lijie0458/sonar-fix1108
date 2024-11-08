package com.dogfood.aa20240808.processV2.system.engine.config;

import com.dogfood.aa20240808.processV2.system.engine.custom.CustomIdentityLinkInterceptor;
import com.dogfood.aa20240808.processV2.system.engine.custom.CustomProcessEngineConfiguration;
import com.dogfood.aa20240808.processV2.system.engine.custom.behavior.CustomActivityBehaviorFactory;
import com.google.common.eventbus.EventBus;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.history.HistoryLevel;
import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;
import org.flowable.engine.*;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration(proxyBeanMethods = false)
public class ProcessEngineServicesAutoConfiguration {

    @Value("${lcp.process.database.table.prefix:}")
    private String databaseTablePrefix;

    @Value("${lcp.process.database.schema:}")
    private String databaseSchema;

    @Value("${lcp.process.database.isPlugin:false}")
    private boolean isDatabasePlugin = false;

    @Bean
    @ConditionalOnMissingBean
    public ProcessEngineFactoryBean processEngine(DataSource dataSource) throws SQLException{
        StandaloneProcessEngineConfiguration configuration = isDatabasePlugin ? new CustomProcessEngineConfiguration() : new StandaloneProcessEngineConfiguration();

        configuration.setDataSource(dataSource);
        if (StringUtils.isNotBlank(databaseTablePrefix) && StringUtils.isNotBlank(databaseSchema)) {
            configuration.setDatabaseSchema(databaseSchema);
            configuration.setDatabaseTablePrefix(databaseTablePrefix);
        } else if (StringUtils.isNotBlank(databaseTablePrefix)) {
            configuration.setDatabaseTablePrefix(databaseTablePrefix);
        } else if (StringUtils.isNotBlank(databaseSchema)) {
            configuration.setDatabaseSchema(databaseSchema);
        }

        configuration.setDisableIdmEngine(true);
        configuration.setDisableEventRegistry(true);
        configuration.setHistoryLevel(HistoryLevel.AUDIT);
        configuration.setIdentityLinkInterceptor(new CustomIdentityLinkInterceptor());
        configuration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
        configuration.setIdGenerator(new StrongUuidGenerator());
        configuration.setLowCodeAppRunningMode(AbstractEngineConfiguration.LOW_CODE_APP_RUNNING_MODE_V2);
        configuration.setAsyncExecutorActivate(true);
        configuration.setCreateDiagramOnDeploy(false);
        configuration.setActivityBehaviorFactory(new CustomActivityBehaviorFactory());

        ProcessEngineFactoryBean processEngineFactoryBean = new ProcessEngineFactoryBean();
        processEngineFactoryBean.setProcessEngineConfiguration(configuration);
        return processEngineFactoryBean;
    }


    @Bean
    @ConditionalOnMissingBean
    public RuntimeService runtimeServiceBean(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    @ConditionalOnMissingBean
    public RepositoryService repositoryServiceBean(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    @ConditionalOnMissingBean
    public TaskService taskServiceBean(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    @ConditionalOnMissingBean
    public HistoryService historyServiceBean(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ManagementService managementServiceBean(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicBpmnService dynamicBpmnServiceBean(ProcessEngine processEngine) {
        return processEngine.getDynamicBpmnService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProcessMigrationService processInstanceMigrationService(ProcessEngine processEngine) {
        return processEngine.getProcessMigrationService();
    }

    @Bean
    public EventBus eventBusBean() {
        return new EventBus("process-event-bus");
    }

}
