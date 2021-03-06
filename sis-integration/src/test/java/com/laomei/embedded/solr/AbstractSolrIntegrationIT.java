package com.laomei.embedded.solr;

import com.laomei.embedded.AbstractEmbeddedEngineIT;
import com.laomei.embedded.EmbeddedEngine;
import com.laomei.sis.solr.SolrConnectorConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author laomei on 2019/3/6 19:45
 */
public abstract class AbstractSolrIntegrationIT extends AbstractEmbeddedEngineIT {

    protected static final String          SOLR_CLOUD_URL = "localhost:2181";
    protected static final String          USER_DESC      = "user_desc";
    protected static final AtomicBoolean   init           = new AtomicBoolean(false);

    protected              CloudSolrClient solrClient;

    @Override
    public void init() throws IOException, SolrServerException, InterruptedException {
        initSolrClient();
        initSolrCollection();
        super.init();
    }

    @Override
    public void after() throws IOException {
        super.after();
        if (solrClient != null) {
            solrClient.close();
        }
    }

    protected String getAutoOffsetReset() {
        return "earliest";
    }

    protected abstract String getSisMode();

    protected SolrDocumentList query(String collection, Map<String, String> queryMap)
            throws IOException, SolrServerException {
        MapSolrParams params = new MapSolrParams(queryMap);
        QueryResponse response = solrClient.query(collection, params);
        return response.getResults();
    }

    @Override
    protected void initEmbeddedEngine() {
        logger.info("build solr embedded engine");
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> additionalConfig = new HashMap<>();
        String jdbcUrl = System.getProperty("spring.datasource.url");
        config.put(SolrConnectorConfig.SOLR_CLOUD_ZK_HOST, SOLR_CLOUD_URL);
        config.put(SolrConnectorConfig.SOLR_CLOUD_INDEX_MODE, getSisMode());
        config.put(SolrConnectorConfig.SOLR_CLOUD_COLLECTION, "user_desc");
        config.put(SolrConnectorConfig.CONNECTOR_NAME, "sis-test-task-solr");
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_URL, jdbcUrl);
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_USERNAME, "root");
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_PASSWORD, "sis-embedded");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "sis.solr.integration.test.v1.0");
        config.put(SolrConnectorConfig.SOURCE_CONFIGURATIONS, getSisSourceConfiguration());
        config.put(SolrConnectorConfig.EXECUTOR_CONFIGURATIONS, getSisExecutorConfiguration());
        SolrConnectorConfig connectorConfig = new SolrConnectorConfig(config);
        additionalConfig.put("topics", "sis.sis.user_desc");
        additionalConfig.put("tasks.max", 1);
        additionalConfig.put("sis.task", "sis.solr.integration.test.v1.0");
        additionalConfig.put("connector.class", "com.laomei.sis.solr.SolrConnector");
        additionalConfig.put("auto.offset.reset", getAutoOffsetReset());
        additionalConfig.put("schema.registry.url", "http://localhost:8082");
        engine = new EmbeddedEngine(connectorConfig, additionalConfig);
    }

    private void initSolrCollection() throws IOException, SolrServerException {
        if (!init.compareAndSet(false, true)) {
            return;
        }
        logger.info("create solr collection {}", USER_DESC);
        // create collection
        CollectionAdminRequest.Create create = CollectionAdminRequest.Create.createCollection(USER_DESC, 1, 1);
        create.process(solrClient);
        // create schema
        addField("name", "string", true, USER_DESC);
        addField("address", "string", true, USER_DESC);
        addField("weight", "pfloat", true, USER_DESC);
    }

    private void addField(String name, String type, boolean required, String collection)
            throws IOException, SolrServerException {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("name", name);
        field.put("type", type);
        field.put("stored", true);
        field.put("required", required);
        new SchemaRequest.AddField(field).process(solrClient, collection);
    }

    private void initSolrClient() {
        solrClient = new CloudSolrClient.Builder()
                .withZkHost(SOLR_CLOUD_URL)
                .build();
    }
}
