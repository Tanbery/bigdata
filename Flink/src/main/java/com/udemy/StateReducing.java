package com.udemy;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ReducingState;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import com.udemy.dto.TimeAndText;
import com.udemy.util.MyApp;

public class StateReducing {
  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    DataStream < String > data = env.socketTextStream("localhost", MyApp.portNumber);

    DataStream < Long > sum = data.map(new MapFunction < String, TimeAndText> () {
        public TimeAndText map(String s) {
          String[] words = s.split(",");
          return new TimeAndText (Long.parseLong(words[0]), words[1]);
        }
      })
      .keyBy("text")
      .flatMap(new StatefulMap());
    
    sum.print();
      //sum.writeAsText("/home/jivesh/state2");
    // sum.addSink(StreamingFileSink
    //   .forRowFormat(new Path("/home/jivesh/state2"), new SimpleStringEncoder < Long > ("UTF-8"))
    //   .withRollingPolicy(DefaultRollingPolicy.builder().build())
    //   .build());

    // execute program
    env.execute("State");
  }

  public static class StatefulMap extends RichFlatMapFunction < TimeAndText , Long > {
    private transient ValueState < Long > count;
    private transient ReducingState < Long > sum;

    public void flatMap(TimeAndText input, Collector < Long > out) throws Exception {
      Long currValue = Long.parseLong(input.getText());
      Long currCount = 0L;

      if (count.value() != null) {
        currCount = count.value();
      }

      currCount += 1;

      count.update(currCount);
      sum.add(currValue);

      if (currCount >= 10) {
        /* emit sum of last 10 elements */
        out.collect(sum.get());
        /* clear value */
        count.clear();
        sum.clear();
      }
    }

    public void open(Configuration conf) {

      ValueStateDescriptor < Long > descriptor2 = new ValueStateDescriptor < Long > ("count", Long.class);
      count = getRuntimeContext().getState(descriptor2);

      ReducingStateDescriptor < Long > sumDesc = new ReducingStateDescriptor < Long > ("reducing sum", new SumReduce(), Long.class);
      sum = getRuntimeContext().getReducingState(sumDesc);
    }

    public class SumReduce implements ReduceFunction < Long > {
      public Long reduce(Long commlativesum, Long currentvalue) {
        return commlativesum + currentvalue;
      }
    }
  }
}
