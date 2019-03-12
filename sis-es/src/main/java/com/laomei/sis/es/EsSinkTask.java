package com.laomei.sis.es;

import com.laomei.sis.Pipeline;
import com.laomei.sis.SisPipeline;
import com.laomei.sis.exception.JdbcContextException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author laomei on 2019/3/12 15:23
 */
public class EsSinkTask extends SinkTask {
    private static final Logger log = LoggerFactory.getLogger(EsSinkTask.class);

    private EsConnectorConfig config;

    private Pipeline          pipeline;

    @Override
    public String version() {
        return Version.version();
    }

    @Override
    public void start(final Map<String, String> props) {
        log.info("Starting es sink task; configs: {}", props);
        config = new EsConnectorConfig(props);
        try {
            initPipeline();
        } catch (JdbcContextException e) {
            throw new RuntimeException("init sink pipeline failed", e);
        }
    }

    @Override
    public void put(final Collection<SinkRecord> records) {
        if (records.isEmpty()) {
            return;
        }
        log.info("Received {} records. Begin to handle by pipeline", records.size());
        pipeline.handle(records);
        log.info("Finished handle {} records", records.size());
    }

    @Override
    public void stop() {
        if (pipeline != null) {
            pipeline.shutdown();
        }
    }

    private void initPipeline() throws JdbcContextException {
        EsTaskContext esTaskContext = new EsTaskContext(config.getString("sis.name"), config);
        esTaskContext.initJdbcContext();
        esTaskContext.initTransform();
        esTaskContext.initExecutor();
        esTaskContext.initReducer();
        this.pipeline = new SisPipeline(esTaskContext);
    }
}
