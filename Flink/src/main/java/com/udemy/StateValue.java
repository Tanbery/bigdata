package com.udemy;

import org.apache.flink.util.Collector;

import com.udemy.dto.TimeAndText;
import com.util.MyApp;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class StateValue {
  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStream<String> data = env.socketTextStream("localhost", MyApp.portNumber);

    DataStream<Long> sum = data.map(new MapFunction<String, TimeAndText>() {
      public TimeAndText map(String s) {
        String[] words = s.split(",");
        return new TimeAndText(Long.parseLong(words[0]), words[1]);
      }
    })
        .keyBy("text")
        .flatMap(new StatefulMap());
    sum.print();
    
    // execute program
    env.execute("State");
  }

  static class StatefulMap extends RichFlatMapFunction<TimeAndText, Long> {
    private transient ValueState<Long> sum; // 2
    private transient ValueState<Long> count; // 4

    public void flatMap(TimeAndText input, Collector<Long> out) throws Exception {
      Long currCount = 0L;
      Long currSum = 0L;

      if (count.value() != null) {
        currCount = count.value(); // 2
      }
      if (sum.value() != null) {
        currSum = sum.value(); // 4
      }

      currCount++;
      currSum = currSum + Long.parseLong(input.getText());

      count.update(currCount);
      sum.update(currSum);

      if (currCount >= 5) {
        /* emit sum of last 10 elements */
        out.collect(sum.value());
        /* clear value */
        count.clear();
        sum.clear();
      }
    }

    public void open(Configuration conf) {
      // ValueStateDescriptor<Long> descriptor = new ValueStateDescriptor<Long>("sum",TypeInformation.of(new TypeHint<Long>() {          }));
      @SuppressWarnings("deprecation")
      ValueStateDescriptor<Long> descriptor = new ValueStateDescriptor<Long>("sum",Long.class,0L);
      sum = getRuntimeContext().getState(descriptor);

      // ValueStateDescriptor<Long> descriptor2 = new ValueStateDescriptor<Long>("count",          TypeInformation.of(new TypeHint<Long>() {          }));
      @SuppressWarnings("deprecation")
      ValueStateDescriptor<Long> descriptor2 = new ValueStateDescriptor<Long>("count",Long.class,0L);
      count = getRuntimeContext().getState(descriptor2);
    }
  }
}
