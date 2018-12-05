package com.laomei.sis.model;

/**
 * @author laomei on 2018/12/3 19:11
 */
public class Placeholder {

    private String config;

    public String getConfig() {
        return config;
    }

    public void setConfig(final String config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "Placeholder{ " + "config: " + config + " }";
    }
}
