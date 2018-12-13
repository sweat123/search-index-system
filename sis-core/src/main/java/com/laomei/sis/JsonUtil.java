package com.laomei.sis;

import com.alibaba.fastjson.JSON;

/**
 * @author laomei on 2018/12/3 19:32
 */
public class JsonUtil {

    public static <T> T parse(String str, Class<T> tClass) {
        return JSON.parseObject(str, tClass);
    }
}
