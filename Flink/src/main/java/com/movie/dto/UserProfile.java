package com.movie.dto;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// UserProfile.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    public String userId;
    public String name;
    public int age;
    public String country;
    public String subscriptionType; 
    public String[] favoriteGenres;


    

    // Generate random UserProfile
    public static UserProfile generate() {
        String[] GENRES = {"Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance"};
        // String[] ACTIONS = {"PLAY", "PAUSE", "STOP", "RATE"};
        String[] COUNTRIES = {"US", "UK", "IN", "DE", "FR", "JP"};
        String[] SUBSCRIPTION_TYPES = {"FREE", "PREMIUM"};
        Random rand = new Random();
        
        String userId = "user_" + UUID.randomUUID().toString().substring(0, 1);
        String name = "User" + rand.nextInt(1000);
        int age = 18 + rand.nextInt(50);
        String country = COUNTRIES[rand.nextInt(COUNTRIES.length)];
        String subscription = SUBSCRIPTION_TYPES[rand.nextInt(SUBSCRIPTION_TYPES.length)];

        // Random favorite genres (1-3)
        int genreCount = 1 + rand.nextInt(3);
        Set<String> favoriteGenresSet = new HashSet<>();
        while (favoriteGenresSet.size() < genreCount) {
            favoriteGenresSet.add(GENRES[rand.nextInt(GENRES.length)]);
        }

        return new UserProfile(userId, name, age, country, subscription, favoriteGenresSet.toArray(new String[0]));
    }
}

