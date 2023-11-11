package com.bigdata.kafka.opensearch;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;


public class WikimediaChangeHandler implements EventHandler {
    private  static final Logger LOG=LoggerFactory.getLogger(WikimediaChangeHandler.class.getSimpleName());

    private KafkaProducerBase producer; 
    private String topic;
    
    public WikimediaChangeHandler(){
        this.producer= new KafkaProducerBase();;
        this.topic = "wikimedia.recentchange";
    }
    @Override
    public void onClosed()  {
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
    public void onMessage(String arg0, MessageEvent msgEvent)  {
        LOG.info("EventID:" + msgEvent.getLastEventId());
        producer.send(this.topic, null, msgEvent.getData());
    }

    @Override
    public void onOpen() {
       producer.open();
    }
}
