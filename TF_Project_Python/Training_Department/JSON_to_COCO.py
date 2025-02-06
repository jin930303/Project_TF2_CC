import os
import json

# 입력 및 출력 폴더 설정
input_root_folder = "C:/Users/shk0349/Documents/차종분류/train/Data_Labeling/JSON"
output_root_folder = "C:/Users/shk0349/Documents/차종분류/train/Data_Labeling/COCO"

# 출력 폴더가 없으면 생성
os.makedirs(output_root_folder, exist_ok=True)

# 카테고리 ID 매핑을 위한 딕셔너리 (중복 방지)
category_id_map = {}
category_id_counter = 0  # ID 시퀀스

# 모든 하위 폴더를 순회하면서 JSON 파일 찾기
for root, _, files in os.walk(input_root_folder):
    for filename in files:
        if filename.endswith(".json"):
            input_path = os.path.join(root, filename)

            # 출력 경로 생성 (기존 폴더 구조 유지)
            relative_path = os.path.relpath(input_path, input_root_folder)
            output_path = os.path.join(output_root_folder, relative_path)
            output_dir = os.path.dirname(output_path)

            # 출력 디렉터리가 없으면 생성
            os.makedirs(output_dir, exist_ok=True)

            # JSON 파일 읽기
            with open(input_path, "r", encoding="utf-8") as file:
                data = json.load(file)

            # 필요한 데이터 추출 (brand + model)
            brand = data["car"]["attributes"]["brand"]  # 예: 기아자동차
            model = data["car"]["attributes"]["model"]  # 예: SUV_니로_EV

            # "SUV_" 같은 접두어 제거 후 모델명 가져오기
            if "SUV_" in model:
                model_name = model.replace("SUV_", "")
            elif "세단_" in model:
                model_name = model.replace("세단_", "")
            elif "승합_" in model:
                model_name = model.replace("승합_", "")
            elif "컨버터블_" in model:
                model_name = model.replace("컨버터블_", "") + "_컨버터블"
            elif "쿠페_" in model:
                model_name = model.replace("쿠페_", "") + "_쿠페"
            elif "해치백_" in model:
                model_name = model.replace("해치백_", "")
            elif "화물_" in model:
                model_name = model.replace("화물_", "")
            else:
                model_name = model

            category_name = f"{brand}_{model_name}"  # 예: 기아자동차_니로_EV

            # 새로운 카테고리 ID 부여 (중복 방지)
            if category_name not in category_id_map:
                category_id_counter += 1
                category_id_map[category_name] = category_id_counter

            category_id = category_id_map[category_name]

            # COCO 형식 변환
            coco_format = {
                "images": [
                    {
                        "id": data["id"],
                        "file_name": data["car"]["imagePath"]
                    }
                ],
                "annotations": [
                    {
                        "image_id": data["id"],
                        "category_id": category_id,
                        "category_name": category_name
                    }
                ],
                "categories": [
                    {
                        "id": category_id,
                        "name": category_name
                    }
                ]
            }

            # 변환된 JSON 저장
            with open(output_path, "w", encoding="utf-8") as out_file:
                json.dump(coco_format, out_file, indent=4, ensure_ascii=False)

            print(f"{filename} 변환 완료 → {output_path}")

print("모든 JSON 파일 변환 완료!")
