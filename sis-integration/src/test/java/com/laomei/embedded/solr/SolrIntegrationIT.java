package com.laomei.embedded.solr;

import com.laomei.embedded.EmbeddedEngine;
import com.laomei.embedded.JdbcBaseIT;
import com.laomei.sis.solr.SolrConnectorConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest.Create;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author laomei on 2019/2/22 19:24
 */
public class SolrIntegrationIT extends JdbcBaseIT {

    private static final String SOLR_CLOUD_URL = "localhost:2181";
    private static final String USER_DESC = "user_desc";

    private EmbeddedEngine engine;

    private CloudSolrClient solrClient;

    @Before
    public void init() throws IOException, SolrServerException, InterruptedException {
        super.init();
        initSolrClient();
        initSolrCollection();
        initEmbeddedEngine();
        new Thread(engine).start();
    }

    @After
    public void after() throws IOException {
        if (solrClient != null) {
            solrClient.close();
        }
        if (engine != null) {
            engine.close();
        }
    }

    @Test
    public void testSisSolr() throws IOException, SolrServerException, InterruptedException {
        jdbcTemplate.execute("INSERT INTO user_desc(name, address, weight) VALUES('user5', 'address5', 10.5)");
        // we need to wait to 2s;
        // sis will consume the record provided by dbz and insert the value to solr
        TimeUnit.SECONDS.sleep(5);
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("q", "name:user5");
        MapSolrParams params = new MapSolrParams(queryMap);
        QueryResponse response = solrClient.query(USER_DESC, params);
        SolrDocumentList documents = response.getResults();
        logger.info("***********");
        logger.info("documents: {}", documents);
        logger.info("***********");
        Assert.assertEquals(1, documents.size());
        Object address = documents.get(0).getFieldValue("address");
        Assert.assertEquals("address5", address);
    }

    private void initEmbeddedEngine() {
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> additionalConfig = new HashMap<>();
        String jdbcUrl = System.getProperty("spring.datasource.url");
        config.put(SolrConnectorConfig.SOLR_CLOUD_ZK_HOST, SOLR_CLOUD_URL);
        config.put(SolrConnectorConfig.SOLR_CLOUD_INDEX_MODE, "update");
        config.put(SolrConnectorConfig.SOLR_CLOUD_COLLECTION, "user_desc");
        config.put(SolrConnectorConfig.CONNECTOR_NAME, "sis-test-task");
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_URL, jdbcUrl);
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_USERNAME, "root");
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_PASSWORD, "sis-embedded");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "sis.integration.test.v1.0");
        String sourceConfiguration = readFile("dbz/solr/source_configuration.json");
        String executorConfiguration = readFile("dbz/solr/executor_configuration.json");
        config.put(SolrConnectorConfig.SOURCE_CONFIGURATIONS, sourceConfiguration);
        config.put(SolrConnectorConfig.EXECUTOR_CONFIGURATIONS, executorConfiguration);
        SolrConnectorConfig connectorConfig = new SolrConnectorConfig(config);
        additionalConfig.put("topics", "sis.sis.user_desc");
        additionalConfig.put("tasks.max", 1);
        additionalConfig.put("sis.task", "sis.integration.test");
        additionalConfig.put("connector.class", "com.laomei.sis.solr.SolrConnector");
        additionalConfig.put("auto.offset.reset", "earliest");
        additionalConfig.put("schema.registry.url", "http://localhost:8082");
        engine = new EmbeddedEngine(connectorConfig, additionalConfig);
    }

    private void initSolrCollection() throws IOException, SolrServerException {
        // create collection
        Create create = Create.createCollection(USER_DESC, 1, 1);
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
