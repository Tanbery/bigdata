package com.bigdata.kafka.example2;

import java.util.Properties;


import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaProducerWrapper implements KafkaProducerInterface {
    private  static final Logger LOG=LoggerFactory.getLogger(KafkaProducerWrapper.class.getName());
    private KafkaProducer<String,String> kafkaProducer; 

    public void open(){
        
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        kafkaProducer = new KafkaProducer<String,String>(config);
    }

    public void send(String topic_name, int count)
    {
        LOG.info("Producer is seding Data to Kafka => " + count);
        for(int i=0; i< count; i++)
        {
            ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic_name, "Anahtar " + i, "Deger " + i);
            //kafkaProducer.send(rec);
            kafkaProducer.send(rec,(recordMetadata, e) -> {
                if(e == null)
                    LOG.info("{" + recordMetadata.topic() + "," + recordMetadata.partition()+ "," + recordMetadata.offset()+ "}");
                else{
                    LOG.error(e.toString());
                    e.printStackTrace();
                }
             });
        }
    }
    public void close(){
        kafkaProducer.close();
    }
}
