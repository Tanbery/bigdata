import json
from time import sleep

from kafka import KafkaConsumer

if __name__ == '__main__':
    print('Running Second Consumer to get Parsed Messages')
    parsed_topic_name = 'parsed_recipes'

    consumer = KafkaConsumer(parsed_topic_name, auto_offset_reset='earliest',
                             bootstrap_servers=['localhost:9092'], api_version=(0, 10), consumer_timeout_ms=1000000)
    for msg in consumer:
        print(msg.value)
    # consumer.close()
    # sleep(5)
