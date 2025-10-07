package com.movie.sources;

import java.util.*;

import com.movie.dto.*;

public class DataGen {

    private static final String[] GENRES = {"Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance"};
    private static final String[] ACTIONS = {"PLAY", "PAUSE", "STOP", "RATE"};
    private static final String[] COUNTRIES = {"US", "UK", "IN", "DE", "FR", "JP"};
    private static final String[] SUBSCRIPTION_TYPES = {"FREE", "PREMIUM"};

    private static final Random rand = new Random();

    // Generate random UserProfile
    public static UserProfile generateUserProfile() {
        String userId = "user_" + UUID.randomUUID().toString().substring(0, 8);
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

    // Generate random Movie
    public static Movie generateMovie() {
        String movieId = "movie_" + UUID.randomUUID().toString().substring(0, 8);
        String title = "Movie" + rand.nextInt(1000);
        String genre = GENRES[rand.nextInt(GENRES.length)];
        int releaseYear = 1980 + rand.nextInt(43); // 1980 - 2022
        double rating = Math.round((rand.nextDouble() * 10.0) * 10.0) / 10.0; // 0.0 - 10.0

        return new Movie(movieId, title, genre, releaseYear, rating);
    }

    // Generate random UserActivity
    public static UserActivity generateUserActivity(List<UserProfile> users, List<Movie> movies) {
        UserProfile user = users.get(rand.nextInt(users.size()));
        Movie movie = movies.get(rand.nextInt(movies.size()));
        String action = ACTIONS[rand.nextInt(ACTIONS.length)];
        long timestamp = System.currentTimeMillis();

        return new UserActivity(user.userId, movie.movieId, action, timestamp);
    }

    // Example usage
    public static void main(String[] args) {
        List<UserProfile> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            users.add(generateUserProfile());
        }

        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            movies.add(generateMovie());
        }

        for (int i = 0; i < 20; i++) {
            UserActivity activity = generateUserActivity(users, movies);
            System.out.println(activity.userId + "," + activity.movieId + "," + activity.action + "," + activity.timestamp);
        }
    }
}

