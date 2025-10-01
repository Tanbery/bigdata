package com.udemy;

import java.sql.Timestamp;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import com.udemy.dto.TimeAndText;
import com.util.MyApp;

@SuppressWarnings("deprecation")
public class WinTumblingEventTime {


  public static void flinkV12() throws Exception {
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

    DataStream<String> strStream = env.socketTextStream("localhost", MyApp.portNumber);

    DataStream<TimeAndText> data = strStream.map(new Tokenizer())
      .assignTimestampsAndWatermarks(new AscendingTimestampExtractor<TimeAndText>()
      {
        public long extractAscendingTimestamp(TimeAndText t)
        {
          return t.getTimestamp();
        }
      })
      .windowAll(TumblingEventTimeWindows.of(Time.seconds(5)))
      .reduce(new ReduceRand()); 
    data.print();    
    env.execute("Streaming TumblingEventTimeWindows");
  }

  public static final class Tokenizer implements MapFunction<String, TimeAndText> {
    public TimeAndText map(String value) {
      String[] words = value.split(","); // words = [{01-06-2018},{June},{Category5},{Bat}.{12}
      return new TimeAndText(Long.parseLong(words[0]), words[1]);
    }
  }

  public static class ReduceRand implements ReduceFunction<TimeAndText> {
    public TimeAndText reduce(TimeAndText cur, TimeAndText pre) {
      String text= "" + (Integer.parseInt(cur.getText()) + Integer.parseInt(pre.getText())); 
      return new TimeAndText(System.currentTimeMillis(), text );
    }
  }

  public static void flinkV13() throws Exception {
    // set up the streaming execution environment
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    WatermarkStrategy < TimeAndText> ws =WatermarkStrategy.< TimeAndText> forMonotonousTimestamps()
          .withTimestampAssigner((event, timestamp) -> event.getTimestamp());

    DataStream < String > data = env.socketTextStream("localhost", MyApp.portNumber);

    DataStream < TimeAndText> sum = data.map(new MapFunction < String, TimeAndText> () {
        public TimeAndText map(String s) {
          String[] words = s.split(",");
          return new TimeAndText (Long.parseLong(words[0]), words[1]);
        }
      })

      .assignTimestampsAndWatermarks(ws)
      .windowAll(TumblingEventTimeWindows.of(Time.seconds(5)))
      .reduce(new ReduceFunction < TimeAndText> () {
        public TimeAndText reduce(TimeAndText t1, TimeAndText t2) {
          int num1 = Integer.parseInt(t1.getText());
          int num2 = Integer.parseInt(t2.getText());
          int sum = num1 + num2;
          Timestamp t = new Timestamp(System.currentTimeMillis());
          return new TimeAndText (t.getTime(), "" + sum);
        }
      });
    // sum.addSink(StreamingFileSink
    //   .forRowFormat(new Path("/home/jivesh/window"),
    //     new SimpleStringEncoder < TimeAndText> ("UTF-8"))
    //   .withRollingPolicy(DefaultRollingPolicy.builder().build())
    //   .build());
    sum.print();
    // execute program
    env.execute("Window");
  }
    public static void main(String[] args) throws Exception {
      WinTumblingEventTime.flinkV13();
    }
}