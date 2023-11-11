package com.bigdata.kafka.example2;


import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
// import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerWrapper implements KafkaConsumerInterface{
    
    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerWrapper.class.getName());
    private KafkaConsumer<String, String> kafkaConsumer;

     public void open(){
        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "GroupBigdata");
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "ClientBigdata");
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "sampleConsumer");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");          
        // config.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "60000");
                
        kafkaConsumer = new KafkaConsumer<String, String>(config);
    }

    public void listen(String topic_name){
        
        try {
            kafkaConsumer.subscribe(Arrays.asList(topic_name));
            
            while (true) {
                LOG.info("Reading Records from kafka : ");
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(300));
                Thread.sleep(100);
                for (ConsumerRecord<String, String> rec : records) {
                    LOG.info("Key: " + rec.key() + "  Values:" + rec.value() + " Partition:"+rec.partition()+ " Offset:"+rec.offset());
                }
                Thread.sleep(100);
            }

        }catch (Exception e) {
            LOG.info(e.toString());
            e.printStackTrace();
        }
    }

    public void close(){
        kafkaConsumer.close();
        kafkaConsumer.close();
    }
}
