package com.laomei.sis.mysql;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

/**
 * @author laomei on 2019/3/8 11:32
 */
public class MysqlSinkTask extends SinkTask {
    private static final Logger logger = LoggerFactory.getLogger(MysqlSinkTask.class);

    @Override
    public String version() {
        return Version.version();
    }

    @Override
    public void start(final Map<String, String> props) {

    }

    @Override
    public void put(final Collection<SinkRecord> records) {

    }

    @Override
    public void stop() {

    }
}
