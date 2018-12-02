package com.laomei.sis;

/**
 * @author laomei on 2018/12/2 14:36
 */
public interface Transform {

    SisRecord trans(SisRecord record);

    void close();
}
