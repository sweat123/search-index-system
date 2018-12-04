package com.laomei.sis;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author laomei 2018/12/4 19:23
 */
public abstract class JavaTypeConverterUtil {
    private static final String INT = "Integer";
    private static final String LONG = "Long";
    private static final String FLOAT = "Float";
    private static final String DOUBLE = "Double";
    private static final String BOOLEAN = "Boolean";
    private static final String DATETIME = "DateTime";

    public static Object javaTypeConvertToTargetType(Object javaObject, String targetJavaType) {
        if (javaObject == null) {
            return null;
        }
        if (javaObject instanceof List) {
            return convertMultiValues((List) javaObject, targetJavaType);
        }
        if (javaObject instanceof Number) {
            return convertNumberToTargetType((Number) javaObject, targetJavaType);
        }
        if (javaObject instanceof Timestamp) {
            return timestampConverter(javaObject);
        }
        if (javaObject instanceof LocalDateTime) {
            return localDateTimeConverter((LocalDateTime) javaObject);
        }
        if (javaObject instanceof ZonedDateTime) {
            return zonedDateTimeConverter((ZonedDateTime) javaObject);
        }
        if (javaObject instanceof String) {
            return convertStringToTargetType((String) javaObject, targetJavaType);
        }
        if (javaObject instanceof Boolean) {
            return convertBooleanToTargetType((Boolean) javaObject, targetJavaType);
        }
        return String.valueOf(javaObject);
    }

    private static List convertMultiValues(List javaObject, String type) {
        if (javaObject.isEmpty()) {
            return javaObject;
        }
        Object o = javaObject.get(0);
        if (o instanceof Number) {
            List list = new ArrayList<>(javaObject.size());
            javaObject.forEach(k -> list.add(convertNumberToTargetType((Number) k, type)));
            return list;
        }
        if (o instanceof Timestamp) {
            List list = new ArrayList<>(javaObject.size());
            javaObject.forEach(k -> list.add(timestampConverter(k)));
            return list;
        }
        if (o instanceof String) {
            List list = new ArrayList<>(javaObject.size());
            javaObject.forEach(k -> list.add(convertStringToTargetType((String) k, type)));
            return list;
        }
        if (o instanceof Boolean) {
            List list = new ArrayList<>(javaObject.size());
            javaObject.forEach(k -> list.add(convertBooleanToTargetType((Boolean) k, type)));
            return list;
        }
        return convertStringMultiValues(javaObject);
    }

    private static List<String> convertStringMultiValues(List javaObject) {
        List list = new ArrayList<>(javaObject.size());
        javaObject.forEach(k -> list.add(String.valueOf(k)));
        return list;
    }

    private static Object convertNumberToTargetType(Number javaObject, String targetToJavaType) {
        if (null == javaObject) {
            return null;
        }
        switch (targetToJavaType) {
        case INT: return javaObject.intValue();
        case LONG: return javaObject.longValue();
        case FLOAT: return javaObject.floatValue();
        case DOUBLE: return javaObject.doubleValue();
        case BOOLEAN: return javaObject.longValue() > 0 ? Boolean.TRUE : Boolean.FALSE;
        default: return String.valueOf(javaObject);
        }
    }

    private static Object convertBooleanToTargetType(Boolean o, String type) {
        switch (type) {
        case INT: return o ? 1 : 0;
        case LONG: return o ? 1L : 0;
        case FLOAT: return o ? 1F : 0;
        case DOUBLE: return o ? 1D : 0;
        case BOOLEAN: return o;
        default: return String.valueOf(o);
        }
    }

    private static Object convertStringToTargetType(String o, String type) {
        switch (type) {
        case INT: return Integer.valueOf(o);
        case LONG: return Long.valueOf(o);
        case FLOAT: return Float.valueOf(o);
        case DOUBLE: return Double.valueOf(o);
        case BOOLEAN: return Boolean.valueOf(o);
        case DATETIME: return convertToTimestamp(o);
        default: return o;
        }
    }

    private static Timestamp convertToTimestamp(String o) {
        ZonedDateTime zdt = ZonedDateTime.parse(o, DateTimeFormatter.ISO_DATE_TIME);
        return Timestamp.from(zdt.toInstant());
    }

    private static Timestamp localDateTimeConverter(LocalDateTime time) {
        return Timestamp.valueOf(time);
    }

    private static Timestamp zonedDateTimeConverter(ZonedDateTime zdt) {
        return Timestamp.from(zdt.toInstant());
    }

    private static Object timestampConverter(Object object) {
        return object;
    }
}
