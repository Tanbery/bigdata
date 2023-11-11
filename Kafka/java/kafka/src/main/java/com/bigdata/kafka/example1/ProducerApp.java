package com.bigdata.kafka.example1;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Scanner;


public class ProducerApp {

    private  static final Logger LOG=LoggerFactory.getLogger(ProducerApp.class.getName());

    public static void main(String[] args) {

        Scanner read = new Scanner(System.in);

        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        KafkaProducer<String,String> producer = new KafkaProducer<String,String>(config);

        while(true){
            LOG.info("\n\nData what will send to Kafka :");
            String key = read.nextLine();
            if (key.equalsIgnoreCase("exit")) break;
            ProducerRecord<String, String> rec = new ProducerRecord<String, String>("search", key);            
           producer.send(rec);
         }

         producer.flush();
         producer.close();
         read.close();

    }
    
}
