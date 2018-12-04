package com.laomei.sis.solr;

import com.laomei.sis.Reducer;
import com.laomei.sis.SisRecord;
import org.apache.solr.client.solrj.SolrClient;

import java.util.List;

/**
 * @author laomei on 2018/12/1 20:45
 */
public class SolrDeleteReducer implements Reducer {

    private final SolrConnectorConfig configs;

    private final SolrClient solrClient;

    public SolrDeleteReducer(final SolrConnectorConfig configs, final SolrClient solrClient) {
        this.configs = configs;
        this.solrClient = solrClient;
    }

    @Override
    public void reduce(List<SisRecord> sisRecords) {

    }

    @Override
    public void close() {

    }
}
