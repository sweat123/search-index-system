package com.laomei.sis.solr;

import com.laomei.sis.AbstractReducer;
import com.laomei.sis.SchemaHelper;
import org.apache.solr.client.solrj.SolrClient;

/**
 * @author laomei on 2019/3/5 19:24
 */
public abstract class AbstractSolrReducer extends AbstractReducer {

    protected final SolrClient solrClient;
    protected final String     solrCollection;

    public AbstractSolrReducer(SchemaHelper schemaHelper, SolrClient solrClient, String solrCollection) {
        super(schemaHelper);
        this.solrClient = solrClient;
        this.solrCollection = solrCollection;
    }

    @Override
    public void close() {
        if (schemaHelper != null) {
            schemaHelper.close();
        }
        if (solrClient != null) {
            try {
                solrClient.close();
            } catch (Exception ignore) {
            }
        }
    }
}
