package com.assignment;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.udemy.dto.CabDto;
import com.util.MyApp;

public class CabStream {
    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        DataStream<String> data = env.fromCollection(MyApp.readFile("cab1"));

        DataStream<CabDto> cabData = data.map(new Splitter()); // tuple [id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5]
        // cabData.print();

        DataStream<CabDto> arrivedCab = cabData.filter(new FilterFunction<CabDto>() {
            public boolean filter(CabDto value) {
                return value.getIsReached();
            }
        });
        // ######## Popular/ destination. | Where more number of people reach.
        arrivedCab.keyBy(CabDto::getLocDestination).sum("passengerNumber").print();

        //###### Average number of passengers from each pickup location. | average = total no. of passengers from a location / no. of trips from that location.
        // arrivedCab.keyBy("locPickup").sum("passengerNumber").print();
        
        DataStream<CabDto> reduceCab = arrivedCab.keyBy("locPickup").reduce(
                new ReduceFunction<CabDto>() {
                    public CabDto reduce(CabDto cur, CabDto pre) {
                        // id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5
                        return new CabDto("", "", "", "",cur.getIsReached(), cur.getLocPickup(), "",
                                cur.getPassengerNumber() + pre.getPassengerNumber(), cur.getNumber() + pre.getNumber());

                    }
                });
            // reduceCab.keyBy("locPickup").sum("passengerNumber").print();

        DataStream<Tuple2<String, Double>> avgNumberPerLoc = reduceCab
                .map(new MapFunction<CabDto, Tuple2<String, Double>>() {
                    public Tuple2<String, Double> map(CabDto input) {
                        return new Tuple2<String, Double>(input.getLocPickup(),
                                new Double((input.getPassengerNumber() * 1.0) / input.getNumber()));
                    }
                });
        avgNumberPerLoc.print();

        //########  Average number of trips for each driver.  | average =  total no. of passengers drivers has picked / total no. of trips he made
        DataStream<CabDto> driverCab = arrivedCab.keyBy("driver").reduce(
                new ReduceFunction<CabDto>() {
                    public CabDto reduce(CabDto cur, CabDto pre) {
                        // id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5
                        return new CabDto("", "", "", cur.getDriver(),cur.getIsReached(), "", "",
                                cur.getPassengerNumber() + pre.getPassengerNumber(), cur.getNumber() + pre.getNumber());
                    }
                });
            // reduceCab.keyBy("locPickup").sum("passengerNumber").print();

        DataStream<Tuple2<String, Double>> avgNumberPerDriver = driverCab
                .map(new MapFunction<CabDto, Tuple2<String, Double>>() {
                    public Tuple2<String, Double> map(CabDto input) {
                        return new Tuple2<String, Double>(input.getDriver(), new Double((input.getPassengerNumber() * 1.0) / input.getNumber()));
                    }
                });
        avgNumberPerDriver.print();
 
        // execute program
        env.execute("Aggregation Job");
    }

    public static class Splitter implements MapFunction<String, CabDto> {
        public CabDto map(String value) // [id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5]
        {
            return CabDto.split(value);
        }
    }
}