package com.laomei.embedded;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author laomei on 2019/4/24 14:18
 */
public abstract class AbstractEmbeddedEngineIT extends JdbcBaseIT {

    protected EmbeddedEngine  engine;

    private   ExecutorService executorService = Executors.newFixedThreadPool(2);

    @Override
    public void init() throws IOException, SolrServerException, InterruptedException {
        super.init();
        initEmbeddedEngine();
        executorService.execute(engine);
    }

    @After
    public void after() throws IOException {
        if (engine != null) {
            engine.close();
        }
    }

    protected abstract void initEmbeddedEngine();

    protected abstract String getSisSourceConfiguration();

    protected abstract String getSisExecutorConfiguration();
}
