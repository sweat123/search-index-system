package com.laomei.sis.solr;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author laomei on 2018/12/1 14:52
 */
public class SolrSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(SolrSinkTask.class);

    private SolrConnectorConfig config;

    @Override
    public String version() {
        return Version.version();
    }

    @Override
    public void start(final Map<String, String> props) {
        log.info("Starting task");
        config = new SolrConnectorConfig(props);
        /*
            1. init transforms
            2. init executors
            3. init reducers
         */
        initContext();
    }

    @Override
    public void put(final Collection<SinkRecord> records) {

    }

    @Override
    public void stop() {
    }

    private void initContext() {

    }
}
