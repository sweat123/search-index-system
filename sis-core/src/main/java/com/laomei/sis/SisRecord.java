package com.laomei.sis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author laomei on 2018/12/2 14:36
 */
public class SisRecord {

    private final Map<String, Object> context;

    public SisRecord() {
        this(new HashMap<>());
    }

    public SisRecord(SisRecord record) {
        this(record.getContext());
    }

    public SisRecord(Map<String, Object> context) {
        this.context = new HashMap<>(context);
    }

    public Map<String, Object> getContext() {
        return Collections.unmodifiableMap(context);
    }

    public void addValue(String key, Object value) {
        context.put(key, value);
    }

    public boolean hasValue(String key) {
        return context.containsKey(key);
    }

    public Object getValue(String key) {
        return context.get(key);
    }

    public Object dropValue(String key) {
        return context.remove(key);
    }
}
