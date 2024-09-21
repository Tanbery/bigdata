package com.learning;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple5;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Aggregation {
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStream<String> data = env.readTextFile("/opt/stream/avg1");

    // month, product, category, profit, count
    DataStream<Tuple5<String, String, String, Integer, Integer>> mapped = data.map(new Splitter()); // tuple
                                                                                                    // [June,Category5,Bat,12,1]
    // [June,Category4,Perfume,10,1]
    mapped.keyBy(0).sum(3).print();
    mapped.keyBy(0).min(3).print(); //Save and track just min value. other values will fill randomly
    mapped.keyBy(0).minBy(3).print(); //Save and track whole tuple.
    mapped.keyBy(0).max(3).print();
    mapped.keyBy(0).maxBy(3).print();


    // execute program
    env.execute("Aggregation Job");
  }

  public static class Splitter implements MapFunction<String, Tuple5<String, String, String, Integer, Integer>> {
    public Tuple5<String, String, String, Integer, Integer> map(String value) // 01-06-2018,June,Category5,Bat,12
    {
      String[] words = value.split(","); // words = [{01-06-2018},{June},{Category5},{Bat}.{12}
      // ignore timestamp, we don't need it for any calculations
      return new Tuple5<String, String, String, Integer, Integer>(words[1], words[2], words[3],
          Integer.parseInt(words[4]), 1);
    } // June Category5 Bat 12
  }
}