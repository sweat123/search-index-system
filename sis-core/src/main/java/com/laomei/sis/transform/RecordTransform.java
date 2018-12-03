package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import org.apache.avro.generic.GenericRecord;

/**
 * @author laomei on 2018/12/3 13:30
 */
public class RecordTransform implements Transform {

    @Override
    public SisRecord trans(final SisRecord sisRecord) {
        return unWrapperRecord("after", sisRecord);
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
