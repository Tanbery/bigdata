import json
from time import sleep

# from bs4 import BeautifulSoup
from kafka import KafkaProducer


def publish_message(producer_instance, topic_name, key, value):
    try:
        key_bytes = bytes(key, encoding='utf-8')
        value_bytes = bytes(value, encoding='utf-8')
        producer_instance.send(topic_name, key=key_bytes, value=value_bytes)
        producer_instance.flush()
        print('Message published successfully.')
    except Exception as ex:
        print('Exception in publishing message')
        print(str(ex))


def connect_kafka_producer():
    _producer = None
    try:
        _producer = KafkaProducer(bootstrap_servers=['localhost:9092'], api_version=(0, 10))
    except Exception as ex:
        print('Exception while connecting Kafka')
        print(str(ex))
    finally:
        return _producer
    

def get_inc_value():
    num = 0
    while True:
        yield num
        num+=1

if __name__ == '__main__':
    topic_name = 'raw_recipes'
    print('Running Raw Data Producer..')
    producer = connect_kafka_producer()
    print('Sending Raw Message by First Producer..')
    for num in get_inc_value():
        publish_message(producer, topic_name, 'Raw', "First Kafka Message " + str(num))