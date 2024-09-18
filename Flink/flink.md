
# Docker Compose File
```yml
version: '2'
services:
 flink-master:
   image: bde2020/flink-master:1.14.5-hadoop3.2
   hostname: flink-master
   container_name: flink-master
   volumes:
     - /home/enver/udemy-flink:/opt
   ports:
     - "8080:8080"
     - "8081:8081"

 flink-worker:
   image: bde2020/flink-worker:1.14.5-hadoop3.2
   hostname: flink-worker
   container_name: flink-worker
   volumes:
     - /home/enver/udemy-flink:/opt
   environment:
     - FLINK_MASTER_PORT_6123_TCP_ADDR=flink-master
#     - FLINK_NUM_TASK_SLOTS=2
   depends_on:
      - flink-master

```
# How to run Flink
```shell
docker exec -it flink-master bash

#WordCount Class as default class, other class 
flink run Flink.jar --input file:///opt/wc.txt --output file:///opt/wc_out.csv
flink run Flink.jar --input file:///opt/wc.txt 
flink run -c com.f02.JoinExample1 Flink.jar
flink run Flink.jar
```
# Reading data from Files for DataSet
```java
DataSet<String> text = env.readTextFile(path);
DataSet<String> text = env.readCsvFiles(path) //return tuple2<String,Integer> to Tuple25<...>
DataSet<String> text = env.readFileOfPrimitives(path,Class)
DataSet<String> text = env.readFileOfPrimitives(path,delimeter, Class)
DataSet<String> text = env.readHadoopFile(FileFormat, KeyType, ValueType, Path)
DataSet<String> text = env.readSequenceFile(KeyType, ValueType, Path)
```

# Join Optimization Options for better performance
```java
DataSet<...> joined = firstSet.join(secondSet,JoinHint.OPTIMIZER_CHOOSES);//....

public enum JoinHint {
   OPTIMIZER_CHOOSES ,         //Default Optimization
   BROADCAST_HASH_FIRST,    //if First dataset is small, it will be distributed all nodes fully.
   BROADCAST_HASH_SECOND,//if Second dataset is small, it will be distributed all nodes fully.
   REPARTITION_HASH_FIRST,  //build hash table from First Dataset.. Both dataset are big. choose the smaller dataset for hashing
   REPARTITION_HASH_SECOND,//build hash table from Second Dataset.. Both dataset are big. choose the smaller dataset for hashing
   REPARTITION_SORT_MERGE; //if bothsides are already sorted
}
```

# Reading data from Files for DataStream
```java
DataStream < String > text = env.socketTextStream("localhost", 9999); //nc -l 9999
DataStream < String > text = env.readTextFile(path);
DataStream < String > text = env.readFile(fileformat,path);
DataStream < String > text = env.readFile(fileFormat,path,watchType,interval,pathFilter);
        //fieFormat: Text, Csv, Avro, Parquet, SequenceFile, Json, Orc,KeyValue, Regex,Binary,  FileInputFormat<MyType>
        //watchType: FileProcessingMode.PROCESS_CONTINUOUSLY, check the path with Interval duration for any new file
        //watchType: FileProcessingMode.PROCESS_ONCE, file will be read and set a checkpoint. if run again, it will run from checkpoint
        //pathFilter: if we give Path parameter as Folder, we can excludes specific file type
        //pathFilter: Default FilePathFilter
        FilePathFilter pathFilter = FilePathFilter.createDefaultFilter(); //exclude Files that start with a dot
        //pathFilter: Custom FileFilter
        public class CustomExtensionFilePathFilter extends FilePathFilter {
            @Override
            public boolean filterPath(Path filePath) {
                return !filePath.getPath().endsWith(".txt");
                FileStatus fileStatus = filePath.getFileSystem().getFileStatus(filePath);
                return fileStatus.getLen() < minFileSize;
                FileStatus fileStatus = filePath.getFileSystem().getFileStatus(filePath);
                return filePath.getPath().startsWith(".") || fileStatus.getLen() < minFileSize;
            }
        }
addSource() // External DataSource Kafka, Flume, Twitter API
```

# Writing data 
```java
DataStream.writeAsText(path);
DataStream.writeAsCsv(path,line delimeter, field delimeter);
DataStream.print();
DataStream.writeUsingOutputFormat();
DataStream.writeToSocket();
addSink();
```