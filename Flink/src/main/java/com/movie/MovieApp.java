package com.movie;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.movie.dto.Movie;
import com.movie.dto.UserActivity;
import com.movie.dto.UserProfile;
import com.movie.sources.MovieGen;
import com.movie.sources.UserGen;
import com.movie.sources.UserActivityGen;

public class MovieApp {
    public static void main(String[] args) throws Exception {
        System.out.println("MovieApp started...");
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        

        DataStream<UserProfile> users = env.addSource(new UserGen());
        DataStream<Movie> movies = env.addSource(new MovieGen());
        DataStream<UserActivity> actions = env.addSource(new UserActivityGen());
        
        users.print();
        movies.print();
        actions.print();

        // actions.filter(new FilterFunction<UserActivity>() {
        //     @Override
        //     public boolean filter(UserActivity value) throws Exception {
        //         return value.action.equals("PLAY");
        //     }
        // }).print();
        env.execute("Movie Streaming App");
    }
}
