package com.laomei.sis.transform;

import com.laomei.sis.SisRecord;
import com.laomei.sis.Transform;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author laomei on 2018/12/4 15:20
 */
public class SqlTransform implements Transform {

    private final String sql;

    private final JdbcTemplate jdbcTemplate;

    public SqlTransform(final String sql, final JdbcTemplate jdbcTemplate) {
        this.sql = sql;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SisRecord trans(final SisRecord sisRecord) {
        return null;
    }

    @Override
    public void close() {
    }
}
