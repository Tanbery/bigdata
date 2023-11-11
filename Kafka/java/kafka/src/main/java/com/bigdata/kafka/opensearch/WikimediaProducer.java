package com.bigdata.kafka.opensearch;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.EventHandler;

//Example webpages
// https://stream.wikimedia.org/v2/stream/recentchange
// https://codepen.io/Krinkle/pen/BwEKgW?editors=1010
// https://esjewett.github.io/wm-eventsource-demo/

public class WikimediaProducer {

    // private  static final Logger LOG=LoggerFactory.getLogger(ProducerApp.class.getName());

    public static void main(String[] args) throws InterruptedException {
        
        
        String  url= "https://stream.wikimedia.org/v2/stream/recentchange";
        EventHandler eventHandler = new WikimediaChangeHandler();
        EventSource.Builder builder = new EventSource.Builder(eventHandler,URI.create(url));
        EventSource eventSource= builder.build();
        eventSource.start();
        
        TimeUnit.MINUTES.sleep(2);
    }
    
}
