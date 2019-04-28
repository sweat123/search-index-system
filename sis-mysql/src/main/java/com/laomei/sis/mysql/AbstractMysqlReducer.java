package com.laomei.sis.mysql;

import com.laomei.sis.AbstractReducer;
import com.laomei.sis.SchemaHelper;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author laomei on 2019/4/23 20:33
 */
public abstract class AbstractMysqlReducer extends AbstractReducer {

    protected final JdbcTemplate jdbcTemplate;

    protected final String       tableName;

    public AbstractMysqlReducer(final SchemaHelper schemaHelper, final JdbcTemplate jdbcTemplate, final String tableName) {
        super(schemaHelper);
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
    }

    @Override
    public void close() {
        if (schemaHelper != null) {
            schemaHelper.close();
        }
    }
}
