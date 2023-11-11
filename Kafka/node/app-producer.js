// console.log("app-producer")

const { Kafka } = require('kafkajs')

const topic_name = process.argv[2] || "Logs2";

const kafka = new Kafka({
  clientId: 'kafka_demo',
  brokers: ['localhost:9092'],
});

produce()

async function produce(){
    const producer = kafka.producer()
    try {
        console.log("Connect the Kafka");
        await producer.connect()
        console.log("Connect the Kafka succeed");
        
        console.log("Sending messages to kafka");
        for( i=0; i< 1000; i++){

            const msg_result = await producer.send({
                topic: topic_name,
                messages: [
                    { value: 'Hello: ' + i },
                ],
            });
            console.log(JSON.stringify(msg_result));
        }
        
        console.log("Disconnect from Kafka");
        await producer.disconnect();
        console.log("Disconnect from Kafka succeed");
        
    } catch (error) {
        console.log("Error:" + error);
        
    }finally{
        process.exit(0);

    }
}


