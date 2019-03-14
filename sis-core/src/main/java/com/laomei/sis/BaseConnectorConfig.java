package com.laomei.sis;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * @author laomei on 2018/12/1 14:21
 */
public class BaseConnectorConfig extends AbstractConfig {

    public static final String CONNECTOR_NAME = "sis.name";
    private static final String CONNECTOR_NAME_DOC = "sis task name";
    private static final String CONNECTOR_NAME_DISPLAY = "sis task name";

    public static final String  SOURCE_CONFIGURATIONS = "sis.source.configuration";
    private static final String SOURCE_CONFIGURATIONS_DOC =
            "A json represent sis source configuration including record filter and transform";
    private static final String SOURCE_CONFIGURATIONS_DISPLAY =
            "A json represent sis source configuration including record filter and transform";

    public static final String  EXECUTOR_CONFIGURATIONS = "sis.executor.configuration";
    private static final String EXECUTOR_CONFIGURATIONS_DOC =
            "A json represent sis executor configuration; One or more sqls will be executed in executor in " +
                    "order to aggregate value";
    private static final String EXECUTOR_CONFIGURATIONS_DISPLAY =
            "A json represent sis executor configuration; One or more sqls will be executed in executor in ";

    public static final String DEFAULT_MYSQL_URL = "sis.mysql.default.url";
    private static final String DEFAULT_MYSQL_URL_DOC =
            "Mysql url; Default mysql datasource will be used in sis";
    private static final String DEFAULT_MYSQL_URL_DISPLAY =
            "Mysql url; Default mysql datasource will be used in sis";

    public static final String DEFAULT_MYSQL_USERNAME = "sis.mysql.default.username";
    private static final String DEFAULT_MYSQL_USERNAME_DOC =
            "Mysql username; Default mysql datasource will be used in sis";
    private static final String DEFAULT_MYSQL_USERNAME_DISPLAY =
            "Mysql username; Default mysql datasource will be used in sis";

    public static final String DEFAULT_MYSQL_PASSWORD = "sis.mysql.default.password";
    private static final String DEFAULT_MYSQL_PASSWORD_DOC =
            "Mysql password; Default mysql datasource will be used in sis";
    private static final String DEFAULT_MYSQL_PASSWORD_DISPLAY =
            "Mysql password; Default mysql datasource will be used in sis";

    // support register multiple mysql dataSource; We can use these dataSource when executing sql that
    // get data from different db instance;
    public static final String MYSQL_DATASOURCE_REGISTER = "sis.mysql.dataSource.register";
    private static final String MYSQL_DATASOURCE_REGISTER_DOC = "support register multiple mysql dataSource; " +
            "We can use these dataSource when executing sql that will get data from different db instance";
    private static final String MYSQL_DATASOURCE_REGISTER_DISPLAY = "support register multiple mysql dataSource; " +
            "We can use these dataSource when executing sql that will get data from different db instance";


    //group
    private static final String CONNECTOR = "Connector";
    private static final String SOURCE_GROUP = "Source";
    private static final String EXECUTOR_GROUP = "Executor";
    private static final String MYSQL_GROUP = "Mysql";

    public static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(CONNECTOR_NAME, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, CONNECTOR_NAME_DOC,
                    CONNECTOR, 1, ConfigDef.Width.LONG, CONNECTOR_NAME_DISPLAY)
            .define(SOURCE_CONFIGURATIONS, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, SOURCE_CONFIGURATIONS_DOC,
                    SOURCE_GROUP, 1, ConfigDef.Width.LONG, SOURCE_CONFIGURATIONS_DISPLAY)
            .define(EXECUTOR_CONFIGURATIONS, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, EXECUTOR_CONFIGURATIONS_DOC,
                    EXECUTOR_GROUP, 1, ConfigDef.Width.LONG, EXECUTOR_CONFIGURATIONS_DISPLAY)
            .define(DEFAULT_MYSQL_URL, ConfigDef.Type.STRING, "",
                    ConfigDef.Importance.LOW, DEFAULT_MYSQL_URL_DOC,
                    MYSQL_GROUP, 1, ConfigDef.Width.LONG, DEFAULT_MYSQL_URL_DISPLAY)
            .define(DEFAULT_MYSQL_USERNAME, ConfigDef.Type.STRING, "",
                    ConfigDef.Importance.LOW, DEFAULT_MYSQL_USERNAME_DOC,
                    MYSQL_GROUP, 2, ConfigDef.Width.LONG, DEFAULT_MYSQL_USERNAME_DISPLAY)
            .define(DEFAULT_MYSQL_PASSWORD, ConfigDef.Type.STRING, "",
                    ConfigDef.Importance.LOW, DEFAULT_MYSQL_PASSWORD_DOC,
                    MYSQL_GROUP, 3, ConfigDef.Width.LONG, DEFAULT_MYSQL_PASSWORD_DISPLAY)
            .define(MYSQL_DATASOURCE_REGISTER, ConfigDef.Type.STRING, null,
                    ConfigDef.Importance.LOW, MYSQL_DATASOURCE_REGISTER_DOC,
                    MYSQL_GROUP, 4, ConfigDef.Width.LONG, MYSQL_DATASOURCE_REGISTER_DISPLAY);

    public final String sourceConfigurations;

    public final String executorConfigurations;

    public final String defaultMysqlUrl;

    public final String defaultMysqlUsername;

    public final String defaultMysqlPassword;

    public final String mysqlDataSourceRegister;

    private final Map<String, ?> originalConfigs;

    public BaseConnectorConfig(ConfigDef configDef, Map<String, ?> props) {
        super(configDef, props);
        this.originalConfigs = props;
        sourceConfigurations = getString(SOURCE_CONFIGURATIONS);
        executorConfigurations = getString(EXECUTOR_CONFIGURATIONS);
        defaultMysqlUrl = getString(DEFAULT_MYSQL_URL);
        defaultMysqlUsername = getString(DEFAULT_MYSQL_USERNAME);
        defaultMysqlPassword = getString(DEFAULT_MYSQL_PASSWORD);
        mysqlDataSourceRegister = getString(MYSQL_DATASOURCE_REGISTER);
    }

    public <T> T getOriginalValue(String key) {
        return (T) originalConfigs.get(key);
    }

    public Map<String, ?> getOriginalConfigs() {
        return originalConfigs;
    }
}
