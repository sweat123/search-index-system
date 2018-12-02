package com.laomei.sis.solr;

import com.laomei.sis.AbstractTaskContext;

/**
 * @author laomei on 2018/12/1 15:14
 */
public class SolrTaskContext extends AbstractTaskContext {

    private final SolrConnectorConfig configs;

    public SolrTaskContext(SolrConnectorConfig configs) {
        this.configs = configs;
    }

    @Override
    public void initSolrCloudReducer() {
        String mode = configs.solrCloudIndexMode;
        if ("update".equals(mode)) {
            reducer = new SolrUpdateReducer();
        } else if ("delete".equals(mode)) {
            reducer = new SolrDeleteReducer();
        } else {
            throw new IllegalArgumentException("illegal index mode; you have to set index mode with 'update' or 'delete'");
        }
    }

    @Override
    public void initTransform() {

    }

    @Override
    public void initExecutor() {

    }
}
