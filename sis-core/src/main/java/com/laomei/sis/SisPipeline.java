package com.laomei.sis;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.connect.sink.SinkRecord;

import java.util.Collection;

/**
 * @author laomei on 2018/12/2 15:08
 */
public class SisPipeline implements Pipeline {

    private final AbstractTaskContext taskContext;

    private final Reducer             reducer;

    private final Transform           transform;

    private final Executor            executor;

    public SisPipeline(AbstractTaskContext taskContext) {
        this.taskContext = taskContext;
        this.transform = taskContext.transform();
        this.executor = taskContext.executor();
        this.reducer = taskContext.reducer();
    }

    @Override
    public void handle(Collection<SinkRecord> sinkRecords) {
        Collection<SisRecord> sisRecords = convertToSisRecord(sinkRecords);
    }

    @Override
    public void shutdown() {
        if (taskContext != null) {
            taskContext.close();
        }
    }

    private Collection<SisRecord> convertToSisRecord(Collection<SinkRecord> sinkRecords) {
        for (SinkRecord record : sinkRecords) {
            if (record == null || record.value() == null) {
                continue;
            }
            String topic = record.topic();
            GenericRecord value = (GenericRecord) record.value();
            GenericRecord before = (GenericRecord) value.get("before");
            GenericRecord after = (GenericRecord) value.get("after");

        }
    }
}
