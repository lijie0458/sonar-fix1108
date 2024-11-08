package com.dogfood.aa20240808.processV2.system.engine.config;

import org.flowable.common.engine.impl.cfg.SpringBeanFactoryProxyMap;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ProcessEngineFactoryBean implements FactoryBean<ProcessEngine>, DisposableBean, ApplicationContextAware {

    protected ProcessEngineConfigurationImpl processEngineConfiguration;

    protected ApplicationContext applicationContext;
    protected ProcessEngine processEngine;

    @Override
    public void destroy() throws Exception {
        if (processEngine != null) {
            processEngine.close();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ProcessEngine getObject() throws Exception {

        if (processEngineConfiguration.getBeans() == null) {
            processEngineConfiguration.setBeans(new SpringBeanFactoryProxyMap(applicationContext));
        }

        this.processEngine = processEngineConfiguration.buildProcessEngine();
        return this.processEngine;
    }

    @Override
    public Class<ProcessEngine> getObjectType() {
        return ProcessEngine.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public ProcessEngineConfigurationImpl getProcessEngineConfiguration() {
        return processEngineConfiguration;
    }

    public void setProcessEngineConfiguration(ProcessEngineConfigurationImpl processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
    }
}
