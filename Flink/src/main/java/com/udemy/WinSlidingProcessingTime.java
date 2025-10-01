package com.udemy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import com.udemy.dto.Avg;
import com.util.MyApp;

public class WinSlidingProcessingTime {
  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStream < String > data = env.socketTextStream("localhost", MyApp.portNumber);

    
    DataStream < Avg> mapped = data.map(new Splitter());                                                                           
    DataStream < Avg> reduced = mapped.keyBy("month")
      .window(SlidingProcessingTimeWindows.of(Time.seconds(3), Time.seconds(1)))
      .reduce(new Reduce1());
    
    reduced.print();
    // reduced.addSink(StreamingFileSink
    //   .forRowFormat(new Path("/home/jivesh/www"),
    //     new SimpleStringEncoder < Avg> ("UTF-8"))
    //   .withRollingPolicy(DefaultRollingPolicy.builder().build())
    //   .build());
    // // execute program
    env.execute("Avg Profit Per Month");
  }

  public static class Reduce1 implements ReduceFunction < Avg> {
    public Avg reduce(Avg cur,      Avg pre) {
      return new Avg (cur.getMonth(), cur.getCategory(), cur.getProductName(), cur.getProfit() + pre.getProfit(), cur.getCount() + pre.getCount());
    }
  }
  public static class Splitter implements MapFunction < String, Avg> {
    public Avg map(String value) // 01-06-2018,June,Category5,Bat,12
    {
      return Avg.split(value);
    } //    June    Category5      Bat                      12 
  }
}
