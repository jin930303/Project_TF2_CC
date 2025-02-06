import json
import os
from ultralytics import YOLO

import json
import os
from ultralytics import YOLO

# 1. JSON 파일이 있는 폴더 경로
json_dir = "C:/Users/shk0349/Documents/차종분류/Final/json/train"
images_dir = "C:/Users/shk0349/Documents/차종분류/Final/image/train"  # 이미지 폴더 경로
temp_labels_dir = "C:/Users/shk0349/Documents/차종분류/Final/json/etc"  # YOLO 형식 라벨 임시 저장 폴더
train_labels_dir = "C:/Users/shk0349/Documents/차종분류/Final/json/train"  # 훈련용 라벨 폴더
val_labels_dir = "C:/Users/shk0349/Documents/차종분류/Final/json/val"  # 검증용 라벨 폴더

os.makedirs(train_labels_dir, exist_ok=True)
os.makedirs(val_labels_dir, exist_ok=True)

# 2. categories 변수 정의
categories = {}

# 3. 폴더 내 모든 JSON 파일 읽기
for file_name in os.listdir(json_dir):
    if file_name.endswith('.json'):  # JSON 파일만 처리
        json_path = os.path.join(json_dir, file_name)

        with open(json_path, 'r') as f:
            data = json.load(f)

        # 4. COCO 형식인지 확인
        if "images" in data and "annotations" in data:
            print(f"{file_name} - COCO 형식 데이터입니다.")
            categories = {cat["id"]: cat["name"] for cat in data["categories"]}

            # 각 이미지에 대한 YOLO 형식 라벨 생성
            for img_info in data["images"]:
                img_id = img_info["id"]
                img_width = img_info["width"]
                img_height = img_info["height"]
                img_name = img_info["file_name"]

                annotations = [ann for ann in data["annotations"] if ann["image_id"] == img_id]
                label_file_path = os.path.splitext(img_name)[0] + ".txt"

                # 훈련 데이터와 검증 데이터 라벨 분리 (이미지 폴더에 맞춰)
                if img_name in os.listdir(train_labels_dir):  # 훈련 데이터 폴더에 존재하는 이미지
                    label_file_path = os.path.join(train_labels_dir, label_file_path)
                elif img_name in os.listdir(val_labels_dir):  # 검증 데이터 폴더에 존재하는 이미지
                    label_file_path = os.path.join(val_labels_dir, label_file_path)
                else:
                    continue  # 이미지가 train과 val 폴더에 모두 없다면 스킵

                # YOLO 형식 변환 및 저장
                with open(label_file_path, 'w') as label_file:
                    for ann in annotations:
                        category_id = ann["category_id"] - 1  # YOLO는 0부터 시작
                        bbox = ann["bbox"]
                        x_center = (bbox[0] + bbox[2] / 2) / img_width
                        y_center = (bbox[1] + bbox[3] / 2) / img_height
                        width = bbox[2] / img_width
                        height = bbox[3] / img_height

                        label_file.write(f"{category_id} {x_center} {y_center} {width} {height}\n")

        else:
            print(f"{file_name} - COCO 형식이 아닙니다.")

if __name__ == '__main__':
    model = YOLO("yolov8n.pt")  # YOLO 모델 로드

    model.train(
        data="data.yaml",  # 수정된 data.yaml 파일 경로
        epochs=10,  # 훈련 에포크
        imgsz=416,  # 작은 이미지 크기
        batch=2,    # 배치 크기 줄이기
        name="Car_Training ver.",
        half=True    # 혼합 정밀도 훈련
    )

