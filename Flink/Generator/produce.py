import json
import random
import time

from faker import Faker
from confluent_kafka import Producer
from datetime import datetime

fake = Faker()

def generate_sales_transactions():
    user = fake.simple_profile()
    
    return {
        "transactionId": fake.uuid4(),
        "productId": random.choice(['product1', 'product2', 'product3', 'product4', 'product5', 'product6']),
        "productName": random.choice(['laptop', 'mobile', 'tablet', 'watch', 'headphone', 'speaker']),
        'productCategory': random.choice(['electronic', 'fashion', 'grocery', 'home', 'beauty', 'sports']),
        'productPrice': round(random.uniform(10, 1000), 2),
        'productQuantity': random.randint(1, 10),
        'productBrand': random.choice(['apple', 'samsung', 'oneplus', 'mi', 'boat', 'sony']),
        'currency': random.choice(['USD', 'GBP']),
        'customerId': user['username'],
        'transactionDate': datetime.utcnow().strftime('%Y-%m-%dT%H:%M:%S.%f%z'),
        "paymentMethod": random.choice(['credit_card', 'debit_card', 'online_transfer'])
    }

def delivery_report(err, msg):
    if err is not None:
        print(f'Message delivery failed: {err}')
    else:
        print(f"Message delivery succeed: {msg.topic()} [{msg.partition()}] [{msg.offset()}] Key: [{msg.key()}] ")
        # print(f"Message delivery succeed: {msg.topic()} [{msg.partition()}] [{msg.offset()}] key: [{msg.key().decode('utf-8')}] value: [{msg.value().decode('utf-8')}]")
        
def main():
    config = {
            'bootstrap.servers': 'localhost:9092',
            'acks':              'all'
        }
    topic = 'financial_transactions'

    producer= Producer(config)

    curr_time = datetime.now()

    while (datetime.now() - curr_time).seconds < 50:
        try:
            transaction = generate_sales_transactions()
            transaction['totalAmount'] = transaction['productPrice'] * transaction['productQuantity']
            # print(transaction)
            producer.produce(topic,
                             key=transaction['transactionId'],
                             value=json.dumps(transaction),
                             on_delivery=delivery_report
                             )
            producer.poll(0)
            time.sleep(1)
        except BufferError:
            print("Buffer full! Waiting...")
            time.sleep(5)
        except Exception as e:
            print(e)

if __name__ == "__main__":
    main()
