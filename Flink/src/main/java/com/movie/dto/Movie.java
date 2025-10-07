package com.movie.dto;

import java.util.Random;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Movie.java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    public String movieId;
    public String title;
    public String genre;
    public int releaseYear;
    public double rating; // 0.0 - 10.0
    // Generate random Movie

    
    public static Movie generate() {
        String[] GENRES = {"Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Romance"};
        Random rand = new Random();

        String movieId = "movie_" + UUID.randomUUID().toString().substring(0, 1);
        String title = "Movie" + rand.nextInt(1000);
        String genre = GENRES[rand.nextInt(GENRES.length)];
        int releaseYear = 1980 + rand.nextInt(43); // 1980 - 2022
        double rating = Math.round((rand.nextDouble() * 10.0) * 10.0) / 10.0; // 0.0 - 10.0

        return new Movie(movieId, title, genre, releaseYear, rating);
    }
}
