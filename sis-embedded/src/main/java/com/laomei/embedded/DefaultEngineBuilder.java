/*
 * DefaultEngineBuilder.java
 * Copyright 2018 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.laomei.embedded;

import com.laomei.sis.BaseConnectorConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luobo.hwz on 2018/12/28 19:37
 */
public class DefaultEngineBuilder implements EngineBuilder {

    private final Map<String, Object> configs = new HashMap<>();

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
    public EmbeddedEngine build() {
        BaseConnectorConfig connectorConfig = new BaseConnectorConfig(
                BaseConnectorConfig.CONFIG_DEF,
                configs
        );
        return new EmbeddedEngine(connectorConfig);
    }
}
