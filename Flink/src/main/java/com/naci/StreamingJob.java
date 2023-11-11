package com.naci;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;



public class StreamingJob {

	public static void main(String[] args) throws Exception {
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		final ParameterTool pt = ParameterTool.fromArgs(args);

		DataStream<String> input = env.addSource(new ClickEventGenerator(pt));
		// input.executeAndCollect();
		// execute program
		System.out.println("##############START########################");
		input.print();
		env.execute("Streaming Analytics");
		System.out.println("##############END##########################");
	}

}
