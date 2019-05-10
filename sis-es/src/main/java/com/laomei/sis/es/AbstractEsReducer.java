package com.laomei.sis.es;

import com.laomei.sis.AbstractReducer;
import com.laomei.sis.SchemaHelper;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author laomei on 2019/3/12 20:16
 */
public abstract class AbstractEsReducer extends AbstractReducer {
    protected static final String ID = "id";

    protected final BulkProcessor       bulkProcessor;

    protected final RestHighLevelClient client;

    protected final EsConnectorConfig   connectorConfig;

    protected final String              index;

    public AbstractEsReducer(final SchemaHelper schemaHelper, final BulkProcessor bulkProcessor,
            final RestHighLevelClient client, final EsConnectorConfig connectorConfig) {
        super(schemaHelper);
        this.bulkProcessor = bulkProcessor;
        this.client = client;
        this.connectorConfig = connectorConfig;
        this.index = connectorConfig.esIndex;
    }

    protected void doReducer(DocWriteRequest request) {
        bulkProcessor.add(request);
    }

    @Override
    public void close() {
        if (schemaHelper != null) {
            this.schemaHelper.close();
        }
        if (bulkProcessor != null) {
            try {
                bulkProcessor.awaitClose(5, TimeUnit.MINUTES);
            } catch (InterruptedException ignore) {
            }
            try {
                client.close();
            } catch (IOException ignore) {
            }
        }
    }
}
