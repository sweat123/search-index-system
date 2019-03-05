package com.laomei.sis.solr;

import com.laomei.sis.AbstractReducer;
import com.laomei.sis.SchemaHelper;
import org.apache.solr.client.solrj.SolrClient;

/**
 * @author laomei on 2019/3/5 19:24
 */
public abstract class AbstractSolrReducer extends AbstractReducer {

    protected final SolrClient solrClient;

    public AbstractSolrReducer(SchemaHelper schemaHelper, SolrClient solrClient) {
        super(schemaHelper);
        this.solrClient = solrClient;
    }
}
