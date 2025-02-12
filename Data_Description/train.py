import os
import json
import cv2
import torch
import glob
import random
import numpy as np
from torch.utils.data import Dataset, DataLoader
from ultralytics import YOLO

# GPU 설정 (GTX 1060 3GB 환경)
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

# 원본 이미지 크기 및 리사이징 크기 설정
ORIGINAL_SIZE = (1280, 720)
RESIZED_SIZE = (640, 360)  # VRAM 고려하여 축소

# 데이터 경로 설정
LABEL_DIR = "C:/Users/918/Documents/새 폴더/Label_Data"
IMAGE_DIR = "C:/Users/918/Documents/새 폴더/Image_Data/train"

# 옵티마이저 선택 (SGD 또는 Adam)
OPTIMIZER_TYPE = "adam"  # 또는 "sgd"
LEARNING_RATE = 0.001  # 기본 학습률

class VehicleDataset(Dataset):
    def __init__(self, label_dir, image_dir, img_size=RESIZED_SIZE):
        self.label_dir = label_dir
        self.image_dir = image_dir
        self.img_size = img_size
        self.data = []
        self._load_data()

    def _load_data(self):
        """JSON 파일을 읽고 유효한 데이터만 필터링"""
        json_files = glob.glob(os.path.join(self.label_dir, "**", "*.json"), recursive=True)

        for json_path in json_files:
            with open(json_path, "r", encoding="utf-8") as f:
                try:
                    data = json.load(f)
                except json.JSONDecodeError:
                    print(f"Error decoding JSON: {json_path}")
                    continue

            for img_info in data.get("images", []):
                image_path = self._find_image(img_info["file_name"])
                if image_path:
                    annotations = [
                        anno for anno in data.get("annotations", [])
                        if anno["image_id"] == img_info["id"]
                    ]
                    self.data.append((image_path, annotations))

    def _find_image(self, file_name):
        """이미지 파일 존재 여부 확인"""
        img_path = glob.glob(os.path.join(self.image_dir, "**", file_name), recursive=True)
        return img_path[0] if img_path else None

    def __len__(self):
        return len(self.data)

    def __getitem__(self, idx):
        image_path, annotations = self.data[idx]
        image = cv2.imread(image_path)
        if image is None:
            raise FileNotFoundError(f"Image not found: {image_path}")

        image = cv2.resize(image, self.img_size)

        bboxes = []
        for anno in annotations:
            bbox = anno["bbox"]
            x1, y1 = bbox[0]
            x2, y2 = bbox[1]

            # 좌표를 리사이징 크기에 맞게 조정
            x1 = int(x1 * (self.img_size[0] / ORIGINAL_SIZE[0]))
            y1 = int(y1 * (self.img_size[1] / ORIGINAL_SIZE[1]))
            x2 = int(x2 * (self.img_size[0] / ORIGINAL_SIZE[0]))
            y2 = int(y2 * (self.img_size[1] / ORIGINAL_SIZE[1]))

            bboxes.append([x1, y1, x2, y2, 1])  # 클래스 ID 추가 (여기서는 1로 가정)

        return torch.tensor(image, dtype=torch.float32).permute(2, 0, 1) / 255.0, torch.tensor(bboxes, dtype=torch.float32)


def train(model_name="yolov10n", optimizer_type=OPTIMIZER_TYPE, num_epochs=10, batch_size=4):
    """YOLO 모델 훈련"""
    dataset = VehicleDataset(LABEL_DIR, IMAGE_DIR)
    dataloader = DataLoader(dataset, batch_size=batch_size, shuffle=True, num_workers=2)

    model = YOLO(model_name).to(DEVICE)

    if optimizer_type == "sgd":
        optimizer = torch.optim.SGD(model.parameters(), lr=LEARNING_RATE, momentum=0.9)
    else:
        optimizer = torch.optim.Adam(model.parameters(), lr=LEARNING_RATE)

    for epoch in range(num_epochs):
        model.train()
        epoch_loss = 0
        for images, targets in dataloader:
            images = images.to(DEVICE)
            targets = [t.to(DEVICE) for t in targets]

            optimizer.zero_grad()
            loss = model(images, targets)
            loss.backward()
            optimizer.step()
            epoch_loss += loss.item()

        print(f"Epoch [{epoch+1}/{num_epochs}], Loss: {epoch_loss:.4f}")

    model.save("trained_yolo.pt")


if __name__ == "__main__":
    train(model_name="yolov10n", optimizer_type="adam", num_epochs=5, batch_size=2)  # VRAM 고려하여 배치 크기 2로 설정
