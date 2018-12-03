package com.laomei.sis;

/**
 * @author laomei on 2018/12/3 13:58
 */
public abstract class PlaceholderUtil {

    /**
     * ${xxx} => xxx
     */
    public static String parsePlaceholder(String str) {
        return str.substring(2, str.length() - 1);
    }
}
