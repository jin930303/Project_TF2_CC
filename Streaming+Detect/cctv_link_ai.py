import base64
import io
from PIL import Image
import numpy as np
import json
from ultralytics import YOLO
import paho.mqtt.client as mqtt    # 브로커 추가 / mosquitto 지속실행 필요
import cv2
import time

model = YOLO("9t_adamw(jin).pt")
client = mqtt.Client()    # mosquitto -c mosquitto.conf
topic = '/cctv/objects'    # 경로
client.connect('localhost', 1883, 60)

# 연결용 함수
def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}")
    client.subscribe(topic)

detect_url = None

# 객체 감지용 색상 함수
def get_colors(num_colors):
    np.random.seed(0)
    colors = [tuple(np.random.randint(0, 255, 3).tolist()) for _ in range(num_colors)]
    return colors

# 메시지 처리 함수에서 cctv_url을 확인하고 VideoCapture에 전달
def on_message(client, userdata, msg):
    global detect_url
    try:
        data = json.loads(msg.payload.decode())
        cctv_url = data.get("cctv_url", "No URL")

        if cctv_url != "No URL":
            detect_url = cctv_url
            print(f"Updated detect_url: {detect_url}")
        else:
            print("No cctv_url found in the message.")
    except Exception as e:
        print(f"Error processing message: {e}")


class_names = model.names    # 모델에서 받은 클래스 이름
num_classes = len(class_names)    # 클래스 번호
colors = get_colors(num_classes)    # 시각박스 컬러색

client.on_connect = on_connect    # 클라이언트 연결 정보
client.on_message = on_message
client.loop_start()  # MQTT 메시지 루프 실행

# detect_url이 설정될 때까지 대기
while detect_url is None:
    time.sleep(1)

cap = cv2.VideoCapture(detect_url)

# ============================================== 전처리단계 종료 ==============================================

# 모델을 이용한 객체탐지 함수
def detect_objects(image: np.array):
    results = model(image, verbose = False)
    class_names = model.names

    for result in results:
        boxes = result.boxes.xyxy
        confidences = result.boxes.conf
        class_ids = result.boxes.cls
        for box, confidence, class_id in zip(boxes, confidences, class_ids):
            x1, y1, x2, y2 = map(int, box)
            label = class_names[int(class_id)]
            cv2.rectangle(image, (x1, y1), (x2, y2), colors[int(class_id)], 2)
            cv2.putText(image, f'{label} {confidence:.2f}', (x1, y1),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.9, colors[int(class_id)], 2)
    return image

# 객체 탐지 반복용 루프
while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break
    result_image = detect_objects(frame)

    _, buffer = cv2.imencode('.jpg', result_image)
    jpg_as_text = base64.b64encode(buffer).decode('utf-8')

    payload = json.dumps({'image':jpg_as_text})
    client.publish(topic, payload)
    # cv2.imshow('Frame', result_image)

    # 영상 출력 중 q가 입력 시 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()    # VideoCapture
cv2.destroyAllWindows()    # 창 닫기
client.disconnect()    # 연결 해제