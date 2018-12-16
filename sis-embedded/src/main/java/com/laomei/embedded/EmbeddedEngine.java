package com.laomei.embedded;

import com.laomei.sis.BaseConnectorConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.sink.SinkTaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author laomei on 2018/12/16 21:02
 */
public class EmbeddedEngine {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BaseConnectorConfig config;

    private final ClassLoader         classLoader;

    private final long                timeOutMs;

    private final Set<String>         topics;

    public EmbeddedEngine(BaseConnectorConfig config) {
        this.config = config;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.timeOutMs = config.getLong("connect.timeout.ms");
        this.topics = Arrays.stream(config.getString("topics").split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    public void run() {
        final String sisTaskName = config.getString("sis.task");
        final String connectorClassName = config.getOriginalValue("connector.class");
        SinkConnector connector = null;

        //instance sink connector
        try {
            Class<? extends SinkConnector> connectorClass = (Class<? extends SinkConnector>) classLoader.loadClass(connectorClassName);
            connector = connectorClass.newInstance();
        } catch (Exception e) {
            logger.error("Failed to start connector; Can not find class {}", connectorClassName);
            return;
        }

        ConnectorContext connectorContext = new ConnectorContext() {
            @Override
            public void requestTaskReconfiguration() {
            }

            @Override
            public void raiseError(Exception e) {
                logger.error(e.getMessage());
            }
        };
        connector.initialize(connectorContext);
        connector.start(convert(config.getOriginalConfigs()));
        Class<? extends Task> taskClass = connector.taskClass();
        SinkTask task = null;
        try {
            task = (SinkTask) taskClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            logger.error("instance sis task failed");
            return;
        }
        task.initialize(new DefaultSinkTaskContext());
        task.start(connector.taskConfigs(1).get(0));

        //todo: get records & call task put method
        /*
         * 1. we can get kafka records from file; The format of record must is the same as record created by debezium
         * 2. put records to sink storage
         */
    }

    public void close() {
    }

    private Map<String, String> convert(Map<String, ?> config) {
        Map<String, String> map = new HashMap<>(config.size());
        config.forEach((key, value) -> {
            map.put(key, String.valueOf(value));
        });
        return map;
    }

    /**
     * simple implement
     */
    protected static class DefaultSinkTaskContext implements SinkTaskContext {

        @Override
        public void offset(Map<TopicPartition, Long> offsets) {
        }

        @Override
        public void offset(TopicPartition tp, long offset) {
        }

        @Override
        public void timeout(long timeoutMs) {
        }

        @Override
        public Set<TopicPartition> assignment() {
            return null;
        }

        @Override
        public void pause(TopicPartition... partitions) {
        }

        @Override
        public void resume(TopicPartition... partitions) {
        }

        @Override
        public void requestCommit() {
        }
    }
}
