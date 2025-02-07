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

# 객체 감지용 색상 함수
def get_colors(num_colors):
    np.random.seed(0)
    colors = [tuple(np.random.randint(0, 255, 3).tolist()) for _ in range(num_colors)]
    return colors

class_names = model.names    # 모델에서 받은 클래스 이름
num_classes = len(class_names)    # 클래스 번호
colors = get_colors(num_classes)    # 시각박스 컬러색

client.on_connect = on_connect    # 클라이언트 연결 정보
cap = cv2.VideoCapture('rtsp://admin:mbc312AI!!@192.168.0.4:554/mbcai_2')    # rtsp 정보(vms 참고)
    # 참고 URL : https://deep-learning-study.tistory.com/107
    # 세팅 예시
        # cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
        # cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
        # bRec = False
        # prevTime = 0

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