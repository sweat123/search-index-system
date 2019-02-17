/*
 * EmbeddedEngineIT.java
 * Copyright 2019 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.laomei.embedded;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.ArrayList;
import java.util.List;

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
        List<String> tables = jdbcTemplate.query("SHOW TABLES", rs -> {
            List<String> results = new ArrayList<>();
            while (rs.next()) {
                String o = rs.getString(1);
                results.add(o);
            }
            return results;
        });
        Assert.assertEquals(tables.size(), 2);
    }
}
