package com.laomei.sis;

import java.util.List;

/**
 * @author laomei on 2018/12/1 20:43
 */
public interface Reducer {

    void reduce(List<SisRecord> sisRecords);

    void close();
}
