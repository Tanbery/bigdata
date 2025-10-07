package com.udemy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;

import com.udemy.dto.Avg;
import com.udemy.util.MyApp;

public class WinGlobalTrigger {
  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStream < String > data = env.socketTextStream("localhost", MyApp.portNumber);

    // month, product, category, profit, count
    DataStream < Avg> mapped = data.map(new Splitter()); // tuple  [June,Category5,Bat,12,1]
    //        [June,Category4,Perfume,10,1]
    // groupBy 'month'
    DataStream < Avg> reduced = mapped
      .keyBy("month")
      .window(GlobalWindows.create())
      .trigger(CountTrigger.of(5))
      .reduce(new Reduce1());
    // June { [Category5,Bat,12,1] Category4,Perfume,10,1}	//rolling reduce
    // reduced = { [Category4,Perfume,22,2] ..... }
   reduced.print();
    // reduced.addSink(StreamingFileSink
    //   .forRowFormat(new Path("/home/jivesh/www"),
    //     new SimpleStringEncoder < Avg> ("UTF-8"))
    //   .withRollingPolicy(DefaultRollingPolicy.builder().build())
    //   .build());

    // execute program
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
      String[] words = value.split(","); // words = [{01-06-2018},{June},{Category5},{Bat}.{12}
      // ignore timestamp, we don't need it for any calculations
      //Long timestamp = Long.parseLong(words[5]);
      return new Avg (words[1], words[2], words[3], Integer.parseInt(words[4]), 1);
    } //    June    Category5      Bat                      12
  }
}
