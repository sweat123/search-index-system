package com.laomei.embedded;

import com.laomei.embedded.util.HttpUtil;
import com.laomei.embedded.util.TopicUtil;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author laomei on 2019/3/3 16:24
 */
public abstract class JdbcBaseIT {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final AtomicBoolean initDbz = new AtomicBoolean(false);
    protected JdbcTemplate jdbcTemplate;

    @Before
    public void init() throws IOException, SolrServerException, InterruptedException {
        initDbzTask();
        String jdbcUrl = System.getProperty("spring.datasource.url");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername("sis-user");
        dataSource.setPassword("sis-password");
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    protected void initDbzTask() throws IOException, InterruptedException {
        if (!initDbz.compareAndSet(false, true)) {
            return;
        }
        createDbzHistoryTopic();
        String config = readFile("dbz/connect.json");
        if (StringUtils.isEmpty(config)) {
            throw new IllegalStateException("dbz config can not be blank");
        }
        HttpUtil.doPost("http://localhost:8083/connectors", config);
        //sleep 5s to make sure dbz task has started;
        TimeUnit.SECONDS.sleep(5);
    }

    private void createDbzHistoryTopic() {
        TopicUtil.createTopic("127.0.0.1:9092", "dbhistory.sis", 1,  (short) 1);
        info("create dbz history topic " + "dbhistory.sis" + " succeed.");
    }

    protected String readFile(String location) {
        String config = null;
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(location)) {
            byte[] bytes = IOUtils.toByteArray(in);
            config = new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException("read file '" + location + "' failed");
        }
        return config;
    }

    protected void info(String msg) {
        logger.info("[IT Test] {}", msg);
    }
}
