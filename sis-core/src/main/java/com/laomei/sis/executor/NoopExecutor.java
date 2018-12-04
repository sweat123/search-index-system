package com.laomei.sis.executor;

import com.laomei.sis.Executor;
import com.laomei.sis.SisRecord;

import java.util.List;

/**
 * @author laomei on 2018/12/4 16:28
 */
public class NoopExecutor implements Executor {
    @Override
    public List<SisRecord> execute(final List<SisRecord> sisRecords) {
        return sisRecords;
    }

    @Override
    public void close() {
    }
}
