package com.movie.sources;

import java.util.UUID;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import com.movie.dto.UserActivity;

public class UserActivityGen implements SourceFunction<UserActivity> {

    private volatile boolean running = true;
    private final int GEN_SPEED_MS = 100;
    @Override
    public void run(SourceContext<UserActivity> ctx) throws Exception {
        while (running) {
            String userId = "user_" + UUID.randomUUID().toString().substring(0, 1);
            String movieId = "movie_" + UUID.randomUUID().toString().substring(0, 1);         
            UserActivity act = UserActivity.generate(userId, movieId);
            ctx.collect(act);
            Thread.sleep(GEN_SPEED_MS); // Adjust the sleep time as needed
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
    
}
