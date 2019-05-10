package com.laomei.embedded.es;

import com.laomei.embedded.AbstractEmbeddedEngineIT;
import com.laomei.embedded.EmbeddedEngine;
import com.laomei.sis.es.EsConnectorConfig;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.solr.client.solrj.SolrServerException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author laomei on 2019/05/10 14:26
 */
public abstract class AbstractElasticsearchIntegrationIT extends AbstractEmbeddedEngineIT {

    protected static final String              INDEX_NAME          = "user_desc";

    protected static final AtomicBoolean       INIT                = new AtomicBoolean(false);

    protected              RestHighLevelClient restHighLevelClient = null;

    protected abstract String getSisMode();

    @Override
    public void init() throws IOException, SolrServerException, InterruptedException {
        initElasticsearchClient();
        initElasticsearchIndex();
        super.init();
    }

    @Override
    public void after() throws IOException {
        restHighLevelClient.close();
    }

    @Override
    protected void initEmbeddedEngine() {
        logger.info("build elasticsearch embedded engine");
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> additionalConfig = new HashMap<>();
        String jdbcUrl = System.getProperty("spring.datasource.url");
        config.put(EsConnectorConfig.ES_ADDRESS, "http://localhost:9200");
        config.put(EsConnectorConfig.ES_MODE, getSisMode());
        config.put(EsConnectorConfig.ES_INDEX, "user_desc");
        config.put(EsConnectorConfig.CONNECTOR_NAME, "sis-test-task-es");
        config.put(EsConnectorConfig.DEFAULT_MYSQL_URL, jdbcUrl);
        config.put(EsConnectorConfig.DEFAULT_MYSQL_USERNAME, "root");
        config.put(EsConnectorConfig.DEFAULT_MYSQL_PASSWORD, "sis-embedded");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "sis.es.integration.test.v1.0");
        config.put(EsConnectorConfig.SOURCE_CONFIGURATIONS, getSisSourceConfiguration());
        config.put(EsConnectorConfig.EXECUTOR_CONFIGURATIONS, getSisExecutorConfiguration());
        EsConnectorConfig connectorConfig = new EsConnectorConfig(config);
        additionalConfig.put("topics", "sis.sis.user_desc");
        additionalConfig.put("tasks.max", 1);
        additionalConfig.put("sis.task", "sis.es.integration.test.v1.0");
        additionalConfig.put("connector.class", "com.laomei.sis.es.EsConnector");
        additionalConfig.put("auto.offset.reset", "earliest");
        additionalConfig.put("schema.registry.url", "http://localhost:8082");
        engine = new EmbeddedEngine(connectorConfig, additionalConfig);
    }

    private void initElasticsearchIndex() throws IOException {
        if (!INIT.compareAndSet(false, true)) {
            return;
        }
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("mappings");
            {
                builder.startObject();
                {
                    builder.startObject("_doc");
                    {
                        builder.startObject("properties");
                        {
                            builder.startObject("id");
                            builder.field("type", "long");
                            builder.endObject();
                        }
                        {
                            builder.startObject("name");
                            builder.field("type", "text");
                            builder.endObject();
                        }
                        {
                            builder.startObject("address");
                            builder.field("type", "text");
                            builder.endObject();
                        }
                        {
                            builder.startObject("weight");
                            builder.field("type", "double");
                            builder.endObject();
                        }
                        builder.endObject();
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(INDEX_NAME);
        createIndexRequest.mapping(builder);
        CreateIndexResponse response = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        logger.info("create elasticsearch index {} successful; resp: {}", INDEX_NAME, response);
    }

    private void initElasticsearchClient() {
        restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
    }
}
