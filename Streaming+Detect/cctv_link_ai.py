import cv2
import json
import base64
import time
import numpy as np
import paho.mqtt.client as mqtt
from ultralytics import YOLO

model = YOLO("11n_adamw_scale.pt")

# MQTT 설정
client = mqtt.Client()
topic = '/cctv/objects'
client.connect('localhost', 1883, 60)

# MQTT 연결 콜백 함수
def on_connect(client, userdata, flags, rc):
    print(f"Connected with result code {rc}")
    client.subscribe(topic)

# 메시지 처리 함수
detect_url = None
detect_object = ""
cctv_name = ""

def on_message(client, userdata, msg):
    global detect_url, detect_object, cap, cctv_name
    try:
        data = json.loads(msg.payload.decode())
        cctv_url = data.get("cctv_url", "No URL")
        new_detect_object = data.get("detect_objects", None)
        new_cctv_name = data.get("cctv_name", None)
        detect_available = data.get("detect_available", None)

        if new_detect_object is not None:
            detect_object = new_detect_object

        if new_cctv_name is not None:
            cctv_name = new_cctv_name

        if cctv_url != "No URL" and cctv_url != detect_url:
            detect_url = cctv_url
            print(f"detect_url: {detect_url}")
            print(f"detect_object: {detect_object}")
            print(f"cctv_name: {cctv_name}")
            if cap is not None:
                cap.release()  # 기존 스트리밍 종료
            cap = cv2.VideoCapture(detect_url)  # 새 URL로 스트리밍 시작
            if not cap.isOpened():
                print(f"❌ VideoCapture를 열 수 없습니다: {detect_url}")
                return
            if detect_available == "exit":
                cap.release()
                cv2.destroyAllWindows()

    except Exception as e:
        print(f"Error processing message: {e}")

# 객체 감지용 색상 함수
def get_colors(num_colors):
    np.random.seed(0)
    colors = [tuple(np.random.randint(0, 255, 3).tolist()) for _ in range(num_colors)]
    return colors

# 모델 관련 초기화
class_names = {0: 'car', 1: 'bus', 2: 'pickup_truck', 3: 'truck', 4: 'etc', 5: 'motor_cycle'}
num_classes = len(class_names)
colors = get_colors(num_classes)

client.on_connect = on_connect
client.on_message = on_message
client.loop_start()

# ============================================== 전처리단계 종료 ==============================================

# 모델을 이용한 객체탐지 함수
def detect_objects(image: np.array, detect_object: str):
    results = model(image, verbose=False)

    for result in results:
        boxes = result.boxes.xyxy
        confidences = result.boxes.conf
        class_ids = result.boxes.cls
        for box, confidence, class_id in zip(boxes, confidences, class_ids):
            label = class_names[int(class_id)]
            print(f"detect_object: {detect_object}, label: {label}")  # detect_object와 label 값 확인
            x1, y1, x2, y2 = map(int, box)

            if detect_object != "total_object" and label != detect_object:
                continue

            if label == "car":
                color = colors[0]
            elif label == "bus":
                color = colors[1]
            elif label == "pickup_truck":
                color = colors[2]
            elif label == "truck":
                color = colors[3]
            elif label == "etc":
                color = colors[4]
            elif label == "motor_cycle":
                color = colors[5]
            else:
                color = colors[int(class_id)]

            cv2.rectangle(image, (x1, y1), (x2, y2), color, 2)
            cv2.putText(image, f'{label} {confidence:.2f}', (x1, y1),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.9, color, 2)
    return image

# 객체 탐지 반복용 루프
cap = None

while True:
    if detect_url is not None:
        if cap is None:
            cap = cv2.VideoCapture(detect_url)
            if not cap.isOpened():
                print(f"❌ VideoCapture를 열 수 없습니다: {detect_url}")
                time.sleep(2)  # 연결 실패 시 재시도

        if cap is not None and cap.isOpened():
            ret, frame = cap.read()
            if not ret:
                print("⚠️ 스트림이 끊겼습니다. 2초 후 다시 연결 시도...")
                cap.release()
                time.sleep(2)  # 스트림 끊기면 재시도
                continue

            result_image = detect_objects(frame, detect_object)

            _, buffer = cv2.imencode('.jpg', result_image)
            jpg_as_text = base64.b64encode(buffer).decode('utf-8')

            payload = json.dumps({'image': jpg_as_text})
            client.publish(topic, payload)

            # 영상 출력 중 q가 입력 시 종료
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break

cap.release()    # VideoCapture 종료
cv2.destroyAllWindows()    # 창 닫기
client.disconnect()    # MQTT 연결 해제