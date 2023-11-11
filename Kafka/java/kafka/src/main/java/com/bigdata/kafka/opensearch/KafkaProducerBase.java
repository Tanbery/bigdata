package com.bigdata.kafka.opensearch;

import java.util.Properties;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.StringSerializer;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

public class KafkaProducerBase  {
    // private  static final Logger LOG=LoggerFactory.getLogger(KafkaProducerBase.class.getName());
    private KafkaProducer<String,String> kafkaProducer; 

    public void open(){
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        
        //Set safe producer config (Kafka<=3)
        // config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");
        // config.put(ProducerConfig.ACKS_CONFIG,"all");
        // config.put(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE));
        
        //Set high throughput producer config
        config.put(ProducerConfig.LINGER_MS_CONFIG,"20");
        config.put(ProducerConfig.BATCH_SIZE_CONFIG,Integer.toString(32*1024));
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,"snappy");

        kafkaProducer = new KafkaProducer<String,String>(config);
    }

    public void send(String topic, String key, String value)
    {
        // LOG.info("Producer is seding Data to Kafka ");
        ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic, key, value);
        kafkaProducer.send(rec);
    }
    public void close(){
        kafkaProducer.close();
    }
}
