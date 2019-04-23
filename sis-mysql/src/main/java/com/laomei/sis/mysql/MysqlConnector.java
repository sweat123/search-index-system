package com.laomei.sis.mysql;

import org.apache.kafka.common.config.Config;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author laomei on 2019/4/23 20:06
 */
public class MysqlConnector extends SinkConnector {
    private static final Logger log = LoggerFactory.getLogger(MysqlConnector.class);

    private Map<String, String> configProps;

    @Override
    public String version() {
        return Version.version();
    }

    @Override
    public void start(final Map<String, String> props) {
        this.configProps = props;
    }

    @Override
    public Class<? extends Task> taskClass() {
        return MysqlSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(final int maxTasks) {
        log.info("Setting task configuration for {} workers.", maxTasks);
        List<Map<String, String>> configs = new ArrayList<>(maxTasks);
        for (int i = 0; i < maxTasks; i ++) {
            configs.add(configProps);
        }
        return configs;
    }

    @Override
    public void stop() {
    }

    @Override
    public Config validate(final Map<String, String> connectorConfigs) {
        return super.validate(connectorConfigs);
    }

    @Override
    public ConfigDef config() {
        return MysqlConnectorConfig.getConfigDef();
    }
}
