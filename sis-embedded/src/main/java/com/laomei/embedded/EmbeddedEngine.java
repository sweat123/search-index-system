package com.laomei.embedded;

import com.laomei.sis.BaseConnectorConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.connect.connector.ConnectorContext;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.sink.SinkConnector;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.apache.kafka.connect.sink.SinkTaskContext;
import org.apache.kafka.connect.storage.Converter;
import org.apache.kafka.connect.util.ConnectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.laomei.embedded.EmbeddedEngineStarter.SOURCE_SCHEMA_REGISTRY_URL;
import static org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

/**
 * @author laomei on 2018/12/16 21:02
 */
public class EmbeddedEngine {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BaseConnectorConfig config;

    private final ClassLoader         classLoader;

    private final Set<String>         topics;

    private final AtomicBoolean       running;

    public EmbeddedEngine(BaseConnectorConfig config) {
        this.config = config;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.topics = Arrays.stream(config.getString("topics").split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
        this.running = new AtomicBoolean(false);
    }

    public void run() {
        final String sisTaskName = config.getString("sis.task");
        final String connectorClassName = config.getOriginalValue("connector.class");
        SinkConnector connector = null;

        //instance sink connector
        try {
            Class<? extends SinkConnector> connectorClass = (Class<? extends SinkConnector>) classLoader.loadClass(connectorClassName);
            connector = connectorClass.newInstance();
        } catch (Exception e) {
            logger.error("Failed to start connector; Can not find class {}", connectorClassName);
            return;
        }

        ConnectorContext connectorContext = new ConnectorContext() {
            @Override
            public void requestTaskReconfiguration() {
            }

            @Override
            public void raiseError(Exception e) {
                logger.error(e.getMessage());
            }
        };
        connector.initialize(connectorContext);
        connector.start(convertAsString(config.getOriginalConfigs()));
        Class<? extends Task> taskClass = connector.taskClass();
        SinkTask task = null;
        try {
            task = (SinkTask) taskClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            logger.error("instance sis task failed");
            return;
        }
        task.initialize(new DefaultSinkTaskContext());
        task.start(connector.taskConfigs(1).get(0));

        running.set(true);
        KafkaConsumer<byte[], byte[]> sourceKafkaConsumer = null;
        try {
            sourceKafkaConsumer = createSourceKafkaConsumer();
            Converter keyConverter = buildConverter(true);
            Converter valueConverter = buildConverter(false);
            sourceKafkaConsumer.subscribe(topics);

            logger.info("sis task '{}' started", sisTaskName);

            while (running.get()) {
                ConsumerRecords<byte[], byte[]> msgs = sourceKafkaConsumer.poll(500);
                if (msgs.isEmpty()) {
                    continue;
                }
                logger.info("embedded engine receive {} records", msgs.count());
                List<SinkRecord> sinkRecords = new ArrayList<>(msgs.count());
                for (ConsumerRecord<byte[], byte[]> msg : msgs) {
                    SchemaAndValue keyAndSchema = keyConverter.toConnectData(msg.topic(), msg.key());
                    SchemaAndValue valueAndSchema = valueConverter.toConnectData(msg.topic(), msg.value());
                    Long timestamp = ConnectUtils.checkAndConvertTimestamp(msg.timestamp());
                    SinkRecord sinkRecord = new SinkRecord(msg.topic(), msg.partition(),
                            keyAndSchema.schema(), keyAndSchema.value(),
                            valueAndSchema.schema(), valueAndSchema.value(),
                            msg.offset(),
                            timestamp,
                            msg.timestampType());
                    sinkRecords.add(sinkRecord);
                }
                logger.info("put {} records to sink task", msgs.count());
                task.put(sinkRecords);
            }
        } catch (Throwable t) {
            logger.error("embedded engine work error", t);
        } finally {
            if (sourceKafkaConsumer != null) {
                sourceKafkaConsumer.close();
            }
            logger.info("stop task");
            task.stop();
            logger.info("stop connector");
            connector.stop();
            logger.info("sis task '{}' stopped", sisTaskName);
        }
    }

    public void close() {
        logger.info("begin to close embedded engine");
        running.set(false);
    }

    private Converter buildConverter(boolean isKey) throws ConverterException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Class clazz;
        try {
            clazz = classLoader.loadClass("io.confluent.connect.avro.AvroConverter");
        } catch (ClassNotFoundException e) {
            throw new ConverterException(e);
        }
        final Converter converter;
        try {
            converter = (Converter) clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ConverterException(e);
        }
        converter.configure(Collections.singletonMap(SOURCE_SCHEMA_REGISTRY_URL, config.getOriginalValue(SOURCE_SCHEMA_REGISTRY_URL)), isKey);
        return converter;
    }

    private KafkaConsumer<byte[], byte[]> createSourceKafkaConsumer() {
        final Map<String, Object> props = new HashMap<>();
        final Map<String, ?> originConfigs = config.getOriginalConfigs();
        props.put(BOOTSTRAP_SERVERS_CONFIG, originConfigs.get(BOOTSTRAP_SERVERS_CONFIG));
        props.put(GROUP_ID_CONFIG, originConfigs.get(GROUP_ID_CONFIG));
        props.put(AUTO_OFFSET_RESET_CONFIG, originConfigs.get(AUTO_OFFSET_RESET_CONFIG));
        props.put(ENABLE_AUTO_COMMIT_CONFIG, true);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArrayDeserializer");
        return new KafkaConsumer<>(props);
    }

    private Map<String, String> convertAsString(Map<String, ?> config) {
        Map<String, String> map = new HashMap<>(config.size());
        config.forEach((key, value) -> {
            map.put(key, String.valueOf(value));
        });
        return map;
    }

    /**
     * simple implement
     */
    protected static class DefaultSinkTaskContext implements SinkTaskContext {

        @Override
        public void offset(Map<TopicPartition, Long> offsets) {
        }

        @Override
        public void offset(TopicPartition tp, long offset) {
        }

        @Override
        public void timeout(long timeoutMs) {
        }

        @Override
        public Set<TopicPartition> assignment() {
            return null;
        }

        @Override
        public void pause(TopicPartition... partitions) {
        }

        @Override
        public void resume(TopicPartition... partitions) {
        }

        @Override
        public void requestCommit() {
        }
    }
}
