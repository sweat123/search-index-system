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

    //group
    private static final String SOURCE_GROUP = "Source";
    private static final String EXECUTOR_GROUP = "Executor";

    protected static final ConfigDef CONFIG_DEF = new ConfigDef()
            .define(SOURCE_CONFIGURATIONS, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, SOURCE_CONFIGURATIONS_DOC,
                    SOURCE_GROUP, 1, ConfigDef.Width.LONG, SOURCE_CONFIGURATIONS_DISPLAY)
            .define(EXECUTOR_CONFIGURATIONS, ConfigDef.Type.STRING, ConfigDef.NO_DEFAULT_VALUE,
                    ConfigDef.Importance.HIGH, EXECUTOR_CONFIGURATIONS_DOC,
                    EXECUTOR_GROUP, 1, ConfigDef.Width.LONG, EXECUTOR_CONFIGURATIONS_DISPLAY);

    public final String sourceConfigurations;

    public final String executorConfigrations;

    public BaseConnectorConfig(ConfigDef configDef, Map<?, ?> props) {
        super(configDef, props);
        sourceConfigurations = getString(SOURCE_CONFIGURATIONS);
        executorConfigrations = getString(EXECUTOR_CONFIGURATIONS);
    }
}
