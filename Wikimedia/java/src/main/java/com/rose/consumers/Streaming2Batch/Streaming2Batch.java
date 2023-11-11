package com.rose.consumers.Streaming2Batch;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;


import com.rose.consumers.KafkaConsumerBase;

import org.apache.flink.api.java.utils.ParameterTool;

public class Streaming2Batch {

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final ParameterTool pt = ParameterTool.fromArgs(args);

        KafkaConsumerBase consumer= new KafkaConsumerBase();

//         FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(
//             "clickevent.recentchange", // Kafka topic to read from
//     new SimpleStringSchema(), // Deserialization schema for Kafka messages (assuming strings in this example)
//     properties
// );

        // DataStream<String> input = env.addSource(new ClickEventGenerator(pt));
        // // input.executeAndCollect();
        // // execute program
        // System.out.println("##############START########################");
        // input.print();
        // env.execute("Streaming Analytics");
        // System.out.println("##############END##########################");
    }

   
}
