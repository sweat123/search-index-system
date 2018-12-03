package com.laomei.sis;

import java.util.List;

/**
 * @author laomei on 2018/12/2 14:36
 */
public interface Transform {

    List<SisRecord> trans(List<SisRecord> sisRecords);

    void close();
}
