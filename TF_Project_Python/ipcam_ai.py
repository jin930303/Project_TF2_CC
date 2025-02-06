import base64
import io
from PIL import Image
import numpy as np
import json
from ultralytics import YOLO
import paho.mqtt.client as mqtt # 브로커 추가 (mosquitto가 실행중이여야 함)
import cv2
import time

model = YOLO('')       # 모델 넣을 곳
client = mqtt.Client() # mosquitto -c mosquitto.conf
topic = '/camera/objects' # 경로
client.connect('localhost',1883,60)

def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code{rc}")

# 객체 감지용 사각박스 함수
def get_colors(num_colors):
    np.random.seed(0)
    colors = [tuple(np.random.randint(0, 255, 3).tolist()) for _ in range(num_colors)]
    return colors

class_names = model.names           # 모델에서 받은 클래스 이름
num_classes = len(class_names)      # 클래스 번호
colors = get_colors(num_classes)    # 사각박스 컬러색

client.on_connet = on_connect # 클라이언트 연결정보
cap = cv2.VideoCapture("rtsp://ID 수정: PW 수정@192.168.0.4:554/체널명")  # rtsp 정보(vms 참고)
cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 640)
bRec = False
# prevTime

# 모델이 객체를 탐지용 함수
def detect_objects(image: np.array):
    results = model(image, verbose=False)
    class_names = model.names

    for result in results:
        boxes = result.boxes.xyxy  # 바운딩 박스
        confidences = result.boxes.conf  # 신뢰도
        class_ids = result.boxes.cls  # 클래스 이름

        for box, confidences, class_ids in zip(boxes, confidences, class_ids):
            x1, y1, x2, y2 = map(int, box)  # 좌표를 정수로 변환
            label = class_names[int(class_ids)]  # 클래스 이름
            cv2.rectangle(image, (x1, y1), (x2, y2), (255, 0, 0), 2)
            cv2.putText(image, f'{label} {confidences:.2f}', (x1, y1), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (200, 15, 60), 2)

        return image

# 객체 탐지 반복문용

while cap.isOpened():
    ret, frame = cap.read()
    if not ret:
        break
    result_image = detect_objects(frame)

    _, buffer = cv2.imencode('.jpg',result_image)
    jpg_as_text = base64.b64encode(buffer).decode('utf-8')

    payload = json.dumps({'image': jpg_as_text})
    client.publish(topic,payload)
    cv2.imshow('Frame', result_image)

    if cv2.waitKey(1) & 0xFF == ord('q'): # 영상 출력에 q가 입력되면 종료
        break

cap.release()       # VideoCapture
cv2.destroyAllWindows() # 창 닫기
client.disconnect()  # 연결 해제





