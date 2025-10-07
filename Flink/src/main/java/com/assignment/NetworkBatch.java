package com.assignment;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple4;

import com.udemy.dto.NetworkDto;
import com.udemy.util.MyApp;

public class NetworkBatch {
    public static void main(String[] args) throws Exception {

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<String> data = env.fromCollection(MyApp.readFile("ipdata"));

        // user_id,network_name,user_IP,user_country,website, click
        DataSet<NetworkDto> netData = data.map(new MapFunction<String,NetworkDto>() {
            public NetworkDto map(String str){
                return NetworkDto.split(str);
            }
        }); 
        // netData.first(10).print();
        
        DataSet<Tuple4<String, Integer,String,Integer>> tupNetData=netData
        .map(d -> Tuple4.of(d.getWebsite(), d.getClickCount(),d.getUserIp(), d.getNumber()))
        .returns(Types.TUPLE(Types.STRING,Types.INT,Types.STRING,Types.INT));
        
        // total number of clicks on every website in separate file
        tupNetData.groupBy(0).sum(1).print();

        // the website with maximum number of clicks in separate file.
        tupNetData.groupBy(0).maxBy(1).print();
        //the website with minimum number of clicks in separate file.
        tupNetData.groupBy(0).minBy(1).print();
        //Calculate number of distinct users on every website in separate file. 
        tupNetData.distinct().groupBy(2).sum(3).print();
        // Calculate the average time spent on website by users
        tupNetData.groupBy(0).sum(1).andSum(3).print();
        
    }
}