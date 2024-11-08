package com.dogfood.aa20240808.processV2.system.engine.config;

import com.dogfood.aa20240808.repository.process.ACTGEPROPERTYMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppStartProcessDataInit implements BeanPostProcessor {

    Logger log = LoggerFactory.getLogger(AppStartProcessDataInit.class);
    @Resource
    private ACTGEPROPERTYMapper actgepropertyMapper;
    private final Map<String, String> properties = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        if (bean instanceof ProcessEngineFactoryBean) {
            processInitData();
        }
        return bean;
    }

    public void processInitData() {
        log.info("应用启动前流程数据初始化开始");
        initProperties();
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            if (actgepropertyMapper.selectValueByName(entry.getKey()) == null) {
                actgepropertyMapper.insert(entry.getKey(), entry.getValue(), 1L);
            } else if (actgepropertyMapper.selectValueByName(entry.getKey()) != null &&
                    actgepropertyMapper.selectValueByName(entry.getKey()).contains("6.6.0.1")) {
                actgepropertyMapper.updateValueByName(entry.getKey(), entry.getValue());
            }
        }
    }

    private void initProperties() {
        String version = "6.6.0.2";
        properties.put("batch.schema.version",version);
        properties.put("cfg.execution-related-entities-count","true");
        properties.put("cfg.task-related-entities-count","true");
        properties.put("common.schema.version",version);
        properties.put("entitylink.schema.version",version);
        properties.put("eventsubscription.schema.version",version);
        properties.put("identitylink.schema.version",version);
        properties.put("job.schema.version",version);
        properties.put("next.dbid","1");
        properties.put("schema.history","create(" + version + ")");
        properties.put("schema.version",version);
        properties.put("task.schema.version",version);
        properties.put("variable.schema.version",version);
    }
}