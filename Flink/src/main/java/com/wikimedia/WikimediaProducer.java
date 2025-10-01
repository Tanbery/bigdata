package com.wikimedia;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;

public class WikimediaProducer implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WikimediaProducer.class.getSimpleName());

    private KafkaProducerBase producer;
    private String topic;

    public WikimediaProducer(String topicname) {
        this.producer = new KafkaProducerBase();
        this.topic = topicname;
    }

    @Override
    public void onClosed() {
        producer.close();
    }

    @Override
    public void onComment(String arg0) {

    }

    @Override
    public void onError(Throwable t) {
        LOG.error("Error in Stream Reading", t);
    }

    @Override
    public void onOpen() {
        producer.open();
    }

    @Override
    public void onMessage(String arg0, MessageEvent msgEvent) {
        
        producer.send(this.topic, msgEvent.getLastEventId(), msgEvent.getData());
    }

    private class KafkaProducerBase {

        private KafkaProducer<String, String> kafkaProducer;

        public void open() {

            // Reading config parameter automatically
            // Properties producerConfig= new Properties();
            // try(InputStream stream = WikimediaProducer.class.getClassLoader().getResourceAsStream("producer.properties")){
            // producerConfig.load(stream);
            // }

            Properties config = new Properties();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            // Set safe producer config (Kafka<=3)
            // config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");
            // config.put(ProducerConfig.ACKS_CONFIG,"all");
            // config.put(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE));

            // Set high throughput producer config
            // config.put(ProducerConfig.LINGER_MS_CONFIG, "20");
            // config.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));
            // config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

            kafkaProducer = new KafkaProducer<String, String>(config);
        }

        public void send(String topic, String key, String value) {
            // LOG.info("Producer is seding Data to Kafka ");
            ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic, key, value);
            // LOG.info(key);
            System.out.println("Key:" + key);
            kafkaProducer.send(rec, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) 
                        System.out.println("{"+ "Topic:" + recordMetadata.topic() + " Partition:" + recordMetadata.partition() + " Offset: " + recordMetadata.offset() + " Timestamp:" + recordMetadata.timestamp() + "}");
                    
                    else 
                        e.printStackTrace();
                
                }
            });
        }

        public void close() {
            kafkaProducer.close();
        }
    }

    public static void main(String[] args) throws Exception {

        String url = "https://stream.wikimedia.org/v2/stream/recentchange"; // env.getProperty("wikimedia.url");
        EventHandler eventHandler = new WikimediaProducer("wikimedia.recentchange");
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();
        eventSource.start();
        LOG.info("Sleep");
        TimeUnit.MINUTES.sleep(1);
        LOG.info("Wakeup");
    }
}
