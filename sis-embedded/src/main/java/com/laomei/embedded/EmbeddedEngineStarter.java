package com.laomei.embedded;

import com.laomei.sis.BaseConnectorConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;

/**
 * @author laomei on 2018/12/16 21:01
 */
public class EmbeddedEngineStarter {

    public static final String SOURCE_KAFKA_BOOTSTRAP_SERVERS = ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;

    public static final String SOURCE_KAFKA_GROUP_ID = ConsumerConfig.GROUP_ID_CONFIG;

    public static final String SOURCE_KAFKA_AUTO_OFFSET_RESET = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;

    public static final String SOURCE_SCHEMA_REGISTRY_URL = "schema.registry.url";

    public static final String SOURCE_KEY_DESERIALIZER_CLASS = ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;

    public static final String SOURCE_VALUE_DESERIALIZER_CLASS = ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

    public static void main(String[] args) {
        EmbeddedEngine embeddedEngine = DefaultEngineBuilder.create()
                .addConfig(SOURCE_KAFKA_BOOTSTRAP_SERVERS, "")
                .addConfig(SOURCE_KAFKA_GROUP_ID, "")
                .addConfig(SOURCE_VALUE_DESERIALIZER_CLASS, "")
                .addConfig(SOURCE_KEY_DESERIALIZER_CLASS, "")
                .addConfig(SOURCE_KAFKA_AUTO_OFFSET_RESET, "")
                .addConfig(SOURCE_SCHEMA_REGISTRY_URL, "")
                //add more
                .build();
    }
}
