package com.laomei.embedded;

import static org.apache.kafka.clients.CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

/**
 * @author laomei on 2018/12/16 21:01
 */
public class EmbeddedEngineStarter {

    public static final String SOURCE_SCHEMA_REGISTRY_URL = "schema.registry.url";

    public static void main(String[] args) {
        EmbeddedEngine embeddedEngine = DefaultEngineBuilder.create()
                .addConfig(BOOTSTRAP_SERVERS_CONFIG, "")
                .addConfig(GROUP_ID_CONFIG, "")
                .addConfig(AUTO_OFFSET_RESET_CONFIG, "")
                .addConfig(SOURCE_SCHEMA_REGISTRY_URL, "")
                //add more
                .build();
    }
}
