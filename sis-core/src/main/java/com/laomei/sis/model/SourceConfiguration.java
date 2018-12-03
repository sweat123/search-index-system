package com.laomei.sis.model;

/**
 * @author laomei on 2018/12/3 19:08
 */
public class SourceConfiguration {

    private String topic;

    private Record record;

    private Fields fields;

    private Placeholder placeholder;

    private Filter filter;

    public String getTopic() {
        return topic;
    }

    public void setTopic(final String topic) {
        this.topic = topic;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(final Record record) {
        this.record = record;
    }

    public Fields getFields() {
        return fields;
    }

    public void setFields(final Fields fields) {
        this.fields = fields;
    }

    public Placeholder getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(final Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(final Filter filter) {
        this.filter = filter;
    }
}
