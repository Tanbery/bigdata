package com.udemy;
import java.sql.Timestamp;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import com.udemy.dto.TimeAndText;
import com.udemy.util.MyApp;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;

//TumblingEvent

public class WinSessionEventTime {
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    WatermarkStrategy < TimeAndText> ws =
      WatermarkStrategy
      . < TimeAndText> forMonotonousTimestamps()
      .withTimestampAssigner((event, timestamp) -> event.getTimestamp());

    DataStream < String > data = env.socketTextStream("localhost", MyApp.portNumber);

    DataStream < TimeAndText> sum = data.map(new MapFunction < String, TimeAndText> () {
        public TimeAndText map(String s) {
          String[] words = s.split(",");
          return new TimeAndText (Long.parseLong(words[0]), words[1]);
        }
      })

      .assignTimestampsAndWatermarks(ws)
      .windowAll(EventTimeSessionWindows.withGap(Time.seconds(1)))
      .reduce(new ReduceFunction < TimeAndText> () {
        public TimeAndText reduce(TimeAndText t1, TimeAndText t2) {
          int num1 = Integer.parseInt(t1.getText());
          int num2 = Integer.parseInt(t2.getText());
          int sum = num1 + num2;
          Timestamp t = new Timestamp(System.currentTimeMillis());
          return new TimeAndText (t.getTime(), "" + sum);
        }
      });
      sum.print();
    // sum.addSink(StreamingFileSink
    //   .forRowFormat(new Path("/home/jivesh/window"),
    //     new SimpleStringEncoder < TimeAndText> ("UTF-8"))
    //   .withRollingPolicy(DefaultRollingPolicy.builder().build())
    //   .build());

    // execute program
    env.execute("Window");
  }
}
