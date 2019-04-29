package com.laomei.sis.mysql;

import com.laomei.sis.DefaultTaskContext;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Not support delete operator
 * @author laomei on 2019/4/23 19:40
 */
public class MysqlTaskContext extends DefaultTaskContext {

    private final MysqlConnectorConfig config;

    private HikariDataSource           dataSource;

    public MysqlTaskContext(final String name, final MysqlConnectorConfig config) {
        super(name, config);
        this.config = config;
    }

    @Override
    public void initSinkReducer() {
        String url = config.sinkMysqlUrl;
        String username = config.sinkMysqlUsername;
        String password = config.sinkMysqlPassword;
        JdbcTemplate jdbcTemplate;
        HikariDataSource ds;
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setMaximumPoolSize(10);
            ds = new HikariDataSource(config);
            jdbcTemplate = new JdbcTemplate(ds);
        } catch (Exception e) {
            throw new RuntimeException("init sink mysql jdbc template failed", e);
        }
        this.dataSource = ds;
        MysqlSchemaHelper schemaHelper = new MysqlSchemaHelper(jdbcTemplate, config.sinkMysqlTable);
        schemaHelper.init();
        // not we only support upsert
        reducer = new MysqlUpsertReducer(schemaHelper, jdbcTemplate, config.sinkMysqlTable);
    }

    @Override
    public void close() {
        super.close();
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
