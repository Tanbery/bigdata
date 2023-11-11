package com.rose.wikimedia;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
// import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
// import org.apache.flink.streaming.api.windowing.time.Time;

// import com.rose.consumers.KafkaConsumerBase;



public class Wikimedia01min {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // final ParameterTool pt = ParameterTool.fromArgs(args);

        Properties config = new Properties();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "GroupBigdata");
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "ClientBigdata");
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, "sampleConsumer");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        // config.put(ConsumerConfig., "snappy");
        // KafkaConsumerBase consumer= new KafkaConsumerBase();

        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>("wikimedia.recentchange",new SimpleStringSchema(), config);
        DataStream<String> kafkaStream = env.addSource(kafkaConsumer);
        // DataStream<String> windowedStream = kafkaStream.timeWindowAll(Time.minutes(5)),trigger(CountTrigger.of(1));
        kafkaStream.print();
        env.execute("Read from Kafka Example");
    }

}
