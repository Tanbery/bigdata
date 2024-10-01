package com.learning;



import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.*;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CabAssignment {

  public static void main(String[] args) throws Exception {
    // set up the stream execution environment
    final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    // Checking input parameters
    final ParameterTool params = ParameterTool.fromArgs(args);

    // make parameters available in the web interface
    env.getConfig().setGlobalJobParameters(params);

    DataStream<String> text=env.readTextFile(CabAssignment.class.getClassLoader().getResource("./cab.txt").getPath());
    //cab id, cab number plate, cab type, cab driver name, ongoing trip/not, pickup location, destination,passenger count ==> 8
    
    //Mapped and Filtered data
    DataStream < Tuple9<String,String, String, String, String, String, String, Integer, Integer>> capData = text.map(new Tokenizer()).filter(new FilterFunction< Tuple9<String,String, String, String, String, String, String, Integer, Integer>> ()
    {
      public boolean filter(Tuple9<String,String, String, String, String, String, String, Integer, Integer> value) {
        return value.f4.equalsIgnoreCase("yes");
      }
    }); 
    
    //
    DataStream < Tuple5<String,String,String,Integer, Integer>> validCap=capData.map(new MapFunction<Tuple9<String,String,String,String,String,String,String,Integer, Integer>,Tuple5<String,String,String,Integer, Integer>>() {
      public Tuple5<String,String,String,Integer, Integer> map(Tuple9<String,String, String, String, String, String, String, Integer,Integer> val) {

          return new Tuple5<String,String,String,Integer, Integer>(val.f3,val.f5,val.f6,val.f7,1); //Driver, pickup,dest,passCount,1
      }
    });
    // .keyBy(value -> new Tuple3<String,String,String>(value.f0, value.f1, value.f2)).reduce(new ReduceFunction<Tuple5<String,String,String,Integer,Integer>>() {
    //   public Tuple5<String, String, String, Integer, Integer> reduce(
    //         Tuple5<String, String, String, Integer, Integer> current,
    //         Tuple5<String, String, String, Integer, Integer> pre_result) {
    //       return new Tuple5<String, String, String, Integer, Integer>(current.f0, current.f1, current.f2, current.f3 + pre_result.f3, current.f4 + pre_result.f4);
    //     }
    // });
    // validCap.keyBy(value -> new Tuple3<String,String,String>(value.f0, value.f1, value.f2)).sum(4).print();

    //Populer Destination
    validCap.keyBy(value -> value.f2).reduce(new ReduceFunction<Tuple5<String,String,String,Integer,Integer>>() {
        public Tuple5<String, String, String, Integer, Integer> reduce(
            Tuple5<String, String, String, Integer, Integer> current,
            Tuple5<String, String, String, Integer, Integer> pre_result) {
          return new Tuple5<String, String, String, Integer, Integer>("", "", current.f2, current.f3 + pre_result.f3, current.f4 + pre_result.f4);
        }}).keyBy(value -> value.f2).maxBy(4).print();




    // validCap.print();
    // capData.print();
    
    env.execute("Cab Assignement");
  }

  public static final class Tokenizer implements MapFunction < String, Tuple9<String,String, String, String, String, String, String, Integer, Integer>> {
    public Tuple9<String,String, String, String, String, String, String, Integer, Integer> map(String value) {

      //cab id, cab number plate, cab type, cab driver name, ongoing trip/not, pickup location, destination,passenger count ==> 8
      String[] fields = value.split(","); 
      fields[0] = (!fields[0].equalsIgnoreCase("'null'")) ?  fields[0]:""; 
      fields[1] = (!fields[1].equalsIgnoreCase("'null'")) ?  fields[1]:""; 
      fields[2] = (!fields[2].equalsIgnoreCase("'null'")) ?  fields[2]:""; 
      fields[3] = (!fields[3].equalsIgnoreCase("'null'")) ?  fields[3]:""; 
      fields[4] = (!fields[4].equalsIgnoreCase("'null'")) ?  fields[4]:""; 
      fields[5] = (!fields[5].equalsIgnoreCase("'null'")) ?  fields[5]:""; 
      fields[6] = (!fields[6].equalsIgnoreCase("'null'")) ?  fields[6]:""; 
      int passengerCount = (!fields[7].equalsIgnoreCase("'null'")) ?  Integer.parseInt(fields[7]):0; 
      return new Tuple9<String,String, String, String, String, String, String, Integer, Integer>
                                (fields[0], fields[1],fields[2],fields[3],fields[4],fields[5],fields[6],passengerCount,1);
    }
  }

}