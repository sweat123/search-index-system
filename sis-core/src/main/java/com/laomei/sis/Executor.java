package com.laomei.sis;

import com.laomei.sis.SisRecord;

/**
 * @author laomei on 2018/12/2 14:41
 */
public interface Executor {

    SisRecord execute(SisRecord record);

    void close();
}
