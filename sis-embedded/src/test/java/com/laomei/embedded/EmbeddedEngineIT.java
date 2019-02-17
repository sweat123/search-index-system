/*
 * EmbeddedEngineIT.java
 * Copyright 2019 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.laomei.embedded;

import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author luobo.hwz on 2019/2/17 15:22
 */
public class EmbeddedEngineIT {

    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:13306/sis?useSSL=false");
        dataSource.setUsername("sis-user");
        dataSource.setPassword("sis-password");
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    public void test() {
        jdbcTemplate.query("SHOW TABLES", new ResultSetExtractor<Object>() {
            @Override
            public Object extractData(final ResultSet rs) throws SQLException, DataAccessException {
                while (rs.next()) {
                    Object o = rs.getObject(0);
                    System.out.println(o);
                }
                return null;
            }
        });
    }
}
