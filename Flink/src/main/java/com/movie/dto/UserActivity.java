package com.movie.dto;

import java.util.Random;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// UserActivity.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivity {
    public String userId;
    public String movieId;
    public String action; // PLAY, PAUSE, STOP, RATE
    public long timestamp;

    public static UserActivity generate(String userId, String movieId) {
        String[] ACTIONS = {"PLAY", "PAUSE", "STOP", "RATE"};
        Random rand = new Random();
        
        String action = ACTIONS[rand.nextInt(ACTIONS.length)];
        long timestamp = System.currentTimeMillis();

        return new UserActivity(userId, movieId, action, timestamp);
    }

}

