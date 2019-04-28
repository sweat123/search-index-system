package com.laomei.sis;

import com.laomei.sis.exception.JdbcContextException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author laomei on 2018/12/4 15:28
 */
public class JdbcContext {

    public static final String DEFAULT_JDBC_TEMPLATE = "default_jdbc_template";

    private final Map<String, JdbcTemplate> jdbcTemplateMap;

    private final Set<DataSource> dataSources;

    public JdbcContext() {
        this.jdbcTemplateMap = new HashMap<>();
        this.dataSources = new HashSet<>();
    }

    public void addDataSource(String url, String username, String password, String alias)
            throws JdbcContextException {
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
            throw new JdbcContextException("init jdbc template failed", e);
        }
        jdbcTemplateMap.put(alias, jdbcTemplate);
        dataSources.add(ds);
    }

    public JdbcTemplate getJdbcTemplateByAlias(String alias) {
        return jdbcTemplateMap.get(alias);
    }

    public JdbcTemplate getDefaultJdbcTemplate() {
        return getJdbcTemplateByAlias(DEFAULT_JDBC_TEMPLATE);
    }

    public void close() {
        for (DataSource dataSource : dataSources) {
            if (dataSource instanceof HikariDataSource) {
                ((HikariDataSource) dataSource).close();
            }
        }
    }
}
