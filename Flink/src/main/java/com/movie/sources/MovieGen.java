package com.movie.sources;

import org.apache.flink.streaming.api.functions.source.SourceFunction;

import com.movie.dto.Movie;

public class MovieGen implements SourceFunction<Movie> {

    private volatile boolean running = true;
    private final int GEN_SPEED_MS = 5000;
    @Override
    public void run(SourceContext<Movie> ctx) throws Exception {
        while (running) {
            Movie movie = Movie.generate();
            ctx.collect(movie);
            Thread.sleep(GEN_SPEED_MS); // Adjust the sleep time as needed
        }
    }

    @Override
    public void cancel() {
        running = false;
    }
    
}

