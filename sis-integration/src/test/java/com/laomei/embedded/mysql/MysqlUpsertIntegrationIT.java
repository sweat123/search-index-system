package com.laomei.embedded.mysql;

import com.laomei.embedded.AbstractEmbeddedEngineIT;
import com.laomei.embedded.EmbeddedEngine;
import com.laomei.sis.mysql.MysqlConnectorConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author laomei on 2019/4/24 14:14
 */
public class MysqlUpsertIntegrationIT extends AbstractEmbeddedEngineIT {

    @Before
    @Override
    public void init() throws IOException, SolrServerException, InterruptedException {
        super.init();
        //make sure the mysql connector task is started;
        TimeUnit.SECONDS.sleep(5);
    }

    @After
    @Override
    public void after() throws IOException {
        super.after();
    }

    @Test
    public void testSisUpsertMysql() throws InterruptedException {
        AtomicReference<String> name = new AtomicReference<>(null);
        AtomicReference<String> address = new AtomicReference<>(null);
        getColumn(name, address);
        Assert.assertEquals("user2", name.get());
        Assert.assertEquals("address2", address.get());
        jdbcTemplate.update("UPDATE user_desc SET address = 'address2-new' WHERE id = 2");
        //sleep 3s to make sure the change has been updated to mysql;
        TimeUnit.SECONDS.sleep(3);
        getColumn(name, address);
        Assert.assertEquals("address2-new", address.get());
    }

    private void getColumn(AtomicReference<String> name, AtomicReference<String> address) {
        jdbcTemplate.query("SELECT id, name, address FROM user_desc_sink WHERE id = 2",
                rs -> {
                    String n = rs.getString("name");
                    String add = rs.getString("address");
                    name.set(n);
                    address.set(add);
                });
    }

    @Override
    protected void initEmbeddedEngine() {
        logger.info("build mysql embedded engine");
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> additionalConfig = new HashMap<>();
        String jdbcUrl = System.getProperty("spring.datasource.url");
        String username = System.getProperty("spring.datasource.username");
        String password = System.getProperty("spring.datasource.password");
        config.put(MysqlConnectorConfig.SINK_MYSQL_URL, jdbcUrl);
        config.put(MysqlConnectorConfig.SINK_MYSQL_USERNAME, username);
        config.put(MysqlConnectorConfig.SINK_MYSQL_PASSWORD, password);
        config.put(MysqlConnectorConfig.SINK_MYSQL_TABLE, "user_desc_sink");
        config.put(MysqlConnectorConfig.CONNECTOR_NAME, "sis-test-task-mysql");
        config.put(MysqlConnectorConfig.DEFAULT_MYSQL_URL, jdbcUrl);
        config.put(MysqlConnectorConfig.DEFAULT_MYSQL_USERNAME, "root");
        config.put(MysqlConnectorConfig.DEFAULT_MYSQL_PASSWORD, "sis-embedded");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "sis.mysql.integration.test.v1.0");
        config.put(MysqlConnectorConfig.SOURCE_CONFIGURATIONS, getSisSourceConfiguration());
        config.put(MysqlConnectorConfig.EXECUTOR_CONFIGURATIONS, getSisExecutorConfiguration());
        MysqlConnectorConfig connectorConfig = new MysqlConnectorConfig(config);
        additionalConfig.put("topics", "sis.sis.user_desc");
        additionalConfig.put("tasks.max", 1);
        additionalConfig.put("sis.task", "sis.mysql.integration.test.v1.0");
        additionalConfig.put("connector.class", "com.laomei.sis.mysql.MysqlConnector");
        additionalConfig.put("auto.offset.reset", "earliest");
        additionalConfig.put("schema.registry.url", "http://localhost:8082");
        engine = new EmbeddedEngine(connectorConfig, additionalConfig);
    }

    @Override
    protected String getSisSourceConfiguration() {
        return readFile("mysql/update_source_configuration.json");
    }

    @Override
    protected String getSisExecutorConfiguration() {
        return readFile("mysql/empty_executor_configuration.json");
    }
}
