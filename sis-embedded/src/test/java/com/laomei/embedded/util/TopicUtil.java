package com.laomei.embedded.util;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;

/**
 * @author laomei on 2019/2/21 21:34
 */
public class TopicUtil {

    public static void createTopic(String bootstrap, String topic, int partition, short replicas) {
        try (AdminClient adminClient = AdminClient.create(Collections.singletonMap(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap))) {
            NewTopic newTopic = new NewTopic(topic, partition, replicas);
            final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singletonList(newTopic));
            createTopicsResult.values().get(topic);
        }
    }
}
