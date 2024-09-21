package com.learning;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;

public class WordCount {

  public static void main(String[] args) throws Exception {
    ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

    ParameterTool params = ParameterTool.fromArgs(args);

    env.getConfig().setGlobalJobParameters(params); //make the parameter available for all nodes.

    DataSet<String> text = env.fromCollection(readFile("wc.txt"));
    //if (!params.has("input")) return;

    
    // DataSet<String> text = env.readTextFile(params.get("input"));
    //readCsvFiles(path)==> return tuple2<String,Integer> to Tuple25<...>
    //readFileOfPrimitives(path,Class)
    //readFileOfPrimitives(path,delimeter, Class)
    //readHadoopFile(FileFormat, Keytype, ValueType, Path)
    //readSequenceFile(KeyType, ValueType, Path)
       
    
    DataSet<String> filtered = text.filter(new FilterFunction<String>()
    {
      public boolean filter(String value) {
        return value.startsWith("N");
      }
    });
    
   
    DataSet<Tuple2<String, Integer>> tokenized = filtered.map(new Tokenizer());

    DataSet<Tuple2<String, Integer>> counts = tokenized.groupBy(0).sum(1);
    
    
    if (params.has("output")) 
      counts.writeAsCsv(params.get("output"), "\n", " ");
    
    counts.print();
    env.execute("WordCount Example");

  }
  private static ArrayList<String> readFile(String filename) {
      InputStream in = WordCount.class.getClassLoader().getResourceAsStream(filename);
      //  WordCount.class.getClassLoader().getResource(filename);
    
        ArrayList<String> out = new ArrayList<>();
        Scanner s = new Scanner(in);
        s.useDelimiter("\n");
        while(s.hasNext()) {
            out.add(s.next());
        }
        s.close();
        return out;
    }

  public static final class Tokenizer implements MapFunction<String, Tuple2<String, Integer>> {
    public Tuple2<String, Integer> map(String value) {
      return new Tuple2<String, Integer>(value, Integer.valueOf(1));
    }
  }
}