package com.bigdata.kafka.example1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class ConsumerApp {
    private static final Logger LOG = LoggerFactory.getLogger(ProducerApp.class.getName());

    public static void main(String[] args) {
        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "bigdataTeam");
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "sampleConsumer");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(config);
        
        // final Thread mainThread = Thread.currentThread();
        // Runtime.getRuntime().addShutdownHook(new Thread() {
        //     public void run() {
        //         LOG.info("Detected Shutdown, let's exit by wakeup Consumer");
        //         kafkaConsumer.wakeup();
        //         try {
        //             mainThread.join();
        //         } catch (InterruptedException e) {
        //             e.printStackTrace();
        //         }
        //     }
        // });

        try {
            kafkaConsumer.subscribe(Arrays.asList("search"));

            while (true) {
                LOG.info("Reading Records from kafka : ");
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(3000));
                for (ConsumerRecord<String, String> rec : records) {
                    LOG.info(rec.key() + "|" + rec.value());
                }
            }

        } catch (WakeupException we) {
           LOG.info("Consumer is starting showdown");
        }catch (Exception e) {
            LOG.info(e.toString());
        }finally{
            kafkaConsumer.close();
        }

    }
}