package com.laomei.sis.es;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;

/**
 * @author yulei
 * Date: 2018/11/15
 * Time: 19:47
 */
public class EsUtil {
    private static final Logger logger = LoggerFactory.getLogger(EsUtil.class);

    public static Object transformJavaObjectToEs(final String esType, final Object v) {
        if (esType == null || v == null) {
            return v;
        }
        if ("keyword".equals(esType) || "text".equals(esType)) {
            return v.toString();
        }
        if ("date".equals(esType)) {
            if (v instanceof java.util.Date) {
                return ((java.util.Date) v).toInstant().toString();
            }
            if (v instanceof String) {
                String s = (String) v;
                if (s.contains("T")) {
                    return v;
                }
                try {
                    return Timestamp.valueOf(s).toInstant().toString();
                } catch (RuntimeException e) {
                    return v;
                }
            }
            return v;
        }
        if ("boolean".equals(esType)) {
            if (v instanceof Boolean) {
                return v;
            } else if (v instanceof String) {
                return BooleanUtils.toBooleanObject((String) v);
            } else if (v instanceof Number) {
                return ((Number) v).intValue() > 0;
            } else {
                logger.warn("unrecognized boolean type for:" + v.getClass().getName());
            }
        }
        if ("long".equals(esType)
                || "integer".equals(esType)
                || "float".equals(esType)
                || "double".equals(esType)) {
            if (v instanceof Boolean) {
                return (Boolean) v ? 1 : 0;
            }
            if (v instanceof String) {
                if ("true".equals(v)) {
                    return 1;
                }
                if ("false".equals(v)) {
                    return 0;
                }
            }
            if (v instanceof Number) {
                switch (esType) {
                    case "long":
                        return ((Number) v).longValue();
                    case "integer":
                        return ((Number) v).intValue();
                    case "float":
                        return ((Number) v).floatValue();
                    case "double":
                        return ((Number) v).doubleValue();
                }
            }
            return v;
        }
        return v;
    }
}
