package com.rose.wikimedia;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.util.XORShiftRandom;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Categories are taken from:
 * https://support.google.com/merchants/answer/6324436?hl=en
 */
public class ClickEventGenerator extends RichParallelSourceFunction<String> {
    private KafkaProducerBase producer;
    private String topic = "clickevent.recentchange";

    private final long numberOfUsers;

    private boolean running = true;
    private ObjectMapper mapper;
    private long eventTimeMillis;
    private static Random RND = new XORShiftRandom(1337);
    // private List<String> categories;
    private List<User> users;

    public ClickEventGenerator(ParameterTool pt) {
        this.numberOfUsers = pt.getLong("generator.numberOfUsers", 10_000_000L);
        // this.topic=topicname;
    }

    @Override
    public void open(Configuration parameters) throws Exception {

        this.producer = new KafkaProducerBase();
        producer.open();

        this.mapper = new ObjectMapper();
        // lets start with the real time
        this.eventTimeMillis = System.currentTimeMillis();
        this.users = new ArrayList<>();
        // create initial users
        for (int i = 0; i < 50_000; i++) {
            users.add(new AuthenticatedUser(i, getNextIP()));
        }
    }

    @Override
    public void run(SourceContext<String> sourceContext) throws Exception {

        long sequenceId = 0;
        while (running) {
            ObjectNode node = mapper.createObjectNode();

            User u = getNextUser();
            long ts = getNextTimestamp();

            node.put("user.ip", intIPtoString(u.getIP()));
            node.put("user.accountId", u.getId());

            // node.put("page", getNextUrl());

            node.put("host.timestamp", ts);

            node.put("host.sequence", sequenceId);

            sourceContext.collectWithTimestamp(mapper.writeValueAsString(node), ts);
            // send it to Kafka
            producer.send(topic, Long.toString(u.getId()), mapper.writeValueAsString(node));
            sequenceId++;

            if (sequenceId % 10000 == 0) {
                sourceContext.emitWatermark(new Watermark(ts - 60000)); // 1 min max delay
            }
            // Thread.sleep(10000);
        }
    }

    private static String intIPtoString(int ip) {
        int b1 = (ip >>> 24) & 0xff;
        int b2 = (ip >>> 16) & 0xff;
        int b3 = (ip >>> 8) & 0xff;
        int b4 = ip & 0xff;
        return new StringBuffer().append(b1).append(".")
                .append(b2).append(".")
                .append(b3).append(".")
                .append(b4)
                .toString();
    }

    private static int getNextIP() {
        return RND.nextInt();
    }

    private final static User anonymousUser = new User() {
        @Override
        public long getId() {
            return -1;
        }

        @Override
        public int getIP() {
            return getNextIP();
        }
    };

    private User getNextUser() {
        if (RND.nextBoolean()) {
            return anonymousUser;
        }
        // replace one user (note: this might generate data with the same user being
        // logged in multiple times)
        users.set(RND.nextInt(users.size()),
                new AuthenticatedUser(ThreadLocalRandom.current().nextLong(this.numberOfUsers), getNextIP()));

        // get a user from the local state.
        int userIndex = RND.nextInt(users.size());
        return users.get(userIndex);
    }

    private long getNextTimestamp() {
        this.eventTimeMillis += 10;
        return (this.eventTimeMillis) - RND.nextInt(60 * 1000); // max delay of one minute
    }

    @Override
    public void cancel() {
        running = false;
        producer.close();
    }

    private interface User {
        long getId();

        int getIP();
    }

    private static class AuthenticatedUser implements User {

        private long id;
        private int ip;

        public AuthenticatedUser(long id, int ip) {
            this.id = id;
            this.ip = ip;
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public int getIP() {
            return ip;
        }
    }

    private class KafkaProducerBase {
        // private static final Logger LOG=LoggerFactory.getLogger(KafkaProducerBase.class.getName());
        private KafkaProducer<String, String> kafkaProducer;

        public void open() {
            Properties config = new Properties();
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

            // Set safe producer config (Kafka<=3)
            // config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");
            // config.put(ProducerConfig.ACKS_CONFIG,"all");
            // config.put(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE));

            // Set high throughput producer config
            config.put(ProducerConfig.LINGER_MS_CONFIG, "20");
            config.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32 * 1024));
            config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

            kafkaProducer = new KafkaProducer<String, String>(config);
        }

        public void send(String topic, String key, String value) {
            // LOG.info("Producer is seding Data to Kafka ");
            ProducerRecord<String, String> rec = new ProducerRecord<String, String>(topic, key, value);
            kafkaProducer.send(rec);
        }

        public void close() {
            kafkaProducer.close();
        }
    }

    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        final ParameterTool pt = ParameterTool.fromArgs(args);

        DataStream<String> input = env.addSource(new ClickEventGenerator(pt));
        // input.executeAndCollect();
        // execute program
        System.out.println("##############START########################");
        input.print();
        env.execute("Streaming Analytics");
        System.out.println("##############END##########################");
    }
}
