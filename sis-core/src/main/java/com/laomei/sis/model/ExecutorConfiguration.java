package com.laomei.sis.model;

/**
 * @author laomei on 2018/12/3 19:25
 */
public class ExecutorConfiguration {

    private String sql;

    private String name;

    private boolean required;

    public String getSql() {
        return sql;
    }

    public void setSql(final String sql) {
        this.sql = sql;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(final boolean required) {
        this.required = required;
    }
}
