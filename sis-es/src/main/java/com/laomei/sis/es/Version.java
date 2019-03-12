package com.laomei.sis.es;

import java.util.Properties;

/**
 * @author laomei on 2019/3/12 18:42
 */
public class Version {

    private static String VERSION = "unknown";

    public static String version() {
        if (VERSION == null) {
            synchronized (Version.class) {
                if (VERSION == null) {
                    initVersion();
                }
            }
        }
        return VERSION;
    }

    private static void initVersion() {
        try {
            Properties props = new Properties();
            props.load(Version.class.getResourceAsStream("/build.version"));
            VERSION = props.getProperty("version");
        } catch (Exception ignore) {
        }
    }
}
