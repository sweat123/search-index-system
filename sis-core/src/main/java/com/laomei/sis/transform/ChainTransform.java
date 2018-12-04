package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;

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
