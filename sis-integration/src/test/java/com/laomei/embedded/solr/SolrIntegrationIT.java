package com.laomei.embedded.solr;

import com.laomei.embedded.EmbeddedEngine;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CollectionAdminRequest.Create;
import org.apache.solr.client.solrj.request.schema.SchemaRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author laomei on 2019/2/22 19:24
 */
public class SolrIntegrationIT {

    private static final String USER_DESC = "user_desc";

    private EmbeddedEngine engine;

    private SolrClient solrClient;

    @Before
    public void init() {
        initSolrClient();
//        String jdbcUrl = System.getProperty("spring.datasource.url");
//        Map<String, Object> config = new HashMap<>();
//        config.put(SolrConnectorConfig.SOLR_CLOUD_ZK_HOST, "127.0.0.1:2181/solr");
//        config.put(SolrConnectorConfig.SOLR_CLOUD_INDEX_MODE, "update");
//        config.put(SolrConnectorConfig.SOLR_CLOUD_COLLECTION, "test");
//        config.put(SolrConnectorConfig.CONNECTOR_NAME, "sis-test-task");
//        config.put(SolrConnectorConfig.DEFAULT_MYSQL_URL, jdbcUrl);
//        config.put(SolrConnectorConfig.DEFAULT_MYSQL_USERNAME, "root");
//        config.put(SolrConnectorConfig.DEFAULT_MYSQL_PASSWORD, "sis-embedded");
//        config.put("topics", "sis.sis.user_desc");
//        config.put("tasks.max", 1);
//        config.put("connector.class", "com.laomei.sis.solr.SolrConnector");
//        SolrConnectorConfig connectorConfig = new SolrConnectorConfig(config);
//        engine = new EmbeddedEngine(connectorConfig);
//        new Thread(engine).start();
    }

    @After
    public void after() throws IOException {
        if (solrClient != null) {
            solrClient.close();
        }
    }

    @Test
    public void testSolrInit() throws IOException, SolrServerException {
//        engine.close();

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
        solrClient = new HttpSolrClient.Builder("http://localhost:8983/solr")
                .withConnectionTimeout(10000)
                .withSocketTimeout(60000)
                .build();
    }
}
