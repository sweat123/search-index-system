package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;

import java.util.Collections;
import java.util.List;

/**
 * @author laomei on 2018/12/3 10:34
 */
public class ChainTransform implements Transform {

    private List<Transform> transforms;

    public ChainTransform(List<Transform> transforms) {
        this.transforms = transforms;
    }

    @Override
    public List<SisRecord> trans(final List<SisRecord> sisRecords) {
        List<SisRecord> records = sisRecords;
        for (Transform transform : transforms) {
            records = transform.trans(records);
            if (records.isEmpty()) {
                return Collections.emptyList();
            }
        }
        return records;
    }

    @Override
    public SisRecord trans(final SisRecord sisRecord) {
        SisRecord record = null;
        for (Transform transform : transforms) {
            record = transform.trans(sisRecord);
            if (record == null) {
                return null;
            }
        }
        return record;
    }

    @Override
    public void close() {
        transforms.forEach(Transform::close);
    }
}
