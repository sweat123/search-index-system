package com.laomei.embedded;

import com.laomei.sis.BaseConnectorConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author laomei on 2018/12/28 19:37
 */
public class DefaultEngineBuilder implements EngineBuilder {

    private final Map<String, Object> configs = new HashMap<>();

    private final Map<String, Object> additionalConfigs = new HashMap<>();

    public static EngineBuilder create() {
        return new DefaultEngineBuilder();
    }

    private DefaultEngineBuilder() {
    }

    @Override
    public EngineBuilder addConfig(final String key, final Object config) {
        configs.put(key, config);
        return this;
    }

    @Override
    public EngineBuilder addAdditionalConfig(final String key, final Object config) {
        additionalConfigs.put(key, config);
        return this;
    }

    @Override
    public EmbeddedEngine build() {
        BaseConnectorConfig connectorConfig = new BaseConnectorConfig(
                BaseConnectorConfig.CONFIG_DEF,
                configs
        );
        return new EmbeddedEngine(connectorConfig, additionalConfigs);
    }
}
