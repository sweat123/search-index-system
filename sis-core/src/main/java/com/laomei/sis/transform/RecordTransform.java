package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import org.apache.avro.generic.GenericRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * @author laomei on 2018/12/3 13:30
 */
public class RecordTransform implements Transform {

    @Override
    public List<SisRecord> trans(final List<SisRecord> sisRecords) {
        List<SisRecord> records = new ArrayList<>(sisRecords.size());
        for (SisRecord record : sisRecords) {
            records.add(unWrapperRecord("after", record));
        }
        return records;
    }

    @Override
    public void close() {
    }

    private SisRecord unWrapperRecord(String key, SisRecord record) {
        SisRecord sisRecord = new SisRecord(record.getTopic());
        GenericRecord data = (GenericRecord) record.getValue(key);
        data.getSchema().getFields().forEach(field -> {
            sisRecord.addValue(field.name(), data.get(field.name()));
        });
        return sisRecord;
    }
}
