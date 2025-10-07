package com.udemy;

import java.sql.Timestamp;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import com.udemy.dto.TimeAndText;
import com.udemy.util.MyApp;

//TumblingEvent

@SuppressWarnings("deprecation")
public class WatermarkCustom {
	public static void main(String[] args) throws Exception {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

		DataStream<String> data = env.socketTextStream("localhost", MyApp.portNumber);

		DataStream<TimeAndText> sum = data.map(new MapFunction<String, TimeAndText>() {
			public TimeAndText map(String s) {
				String[] words = s.split(",");
				return new TimeAndText(Long.parseLong(words[0]), words[1]);
			}
		})
				.assignTimestampsAndWatermarks(new DemoWatermark())
				.windowAll(EventTimeSessionWindows.withGap(Time.seconds(10)))
				// .allowedLateness(Time.seconds(2))
				// .sideOutputLateData(null)
				.reduce(new ReduceFunction<TimeAndText>() {
					public TimeAndText reduce(TimeAndText t1, TimeAndText t2) {
						int num1 = Integer.parseInt(t1.getText());
						int num2 = Integer.parseInt(t2.getText());
						int sum = num1 + num2;
						Timestamp t = new Timestamp(System.currentTimeMillis());
						return new TimeAndText(t.getTime(), "" + sum);
					}
				});
		sum.print();
		// sum.writeAsText("/home/jivesh/window");

		// execute program
		env.execute("Window");
	}

	// public static class DemoWatermark implements AssignerWithPunctuatedWatermarks<TimeAndText> {
	public static class DemoWatermark implements AssignerWithPeriodicWatermarks<TimeAndText> {
		private final long allowedlatetime = 3500; // 3.5 seconds

		private long currentMaxTimestamp = 0;

		public long extractTimestamp(TimeAndText element, long previousElementTimestamp) {
			long timestamp = element.getTimestamp();
			currentMaxTimestamp = Math.max(timestamp, currentMaxTimestamp);
			return timestamp;
		}

		public Watermark getCurrentWatermark() {
			// return the watermark as current highest timestamp minus the out-of-orderness
			// bound
			return new Watermark(currentMaxTimestamp - allowedlatetime);
		}
	}
}
