from models.yolo_train import train_yolo

if __name__ == "__main__":
    # 예를 들어, YOLO10n + Adam 조합으로 학습 실행
    train_yolo("yolo11n", "adam")
    # 필요에 따라 다른 조합도 실행 가능