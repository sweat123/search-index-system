package com.laomei.sis;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author laomei on 2018/12/2 14:36
 */
public class SisRecord {

    private final Map<String, Object> context;

    private final String topic;

    public SisRecord(String topic) {
        this(topic, new HashMap<>());
    }

    public SisRecord(SisRecord record) {
        this(record.getTopic(), record.getContext());
    }

    public SisRecord(String topic, Map<String, Object> context) {
        this.topic = topic;
        this.context = new HashMap<>(context);
    }

    public SisRecord(SinkRecord record) {
        this.context = new HashMap<>();
        this.topic = record.topic();
        GenericRecord value = (GenericRecord) record.value();
        GenericRecord before = (GenericRecord) value.get("before");
        GenericRecord after = (GenericRecord) value.get("after");
        context.put("after", after);
        context.put("before", before);
    }

    public Map<String, Object> getContext() {
        return Collections.unmodifiableMap(context);
    }

    public String getTopic() {
        return topic;
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

    public boolean isEmpty() {
        return context.isEmpty();
    }
}
