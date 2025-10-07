package com.udemy;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import com.udemy.dto.Avg;
import com.udemy.util.MyApp;

public class WinTumblingProcessingTime {

  public static void main(String[] args) throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    // env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

    DataStream<String> strStream = env.socketTextStream("localhost", MyApp.portNumber);
    DataStream<Avg> mapped = strStream.map(new Tokenizer()); 
    DataStream<Avg> reduced =mapped.keyBy(Avg::getMonth).window(TumblingProcessingTimeWindows.of(Time.seconds(2))).reduce(new ReduceAvg()); 
    reduced.print();
    
    env.execute("Streaming WordCount");
  }

  public static final class Tokenizer implements MapFunction<String, Avg> {
    public Avg map(String value) {
      String[] words = value.split(","); // words = [{01-06-2018},{June},{Category5},{Bat}.{12}
      return new Avg(words[1], words[2], words[3], Integer.parseInt(words[4]), 1);
    }
  }

  public static class ReduceAvg implements ReduceFunction<Avg> {
    public Avg reduce(Avg current, Avg pre_result) {
      return new Avg(current.getMonth(), current.getCategory(), current.getProductName(), 
                    current.getProfit() + pre_result.getProfit(), 
                    current.getCount() + pre_result.getCount());
    }
  }


  

}