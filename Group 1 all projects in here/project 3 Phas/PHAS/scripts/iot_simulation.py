import random
import time
import requests

# Simulate IoT health data
while True:
    data = {
        "user_id": 1,
        "metric_name": "heart_rate",
        "value": random.randint(60, 100)
    }
    response = requests.post('http://127.0.0.1:5000/upload_health_data', json=data)
    print(response.json())
    time.sleep(5)  # Send data every 5 seconds
