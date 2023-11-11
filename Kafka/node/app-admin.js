// console.log("app-producer")

const { Kafka } = require("kafkajs");

const kafka = new Kafka({
  clientId: "kafka_demo",
  brokers: ["localhost:9092"],
});

createTopic();

async function createTopic() {
  const admin = kafka.admin();
  try {
    console.log("Connect the Kafka");
    await admin.connect();
    console.log("Connect the Kafka succeed");
    // admin.listTopics()
    console.log("Create topics in kafka");
    await admin.createTopics({
      topics: [
        {
          topic: "Logs",
          numPartitions: 1,
        },
        {
          topic: "Logs2",
          numPartitions: 2,
        },
      ],
    });
    
    console.log("Create topics in kafka succeed");
    console.log("Disconnect from Kafka");
    await admin.disconnect();
    console.log("Disconnect from Kafka succeed");

  } catch (error) {
    console.log("Error:" + error);
  } finally {
    process.exit(0);
  }
}
