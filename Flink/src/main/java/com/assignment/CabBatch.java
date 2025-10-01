package com.assignment;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;

import com.udemy.dto.CabDto;
import com.util.MyApp;

public class CabBatch {
    public static void main(String[] args) throws Exception {

        final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        DataSet<String> data = env.fromCollection(MyApp.readFile("cab1"));

        DataSet<CabDto> cabData = data.map(new Splitter()); // tuple [id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5]
        // cabData.print();

        DataSet<CabDto> arrivedCab = cabData.filter(new FilterFunction<CabDto>() {
            public boolean filter(CabDto value) {
                return value.getIsReached();
            }
        });
        arrivedCab.print();
        
        // ######## Popular/ destination. | Where more number of people reach.
        // arrivedCab.groupBy("locDestination").sum("passengerNumber").print();
        // arrivedCab.groupBy(4).sum(7).print();

        //###### Average number of passengers from each pickup location. | average = total no. of passengers from a location / no. of trips from that location.
        // arrivedCab.keyBy("locPickup").sum("passengerNumber").print();
        
        // DataStream<DtoCab> reduceCab = arrivedCab.keyBy("locPickup").reduce(
        //         new ReduceFunction<DtoCab>() {
        //             public DtoCab reduce(DtoCab cur, DtoCab pre) {
        //                 // id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5
        //                 return new DtoCab("", "", "", "",cur.getIsReached(), cur.getLocPickup(), "",
        //                         cur.getPassengerNumber() + pre.getPassengerNumber(), cur.getNumber() + pre.getNumber());

        //             }
        //         });
        //     // reduceCab.keyBy("locPickup").sum("passengerNumber").print();

        // DataStream<Tuple2<String, Double>> avgNumberPerLoc = reduceCab
        //         .map(new MapFunction<DtoCab, Tuple2<String, Double>>() {
        //             public Tuple2<String, Double> map(DtoCab input) {
        //                 return new Tuple2<String, Double>(input.getLocPickup(),
        //                         new Double((input.getPassengerNumber() * 1.0) / input.getNumber()));
        //             }
        //         });
        // avgNumberPerLoc.print();

        //########  Average number of trips for each driver.  | average =  total no. of passengers drivers has picked / total no. of trips he made
        // DataStream<DtoCab> driverCab = arrivedCab.keyBy("driver").reduce(
        //         new ReduceFunction<DtoCab>() {
        //             public DtoCab reduce(DtoCab cur, DtoCab pre) {
        //                 // id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5
        //                 return new DtoCab("", "", "", cur.getDriver(),cur.getIsReached(), "", "",
        //                         cur.getPassengerNumber() + pre.getPassengerNumber(), cur.getNumber() + pre.getNumber());

        //             }
        //         });
            // reduceCab.keyBy("locPickup").sum("passengerNumber").print();

        // DataStream<Tuple2<String, Double>> avgNumberPerDriver = driverCab
        //         .map(new MapFunction<DtoCab, Tuple2<String, Double>>() {
        //             public Tuple2<String, Double> map(DtoCab input) {
        //                 return new Tuple2<String, Double>(input.getDriver(),
        //                         new Double((input.getPassengerNumber() * 1.0) / input.getNumber()));
        //             }
        //         });
        // avgNumberPerDriver.print();
 
        // execute program
        // env.execute("Aggregation Job");
    }

    public static class Splitter implements MapFunction<String, CabDto> {
        public CabDto map(String value) // [id_4214,PB7526,Sedan,Wanda,yes,Sector 19,Sector 10,5]
        {
            return CabDto.split(value);
        }
    }
}