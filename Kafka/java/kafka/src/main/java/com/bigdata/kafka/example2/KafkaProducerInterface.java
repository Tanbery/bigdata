package com.bigdata.kafka.example2;

public interface KafkaProducerInterface {
    public void open();
    public void send(String topic_name, int count);
    public void close();
}
