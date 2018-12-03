package com.laomei.sis;

import org.apache.kafka.connect.sink.SinkRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author laomei on 2018/12/2 15:08
 */
public class SisPipeline implements Pipeline {

    private static final Logger log = LoggerFactory.getLogger(SisPipeline.class);

    private final AbstractTaskContext taskContext;

    private final Reducer             reducer;

    private final TransformContext    transformContext;

    private final Executor            executor;

    private final AtomicBoolean       isClose;

    public SisPipeline(AbstractTaskContext taskContext) {
        this.taskContext = taskContext;
        this.transformContext = taskContext.transformContext();
        this.executor = taskContext.executor();
        this.reducer = taskContext.reducer();
        this.isClose = new AtomicBoolean(false);
    }

    @Override
    public void handle(Collection<SinkRecord> sinkRecords) {
        if (isClose.get()) {
            return;
        }
        List<SisRecord> sisRecords = convertToSisRecord(sinkRecords);
        List<SisRecord> transedRecords = new ArrayList<>(sisRecords.size());
        for (SisRecord sisRecord : sisRecords) {
            String topic = sisRecord.getTopic();
            Transform transform = transformContext.getTransform(topic);
            SisRecord record = transform.trans(sisRecord);
            if (record != null) {
                transedRecords.add(record);
            }
        }
        if (transedRecords.isEmpty()) {
            return;
        }
        List<SisRecord> executedRecords = executor.execute(transedRecords);
        if (executedRecords.isEmpty()) {
            return;
        }
        reducer.reduce(sisRecords);
    }

    @Override
    public void shutdown() {
        if (isClose.compareAndSet(false, true)) {
            if (taskContext != null) {
                log.info("shutdown SisPipeline; Pipeline task name {}", taskContext.getTaskName());
                taskContext.close();
            }
        }
    }

    private List<SisRecord> convertToSisRecord(Collection<SinkRecord> sinkRecords) {
        List<SisRecord> sisRecords = new ArrayList<>(sinkRecords.size());
        for (SinkRecord record : sinkRecords) {
            if (record == null || record.value() == null) {
                continue;
            }
            sisRecords.add(new SisRecord(record));
        }
        return sisRecords;
    }
}
