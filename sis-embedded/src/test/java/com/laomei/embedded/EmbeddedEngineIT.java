/*
 * EmbeddedEngineIT.java
 * Copyright 2019 Qunhe Tech, all rights reserved.
 * Qunhe PROPRIETARY/CONFIDENTIAL, any form of usage is subject to approval.
 */

package com.laomei.embedded;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        initDbzTask();
    }

    @Test
    public void testDBInit() {
        List<String> tables = jdbcTemplate.query("SHOW TABLES", rs -> {
            List<String> results = new ArrayList<>();
            while (rs.next()) {
                String o = rs.getString(1);
                results.add(o);
            }
            return results;
        });
        tables.forEach(System.out::println);
        Assert.assertEquals(tables.size(), 2);
    }

    @Test
    public void testKafkaInit() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "sis-integration-test-group-v1");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        config.put("schema-registry-url", "127.0.0.1");
//        KafkaConsumer<GenericRecord, GenericRecord> consumer = new KafkaConsumer<>(config);
//        consumer.subscribe("");
    }

    private void initDbzTask() {

    }
}
