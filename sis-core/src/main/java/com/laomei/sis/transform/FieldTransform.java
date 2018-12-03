package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import org.apache.avro.generic.GenericRecord;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Compare 'before' and 'after' value in record;
 * If filed value is same, we need drop current record;
 *
 * @author laomei on 2018/12/3 12:35
 */
public class FieldTransform implements Transform {

    private final Collection<String> fields;

    private FieldTransform(Collection<String> fields) {
        this.fields = fields;
    }

    @Override
    public List<SisRecord> trans(final List<SisRecord> sisRecords) {
        final List<SisRecord> records = new ArrayList<>(sisRecords.size());
        for (SisRecord sisRecord : sisRecords) {
            if (fieldsValueNotSame(sisRecord)) {
                records.add(sisRecord);
            }
        }
        return records;
    }

    /**
     * 1. If the value 'before' and 'after' is both null, return false;
     * 2. If any field value in 'before' and 'after' is different, return false;
     * 3. return true;
     */
    private boolean fieldsValueNotSame(SisRecord sisRecord) {
        GenericRecord before = (GenericRecord) sisRecord.getValue("before");
        GenericRecord after = (GenericRecord) sisRecord.getValue("after");
        if (after == null && before == null) {
            return false;
        }
        if (after == null || before == null) {
            return true;
        }
        for (String field : fields) {
            if (!Objects.equals(before.get(field), after.get(field))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void close() {
    }
}
