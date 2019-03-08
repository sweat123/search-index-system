package com.laomei.sis.solr;

import com.laomei.sis.SchemaHelper;
import com.laomei.sis.SisRecord;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author laomei on 2018/12/1 20:45
 */
public class SolrDeleteReducer extends AbstractSolrReducer {
    private static final String ID = "id";

    public SolrDeleteReducer(SchemaHelper schemaHelper, SolrConnectorConfig configs, SolrClient solrClient) {
        super(schemaHelper, solrClient, configs.solrCloudCollection);
    }

    @Override
    public void reduce(List<SisRecord> sisRecords) {
        List<String> ids = sisRecords.stream()
                .map(record -> record.getValue(ID))
                .map(String::valueOf)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }
        try {
            solrClient.deleteById(solrCollection, ids);
            solrClient.commit(solrCollection);
        } catch (SolrServerException | IOException e) {
            logger.error("delete solr document failed. solr collection {}", solrCollection, e);
            return;
        }
        logger.info("number of {} solr documents have been deleted; solr collection {}", ids.size(), solrCollection);
    }
}
