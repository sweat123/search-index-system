package com.laomei.sis.solr;

import com.laomei.sis.DefaultTaskContext;

/**
 * @author laomei on 2018/12/1 15:14
 */
public class SolrTaskContext extends DefaultTaskContext {

    private final SolrConnectorConfig configs;

    public SolrTaskContext(String name, SolrConnectorConfig configs) {
        super(name, configs);
        this.configs = configs;
    }

    @Override
    public void initSinkReducer() {
        String mode = configs.solrCloudIndexMode;
        if ("update".equals(mode)) {
            reducer = new SolrUpdateReducer(configs);
        } else if ("delete".equals(mode)) {
            reducer = new SolrDeleteReducer(configs);
        } else {
            throw new IllegalArgumentException("illegal solr index mode; you have to set index mode with 'update' or 'delete'");
        }
    }
}
