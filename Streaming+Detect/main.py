from fastapi import FastAPI, UploadFile, File ,Form
from pydantic import BaseModel
import io       # 파일의 입출력을 위한 모듈
import base64   # 데이터를 Base64로 인코딩 디코딩
from PIL import Image   # pillow 이미지 처리 라이브러리
import numpy as np      # 배열및 행열 연산을 위한 라이브러리
from ultralytics import YOLO    # yolo8 모델 사용 울트라리틱스
import cv2                      # 컴뷰터 비전 작업을 위한 라이브러리

app = FastAPI()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host = "0.0.0.0", port = 8001)