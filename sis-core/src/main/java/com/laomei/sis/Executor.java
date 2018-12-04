package com.laomei.sis;

import java.util.List;

/**
 * @author laomei on 2018/12/2 14:41
 */
public interface Executor {

    List<SisRecord> execute(List<SisRecord> sisRecords);

    void close();
}
