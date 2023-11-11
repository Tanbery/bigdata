package com.bigdata.kafka.example2;

public class AppProducer {

    // private  static final Logger LOG=LoggerFactory.getLogger(ProducerApp.class.getName());

    public static void main(String[] args) {
        KafkaProducerInterface producer= new KafkaProducerWrapper();
        producer.open();
        for(int i=0;i<10;i++)
            producer.send("search", 10);
        producer.close();
    }
    
}
