package com.learning;



import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class WordCountStreaming {

  public static void main(String[] args) throws Exception {
    // set up the stream execution environment
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // Checking input parameters
    final ParameterTool params = ParameterTool.fromArgs(args);

    // make parameters available in the web interface
    env.getConfig().setGlobalJobParameters(params);

    //Process Data from Socket as Stream
    // DataStream < String > text = env.socketTextStream("localhost", 9090); 
    
    //Process data from a file as Stream
    DataStream<String> text=env.readTextFile("/opt/stream/wc.txt");

    //Process Data from a folder
    // TextInputFormat fmt= new TextInputFormat(new Path(filePath));
    // FilePathFilter pathFilter = FilePathFilter.createDefaultFilter(); 
    // DataStream<String> text = env.readFile(fmt,filePath,FileProcessingMode.PROCESS_CONTINUOUSLY,5000,pathFilter);
    DataStream < Tuple2 < String, Integer >> counts = text.filter(new FilterFunction < String > () {
        public boolean filter(String value) {
          return value.startsWith("N");
        }
      })

      .map(new Tokenizer()) // split up the lines in pairs (2-tuples) containing: tuple2 {(name,1)...}
      .keyBy(t -> t.f0)
      .sum(1); // group by the tuple field "0" and sum up tuple field "1"

    counts.print();
    
    env.execute("Streaming WordCount");
  }

  public static final class Tokenizer implements MapFunction < String, Tuple2 < String, Integer >> {
    public Tuple2 < String,
    Integer > map(String value) {
      return new Tuple2 < String, Integer > (value, Integer.valueOf(1));
    }
  }

}