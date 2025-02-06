import torch
import torch.optim as optim
from torch.optim.lr_scheduler import ReduceLROnPlateau
from torch.utils.data import DataLoader
from models.dataset_loader import JSONDataset, get_transforms
from ultralytics import YOLO  # ultralytics YOLO 사용

# 하이퍼파라미터 설정
BATCH_SIZE = 4            # 배치 크기
ACCUMULATION_STEPS = 4     # 그래디언트 누적 (가상 배치 크기 증가)
LR = 0.001
EPOCHS = 50
PATIENCE = 5              # LR 스케줄러 patience 설정

# JSON 및 이미지 경로 (train/val 분리됨)
TRAIN_JSON_DIR = "dataset/annotations/Label/train"
VAL_JSON_DIR = "dataset/annotations/Label/val/"
TRAIN_IMAGE_DIR = "dataset/images/Data_image/train/"
VAL_IMAGE_DIR = "dataset/images/Data_image/val/"

# 데이터셋 및 DataLoader 생성 (이미지 폴더 변경)
train_dataset = JSONDataset(json_dir=TRAIN_JSON_DIR, image_base_dir=TRAIN_IMAGE_DIR, transform=get_transforms())
val_dataset = JSONDataset(json_dir=VAL_JSON_DIR, image_base_dir=VAL_IMAGE_DIR, transform=get_transforms())
train_loader = DataLoader(train_dataset, batch_size=BATCH_SIZE, shuffle=True, num_workers=4)
val_loader = DataLoader(val_dataset, batch_size=BATCH_SIZE, shuffle=False, num_workers=4)

# 옵티마이저 선택 함수
def get_optimizer(model, optimizer_choice):
    if optimizer_choice == "adam":
        return optim.Adam(model.parameters(), lr=LR, betas=(0.9, 0.999), eps=1e-8)
    elif optimizer_choice == "adamw":
        return optim.AdamW(model.parameters(), lr=LR, betas=(0.9, 0.999), eps=1e-8, weight_decay=0.0005)
    elif optimizer_choice == "sgd":
        return optim.SGD(model.parameters(), lr=0.01, momentum=0.9, weight_decay=0.0005)
    else:
        raise ValueError("Invalid optimizer_choice. Choose from 'adam', 'adamw', or 'sgd'.")

# 모델 선택 함수
def get_model(model_choice):
    if model_choice == "yolo10n":
        return YOLO("yolov10n.pt")  # YOLO 모델 로드
    elif model_choice == "yolo11n":
        return YOLO("yolov11n.pt")
    else:
        raise ValueError("Invalid model_choice. Choose from 'yolo10n' or 'yolo11n'.")

def train_yolo(model_choice, optimizer_choice):
    model = get_model(model_choice)
    model.train()
    optimizer = get_optimizer(model, optimizer_choice)
    scheduler = ReduceLROnPlateau(optimizer, mode='min', factor=0.5, patience=PATIENCE, min_lr=1e-6, verbose=True)

    for epoch in range(EPOCHS):
        model.train()
        running_loss = 0.0
        optimizer.zero_grad()

        # Training loop (Gradient Accumulation)
        for i, (images, labels) in enumerate(train_loader):
            images = images.to("cuda" if torch.cuda.is_available() else "cpu")
            labels = labels.to("cuda" if torch.cuda.is_available() else "cpu")

            results = model(images, augment=True)  # YOLO forward pass
            loss = results.loss if hasattr(results, "loss") else torch.tensor(0.0)
            loss = loss / ACCUMULATION_STEPS
            loss.backward()
            running_loss += loss.item()

            if (i + 1) % ACCUMULATION_STEPS == 0:
                optimizer.step()
                optimizer.zero_grad()

        avg_train_loss = running_loss / len(train_loader)

        # Validation loop
        model.eval()
        val_loss = 0.0
        with torch.no_grad():
            for images, labels in val_loader:
                images = images.to("cuda" if torch.cuda.is_available() else "cpu")
                labels = labels.to("cuda" if torch.cuda.is_available() else "cpu")
                results = model(images)
                loss = results.loss if hasattr(results, "loss") else torch.tensor(0.0)
                val_loss += loss.item()

        avg_val_loss = val_loss / len(val_loader)
        scheduler.step(avg_val_loss)

        print(f"[{model_choice} + {optimizer_choice}] Epoch {epoch+1}/{EPOCHS}, Train Loss: {avg_train_loss:.4f}, Val Loss: {avg_val_loss:.4f}, LR: {optimizer.param_groups[0]['lr']:.6f}")

    model.save(f"trained_{model_choice}_{optimizer_choice}.pt")

# 모델 학습 실행
if __name__ == "__main__":
    combinations = [
        ("yolo10n", "adam"),
        ("yolo10n", "adamw"),
        ("yolo10n", "sgd"),
        ("yolo11n", "adam"),
        ("yolo11n", "adamw"),
        ("yolo11n", "sgd"),
    ]
    for model_choice, optimizer_choice in combinations:
        print(f"Training {model_choice} with {optimizer_choice}...")
        train_yolo(model_choice, optimizer_choice)
