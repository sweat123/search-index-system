package com.laomei.sis.mysql;

import com.laomei.sis.BaseConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.Map;

/**
 * @author laomei on 2019/3/8 11:51
 */
public class MysqlConnectorConfig extends BaseConnectorConfig {

    public MysqlConnectorConfig(final ConfigDef configDef, final Map<String, ?> props) {
        super(configDef, props);
    }
}
