package com.udemy;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.udemy.dto.Avg;
import com.util.MyApp;

public class OperAggregation {
  
  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // DataStream<String> data = env.readTextFile("/opt/stream/avg1");
    DataStream<String> data = env.fromCollection(MyApp.readFile("avg1"));

    // month, product, category, profit, count
    DataStream<Avg> mapped = data.map(new Splitter()); // tuple [June,Category5,Bat,12,1]
    // [June,Category4,Perfume,10,1]
    mapped.keyBy("month").sum  ("profit").print();
    mapped.keyBy("month").min  ("profit").print(); //Save and track just min value. other values will fill randomly
    mapped.keyBy("month").minBy("profit").print(); //Save and track whole tuple.
    mapped.keyBy("month").max  ("profit").print();
    mapped.keyBy("month").maxBy("profit").print();

    // execute program
    env.execute("Aggregation Job");
  }

  public static class Splitter implements MapFunction<String, Avg> {
    public Avg map(String value) // 01-06-2018,June,Category5,Bat,12
    {
      String[] words = value.split(","); // words = [{01-06-2018},{June},{Category5},{Bat}.{12}      // ignore timestamp, we don't need it for any calculations
      return new Avg(words[1], words[2], words[3],Integer.parseInt(words[4]), 1);
    } // June Category5 Bat 12
  }
}