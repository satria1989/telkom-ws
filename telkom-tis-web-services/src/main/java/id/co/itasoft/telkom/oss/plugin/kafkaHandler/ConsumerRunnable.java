package id.co.itasoft.telkom.oss.plugin.kafkaHandler;

import bsh.Interpreter;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ConsumerRunnable implements Runnable {

    private final KafkaConsumer<String, String> kafkaConsumer;
    private boolean closing = false;
    private boolean debug = true;

    public ConsumerRunnable(Properties clientConfiguration, String topic) {
        this.kafkaConsumer = new KafkaConsumer<>(clientConfiguration);
        this.kafkaConsumer.subscribe(Collections.singletonList(topic), new ConsumerRebalanceListener() {
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                if (debug) {
//                    LogUtil.info(getClass().getName(), "Partitions revoked: " + partitions);
                }
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                try {
//                    LogUtil.info(getClass().getName(), "Partitions " + partitions + " assigned, consumer seeking to end.");

                    for (TopicPartition partition : partitions) {
                        long position = kafkaConsumer.position(partition);
                        if (debug) {
//                            LogUtil.info(getClass().getName(), "current Position: " + position);
//                            LogUtil.info(getClass().getName(), "Seeking to end...");
                        }
                        kafkaConsumer.seekToEnd(Arrays.asList(partition));
                        if (debug) {
//                            LogUtil.info(getClass().getName(), "Seek from the current position: " + kafkaConsumer.position(partition));
                        }
                        kafkaConsumer.seek(partition, position);
                    }
//                    LogUtil.info(getClass().getName(), "Producer can now begin producing messages.");
                } catch (final Exception e) {
                    LogUtil.error(getClass().getName(), e, "Error when assigning partitions");
                }
            }
        });
    }

    @Override
    public void run() {
//        LogUtil.info(getClass().getName(), "Consumer is starting.");

        while (!closing) {
            try {
//                LogUtil.info(getClass().getName(), "Exccc");
                Duration duration = Duration.of(1, ChronoUnit.SECONDS);
                Iterator<ConsumerRecord<String, String>> it = this.kafkaConsumer.poll(duration).iterator();
//                LogUtil.info(getClass().getName(), "testtttt" + it.hasNext());
                // Iterate through all the messages received and print their content.
                while (it.hasNext()) {
//                    LogUtil.info(getClass().getName(), "Iterator next");
                    ConsumerRecord<String, String> record = it.next();
                    String key = record.key();
                    String value = record.value();
//                    LogUtil.info(getClass().getName(), "Executing script. Offset: " + record.offset());
                    if (debug) {
//                        LogUtil.info(getClass().getName(), "Key=" + key + ". Value=" + value);
                    }
                    executeScript(key, value);
                }

                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                LogUtil.error(getClass().getName(), e, "Producer/Consumer loop has been unexpectedly interrupted");
                shutdown();
            } catch (final Exception e) {
                LogUtil.error(getClass().getName(), e, "Consumer has failed with exception: " + e);
                shutdown();
            }
        }

//        LogUtil.info(getClass().getName(), "Consumer is shutting down.");
        this.kafkaConsumer.close();

//        while (!closing) {
//            try {
//                Duration duration = Duration.of(1, ChronoUnit.SECONDS);
//                Iterator<ConsumerRecord<String, String>> it = this.kafkaConsumer.poll(duration).iterator();
//
//                // Iterate through all the messages received and log their content.
//                while (it.hasNext()) {
//                    ConsumerRecord<String, String> record = it.next();
//                    String key = record.key();
//                    String value = record.value();
//                    String topic = record.topic();
//                    int partition = record.partition();
//                    long offset = record.offset();
//
//                    // Log message details
//                    LogUtil.info(getClass().getName(), String.format("Received message. Key=%s, Value=%s, Topic=%s, Partition=%d, Offset=%d",
//                            key, value, topic, partition, offset));
//
//                    // Optionally, log the message body if debugging is enabled
//                    if (debug) {
//                        LogUtil.info(getClass().getName(), "Message content: " + value);
//                    }
//                }
//
//                Thread.sleep(1000); // Sleep to simulate consumer idle time
//            } catch (final InterruptedException e) {
//                LogUtil.error(getClass().getName(), e, "Consumer loop interrupted");
//                shutdown();
//            } catch (final Exception e) {
//                LogUtil.error(getClass().getName(), e, "Error while consuming messages: " + e.getMessage());
//                shutdown();
//            }
//        }

//        LogUtil.info(getClass().getName(), "Consumer is shutting down.");
        this.kafkaConsumer.close();
    }

    public void shutdown() {
        closing = true;
    }

    public void executeScript(String key, String value) {
        try {
//            LogUtil.info(getClass().getName(), "executeScript");
            Interpreter interpreter = new Interpreter();
            interpreter.setClassLoader(getClass().getClassLoader());
            interpreter.set("key", key);
            interpreter.set("value", value);

            Object keyObj = interpreter.get("key");
            Object valueObj = interpreter.get("value");

// Creating JSON from interpreter variables
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key", keyObj);
            jsonObject.put("value", valueObj);

            String jsonString = jsonObject.toString();
//            LogUtil.info(getClass().getName(), "json S" + jsonString);


//            Object result = interpreter.eval(script);
//            LogUtil.info(getClass().getName(), "Script execution complete.");
//            if (debug) {
//                LogUtil.info(getClass().getName(), "Script result: " + result);
//            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Error executing script");
        }
    }
}