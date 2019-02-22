package com.laomei.embedded.solr;

import com.laomei.embedded.EmbeddedEngine;
import com.laomei.sis.solr.SolrConnectorConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author laomei on 2019/2/22 19:24
 */
public class SolrIntegrationTest {

    private EmbeddedEngine engine;

    @Before
    public void init() {
        String jdbcUrl = System.getProperty("spring.datasource.url");
        Map<String, Object> config = new HashMap<>();
        config.put(SolrConnectorConfig.SOLR_CLOUD_ZK_HOST, "127.0.0.1:2181/solr");
        config.put(SolrConnectorConfig.SOLR_CLOUD_INDEX_MODE, "update");
        config.put(SolrConnectorConfig.SOLR_CLOUD_COLLECTION, "test");
        config.put(SolrConnectorConfig.CONNECTOR_NAME, "sis-test-task");
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_URL, jdbcUrl);
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_USERNAME, "root");
        config.put(SolrConnectorConfig.DEFAULT_MYSQL_PASSWORD, "sis-embedded");
        config.put("topics", "sis.sis.user_desc");
        config.put("tasks.max", 1);
        config.put("connector.class", "com.laomei.sis.solr.SolrConnector");
        SolrConnectorConfig connectorConfig = new SolrConnectorConfig(config);
        engine = new EmbeddedEngine(connectorConfig);
        new Thread(engine).start();
    }

    @Test
    public void test() {
        engine.close();
    }
}
