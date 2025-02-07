from fastapi import FastAPI, UploadFile, File ,Form
from pydantic import BaseModel
import io       # 파일의 입출력을 위한 모듈
import base64   # 데이터를 Base64로 인코딩 디코딩
from PIL import Image   # pillow 이미지 처리 라이브러리
import numpy as np      # 배열및 행열 연산을 위한 라이브러리
from ultralytics import YOLO    # yolo8 모델 사용 울트라리틱스
import cv2                      # 컴뷰터 비전 작업을 위한 라이브러리

app = FastAPI()
model = YOLO('')                # 가중치 파일 넣을 곳
                                # YOLO Adam 정해서 넣기

class DetectionResult(BaseModel):   # pandatic 을 사용하여 데이터 모델을 정의(응답데이터를 구조화)
    message : str                   # 클라이언트가 보낸 메세지
    image : str                     # Base64로 인코딩된 탐지결과 이미지

def detect_objects(image: Image):
    img = np.array(image)                           # 이미지를 numpy 배열로 변환
    results = model(img)                            # 객체 탐지
    class_names = model.names                       # 클래스이름 저장

    # 결과를 바운딩 박스, 클래스이름, 정확도로 이미지에 표시
    for result in results:
        boxes = result.boxes.xyxy                   # 바운딩 박스
        confidences = result.boxes.conf             # 신뢰도
        class_ids = result.boxes.cls                # 클래스 이름

        for box, confidences, class_ids in zip(boxes, confidences, class_ids):
            x1, y1, x2, y2 = map(int, box)          # 좌표를 정수로 변환
            label = class_names[int(class_ids)]     # 클래스 이름
            cv2.rectangle(img, (x1,y1), (x2, y2), (255,0,0),2)
            cv2.putText(img, f'{label} {confidences:.2f}',(x1,y1),cv2.FONT_HERSHEY_SIMPLEX, 0.9, (200,15,60), 2)

    result_image = Image.fromarray(img)             # 결과 이미지를 PIL로 변환
    return result_image

@app.get("/")
async def read_root():
    return {"message" : "Hello FastAPI"}

@app.post("/detect",response_model=DetectionResult)
async def detect_service(message : str = Form(...), file: UploadFile = File(...)):
    # 이미지를 읽어서 PIL이미지로 변환
    image = Image.open(io.BytesIO(await file.read()))
    # 알파채널이 있으면 알파채널 제거하고 RGB로 변환
    if image.mode =='RGBA':
        image = image.convert('RGB')
    elif image.mode !='RGB':
        image = image.convert('RGB')
    # 객체 탐지를 수행
    result_image = detect_objects(image)

    # 이미지결과를 base64로 인코딩
    bufferd = io.BytesIO()
    result_image.save(bufferd,format="JPEG")
    img_str = base64.b64encode(bufferd.getvalue()).decode("utf-8")

    return DetectionResult(message=message,image=img_str)

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host = "0.0.0.0", port = 8001)

