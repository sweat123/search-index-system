package com.laomei.sis;

import com.laomei.sis.transform.ChainTransform;

import java.util.HashMap;
import java.util.Map;

/**
 * @author laomei on 2018/12/3 19:40
 */
public class TransformContext {

    private final Map<String, ChainTransform> transformMap;

    public TransformContext() {
        this.transformMap = new HashMap<>();
    }

    public void addTransforms(String topic, ChainTransform transform) {
        transformMap.put(topic, transform);
    }

    public Transform getTransform(String topic) {
        return transformMap.get(topic);
    }

    public void shutdown() {
        transformMap.values().forEach(ChainTransform::close);
    }
}
