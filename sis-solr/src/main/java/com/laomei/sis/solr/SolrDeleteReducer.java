package com.laomei.sis.solr;

import com.laomei.sis.SchemaHelper;
import com.laomei.sis.SisRecord;
import org.apache.solr.client.solrj.SolrClient;

import java.util.List;

/**
 * @author laomei on 2018/12/1 20:45
 */
public class SolrDeleteReducer extends AbstractSolrReducer {

    private final SolrConnectorConfig configs;

    public SolrDeleteReducer(SchemaHelper schemaHelper, SolrConnectorConfig configs, SolrClient solrClient) {
        super(schemaHelper, solrClient);
        this.configs = configs;
    }

    @Override
    public void reduce(List<SisRecord> sisRecords) {

    }

    @Override
    public void close() {
    }
}
