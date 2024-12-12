package id.co.itasoft.telkom.oss.plugin.kafkaHandler;


import id.co.itasoft.telkom.oss.plugin.dao.ConfigurationDao;
import id.co.itasoft.telkom.oss.plugin.dao.LogHistoryDao;
import id.co.itasoft.telkom.oss.plugin.function.LogInfo;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.serialization.StringSerializer;
import org.joget.apps.form.service.FileUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KafkaProducerService {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    private final LogInfo logInfo = new LogInfo();
    LogHistoryDao logHistoryDao = new LogHistoryDao();

    public Properties getClientConfig() {
        Properties props = new Properties();
        try {
            ConfigurationDao getConfigurationTicket = new ConfigurationDao();
            JSONObject configKafka = getConfigurationTicket.getConfigurationMapping();

            // Common properties
            props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, configKafka.getString("kfk_bootstrap"));
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, configKafka.getString("kfk_security"));
            props.put(SaslConfigs.SASL_MECHANISM, configKafka.getString("kfk_mechanism"));

            // Configure SASL with JAAS
            String jaasConfig = configKafka.getString("kfk_jaas").replace("&quot;", "\"");
            props.put(SaslConfigs.SASL_JAAS_CONFIG, jaasConfig);

            // SSL Truststore properties
            File trustStoreFile = FileUtil.getFile(configKafka.getString("kfk_tsloc"), "configuration", configKafka.getString("id"));
            // LogUtil.info("pathSecurity", trustStoreFile.getPath());
            props.put("ssl.truststore.location", trustStoreFile.getPath());
            props.put("ssl.truststore.password", configKafka.getString("kfk_tspwd"));

            // Producer-specific properties
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.CLIENT_ID_CONFIG, "kafka-joget-producer");
            props.put(ProducerConfig.ACKS_CONFIG, "-1");
            props.put(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG, "use_all_dns_ips");

            // LogUtil.info("check properties :", props.toString());
        } catch (Exception ex) {
            LogUtil.error("KafkaProducerService", ex, "Failed to create Kafka producer config.");
        }

        return props;
    }

    public void sendMessage(String topic, String key, String message, String recordKey, String type) {
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLoader = currentThread.getContextClassLoader();
        currentThread.setContextClassLoader(this.getClass().getClassLoader());

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(getClientConfig())) {

            String version = "1.0";
            Header messageTypeHeader = new RecordHeader("messageType", type.getBytes());
            Header versionHeader = new RecordHeader("version", version.getBytes());

            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
            record.headers().add(messageTypeHeader);
            record.headers().add(versionHeader);

            // Send message asynchronously with callback
            producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    LogUtil.info(getClass().getName(), "Message sent successfully to Kafka, topic: " + metadata.topic()
                            + ", partition: " + metadata.partition() + ", offset: " + metadata.offset());
                    JSONObject request = new JSONObject(message);
                    JSONObject response = new JSONObject();
                    response.put("message", "Message sent successfully to Kafka, topic: " + metadata.topic()
                            + ", partition: " + metadata.partition() + ", offset: " + metadata.offset());
                    logHistoryDao.SENDHISTORY(recordKey, "PRODUCE_KAFKA", "", "", request, response, 200);
                } else {
                    LogUtil.error(getClass().getName(), exception, "Failed to send message to Kafka: " + exception.getMessage());
                }
            });

        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Failed to send message to Kafka");
        } finally {
            currentThread.setContextClassLoader(threadContextClassLoader);
        }
    }

}
