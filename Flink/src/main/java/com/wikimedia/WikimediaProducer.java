package com.wikimedia;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;

import okhttp3.Headers;

public class WikimediaProducer implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(WikimediaProducer.class.getSimpleName());

    private KafkaWrapper producer;
    private String topic;
    private ObjectMapper mapper = new ObjectMapper();

    public WikimediaProducer(KafkaWrapper kafka, String topicname) {
        this.producer = kafka;
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
        try {
            JsonNode jsonNode = mapper.readTree(msgEvent.getData());
            if (jsonNode.has("id") ) {
                producer.send(this.topic, jsonNode.get("id").asText(), msgEvent.getData());
            } else {
                System.out.println("Invalid ID Error: Key:" + msgEvent.getLastEventId() + " Value:" + msgEvent.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error in processing JSON", e);
        }

    }

    public static void main(String[] args) throws Exception {

        String url = "https://stream.wikimedia.org/v2/stream/recentchange"; // env.getProperty("wikimedia.url");
        KafkaWrapper kfk = new KafkaWrapper();
        EventHandler eventHandler = new WikimediaProducer(kfk, "wikimedia.recentchange");
        
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "WikimediaKafkaClient/1.0 (your_email@example.com)");
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url))
                .headers(Headers.of(headers));
        EventSource eventSource = builder.build();


        eventSource.start();
        LOG.info("Sleep");
        TimeUnit.MINUTES.sleep(1);
        LOG.info("Wakeup");
    }
}
