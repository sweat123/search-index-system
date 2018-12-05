package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import org.apache.kafka.connect.data.Struct;

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
        Struct data = (Struct) record.getValue(key);
        data.schema().fields().forEach(field -> {
            String name = field.name();
            Object value = data.get(field);
            sisRecord.addValue(name, value);
        });
        return sisRecord;
    }
}
