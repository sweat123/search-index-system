package com.laomei.embedded;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author laomei on 2019/2/17 15:22
 */
public class EmbeddedEngineIT extends JdbcBaseIT {

    @Before
    public void init() throws IOException, SolrServerException, InterruptedException {
        super.init();
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
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "sis-integration-test-group-v1");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        config.put("schema.registry.url", "http://localhost:8082");
        KafkaConsumer<GenericRecord, GenericRecord> consumer = new KafkaConsumer<>(config);
        consumer.subscribe(Collections.singletonList("sis.sis.user_desc"));

        ConsumerRecords<GenericRecord, GenericRecord> records = consumer.poll(3000);
        // there will be 4 records in kafka topic;
        int cnt = records.count();
        Assert.assertTrue(cnt > 0);

        jdbcTemplate.execute("UPDATE user_desc SET address = 'newAddress' WHERE id = 1");

        records = consumer.poll(3000);
        Assert.assertEquals(records.count(), 1);
        ConsumerRecord<GenericRecord, GenericRecord> record = records.iterator().next();
        GenericRecord after = (GenericRecord) record.value().get("after");
        String address = String.valueOf(after.get("address"));
        Assert.assertEquals("newAddress", address);
    }
}
