package com.dogfood.aa20240808.processV2.system.engine.custom;

import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;


public class CustomProcessEngineConfiguration extends StandaloneProcessEngineConfiguration {

    public static final String CUSTOM_MYBATIS_MAPPING_FILE = "process/mapping/flowable/mappings.xml";

    private void parseDataSourceType() {
        mybatisMappingFile = CUSTOM_MYBATIS_MAPPING_FILE;
        // 重写自定义Schema管理命令
        setSchemaManagementCmd(new CustomSchemaOperationsProcessEngineBuild());

        String databaseProductName = "PLUGIN_" + getDatabaseType(dataSource);
        // 判断Flowable引擎是否支持的数据库类型，若不支持则使用外置的配置文件进行替换，否则不做处理，默认使用引擎内置的配置文件
        if (!databaseTypeMappings.containsKey(databaseProductName)) {
            // 设置mybatis mapping文件路径，替换引擎默认的mybatis mapping文件
            // 对databaseProductName去除空格，并去除其他非数字字母字符，转换为小写
            String databaseProductType = databaseProductName.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            databaseTypeMappings.setProperty(databaseProductName, databaseProductType);
            databaseType = databaseProductType;
        }
    }

    @Override
    public String pathToEngineDbProperties() {
        return "process/mapping/flowable/default.properties";
    }

    @Override
    public ProcessEngineConfiguration setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        parseDataSourceType();
        return this;
    }

    /**
     * 返回数据库类型
     * @param dataSource
     */
    private String getDatabaseType(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData databaseMetaData = connection.getMetaData();
            return databaseMetaData.getDatabaseProductName();
        } catch (SQLException e) {
            throw new RuntimeException("Exception while initializing Database connection", e);
        }
    }
}
