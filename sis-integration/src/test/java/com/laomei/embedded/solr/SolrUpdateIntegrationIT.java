package com.laomei.embedded.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author laomei on 2019/2/22 19:24
 */
public class SolrUpdateIntegrationIT extends AbstractSolrIntegrationIT {

    @Test
    public void testSisUpdateSolr() throws IOException, SolrServerException, InterruptedException {
        jdbcTemplate.execute("INSERT INTO user_desc(name, address, weight) VALUES('user5', 'address5', 10.5)");
        // we need to wait to 2s;
        // sis will consume the record provided by dbz and insert the value to solr
        TimeUnit.SECONDS.sleep(5);
        SolrDocumentList documents = query(USER_DESC, Collections.singletonMap("q", "name:user5"));
        logger.info("***********");
        logger.info("documents: {}", documents);
        logger.info("***********");
        Assert.assertEquals(1, documents.size());
        Object address = documents.get(0).getFieldValue("address");
        Assert.assertEquals("address5", address);
    }

    @Override
    protected String getSisMode() {
        return "update";
    }

    @Override
    protected String getSisSourceConfiguration() {
        return readFile("dbz/solr/update_source_configuration.json");
    }

    @Override
    protected String getSisExecutorConfiguration() {
        return readFile("dbz/solr/empty_executor_configuration.json");
    }
}
