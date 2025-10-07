package com.udemy;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup;

import com.udemy.dto.Avg;
import com.udemy.util.MyApp;

public class CheckPointing {
  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    //Start checkpoint every 1000ms
    env.enableCheckpointing(1000); 
    //to set minumum progress time to happen between checkpoints
    env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
    //Checkpoint have to complete within 10_000ms or discarded
    env.getCheckpointConfig().setCheckpointTimeout(10_000);
    //set exactly-once (this is default)
    env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE); //AT_LEAST_ONCE
    //allow only one checkpoint to be in progress at the same time
    env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
    //enable externalized checkpoints which are retained after job cancellation
    env.getCheckpointConfig().enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION); //DELETE_ON_CANCELLATION or NO_EXTERNALIZED_CHECKPOINTS
    //
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, 10));

    // DataStream<String> data = env.readTextFile(""/opt/stream/"avg");
    // DataStream<String> data = env.fromCollection(readFile("avg"));
    DataStream<String> data = env.fromCollection(MyApp.readFile("avg1"));

    // month, product, category, profit, count
    DataStream<Avg> mapped = data.map(new Splitter()); // tuple                                                                                                
    // [June,Category5,Bat,12,1]
    // [June,Category4,Perfume,10,1]

    
    DataStream<Avg> reduced = mapped.keyBy("month")
        .reduce(new Reduce1());
    // June { [Category5,Bat,12,1] Category4,Perfume,10,1} //rolling reduce
    // reduced = { [Category4,Perfume,22,2] ..... }
    
    // month, avg. profit
    DataStream<Tuple2<String, Double>> profitPerMonth = reduced.map(new MapFunction< Avg, Tuple2<String, Double>>() 
        {
          public Tuple2<String, Double> map(Avg input) {
            return new Tuple2<String, Double>(input.getMonth(), new Double((input.getProfit() * 1.0) / input.getCount()));
          }
        });
    reduced.print();
    profitPerMonth.print();
    
    // execute program
    env.execute("Avg Profit Per Month");
  }

  // *************************************************************************
  // USER FUNCTIONS // pre_result = Category4,Perfume,22,2
  // *************************************************************************

  public static class Reduce1 implements ReduceFunction<Avg> {
    public Avg reduce(Avg current, Avg pre_result) {
      return new Avg(current.getMonth(), current.getCategory(), current.getProductName(), 
                    current.getProfit() + pre_result.getProfit(), 
                    current.getCount() + pre_result.getCount());
    }
  }

  public static class Splitter implements MapFunction<String, Avg> {
    public Avg map(String value) // 01-06-2018,June,Category5,Bat,12
    {
      String[] words = value.split(","); // words = [{01-06-2018},{June},{Category5},{Bat}.{12}
      // ignore timestamp, we don't need it for any calculations
      return new Avg(words[1], words[2], words[3], Integer.parseInt(words[4]), 1);
    } // June Category5 Bat 12
  }
  
}