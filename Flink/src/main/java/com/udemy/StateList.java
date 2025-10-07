package com.udemy;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import com.udemy.dto.TimeAndText;
import com.udemy.util.MyApp;

public class StateList {
  @SuppressWarnings("deprecation")
  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    DataStream < String > data = env.socketTextStream("localhost", MyApp.portNumber);

    DataStream <TimeAndText> sum = data.map(new MapFunction < String, TimeAndText> () {
        public TimeAndText map(String s) {
          String[] words = s.split(",");
          return new TimeAndText (Long.parseLong(words[0]), words[1]);
        }
      })
      .keyBy("text")
      .flatMap(new StatefulMap());
    
    sum.print();

    // sum.addSink(StreamingFileSink
    //   .forRowFormat(new Path("/home/jivesh/state2"), new SimpleStringEncoder < Tuple2 < String, Long >> ("UTF-8"))
    //   .withRollingPolicy(DefaultRollingPolicy.builder().build())
    //   .build());

    // execute program
    env.execute("State");
  }

  public static class StatefulMap extends RichFlatMapFunction < TimeAndText , TimeAndText> {
    private transient ValueState < Long > count;
    private transient ListState < Long > numbers;

    public void flatMap(TimeAndText input, Collector < TimeAndText> out) throws Exception {
      Long currValue = Long.parseLong(input.getText());
      Long currCount = 0L;

      if (count.value() != null) 
        currCount = count.value();
      currCount += 1;
      count.update(currCount);
      numbers.add(currValue);

      if (currCount >= 10) {
        Long sum = 0L;
        for (Long number: numbers.get()) {
          sum = sum + number;
        }
        out.collect(new TimeAndText(System.currentTimeMillis(), sum.toString()));
        
        /* clear value */
        count.clear();
        numbers.clear();
      }
    }
    
    public void open(Configuration conf) {
      ListStateDescriptor < Long > listDesc = new ListStateDescriptor < Long > ("numbers", Long.class);
      numbers = getRuntimeContext().getListState(listDesc);

      ValueStateDescriptor < Long > descriptor2 = new ValueStateDescriptor < Long > ("count", Long.class);
      count = getRuntimeContext().getState(descriptor2);
    }
  }
}
