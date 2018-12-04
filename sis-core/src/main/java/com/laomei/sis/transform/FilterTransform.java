package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import com.laomei.sis.model.Filter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * @author laomei on 2018/12/3 15:58
 */
public class FilterTransform implements Transform {

    /**
     *  a gt b ==> a > b
     */
    private static final String GT = "gt";

    /**
     * a gte b ==> a >= b
     */
    private static final String GTE = "gte";

    /**
     * a lt b ==> a < b
     */
    private static final String LT = "lt";

    /**
     * a lte b ==> a <= b
     */
    private static final String LTE = "lte";

    private final Filter filter;

    public FilterTransform(final Filter filter) {
        this.filter = filter;
    }

    @Override
    public SisRecord trans(final SisRecord sisRecord) {
        Map<String, Object> ctx = sisRecord.getContext();
        //not null
        if (null != filter.getExist()) {
            ctx = doExistFilter(ctx, filter.getExist());
            if (null == ctx) {
                return null;
            }
        }
        //null
        if (null != filter.getNotExist()) {
            ctx = doNotExistFilter(ctx, filter.getNotExist());
            if (null == ctx) {
                return null;
            }
        }
        //field equal xxx
        if (null != filter.getMatch()) {
            ctx = doMatchFilter(ctx, filter.getMatch());
            if (null == ctx) {
                return null;
            }
        }
        //field not equal xxx
        if (null != filter.getNotMatch()) {
            ctx = doNotMatchFilter(ctx, filter.getNotMatch());
            if (null == ctx) {
                return null;
            }
        }
        //field in range
        if (null != filter.getRange()) {
            ctx = doRangeFilter(ctx, filter.getRange());
            if (null == ctx) {
                return null;
            }
        }
        //field in (a, b, c)
        if (null != filter.getIn()) {
            ctx = doInFilter(ctx, filter.getIn());
            if (null == ctx) {
                return null;
            }
        }
        //field not in (a, b, c)
        if (null != filter.getNotIn()) {
            ctx = doNotInFilter(ctx, filter.getNotIn());
            if (null == ctx) {
                return null;
            }
        }
        return ctx == null ? null : sisRecord;
    }

    @Override
    public void close() {
    }

    private Map<String, Object> doNotInFilter(Map<String, Object> context, Map<String, List<String>> notInfilter) {
        for (Map.Entry<String, List<String>> filter : notInfilter.entrySet()) {
            String field = filter.getKey();
            List<String> values = filter.getValue();
            Object ctxV = context.get(field);
            if (!context.containsKey(field)) {
                continue;
            }
            if (!notInValues(ctxV, values)) {
                return null;
            }
        }
        return context;
    }

    private Map<String, Object> doInFilter(Map<String, Object> context, Map<String, List<String>> infilter) {
        for (Map.Entry<String, List<String>> filter : infilter.entrySet()) {
            String field = filter.getKey();
            List<String> values = filter.getValue();
            Object ctxV = context.get(field);
            if (!context.containsKey(field)) {
                continue;
            }
            if (!inValues(ctxV, values)) {
                return null;
            }
        }
        return context;
    }

    private Map<String, Object> doRangeFilter(Map<String, Object> context, Map<String, Map<String, String>> rangefilter) {
        for (Map.Entry<String, Map<String, String>> filter : rangefilter.entrySet()) {
            String field = filter.getKey();
            Object ctxVal = context.get(field);
            if (!context.containsKey(field)) {
                continue;
            }
            if (!ctxFieldInRange(ctxVal, filter.getValue())) {
                return null;
            }
        }
        return context;
    }

    private Map<String, Object> doNotMatchFilter(Map<String, Object> context, Map<String, String> notMatchs) {
        for (Map.Entry<String, String> notMatch : notMatchs.entrySet()) {
            String field = notMatch.getKey();
            String notExpVal = notMatch.getValue();
            Object ctxVal = context.get(field);
            if (!context.containsKey(field)) {
                continue;
            }
            if (ctxVal instanceof Number && isEqual(notExpVal, (Number) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof Timestamp && isEqual(notExpVal, (Timestamp) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof LocalDateTime && isEqual(notExpVal, (LocalDateTime) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof ZonedDateTime && isEqual(notExpVal, (ZonedDateTime) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof Boolean && isEqual(notExpVal, (Boolean) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof String && isEqual(notExpVal, (String) ctxVal)) {
                return null;
            }
            if (isEqual(notExpVal, String.valueOf(ctxVal))) {
                return null;
            }
        }
        return context;
    }

    private Map<String, Object> doMatchFilter(Map<String, Object> context, Map<String, String> matchs) {
        for (Map.Entry<String, String> match : matchs.entrySet()) {
            String field = match.getKey();
            String expVal = match.getValue();
            Object ctxVal = context.get(field);
            if (!context.containsKey(field)) {
                continue;
            }
            if (ctxVal instanceof Number && !isEqual(expVal, (Number) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof Timestamp && !isEqual(expVal, (Timestamp) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof LocalDateTime && !isEqual(expVal, (LocalDateTime) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof ZonedDateTime && !isEqual(expVal, (ZonedDateTime) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof Boolean && !isEqual(expVal, (Boolean) ctxVal)) {
                return null;
            }
            if (ctxVal instanceof String && !isEqual(expVal, (String) ctxVal)) {
                return null;
            }
            if (!isEqual(expVal, String.valueOf(ctxVal))) {
                return null;
            }
        }
        return context;
    }

    private Map<String, Object> doNotExistFilter(Map<String, Object> context, List<String> notExist) {
        for (String field : notExist) {
            Object value = context.get(field);
            if (null != value) {
                return null;
            }
        }
        return context;
    }

    private Map<String, Object> doExistFilter(Map<String, Object> context, List<String> exists) {
        for (String field : exists) {
            Object value = context.get(field);
            if (!context.containsKey(field)) {
                continue;
            }
            if (null == value) {
                return null;
            }
        }
        return context;
    }

    private boolean notInValues(Object ctxVal, List<String> vals) {
        if (ctxVal instanceof Number) {
            return numberCtxValNotInValues((Number) ctxVal, vals);
        }
        if (ctxVal instanceof Timestamp) {
            return timestampCtxValNotInValues((Timestamp) ctxVal, vals);
        }
        if (ctxVal instanceof LocalDateTime) {
            return localDateTimeValNotInValues((LocalDateTime) ctxVal, vals);
        }
        if (ctxVal instanceof ZonedDateTime) {
            return zonedDateValTimeNotInValues((ZonedDateTime) ctxVal, vals);
        }
        if (ctxVal instanceof Boolean) {
            return booleanValNotInValues((Boolean) ctxVal, vals);
        }
        if (ctxVal instanceof String) {
            return stringValNotInVals(String.valueOf(ctxVal), vals);
        }
        return stringValNotInVals(String.valueOf(ctxVal), vals);
    }

    private boolean booleanValNotInValues(Boolean ctxVal, List<String> vals) {
        return vals.stream()
                .map(Boolean::parseBoolean)
                .noneMatch(ctxVal::equals);
    }

    private boolean zonedDateValTimeNotInValues(ZonedDateTime ctxVal, List<String> vals) {
        return vals.stream()
                .map(k -> ZonedDateTime.parse(k, ISO_LOCAL_DATE))
                .noneMatch(ctxVal::equals);
    }

    private boolean localDateTimeValNotInValues(LocalDateTime ctxVal, List<String> vals) {
        return vals.stream()
                .map(k -> LocalDateTime.parse(k, ISO_DATE_TIME))
                .noneMatch(ctxVal::equals);
    }

    private boolean timestampCtxValNotInValues(Timestamp ctxVal, List<String> vals) {
        return vals.stream()
                .map(k -> Timestamp.valueOf(LocalDateTime.parse(k, ISO_DATE_TIME)))
                .noneMatch(ctxVal::equals);
    }

    private boolean numberCtxValNotInValues(Number ctxVal, List<String> vals) {
        if (ctxVal instanceof Integer) {
            return ctxValIntegerValNotInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Long) {
            return ctxValLongValNotInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Short) {
            return ctxValShortValNotInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Float) {
            return ctxValFloatValNotInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Double) {
            return ctxValDoubleValNotInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Byte) {
            return ctxValByteValNotInVals(ctxVal, vals);
        }
        if (ctxVal instanceof BigDecimal) {
            return ctxValBigDecimalValNotInVals(ctxVal, vals);
        }
        if (ctxVal instanceof BigInteger) {
            return ctxValBigIntegerValNotInVals(ctxVal, vals);
        }
        return stringValNotInVals(String.valueOf(ctxVal), vals);
    }

    private boolean stringValNotInVals(String ctxVal, List<String> vals) {
        return vals.stream().noneMatch(ctxVal::equals);
    }

    private boolean ctxValBigIntegerValNotInVals(Number ctxVal, List<String> vals) {
        return vals.stream().map(BigInteger::new).noneMatch(ctxVal::equals);
    }

    private boolean ctxValBigDecimalValNotInVals(Number ctxVal, List<String> vals) {
        return vals.stream().map(BigDecimal::new).noneMatch(ctxVal::equals);
    }

    private boolean ctxValByteValNotInVals(Number ctxVal, List<String> vals) {
        return vals.stream().map(Byte::valueOf).noneMatch(ctxVal::equals);
    }

    private boolean ctxValDoubleValNotInVals(Number ctxVal, List<String> vals) {
        return vals.stream().map(Double::valueOf).noneMatch(ctxVal::equals);
    }

    private boolean ctxValFloatValNotInVals(Number ctxVal, List<String> vals) {
        return vals.stream().map(Float::valueOf).noneMatch(ctxVal::equals);
    }

    private boolean ctxValShortValNotInVals(Number ctxVal, List<String> vals) {
        return vals.stream().map(Short::valueOf).noneMatch(ctxVal::equals);
    }

    private boolean ctxValLongValNotInVals(Number ctxVal, List<String> vals) {
        return vals.stream().map(Long::valueOf).noneMatch(ctxVal::equals);
    }

    private boolean ctxValIntegerValNotInVals(Number ctxVal, List<String> vals) {
        return vals.stream().map(Integer::valueOf).noneMatch(ctxVal::equals);
    }

    private boolean inValues(Object ctxVal, List<String> vals) {
        if (ctxVal instanceof Number) {
            return numberCtxValInValues((Number) ctxVal, vals);
        }
        if (ctxVal instanceof Timestamp) {
            return timestampCtxValInValues((Timestamp) ctxVal, vals);
        }
        if (ctxVal instanceof LocalDateTime) {
            return localDateTimeValInValues((LocalDateTime) ctxVal, vals);
        }
        if (ctxVal instanceof ZonedDateTime) {
            return zonedDateValTimeInValues((ZonedDateTime) ctxVal, vals);
        }
        if (ctxVal instanceof Boolean) {
            return booleanValInValues((Boolean) ctxVal, vals);
        }
        if (ctxVal instanceof String) {
            return stringValInVals(String.valueOf(ctxVal), vals);
        }
        return stringValInVals(String.valueOf(ctxVal), vals);
    }

    private boolean booleanValInValues(Boolean ctxVal, List<String> vals) {
        return vals.stream()
                .map(Boolean::parseBoolean)
                .anyMatch(ctxVal::equals);
    }

    private boolean zonedDateValTimeInValues(ZonedDateTime ctxVal, List<String> vals) {
        return vals.stream()
                .map(k -> ZonedDateTime.parse(k, ISO_LOCAL_DATE))
                .anyMatch(ctxVal::equals);
    }

    private boolean localDateTimeValInValues(LocalDateTime ctxVal, List<String> vals) {
        return vals.stream()
                .map(k -> LocalDateTime.parse(k, ISO_DATE_TIME))
                .anyMatch(ctxVal::equals);
    }

    private boolean timestampCtxValInValues(Timestamp ctxVal, List<String> vals) {
        return vals.stream()
                .map(k -> Timestamp.valueOf(LocalDateTime.parse(k, ISO_DATE_TIME)))
                .anyMatch(ctxVal::equals);
    }

    private boolean numberCtxValInValues(Number ctxVal, List<String> vals) {
        if (ctxVal instanceof Integer) {
            return ctxValIntegerValInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Long) {
            return ctxValLongValInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Short) {
            return ctxValShortValInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Float) {
            return ctxValFloatValInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Double) {
            return ctxValDoubleValInVals(ctxVal, vals);
        }
        if (ctxVal instanceof Byte) {
            return ctxValByteValInVals(ctxVal, vals);
        }
        if (ctxVal instanceof BigDecimal) {
            return ctxValBigDecimalValInVals((BigDecimal) ctxVal, vals);
        }
        if (ctxVal instanceof BigInteger) {
            return ctxValBigIntegerValInVals((BigInteger) ctxVal, vals);
        }
        return stringValInVals(String.valueOf(ctxVal), vals);
    }

    private boolean stringValInVals(String ctxVal, List<String> vals) {
        return vals.stream().anyMatch(ctxVal::equals);
    }

    private boolean ctxValBigIntegerValInVals(BigInteger ctxVal, List<String> vals) {
        return vals.stream()
                .map(BigInteger::new)
                .anyMatch(k -> ctxVal.compareTo(k) == 0);
    }

    private boolean ctxValBigDecimalValInVals(BigDecimal ctxVal, List<String> vals) {
        return vals.stream()
                .map(BigDecimal::new)
                .anyMatch(k -> ctxVal.compareTo(k) == 0);
    }

    private boolean ctxValByteValInVals(Number ctxVal, List<String> vals) {
        byte ctxValByte = ctxVal.byteValue();
        return vals.stream()
                .map(Byte::valueOf)
                .anyMatch(k -> Byte.compare(ctxValByte, k) == 0);
    }

    private boolean ctxValDoubleValInVals(Number ctxVal, List<String> vals) {
        double ctxValDouble = ctxVal.doubleValue();
        return vals.stream()
                .map(Double::valueOf)
                .anyMatch(k -> Double.compare(ctxValDouble, k) == 0);
    }

    private boolean ctxValFloatValInVals(Number ctxVal, List<String> vals) {
        float ctxValFloat = ctxVal.floatValue();
        return vals.stream()
                .map(Float::valueOf)
                .anyMatch(k -> Float.compare(ctxValFloat, k) == 0);
    }

    private boolean ctxValShortValInVals(Number ctxVal, List<String> vals) {
        short ctxValShort = ctxVal.shortValue();
        return vals.stream()
                .map(Short::valueOf)
                .anyMatch(k -> Short.compare(ctxValShort, k) == 0);
    }

    private boolean ctxValLongValInVals(Number ctxVal, List<String> vals) {
        long ctxValLong = ctxVal.longValue();
        return vals.stream()
                .map(Long::valueOf)
                .anyMatch(k -> Long.compare(ctxValLong, k) == 0);
    }

    private boolean ctxValIntegerValInVals(Number ctxVal, List<String> vals) {
        int ctxValInt = ctxVal.intValue();
        return vals.stream()
                .map(Integer::valueOf)
                .anyMatch(k -> Integer.compare(ctxValInt, k) == 0);
    }

    /**
     * ZonedDateTime is equal to expVal
     * @param expVal
     * @param ctxVal
     * @return
     */
    private boolean isEqual(String expVal, ZonedDateTime ctxVal) {
        return ZonedDateTime.parse(expVal, ISO_LOCAL_DATE).equals(ctxVal);
    }

    /**
     * LocalDateTime is equal to expVal
     * @param expVal
     * @param ctxVal
     * @return
     */
    private boolean isEqual(String expVal, LocalDateTime ctxVal) {
        return LocalDateTime.parse(expVal, ISO_LOCAL_DATE).equals(ctxVal);
    }

    /**
     * String is equal to expVal
     * @param expVal
     * @param ctxVal
     * @return
     */
    private boolean isEqual(String expVal, String ctxVal) {
        return String.valueOf(ctxVal).equals(expVal);
    }

    /**
     * Boolean is equal to expVal
     * @param expVal
     * @param ctxVal
     * @return
     */
    private boolean isEqual(String expVal, Boolean ctxVal) {
        try {
            int val = Integer.parseInt(expVal);
            if (val > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {}
        boolean expBooleanVal = Boolean.parseBoolean(expVal);
        return expBooleanVal == ctxVal;
    }

    /**
     * Timestamp is equal to expVal
     * @param expVal
     * @param ctxVal
     * @return
     */
    private boolean isEqual(String expVal, Timestamp ctxVal) {
        return ctxVal.equals(Timestamp.valueOf(LocalDateTime.parse(expVal, ISO_DATE_TIME)));
    }

    /**
     * Number is equal to expVal
     * @param expVal
     * @param ctxVal
     * @return
     */
    private boolean isEqual(String expVal, Number ctxVal) {
        if (ctxVal instanceof Integer) {
            return Integer.compare(ctxVal.intValue(), Integer.valueOf(expVal)) == 0;
        }
        if (ctxVal instanceof Long) {
            return Long.compare(ctxVal.longValue(), Long.valueOf(expVal)) == 0;
        }
        if (ctxVal instanceof Short) {
            return Short.compare(ctxVal.shortValue(), Short.valueOf(expVal)) == 0;
        }
        if (ctxVal instanceof Float) {
            return Float.compare(ctxVal.floatValue(), Float.valueOf(expVal)) == 0;
        }
        if (ctxVal instanceof Double) {
            return Double.compare(ctxVal.doubleValue(), Double.valueOf(expVal)) == 0;
        }
        if (ctxVal instanceof Byte) {
            return Byte.compare(ctxVal.byteValue(), Byte.valueOf(expVal)) == 0;
        }
        if (ctxVal instanceof BigDecimal) {
            BigDecimal decimal = new BigDecimal(expVal);
            return ((BigDecimal) ctxVal).compareTo(decimal) == 0;
        }
        if (ctxVal instanceof BigInteger) {
            BigInteger integer = new BigInteger(expVal);
            return ((BigInteger) ctxVal).compareTo(integer) == 0;
        }
        return String.valueOf(ctxVal).equals(String.valueOf(expVal));
    }

    private boolean ctxFieldInRange(Object ctxValue, Map<String, String> ranges) {
        if (ctxValue instanceof Number) {
            return ctxNumberInRange((Number) ctxValue, ranges);
        } else if (ctxValue instanceof Boolean) {
            return ctxNumberInRange(((Boolean) ctxValue) ? 1 : 0, ranges);
        } else if (ctxValue instanceof Timestamp) {
            return ctxTsInRange((Timestamp) ctxValue, ranges);
        } else if (ctxValue instanceof ZonedDateTime) {
            return ctxZdtInRange((ZonedDateTime) ctxValue, ranges);
        } else if (ctxValue instanceof LocalDateTime) {
            return ctxLdtInRange((LocalDateTime) ctxValue, ranges);
        } else if (ctxValue instanceof String) {
            return ctxStrInRange((String) ctxValue, ranges);
        } else {
            return ctxStrInRange(String.valueOf(ctxValue), ranges);
        }
    }

    /**
     * LocalDateTime in range
     * @param ctxVal
     * @param range
     * @return
     */
    private boolean ctxLdtInRange(LocalDateTime ctxVal, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && ctxVal.compareTo(LocalDateTime.parse(gtv, ISO_DATE_TIME)) <= 0) {
            return false;
        }
        if (null != gtev && ctxVal.compareTo(LocalDateTime.parse(gtev, ISO_DATE_TIME)) < 0) {
            return false;
        }
        if (null != ltv && ctxVal.compareTo(LocalDateTime.parse(ltv, ISO_DATE_TIME)) >= 0) {
            return false;
        }
        if (null != ltev && ctxVal.compareTo(LocalDateTime.parse(ltev, ISO_DATE_TIME)) > 0) {
            return false;
        }
        return true;
    }

    /**
     * ZonedDateTime in range
     * @param ctxVal
     * @param range
     * @return
     */
    private boolean ctxZdtInRange(ZonedDateTime ctxVal, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && ctxVal.compareTo(ZonedDateTime.parse(gtv, ISO_DATE_TIME)) <= 0) {
            return false;
        }
        if (null != gtev && ctxVal.compareTo(ZonedDateTime.parse(gtev, ISO_DATE_TIME)) < 0) {
            return false;
        }
        if (null != ltv && ctxVal.compareTo(ZonedDateTime.parse(ltv, ISO_DATE_TIME)) >= 0) {
            return false;
        }
        if (null != ltev && ctxVal.compareTo(ZonedDateTime.parse(ltev, ISO_DATE_TIME)) > 0) {
            return false;
        }
        return true;
    }

    /**
     * Timestamp in range
     * @param ctxVal
     * @param range
     * @return
     */
    private boolean ctxTsInRange(Timestamp ctxVal, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && ctxVal.compareTo(Timestamp.valueOf(gtv)) <= 0) {
            return false;
        }
        if (null != gtev && ctxVal.compareTo(Timestamp.valueOf(gtev)) < 0) {
            return false;
        }
        if (null != ltv && ctxVal.compareTo(Timestamp.valueOf(ltv)) >= 0) {
            return false;
        }
        if (null != ltev && ctxVal.compareTo(Timestamp.valueOf(ltev)) > 0) {
            return false;
        }
        return true;
    }

    /**
     * string in range
     * @param ctxVal
     * @param range
     * @return
     */
    private boolean ctxStrInRange(String ctxVal, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && ctxVal.compareTo(gtv) <= 0) {
            return false;
        }
        if (null != gtev && ctxVal.compareTo(gtev) < 0) {
            return false;
        }
        if (null != ltv && ctxVal.compareTo(ltv) >= 0) {
            return false;
        }
        if (null != ltev && ctxVal.compareTo(ltev) > 0) {
            return false;
        }
        return true;
    }

    /**
     * number in range
     * @param ctxVal
     * @param range
     * @return
     */
    private boolean ctxNumberInRange(Number ctxVal, Map<String, String> range) {
        if (ctxVal instanceof Integer) {
            return integerValueInRange((Integer) ctxVal, range);
        }
        if (ctxVal instanceof Double) {
            return doubleValueInRange((Double) ctxVal, range);
        }
        if (ctxVal instanceof Float) {
            return floatValueInRange((Float) ctxVal, range);
        }
        if (ctxVal instanceof Byte) {
            return byteValueInRange((Byte) ctxVal, range);
        }
        if (ctxVal instanceof Long) {
            return longValueInRange((Long) ctxVal, range);
        }
        if (ctxVal instanceof Short) {
            return shortValueInRange((Short) ctxVal, range);
        }
        if (ctxVal instanceof BigInteger) {
            return bigIntegerValueInRange((BigInteger) ctxVal, range);
        }
        if (ctxVal instanceof BigDecimal) {
            return bigDecimalValueInRange((BigDecimal) ctxVal, range);
        }
        return ctxStrInRange(String.valueOf(ctxVal), range);
    }

    private boolean integerValueInRange(Integer o, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && o.compareTo(Integer.parseInt(gtv)) <= 0) {
            return false;
        }
        if (null != gtev && o.compareTo(Integer.parseInt(gtev)) < 0) {
            return false;
        }
        if (null != ltv && o.compareTo(Integer.parseInt(ltv)) >= 0) {
            return false;
        }
        if (null != ltev && o.compareTo(Integer.parseInt(ltev)) > 0) {
            return false;
        }
        return true;
    }

    private boolean longValueInRange(Long o, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && o.compareTo(Long.parseLong(gtv)) <= 0) {
            return false;
        }
        if (null != gtev && o.compareTo(Long.parseLong(gtev)) < 0) {
            return false;
        }
        if (null != ltv && o.compareTo(Long.parseLong(ltv)) >= 0) {
            return false;
        }
        if (null != ltev && o.compareTo(Long.parseLong(ltev)) > 0) {
            return false;
        }
        return true;
    }

    private boolean shortValueInRange(Short o, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && o.compareTo(Short.parseShort(gtv)) <= 0) {
            return false;
        }
        if (null != gtev && o.compareTo(Short.parseShort(gtev)) < 0) {
            return false;
        }
        if (null != ltv && o.compareTo(Short.parseShort(ltv)) >= 0) {
            return false;
        }
        if (null != ltev && o.compareTo(Short.parseShort(ltev)) > 0) {
            return false;
        }
        return true;
    }

    private boolean byteValueInRange(Byte o, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && o.compareTo(Byte.parseByte(gtv)) <= 0) {
            return false;
        }
        if (null != gtev && o.compareTo(Byte.parseByte(gtev)) < 0) {
            return false;
        }
        if (null != ltv && o.compareTo(Byte.parseByte(ltv)) >= 0) {
            return false;
        }
        if (null != ltev && o.compareTo(Byte.parseByte(ltev)) > 0) {
            return false;
        }
        return true;
    }

    private boolean doubleValueInRange(Double o, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && o.compareTo(Double.parseDouble(gtv)) <= 0) {
            return false;
        }
        if (null != gtev && o.compareTo(Double.parseDouble(gtev)) < 0) {
            return false;
        }
        if (null != ltv && o.compareTo(Double.parseDouble(ltv)) >= 0) {
            return false;
        }
        if (null != ltev && o.compareTo(Double.parseDouble(ltev)) > 0) {
            return false;
        }
        return true;
    }

    private boolean floatValueInRange(Float o, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv && o.compareTo(Float.parseFloat(gtv)) <= 0) {
            return false;
        }
        if (null != gtev && o.compareTo(Float.parseFloat(gtev)) < 0) {
            return false;
        }
        if (null != ltv && o.compareTo(Float.parseFloat(ltv)) >= 0) {
            return false;
        }
        if (null != ltev && o.compareTo(Float.parseFloat(ltev)) > 0) {
            return false;
        }
        return true;
    }

    private boolean bigDecimalValueInRange(BigDecimal o, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv) {
            BigDecimal bigDecimalGtv = new BigDecimal(gtv);
            if (o.compareTo(bigDecimalGtv) <= 0) {
                return false;
            }
        }
        if (null != gtev) {
            BigDecimal bigDecimalGtev = new BigDecimal(gtev);
            if (o.compareTo(bigDecimalGtev) <= 0) {
                return false;
            }
        }
        if (null != ltv) {
            BigDecimal bigDecimalLtv = new BigDecimal(ltv);
            if (o.compareTo(bigDecimalLtv) <= 0) {
                return false;
            }
        }
        if (null != ltev) {
            BigDecimal bigDecimalLtev = new BigDecimal(ltev);
            if (o.compareTo(bigDecimalLtev) <= 0) {
                return false;
            }
        }
        return true;
    }

    private boolean bigIntegerValueInRange(BigInteger o, Map<String, String> range) {
        String gtv = range.get(GT);
        String gtev = range.get(GTE);
        String ltv = range.get(LT);
        String ltev = range.get(LTE);
        if (null != gtv) {
            BigInteger bigIntegerGtv = new BigInteger(gtv);
            if (o.compareTo(bigIntegerGtv) <= 0) {
                return false;
            }
        }
        if (null != gtev) {
            BigInteger bigIntegerGtev = new BigInteger(gtev);
            if (o.compareTo(bigIntegerGtev) <= 0) {
                return false;
            }
        }
        if (null != ltv) {
            BigInteger bigIntegerLtv = new BigInteger(ltv);
            if (o.compareTo(bigIntegerLtv) <= 0) {
                return false;
            }
        }
        if (null != ltev) {
            BigInteger bigIntegerLtev = new BigInteger(ltev);
            if (o.compareTo(bigIntegerLtev) <= 0) {
                return false;
            }
        }
        return true;
    }
}
