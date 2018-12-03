package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import com.laomei.sis.model.Filter;

/**
 * @author laomei on 2018/12/3 15:58
 */
public class FilterTransform implements Transform {

    private final Filter filter;

    public FilterTransform(final Filter filter) {
        this.filter = filter;
    }

    @Override
    public SisRecord trans(final SisRecord sisRecord) {
        return null;
    }

    @Override
    public void close() {

    }
}
