import os
import random
import shutil
from glob import glob

# 원본 이미지 파일과 txt 파일이 저장된 경로
base_img_dir = "C:/Users/shk0349/Documents/차종분류/Image/original"
base_txt_dir = "C:/Users/shk0349/Documents/차종분류/Labeling/yolo"
output_train_img = "C:/Users/shk0349/Documents/차종분류/Final/image/train"
output_val_img = "C:/Users/shk0349/Documents/차종분류/Final/image/val"
output_train_txt = "C:/Users/shk0349/Documents/차종분류/Final/json/train"
output_val_txt = "C:/Users/shk0349/Documents/차종분류/Final/json/val"

# train, val 폴더 생성
os.makedirs(output_train_img, exist_ok=True)
os.makedirs(output_val_img, exist_ok=True)
os.makedirs(output_train_txt, exist_ok=True)
os.makedirs(output_val_txt, exist_ok=True)

# 이미지 파일과 txt 파일 리스트 가져오기
image_files = glob(os.path.join(base_img_dir, "*.jpg"))  # 이미지 파일 가져오기
txt_files = glob(os.path.join(base_txt_dir, "*.txt"))  # txt 파일 가져오기

# 이미지 파일과 txt 파일이 일대일 대응한다고 가정하고, 파일 이름으로 매칭
paired_files = []
for img_file in image_files:
    img_name = os.path.basename(img_file)
    txt_file = os.path.join(base_txt_dir, img_name.replace(".jpg", ".txt"))
    if os.path.exists(txt_file):
        paired_files.append((img_file, txt_file))

# train/val 비율 설정
train_ratio = 0.8  # Train 80%, Val 20%

random.shuffle(paired_files)  # 랜덤 셔플
split_idx = int(len(paired_files) * train_ratio)  # Train/Val 비율 계산

train_files = paired_files[:split_idx]
val_files = paired_files[split_idx:]

# 이미지 및 txt 파일 이동 (train)
for img_path, txt_path in train_files:
    shutil.move(img_path, os.path.join(output_train_img, os.path.basename(img_path)))
    shutil.move(txt_path, os.path.join(output_train_txt, os.path.basename(txt_path)))

# 이미지 및 txt 파일 이동 (val)
for img_path, txt_path in val_files:
    shutil.move(img_path, os.path.join(output_val_img, os.path.basename(img_path)))
    shutil.move(txt_path, os.path.join(output_val_txt, os.path.basename(txt_path)))

print("✅ Train/Val 분할 완료!")
