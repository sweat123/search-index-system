package com.laomei.sis.solr;

import com.laomei.sis.DefaultTaskContext;
import com.laomei.sis.SchemaHelper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.CloudSolrClient;

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
        SolrClient solrClient = createSolrClient();
        SchemaHelper schemaHelper = new SolrSchemaHelper(configs.solrCloudCollection, solrClient);
        schemaHelper.init();
        String mode = configs.solrCloudIndexMode;
        if ("update".equals(mode)) {
            reducer = new SolrUpdateReducer(schemaHelper, configs, solrClient);
        } else if ("delete".equals(mode)) {
            reducer = new SolrDeleteReducer(schemaHelper, configs, solrClient);
        } else {
            throw new IllegalArgumentException("illegal solr index mode; you have to set index mode with 'update' or 'delete'");
        }
    }

    private SolrClient createSolrClient() {
        return new CloudSolrClient.Builder().withZkHost(configs.solrCloudZkHost)
                .withConnectionTimeout(configs.solrCloudConnectTimeout)
                .withSocketTimeout(configs.solrCloudSocketTimeout)
                .build();
    }
}
