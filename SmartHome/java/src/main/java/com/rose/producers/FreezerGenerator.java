package com.rose.producers;

import com.rose.devices.Freezer;
import com.rose.devices.BaseDevice;
import com.rose.wikimedia.WikimediaProducer;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
//kafka-topics --bootstrap-server localhost:9092 --list #List the all topics 
//kafka-topics --bootstrap-server localhost:9092 --create --topic smarthome.freezers --partitions 5--replication-factor 1

public class FreezerGenerator extends RichParallelSourceFunction<Tuple2<String, String>> {
    private final static Logger log = LoggerFactory.getLogger(WikimediaProducer.class);
    private KafkaProducerBase producer;
    private String topicname;

    private boolean running = true;

    public FreezerGenerator(ParameterTool pt) {
        this.topicname = pt.get("topic", "smarthome.freezers");
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        log.info("Generator is opening");
        this.producer = new KafkaProducerBase();
        producer.open();
    }

    @Override
    public void cancel() {
        log.info("Generator is cancelling");
        running = false;
        producer.close();
    }

    @Override
    public void run(SourceContext<Tuple2<String, String>> sourceContext) throws Exception {
        log.info("Generator is running");

        long sequenceNum = 0;
        while (running & sequenceNum < 100) {
            BaseDevice sd = Freezer.CreateRandom();
            long ts = System.currentTimeMillis();
            sourceContext.collectWithTimestamp(new Tuple2<>("deviceId: " + sd.getDeviceId() + ", timestamp: " + ts, sd.toJSON()), ts);
            // send it to Kafka
            producer.send(topicname, sd.getDeviceId(), sd.toJSON());
            sequenceNum++;
        }
    }

    private class KafkaProducerBase {

        private KafkaProducer<String, String> kafkaProducer;

        public void open() {
            Properties config = new Properties();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

            kafkaProducer = new KafkaProducer<String, String>(config);
        }

        public void send(String topic, String key, String value) {
            // LOG.info("Producer is seding Data to Kafka ");
            ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic, key, value);

            kafkaProducer.send(rec, new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    if (e == null) {
                        System.out.println("{Topic:" + recordMetadata.topic()
                                + " Partition:" + recordMetadata.partition()
                                + " Offset: " + recordMetadata.offset()
                                + " Timestamp:" + recordMetadata.timestamp()
                                + "}");
                        // log.info( "{Topic:" + recordMetadata.topic()
                        // + " Partition:" + recordMetadata.partition()
                        // + " Offset: " + recordMetadata.offset()
                        // + " Timestamp:"+ recordMetadata.timestamp()
                        // + "}");
                    } else {
                        e.printStackTrace();
                        // log.error(e.getMessage(), e);
                    }
                }
            });
        }

        public void close() {
            kafkaProducer.close();
        }
    }

    // Create random Freezers into Kafka
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final ParameterTool pt = ParameterTool.fromArgs(args);

        DataStream<Tuple2<String, String>> input = env.addSource(new FreezerGenerator(pt));

        System.out.println("##############START########################");
        input.print();
        env.execute("Streaming Analytics");
        System.out.println("##############END##########################");
    }
}
