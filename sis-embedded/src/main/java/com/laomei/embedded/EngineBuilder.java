/*
 * EngineBuilder.java
 * Copyright 2018 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.laomei.embedded;

/**
 * @author luobo.hwz on 2018/12/28 19:35
 */
public interface EngineBuilder {

    EngineBuilder addConfig(String key, Object config);

    EmbeddedEngine build();
}
