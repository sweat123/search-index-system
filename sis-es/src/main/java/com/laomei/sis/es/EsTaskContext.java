package com.laomei.sis.es;

import com.laomei.sis.DefaultTaskContext;
import com.laomei.sis.SchemaHelper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.core.internal.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * @author yulei
 * @author laomei on 2019/3/12 18:12
 */
public class EsTaskContext extends DefaultTaskContext {
    private static final Logger logger = LoggerFactory.getLogger(EsTaskContext.class);

    private final EsConnectorConfig configs;

    public EsTaskContext(String name, EsConnectorConfig configs) {
        super(name, configs);
        this.configs = configs;
    }

    @Override
    public void initSinkReducer() {
        RestHighLevelClient client = createEsClient();
        SchemaHelper schemaHelper = new EsSchemaHelper(client, configs.esIndex, configs.esType);
        try {
            schemaHelper.init();
        } catch (Exception e) {
            try {
                IOUtils.close(client);
            } catch (IOException ignore) {
            }
            throw new RuntimeException(e);
        }
        BulkProcessor processor = createBulkProcessor(client);
        String mode = configs.esMode;
        if ("update".equals(mode)) {
            reducer = new EsUpdateReducer(schemaHelper, processor, client, configs);
        } else if ("delete".equals(mode)) {
            reducer = new EsDeleteReducer(schemaHelper, processor, client, configs);
        } else {
            throw new IllegalArgumentException("illegal solr index mode; you have to set index mode with 'update' or 'delete'");
        }
    }

    private BulkProcessor createBulkProcessor(RestHighLevelClient client) {
        return BulkProcessor.builder(client::bulkAsync, new BulkProcessor.Listener() {
                    @Override
                    public void beforeBulk(final long executionId, final BulkRequest request) {
                    }
                    @Override
                    public void afterBulk(final long executionId, final BulkRequest request, final BulkResponse response) {
                        if (response.hasFailures()) {
                            logger.error("error while processing bulk:" + response.buildFailureMessage());
                        }
                    }
                    @Override
                    public void afterBulk(final long executionId, final BulkRequest request, final Throwable failure) {
                        if (failure != null) {
                            logger.error("error while processing bulk", failure);
                        }
                    }
                }
        ).setFlushInterval(TimeValue.timeValueSeconds(1)).build();
    }

    private RestHighLevelClient createEsClient() {
        String hosts = configs.esAddress;
        String username = configs.esUsername;
        String password = configs.esPassword;
        RestClientBuilder builder = RestClient.builder(
                Stream.of(hosts.split(",")).map(HttpHost::create).toArray(HttpHost[]::new)
        );
        if (StringUtils.hasLength(username) && StringUtils.hasLength(password)) {
            final CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setHttpClientConfigCallback(b -> b.setDefaultCredentialsProvider(provider).setUserAgent("search-index-system"));
        }
        return new RestHighLevelClient(builder);
    }
}
