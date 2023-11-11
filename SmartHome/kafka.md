# KAFKA #
## QUEUE: 
mesaj bir kere publish edilir ve 1 kere consume edilir. Sonra queue uzerinden silinir. Daha cok RabbitMQ bunu yapiyor. 
Tum Comsumer'lari bir Comsumer Grup'un icine koyarsak her consumer bir tane Partition'dan mesajlari alir. 


## Pub/Sub: 
mesaj bir kere publish edilir, 1den fazla consume edilir. Sonra queue uzerinden silinir. 
Her consumer'i ayri bir Comsumer Groupa alirsak, ayni mesaj birden fazla comsumer tarafindan kullanilabilir.  

## Consumer Group: 
Partionlarin birden fazla consumer tarafindan paralel olarak consume edilmesini saglar. 
***  Normalde, 1 consumer 1 partition okumantan sorumludur. 
Queue gibi yapmak istiyorsak: Tum Comsumer'lari bir Comsumer Grup'un icine koyarsak her consumer bir tane Partition'dan mesajlari alir. 
Pub/Sub gibi yapmak istiyorsak: Her consumer'i ayri bir Comsumer Groupa aliriz, ayni mesaj birden fazla comsumer tarafindan kullanilabilir. 


## CLI
```shell
docker-compose -f .\zk-single-kafka-single.yml up -d
docker-compose -f .\zk-single-kafka-single.yml down
docker exec -it kafka1  bash

kafka-topics --bootstrap-server localhost:9092 --list #List the all topics 
kafka-topics --bootstrap-server localhost:9092 --create --topic first-topic #Create a topic
kafka-topics --bootstrap-server localhost:9092 --create --topic second-topic --partitions 5 #Create a topic with partition number
kafka-topics --bootstrap-server localhost:9092 --create --topic third-topic --replication-factor 2  #Create a topic with replication factor
kafka-topics --bootstrap-server localhost:9092 --describe --topic second-topic #get the details of a topic
kafka-topics --bootstrap-server localhost:9092 --delete --topic first_topic #Delete a topic

kafka-topics --command-config config_file --bootstrap-server xyz.rose.com:9092 --create --topic first-topic #Create a topic with a configfile for safe remote connection

kafka-console-producer --bootstrap-server localhost:9092  --topic first-topic
kafka-console-producer --bootstrap-server localhost:9092  --topic first-topic --producer-property acks=all
kafka-console-producer --bootstrap-server localhost:9092  --topic first-topic --property parse.key=true --property key.separator=:

kafka-console-consumer --bootstrap-server localhost:9092 --topic first-topic
kafka-console-consumer --bootstrap-server localhost:9092 --topic first-topic --from-beginning
kafka-console-consumer --bootstrap-server localhost:9092 --topic second-topic --from-beginning  --property print.timestamp=true --property print.key=true  --property print.value=true --property print.partition=true
kafka-console-consumer --bootstrap-server localhost:9092 --topic first-topic --from-beginning --group consumer-groupname

kafka-consumer-groups  --bootstrap-server localhost:9092 --list
kafka-consumer-groups  --bootstrap-server localhost:9092 --describe --group  group1 #we can see all consumers, partitions and offset values.
kafka-consumer-groups --bootstrap-server localhost:9092 --describe --all-groups
kafka-consumer-groups  --bootstrap-server localhost:9092 --group group1 --reset-offsets --to-earliest --topic second-topic --dry-run
kafka-consumer-groups  --bootstrap-server localhost:9092 --group group1 --reset-offsets --to-earliest --topic second-topic --execute

```