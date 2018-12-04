package com.laomei.sis;

/**
 * @author laomei on 2018/12/4 19:15
 */
public interface SchemaHelper {

    void init();

    String getTargetClass(String key);

    void close();
}
