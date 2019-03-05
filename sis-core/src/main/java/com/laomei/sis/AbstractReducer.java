package com.laomei.sis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author laomei on 2018/12/4 19:17
 */
public abstract class AbstractReducer implements Reducer {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final SchemaHelper schemaHelper;

    public AbstractReducer(final SchemaHelper schemaHelper) {
        this.schemaHelper = schemaHelper;
    }

    @Override
    public abstract void reduce(final List<SisRecord> sisRecords);

    @Override
    public abstract void close();
}
