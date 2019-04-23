package com.laomei.sis.mysql;

import com.laomei.sis.SchemaHelper;
import com.laomei.sis.SisRecord;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
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
//            sisRecord.getContext();
        }
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

    private void joinToBuilder(StringBuilder builder, String delim, String[] items) {
        for (int i = 0; i < items.length; i++) {
            if (i > 0) {
                builder.append(delim);
            }
            builder.append(items[i]);
        }
    }

    private void copyToBuilder(StringBuilder builder, String delim, String item, int n) {
        for (int i = 0; i < n; i++) {
            if (i > 0) {
                builder.append(delim);
            }
            builder.append(item);
        }
    }

    private void joinKeyToBuilder(StringBuilder builder, String delim, String[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                builder.append(delim);
            }
            String key = fields[i];
            builder.append(key).append("=").append('?');
        }
    }
}
