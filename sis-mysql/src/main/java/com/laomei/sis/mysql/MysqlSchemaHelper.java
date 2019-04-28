package com.laomei.sis.mysql;

import com.laomei.sis.SchemaHelper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author laomei on 2019/3/8 11:34
 */
public class MysqlSchemaHelper implements SchemaHelper {

    private static final String SHOW_TABLE_COLUMNS = "SHOW COLUMNS FROM %s";

    private final JdbcTemplate  jdbcTemplate;

    private final String        tableName;

    private Map<String, String> schemaMap;

    public MysqlSchemaHelper(final JdbcTemplate jdbcTemplate, final String tableName) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
    }

    @Override
    public void init() {
        final Map<String, String> schema = new HashMap<>(8);
        String sql = String.format(SHOW_TABLE_COLUMNS, tableName);
        jdbcTemplate.query(sql, rs -> {
            String field = rs.getString("Field");
            String type = rs.getString("Type");
            schema.put(field, type);
        });
        schemaMap = schema;
    }

    @Override
    public String getTargetClass(final String key) {
        return schemaMap.get(key);
    }

    @Override
    public String[] getFields() {
        return schemaMap.keySet().toArray(new String[0]);
    }

    @Override
    public void close() {
        if (schemaMap != null) {
            schemaMap.clear();
        }
    }
}
