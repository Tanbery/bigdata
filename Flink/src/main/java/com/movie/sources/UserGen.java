package com.movie.sources;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import com.movie.dto.UserProfile;

public class UserGen implements SourceFunction<UserProfile> {

    private volatile boolean running = true;
    private final int GEN_SPEED_MS = 1000;
    @Override
    public void run(SourceContext<UserProfile> ctx) throws Exception {
        while (running) {
            UserProfile user = UserProfile.generate();
            ctx.collect(user);
            Thread.sleep(GEN_SPEED_MS); // Adjust the sleep time as needed
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
    
}
