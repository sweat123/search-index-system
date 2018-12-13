package com.laomei.sis.model;

import java.util.List;

/**
 * @author laomei on 2018/12/13 15:36
 */
public class DataSourceConfigurations {

    private List<DataSourceConfiguration> dataSources;

    public List<DataSourceConfiguration> getDataSources() {
        return dataSources;
    }

    public void setDataSources(final List<DataSourceConfiguration> dataSources) {
        this.dataSources = dataSources;
    }
}
