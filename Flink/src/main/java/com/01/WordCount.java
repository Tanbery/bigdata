package com.acmecorp;

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

    env.getConfig().setGlobalJobParameters(params);

    // DataSet<String> text = env.readTextFile(params.get("input"));
    DataSet<String> text = env.fromCollection(readFile());
   
    DataSet<String> filtered = text.filter(new FilterFunction<String>()
    {
      public boolean filter(String value) {
        return value.startsWith("N");
      }
    });
    
    DataSet<Tuple2<String, Integer>> tokenized = filtered.map(new Tokenizer());

    DataSet<Tuple2<String, Integer>> counts = tokenized.groupBy(new int[] { 0 }).sum(1);
    System.out.println("COUNT");
    counts.print();
//    env.execute("WordCount Example");

    // if (params.has("output")) {
    // counts.writeAsCsv(params.get("output"), "\n", " ");
    // env.execute("WordCount Example");
    // }

  }
  private static ArrayList<String> readFile() {
        InputStream in = WordCount.class.getClassLoader().getResourceAsStream("wc.txt");
        ArrayList<String> out = new ArrayList<>();
        Scanner s = new Scanner(in);
        s.useDelimiter("\n");
        while(s.hasNext()) {
            out.add(s.next());
        }
        s.close();
        return out;
    }

  public static final class Tokenizer
      implements MapFunction<String, Tuple2<String, Integer>> {
    public Tuple2<String, Integer> map(String value) {
      return new Tuple2<String, Integer>(value, Integer.valueOf(1));
    }
  }
}