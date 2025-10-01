package com.udemy;
//package p1;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.udemy.dto.Numbers;

public class OperIterate {
  
  public static void main(String[] args) throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // env.generateSequence(0, 0)
    DataStream<Numbers> data = env.fromSequence(0, 5).map(new MapFunction<Long, Numbers>() {
      public Numbers map(Long value) {
        return new Numbers(value, 0);
      }
    });

    // prepare stream for iteration
    IterativeStream<Numbers> iteration = data.iterate(5000); // ( 0,0 1,0 2,0 3,0 4,0 5,0 )

    // define iteration
    DataStream<Numbers> plusOne = iteration
        .map(new MapFunction<Numbers, Numbers>() {
          public Numbers map(Numbers value) {
            if (value.getNumber() == 10)
              return value;
            else
              return new Numbers(value.getNumber() + 1, value.getIterCount() + 1);
          }
        }); // plusone 1,1 2,1 3,1 4,1 5,1 6,1

    plusOne.print();

    // part of stream to be used in next iteration (
    DataStream<Numbers> notEqualtoTen = plusOne.filter(new FilterFunction<Numbers>() {
      public boolean filter(Numbers value) {
        if (value.getNumber() == 10)
          return false;
        else
          return true;
      }
    });
    // feed data back to next iteration
    iteration.closeWith(notEqualtoTen);

    // data not feedback to iteration
    DataStream<Numbers> equaltoTen = plusOne.filter(new FilterFunction<Numbers>() {
      public boolean filter(Numbers value) {
        if (value.getNumber() == 10)
          return true;
        else
          return false;
      }
    });
    // notEqualtoTen.print();
    equaltoTen.print();
    // equaltoTen.writeAsText("/shared/ten");

    env.execute("Iteration Demo");
  }
}