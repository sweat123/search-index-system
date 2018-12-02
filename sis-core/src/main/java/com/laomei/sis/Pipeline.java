package com.laomei.sis;

import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collection;

/**
 * @author laomei on 2018/12/2 15:03
 */
public interface Pipeline {

    void handle(Collection<SinkRecord> sinkRecords);

    void shutdown();
}
