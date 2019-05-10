package com.laomei.embedded.es;

import com.laomei.embedded.AbstractEmbeddedEngineIT;
import org.apache.http.HttpHost;
import org.apache.solr.client.solrj.SolrServerException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author laomei on 2019/05/10 14:26
 */
public abstract class AbstractElasticsearchIntegrationIT extends AbstractEmbeddedEngineIT {

    protected static final String              INDEX_NAME          = "user_desc";

    protected static final AtomicBoolean       INIT                = new AtomicBoolean(false);

    protected              RestHighLevelClient restHighLevelClient = null;

    @Override
    public void init() throws IOException, SolrServerException, InterruptedException {
        initElasticsearchClient();
        initElasticsearchIndex();
        super.init();
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
