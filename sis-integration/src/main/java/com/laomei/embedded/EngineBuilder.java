package com.laomei.embedded;

/**
 * @author laomei on 2018/12/28 19:35
 */
public interface EngineBuilder {

    EngineBuilder addConfig(String key, Object config);

    EngineBuilder addAdditionalConfig(String key, Object config);

    EmbeddedEngine build();
}
