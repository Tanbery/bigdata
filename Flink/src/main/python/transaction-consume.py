import json
from time import sleep

from confluent_kafka import Consumer # type: ignore

def main():
    config = {
            # User-specific properties that you must set
            'bootstrap.servers': 'localhost:9092',
            'group.id':          'DataGen-Consumer',
            'auto.offset.reset': 'earliest'
        }

    topic_name = 'financial_transactions'
    print(f'Running Consumer to get messages from {topic_name}')
    consumer = Consumer(config)
    consumer.subscribe([topic_name])
    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
#                 print("Waiting...")
                pass
            elif msg.error():
                print(f"ERROR: {msg.error()}")
            else:
                # Extract the (optional) key and value, and print.
                print("Consumed event from topic {topic}: key = {key:12} value = {value:12}".format(
                    topic=msg.topic(), key=msg.key().decode('utf-8'), value=msg.value().decode('utf-8')))
    except KeyboardInterrupt:
        pass
    finally:
        consumer.close()
#
#
#     for msg in consumer:
#         print(msg.value)
# #     consumer.close()
# #     sleep(5)
if __name__ == '__main__':
    main()
