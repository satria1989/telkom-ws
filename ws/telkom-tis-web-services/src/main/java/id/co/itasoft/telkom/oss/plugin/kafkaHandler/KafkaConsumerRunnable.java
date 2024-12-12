package id.co.itasoft.telkom.oss.plugin.kafkaHandler;

import id.co.itasoft.telkom.oss.plugin.dao.ConfigurationDao;
//import jdk.internal.net.http.common.Log;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.joget.apps.form.service.FileUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

import java.io.File;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerRunnable implements Runnable {

    private volatile boolean running = true; // Flag untuk menghentikan thread secara aman
    private final String topicName = "usrINSERA_new_ticket"; // Nama topic yang akan dikonsumsi

    // Konstruktor default
    public KafkaConsumerRunnable() {
        // Konstruktor kosong jika diperlukan penyesuaian lebih lanjut
    }

    // Mendapatkan konfigurasi Kafka Consumer
    private Properties getClientConfig() {
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

            // Consumer-specific properties
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "usrINSERA_g1"); // Sesuaikan dengan group ID yang diinginkan
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Mulai dari offset awal jika belum ada offset sebelumnya

            // LogUtil.info("check properties for consumer:", props.toString());
        } catch (Exception ex) {
            LogUtil.error("KafkaConsumerRunnable", ex, "Failed to create Kafka consumer config.");
        }

        return props;
    }

    @Override
    public void run() {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(getClientConfig())) {
            // Subscribe ke topic usrINSERA_new_ticket
            consumer.subscribe(Collections.singletonList(topicName));
            // LogUtil.info(getClass().getName(), "Consumer is now subscribed to topic " + topicName);

            // Loop untuk polling pesan dari Kafka
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                for (ConsumerRecord<String, String> record : records) {
                    // LogUtil.info(getClass().getName(), "Received message: " + record.value() +
//                            ", Topic: " + record.topic() +
//                            ", Partition: " + record.partition() +
//                            ", Offset: " + record.offset());

                    // Tambahkan log atau proses data sesuai kebutuhan, contoh penyimpanan log
//                    LogHistoryDao logHistoryDao = new LogHistoryDao();
                    JSONObject json = new JSONObject();
                    json.put("topic", record.topic());
                    json.put("partition", record.partition());
                    json.put("offset", record.offset());
                    json.put("value", record.value());

                    // LogUtil.info(getClass().getName(), "KAFKA LOGS SUCCESS");
                    // LogUtil.info(getClass().getName(), json.toString());

                }
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Error occurred while consuming messages from Kafka.");
        }
    }

}
