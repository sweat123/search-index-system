package com.laomei.sis;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * @author laomei on 2018/12/1 14:21
 */
public class BaseConnectorConfig extends AbstractConfig {

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

    //group
    private static final String SOURCE_GROUP = "Source";
    private static final String EXECUTOR_GROUP = "Executor";
    private static final String MYSQL_GROUP = "Mysql";

    protected static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(SOURCE_CONFIGURATIONS, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, SOURCE_CONFIGURATIONS_DOC,
                    SOURCE_GROUP, 1, ConfigDef.Width.LONG, SOURCE_CONFIGURATIONS_DISPLAY)
            .define(EXECUTOR_CONFIGURATIONS, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, EXECUTOR_CONFIGURATIONS_DOC,
                    EXECUTOR_GROUP, 1, ConfigDef.Width.LONG, EXECUTOR_CONFIGURATIONS_DISPLAY)
            .define(DEFAULT_MYSQL_URL, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, DEFAULT_MYSQL_URL_DOC,
                    MYSQL_GROUP, 1, ConfigDef.Width.LONG, DEFAULT_MYSQL_URL_DISPLAY)
            .define(DEFAULT_MYSQL_USERNAME, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, DEFAULT_MYSQL_USERNAME_DOC,
                    MYSQL_GROUP, 2, ConfigDef.Width.LONG, DEFAULT_MYSQL_USERNAME_DISPLAY)
            .define(DEFAULT_MYSQL_PASSWORD, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, DEFAULT_MYSQL_PASSWORD_DOC,
                    MYSQL_GROUP, 3, ConfigDef.Width.LONG, DEFAULT_MYSQL_PASSWORD_DISPLAY);

    public final String sourceConfigurations;

    public final String executorConfigurations;

    public final String defaultMysqlUrl;

    public final String defaultMysqlUsername;

    public final String defaultMysqlPassword;

    public BaseConnectorConfig(ConfigDef configDef, Map<?, ?> props) {
        super(configDef, props);
        sourceConfigurations = getString(SOURCE_CONFIGURATIONS);
        executorConfigurations = getString(EXECUTOR_CONFIGURATIONS);
        defaultMysqlUrl = getString(DEFAULT_MYSQL_URL);
        defaultMysqlUsername = getString(DEFAULT_MYSQL_USERNAME);
        defaultMysqlPassword = getString(DEFAULT_MYSQL_PASSWORD);
    }
}
