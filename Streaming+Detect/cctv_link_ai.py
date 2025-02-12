import cv2
import json
import base64
import time
import numpy as np
import paho.mqtt.client as mqtt
from ultralytics import YOLO
import oracledb

model = YOLO("11n_adamw_50(jin).pt")

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

# ================= Oracle DB 연결 =================
# 실제 사용자, 비밀번호, DSN 정보로 수정하세요.
connection = oracledb.connect(
    user="c##tf2",
    password="1234",
    dsn="localhost:1521/xe"  # 예: "localhost:1521/orclpdb1"
)
cursor = connection.cursor()

# ================= 전역 변수 및 초기화 =================
detect_url = None
detect_object = ""
cctv_name = ""
cap = None
saved_detections = set()  # 중복 저장 방지


# 객체 감지용 색상 생성 함수
def get_colors(num_colors):
    np.random.seed(0)
    return [tuple(np.random.randint(0, 255, 3).tolist()) for _ in range(num_colors)]


# 모델 관련 초기화 (클래스 이름을 직접 지정)
class_names = {0: 'car', 1: 'bus', 2: 'pickup_truck', 3: 'truck', 4: 'etc', 5: 'motor_cycle'}
num_classes = len(class_names)
colors = get_colors(num_classes)

# BOARD 테이블의 TAG_ID 매핑 (실제 TAG 테이블의 값에 맞게 수정)
tag_id_map = {
    "car": 1,
    "bus": 2,
    "pickup_truck": 3,
    "truck": 4,
    "etc": 5,
    "motor_cycle": 6
}

def on_message(client, userdata, msg):
    global detect_url, detect_object, cap, cctv_name
    try:
        data = json.loads(msg.payload.decode())
        cctv_url = data.get("cctv_url", "No URL")
        new_detect_object = data.get("detect_objects", None)
        cctv_name = data.get("cctv_name", None)

        if new_detect_object is not None:
            detect_object = new_detect_object

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

            # DB에 저장: 중복 저장 방지 및 신뢰도 조건 (예: 0.6 이상)
            detection_key = (label, x1, y1, x2, y2)
            if detection_key not in saved_detections and confidence > 0.8:
                saved_detections.add(detection_key)
                # 객체 영역 잘라내기
                cropped = image[y1:y2, x1:x2]
                ret, buffer = cv2.imencode('.jpg', cropped)
                if not ret:
                    print("JPEG 인코딩 실패")
                    continue
                blob_data = buffer.tobytes()
                detection_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())

                # 새 ID 값을 시퀀스를 통해 가져오기
                try:
                    cursor.execute("SELECT id_seq.NEXTVAL FROM dual")
                    new_id = cursor.fetchone()[0]
                except Exception as e:
                    print(f"시퀀스 조회 실패: {e}")
                    new_id = int(time.time() * 1000)

                tag_id = tag_id_map.get(label, 0)
                sql = """
                                INSERT INTO BOARD (ID, START_TIME, TITLE, TAG_ID, IMG_FILE)
                                VALUES (:id, TO_TIMESTAMP(:start_time, 'YYYY-MM-DD HH24:MI:SS'), :title, :tag_id, :IMG_FILE)
                            """
                try:
                    cursor.execute(sql, [new_id, detection_time, cctv_name, tag_id, blob_data])
                    connection.commit()
                    print(f"Inserted BOARD record with id: {new_id} for detection: {detection_key}")
                    print(f"받은 cctv_name: {cctv_name}")
                except Exception as e:
                    print(f"DB 삽입 실패: {e}")
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

cursor.close()
connection.close()