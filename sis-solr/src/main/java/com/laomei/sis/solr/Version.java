package com.laomei.sis.solr;

import com.laomei.sis.IoUtil;

import java.util.Properties;

/**
 * @author laomei on 2018/12/1 12:32
 */
public class Version {

    private static Properties VERSION = null;

    public static String version() {
        if (VERSION == null) {
            synchronized (Version.class) {
                if (VERSION == null) {
                    initVersion();
                }
            }
        }
        return VERSION.getProperty("version");
    }

    private static void initVersion() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = "com/laomei/sis/solr/build.version";
        VERSION = IoUtil.loadProperties(path, classLoader);
    }
}
