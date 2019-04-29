package com.laomei.sis.mysql;

import com.laomei.sis.JavaTypeConverterUtil;
import com.laomei.sis.SchemaHelper;
import com.laomei.sis.SisRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author laomei on 2019/4/23 20:25
 */
public class MysqlUpsertReducer extends AbstractMysqlReducer {

    private final String   batchSql;

    private final String[] fields;

    public MysqlUpsertReducer(final SchemaHelper schemaHelper, final JdbcTemplate jdbcTemplate, final String tableName) {
        super(schemaHelper, jdbcTemplate, tableName);
        this.fields = initTableFields();
        this.batchSql = initBatchSql();
    }

    @Override
    public void reduce(final List<SisRecord> sisRecords) {
        List<Map<String, Object>> documents = new ArrayList<>(sisRecords.size());
        for (final SisRecord sisRecord : sisRecords) {
            final Map<String, Object> context = sisRecord.getContext();
            Map<String, Object> document = getMysqlDocument(context);
            if (document != null) {
                documents.add(document);
            }
        }
        if (documents.isEmpty()) {
            return;
        }
        // batch execute
        jdbcTemplate.batchUpdate(batchSql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(final PreparedStatement ps, final int i) throws SQLException {
                Map<String, Object> document = documents.get(i);
                int paramNum = fields.length;
                for (int idx = 0; idx < paramNum; idx ++) {
                    Object v = document.get(fields[idx]);
                    ps.setObject(idx + 1, v);
                    ps.setObject(idx + 1 + paramNum, v);
                }
            }

            @Override
            public int getBatchSize() {
                return documents.size();
            }
        });
        logger.info("update mysql table {}, records size {}", tableName, documents.size());
    }

    private Map<String, Object> getMysqlDocument(final Map<String, Object> context) {
        final Map<String, Object> document = new HashMap<>();
        expose(context, document);
        return document;
    }

    private void expose(Map<String, Object> context, Map<String, Object> document) {
        if (null == context) {
            logger.error("context can not be null!");
            throw new NullPointerException("context can not be null!");
        }
        context.forEach((k, v) -> {
            if (v instanceof Map) {
                expose((Map<String, Object>) v, document);
            } else {
                String targetClass = schemaHelper.getTargetClass(k);
                if (StringUtils.hasLength(targetClass)) {
                    String javaType = toJavaType(targetClass).getSimpleName();
                    Object targetValue = JavaTypeConverterUtil.javaTypeConvertToTargetType(v, javaType);
                    document.put(k, targetValue);
                }
            }
        });
    }

    private String[] initTableFields() {
        return schemaHelper.getFields();
    }

    private String initBatchSql() {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(tableName);
        builder.append("(");
        joinToBuilder(builder, ",", fields);
        builder.append(") VALUES(");
        copyToBuilder(builder, ",", "?", fields.length);
        builder.append(") ON DUPLICATE KEY UPDATE ");
        joinKeyToBuilder(builder, ",", fields);
        return builder.toString();
    }

    private static void joinToBuilder(StringBuilder builder, String delim, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                builder.append(delim);
            }
            builder.append(items[i]);
        }
    }

    private static void copyToBuilder(StringBuilder builder, String delim, String item, int n) {
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                builder.append(delim);
            }
            builder.append(item);
        }
    }

    private static void joinKeyToBuilder(StringBuilder builder, String delim, String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                builder.append(delim);
            }
            String key = fields[i];
            builder.append(key).append("=").append('?');
        }
    }

    private static Class toJavaType(String mysqlType) {
        switch (mysqlType) {
            case "tinyint":
            case "smallint":
            case "mediumint": return Integer.class;
            case "bit": return Boolean.class;
            case "float": return Float.class;
            case "double": return Double.class;
            case "blob": return byte[].class;
            case "text":
            case "varchar":
            case "char": return String.class;
            case "int": return Long.class;
            case "bigint": return BigInteger.class;
            case "decimal": return BigDecimal.class;
            case "date": return Date.class;
            case "time": return Time.class;
            case "timestamp":
            case "datetime": return Timestamp.class;
            default: return String.class;
        }
    }
}
