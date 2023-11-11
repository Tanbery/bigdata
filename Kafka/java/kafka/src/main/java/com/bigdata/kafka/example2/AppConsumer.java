package com.bigdata.kafka.example2;


public class AppConsumer {
    // private static final Logger LOG = LoggerFactory.getLogger(ConsumerApp.class.getName());

    public static void main(String[] args) {
        KafkaConsumerInterface consumer= new KafkaConsumerWrapper();

        consumer.open();
        consumer.listen("search");
        consumer.close();
    }
}