package com.laomei.embedded;

import org.apache.avro.generic.GenericRecord;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author laomei on 2019/2/17 15:22
 */
public class EmbeddedEngineIT {

    private JdbcTemplate jdbcTemplate;

    @Before
    public void init() throws IOException, InterruptedException {
        initDbzTask();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:13306/sis?useSSL=false");
        dataSource.setUsername("sis-user");
        dataSource.setPassword("sis-password");
        jdbcTemplate = new JdbcTemplate(dataSource);
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
        config.put("schema-registry-url", "127.0.0.1:8082");
        KafkaConsumer<GenericRecord, GenericRecord> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(Collections.singletonList("sis.sis.user_desc"));

        ConsumerRecords<GenericRecord, GenericRecord> records = consumer.poll(3000);
        // there will be 3 records in kafka topic;
        int cnt = records.count();
        Assert.assertEquals(cnt, 3);

        jdbcTemplate.execute("UPDATE user_desc SET address = 'newAddress' WHERE id = 1");

        records = consumer.poll(3000);
        Assert.assertEquals(records.count(), 1);
        ConsumerRecord<GenericRecord, GenericRecord> record = records.iterator().next();
        String address = String.valueOf(record.value().get("address"));
        Assert.assertEquals("newAddress", address);
    }

    private void initDbzTask() throws IOException, InterruptedException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String config = null;
        try (InputStream in = classLoader.getResourceAsStream("dbz/connect.json")) {
            byte[] bytes = IOUtils.toByteArray(in);
            config = new String(bytes, "UTF-8");
        }
        if (StringUtils.isEmpty(config)) {
            throw new IllegalStateException("dbz config can not be blank");
        }
        HttpUtil.doPost("http://localhost:8083/connectors", config);

        //sleep 5s to make sure dbz task has started;
        TimeUnit.SECONDS.sleep(5);
    }
}
