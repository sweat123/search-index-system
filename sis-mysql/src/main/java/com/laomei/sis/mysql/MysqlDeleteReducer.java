package com.laomei.sis.mysql;

import com.laomei.sis.SchemaHelper;
import com.laomei.sis.SisRecord;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

/**
 * @author laomei on 2019/4/23 20:25
 */
public class MysqlDeleteReducer extends AbstractMysqlReducer {

    public MysqlDeleteReducer(final SchemaHelper schemaHelper, final JdbcTemplate jdbcTemplate, final String tableName) {
        super(schemaHelper, jdbcTemplate, tableName);
    }

    @Override
    public void reduce(final List<SisRecord> sisRecords) {

    }
}
