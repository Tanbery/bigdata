package com.naci;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.util.XORShiftRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class ClickEventGenerator extends RichParallelSourceFunction<String> {
    private final long numberOfUsers;

    private boolean running = true;
    private ObjectMapper mapper;
    private long eventTimeMillis;
    private static Random RND = new XORShiftRandom(1337);
    // private List<String> categories;
    private List<User> users;

    public ClickEventGenerator(ParameterTool pt) {
        this.numberOfUsers = pt.getLong("generator.numberOfUsers", 10_000_000L);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        this.mapper = new ObjectMapper();
        // lets start with the real time
        this.eventTimeMillis = System.currentTimeMillis();
        // this.categories = readCategories();
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
            User u = getNextUser();
            long ts = getNextTimestamp();

            ObjectNode node = mapper.createObjectNode();

            node.put("user.ip", intIPtoString(u.getIP()));
            node.put("user.accountId", u.getId());
            node.put("host.timestamp", ts);
            node.put("host", String.format("lb-brl-%d-%d.globalcorp.com",
                    getRuntimeContext().getNumberOfParallelSubtasks(), getRuntimeContext().getIndexOfThisSubtask()));
            node.put("host.sequence", sequenceId);
            // node.put("page", getNextUrl());

            sourceContext.collectWithTimestamp(mapper.writeValueAsString(node), ts);
            sequenceId++;

            if (sequenceId % 10000 == 0) {
                sourceContext.emitWatermark(new Watermark(ts - 60000)); // 1 min max delay
            }
            Thread.sleep(10000);
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
}
