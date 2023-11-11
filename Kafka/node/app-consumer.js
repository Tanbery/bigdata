// console.log("app-consumer")
const { Kafka } = require("kafkajs");

const topic_name = process.argv[2] || "Logs2";
const kafka = new Kafka({
  clientId: "kafka_demo",
  brokers: ["localhost:9092"],
  // compressionType:"snappy",
});

consume();

async function consume() {
  const consumer = kafka.consumer({ groupId: "kafka_demo_group" });
  try {
    console.log("Connect the Kafka");
    await consumer.connect();
    console.log("Connect the Kafka succeed");
    await consumer.subscribe({ topic: topic_name, fromBeginning: false });

    console.log("Waiting message from kafka");
    await consumer.run({
      eachMessage: async ({ topic, partition, message }) => {
        console.log({
          // topic: topic.toString(),
          value: message.value.toString(),
          parti: partition.toString(),
        });
      },
    });
    // console.log("Disconnect from Kafka");
    // await consumer.disconnect();
    // console.log("Disconnect from Kafka succeed");
  } catch (error) {
    console.log("Error:" + error);
  }
}
