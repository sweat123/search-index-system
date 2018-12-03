package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import com.laomei.sis.model.Placeholder;

/**
 * @author laomei on 2018/12/3 14:00
 */
public class PlaceholderTransform implements Transform {

    private final Placeholder placeholder;

    public PlaceholderTransform(final Placeholder placeholder) {
        this.placeholder = placeholder;
    }

    @Override
    public SisRecord trans(final SisRecord sisRecord) {
        return null;
    }

    @Override
    public void close() {

    }
}
