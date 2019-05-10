package com.laomei.embedded.es;

import org.apache.solr.client.solrj.SolrServerException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.document.DocumentField;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author luobo.hwz on 2019/05/10 16:59
 */
public class ElasticsearchUpdateIntegrationIT extends AbstractElasticsearchIntegrationIT {

    @Before
    @Override
    public void init() throws IOException, SolrServerException, InterruptedException {
        super.init();
    }

    @After
    @Override
    public void after() throws IOException {
        super.after();
    }

    @Test
    public void testEsInitData() throws IOException {
        GetRequest getRequest = new GetRequest("user_desc", "1");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        DocumentField nameField = getResponse.getField("name");
        String name = nameField.getValue();
        Assert.assertEquals("user1", name);
    }

    @Override
    protected String getSisMode() {
        return "update";
    }

    @Override
    protected String getSisSourceConfiguration() {
        return readFile("es/update_source_configuration.json");
    }

    @Override
    protected String getSisExecutorConfiguration() {
        return readFile("es/empty_executor_configuration.json");
    }
}
