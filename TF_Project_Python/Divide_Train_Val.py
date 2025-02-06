import os
import random
import shutil
from glob import glob

# 원본 데이터 경로 (하위 폴더 없이 단일 폴더에 JSON과 이미지 존재)
base_annotation_dir = "dataset/annotations/Label/"  # JSON 파일 폴더
base_image_dir = "dataset/images/Data_Image/"  # 이미지 파일 폴더
output_train_annotation = "dataset/annotations/Label/train/"
output_val_annotation = "dataset/annotations/Label/val/"
output_train_image = "dataset/images/Data_Image/train/"
output_val_image = "dataset/images/Data_Image/val/"

# train, val 폴더 생성
os.makedirs(output_train_annotation, exist_ok=True)
os.makedirs(output_val_annotation, exist_ok=True)
os.makedirs(output_train_image, exist_ok=True)
os.makedirs(output_val_image, exist_ok=True)

# JSON 파일 목록 가져오기
json_files = glob(os.path.join(base_annotation_dir, "*.json"))

# 랜덤 셔플 후 Train/Val 나누기
random.shuffle(json_files)
train_ratio = 0.8  # 80% Train, 20% Val
split_idx = int(len(json_files) * train_ratio)

train_files = json_files[:split_idx]
val_files = json_files[split_idx:]

# 파일 이동 (train)
for json_file in train_files:
    filename = os.path.basename(json_file)  # ex) "car_001.json"
    image_file = os.path.join(base_image_dir, filename.replace(".json", ".jpg"))

    shutil.move(json_file, os.path.join(output_train_annotation, filename))  # JSON 이동
    if os.path.exists(image_file):  # 이미지가 존재하면 함께 이동
        shutil.move(image_file, os.path.join(output_train_image, os.path.basename(image_file)))

# 파일 이동 (val)
for json_file in val_files:
    filename = os.path.basename(json_file)
    image_file = os.path.join(base_image_dir, filename.replace(".json", ".jpg"))

    shutil.move(json_file, os.path.join(output_val_annotation, filename))  # JSON 이동
    if os.path.exists(image_file):  # 이미지가 존재하면 함께 이동
        shutil.move(image_file, os.path.join(output_val_image, os.path.basename(image_file)))

print(f"📂 Train: {len(train_files)}개, Val: {len(val_files)}개")
print("✅ Train/Val 분할 완료!")
