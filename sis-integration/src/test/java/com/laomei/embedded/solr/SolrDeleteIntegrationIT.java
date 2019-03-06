package com.laomei.embedded.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * @author laomei on 2019/3/6 19:44
 */
public class SolrDeleteIntegrationIT extends AbstractSolrIntegrationIT {

    @Test
    public void testSisDeleteSolr() throws InterruptedException, IOException, SolrServerException {
        jdbcTemplate.execute("UPDATE user_desc SET address='-1' WHERE name='user1'");
        TimeUnit.SECONDS.sleep(2);
        SolrDocumentList list = query(USER_DESC, Collections.singletonMap("q", "name:user1"));
        Assert.assertTrue(list.isEmpty());
    }

    @Override
    protected String getAutoOffsetReset() {
        return "latest";
    }

    @Override
    protected String getSisMode() {
        return "delete";
    }

    @Override
    protected String getSisSourceConfiguration() {
        return readFile("dbz/solr/delete_source_configuration.json");
    }

    @Override
    protected String getSisExecutorConfiguration() {
        return readFile("dbz/solr/empty_executor_configuration.json");
    }
}
