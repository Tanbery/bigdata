package com.bigdata.kafka.example2;

public interface KafkaConsumerInterface {
    public void open();
    public void listen(String topic_name);
    public void close();
}
