package com.laomei.sis.config;

import com.laomei.sis.BaseConnectorConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * @author laomei on 2018/12/14 19:22
 */
public class ConfigurationTest {

    private Map<String, Object> baseProps = new HashMap<>();

    @Before
    public void init() {
        baseProps.put("sis.name", "test-task");
        baseProps.put("sis.mysql.default.url", "localhost:3306");
        baseProps.put("sis.mysql.default.username", "test_user");
        baseProps.put("sis.mysql.default.password", "default");
        baseProps.put("sis.source.configuration", "{\\\"sourceConfigurations\\\": []}");
        baseProps.put("sis.executor.configuration", "{\\\"executorConfigurations\\\": [{}]}");
        baseProps.put("connector.class", "com.laomei.sis.solr.SolrConnector");
    }

    @Test
    public void test() {
        BaseConnectorConfig config = new BaseConnectorConfig(BaseConnectorConfig.CONFIG_DEF, baseProps);
        String defaultMysqlUrl = config.defaultMysqlUrl;
        String taskName = config.getString("sis.name");
        String connectClass = config.getOriginalValue("connector.class");
        assertThat(defaultMysqlUrl, equalTo("localhost:3306"));
        assertThat(taskName, equalTo("test-task"));
        assertThat(connectClass, equalTo("com.laomei.sis.solr.SolrConnector"));
    }
}
