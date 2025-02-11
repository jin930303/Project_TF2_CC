import os
import json

# 입력 폴더 및 이미지 폴더 경로 설정
input_root_folder = "C:/Users/shk0349/Documents/차종분류/Labeling/json_original"
image_folder = "C:/Users/shk0349/Documents/차종분류/Image"
output_folder = "C:/Users/shk0349/Documents/차종분류/Labeling/json_transform"

# 출력 폴더 생성
os.makedirs(output_folder, exist_ok=True)

# 카테고리 ID 매핑을 위한 딕셔너리 (중복 방지)
category_id_map = {}
category_id_counter = 0  # ID 시퀀스

# 이미지 폴더 내 파일 목록 가져오기 (확장자 제거 후 소문자로 변환하여 검색)
image_files = {os.path.splitext(f)[0].lower(): f for f in os.listdir(image_folder)}

# 모든 JSON 파일을 읽어 변환
for root, _, files in os.walk(input_root_folder):
    for filename in files:
        if filename.endswith(".json"):
            input_path = os.path.join(root, filename)

            # JSON 파일 읽기
            with open(input_path, "r", encoding="utf-8") as file:
                data = json.load(file)

            # JSON에서 최상위 imagePath 값 가져오기
            if "imagePath" not in data:
                print(f"❌ 'imagePath' 없음: {filename}, 변환 스킵")
                continue

            image_path = data["imagePath"]
            image_filename = os.path.basename(image_path)  # 파일명만 추출
            image_name, _ = os.path.splitext(image_filename)  # 확장자 제거
            image_name_lower = image_name.lower()  # 소문자로 변환

            # 이미지 존재 여부 확인
            if image_name_lower not in image_files:
                print(f"🚫 이미지 없음: {image_name}, 변환 스킵")
                continue

            # 필요한 데이터 추출
            brand = data["car"]["attributes"]["brand"]
            model = data["car"]["attributes"]["model"]
            bbox = data["car"]["bbox"]

            # 모델명에서 접두어 정리
            prefixes = ["SUV_", "세단_", "승합_", "컨버터블_", "쿠페_", "해치백_", "화물_"]
            for prefix in prefixes:
                if model.startswith(prefix):
                    model = model.replace(prefix, "")
                    if "컨버터블_" in prefix:
                        model += "_컨버터블"
                    elif "쿠페_" in prefix:
                        model += "_쿠페"
                    break

            category_name = f"{brand}_{model}"  # 예: BMW_X1

            # 새로운 카테고리 ID 부여 (중복 방지)
            if category_name not in category_id_map:
                category_id_map[category_name] = category_id_counter
                category_id_counter += 1

            category_id = category_id_map[category_name]

            # 변환 데이터 생성
            converted_data = {
                "annotations": [
                    {
                        "category_id": category_id,
                        "bbox": bbox
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
            output_path = os.path.join(output_folder, f"{image_name}.json")
            with open(output_path, "w", encoding="utf-8") as out_file:
                json.dump(converted_data, out_file, indent=4, ensure_ascii=False)
            print(f"✅ {image_name}.json 변환 완료 → {output_path}")

print("🎉 모든 JSON 파일 변환 완료!")
