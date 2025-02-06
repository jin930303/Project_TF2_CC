import os
import json
import cv2
from glob import glob
from torch.utils.data import Dataset
import albumentations as A
from albumentations.pytorch import ToTensorV2

def get_transforms():
    return A.Compose([
        A.HorizontalFlip(p=0.5),
        A.HueSaturationValue(hue_shift_limit=10, sat_shift_limit=15, val_shift_limit=10, p=0.5),
        A.Affine(
            scale=(0.9, 1.1),  # 기존 scale_limit=0.1 → 90%~110% 크기 조절
            translate_percent=(0.0625, 0.0625),  # 기존 shift_limit=0.0625 → 최대 6.25% 이동
            rotate=(-15, 15),  # 기존 rotate_limit=15 → -15도 ~ 15도 회전
            p=0.5  # 50% 확률로 적용
        ),
        A.Resize(416, 416),
        ToTensorV2()
    ])

class JSONDataset(Dataset):
    """
    Dataset that loads individual JSON files (COCO 형식으로 작성됨; 각 파일에 1개의 이미지 및 annotation 포함)
    from a given directory (재귀적으로 탐색).

    Args:
        json_dir (str): JSON 파일들이 있는 폴더 (예: dataset/annotation/train/ 또는 .../val/)
        image_base_dir (str): 이미지가 저장된 기본 폴더 (예: dataset/images/)
        transform: 데이터 증강 transform (Albumentations)
    """
    def __init__(self, json_dir, image_base_dir, transform=None):
        self.json_files = glob(os.path.join(json_dir, "**/*.json"), recursive=True)
        self.image_base_dir = image_base_dir
        self.transform = transform if transform is not None else get_transforms()

    def __len__(self):
        return len(self.json_files)

    def __getitem__(self, idx):
        json_path = self.json_files[idx]
        with open(json_path, "r", encoding="utf-8") as f:
            data = json.load(f)
        # 가정: 각 JSON 파일은 COCO 형식으로, 하나의 이미지와 하나의 annotation을 포함
        image_info = data["images"][0]
        annotation = data["annotations"][0]
        file_name = image_info["file_name"]
        image_path = os.path.join(self.image_base_dir, file_name)
        image = cv2.imread(image_path)
        if image is None:
            raise FileNotFoundError(f"Image not found: {image_path}")
        image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        if self.transform:
            image = self.transform(image=image)["image"]
        label = annotation["category_id"]  # 또는 필요에 따라 category_name 사용
        return image, label