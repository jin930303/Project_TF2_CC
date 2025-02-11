import os
import json

# 입력 및 출력 폴더 설정
input_root_folder = "C:/Users/shk0349/Documents/차종분류/Labeling/json_trans"
output_root_folder = "C:/Users/shk0349/Documents/차종분류/Labeling/yolo"
image_width = 1280
image_height = 720

# 출력 폴더가 없으면 생성
os.makedirs(output_root_folder, exist_ok=True)

# 카테고리 ID 매핑을 위한 딕셔너리 (중복 방지)
category_id_map = {}
category_id_counter = 0  # ID 시퀀스

# 차종 정보를 기록할 파일
category_info_path = os.path.join(output_root_folder, "category_info.txt")

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

            # annotations 배열에서 첫 번째 항목을 가져오기
            annotation = data["annotations"][0]  # 첫 번째 항목에 접근

            # 필요한 데이터 추출
            brand = annotation["car"]["attributes"]["brand"]
            model = annotation["car"]["attributes"]["model"]
            bbox = annotation["car"]["bbox"]  # 자동차 바운딩 박스
            image_id = annotation["id"]

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
                category_id_counter += 1
                category_id_map[category_name] = category_id_counter

            category_id = category_id_map[category_name]

            # YOLO 형식 변환 (바운딩 박스 정규화)
            x_min, y_min = bbox[0]
            x_max, y_max = bbox[1]

            # YOLO 형식: [class_id, x_center, y_center, width, height]
            x_center = (x_min + x_max) / 2 / image_width
            y_center = (y_min + y_max) / 2 / image_height
            width = (x_max - x_min) / image_width
            height = (y_max - y_min) / image_height

            # YOLO 포맷의 annotation
            yolo_annotation = [category_id - 1, x_center, y_center, width, height]  # 클래스 ID는 0부터 시작

            # 출력 경로의 파일명을 기반으로 YOLO 파일 저장
            yolo_output_path = os.path.join(output_root_folder, f"{filename[:-5]}.txt")

            with open(yolo_output_path, "w", encoding="utf-8") as out_file:
                out_file.write(" ".join(map(str, yolo_annotation)) + "\n")

            # 차종 정보가 기록될 category_info.txt 파일에 차종 및 ID 기록
            with open(category_info_path, "a", encoding="utf-8") as info_file:
                info_file.write(f"Category Name: {category_name} (ID: {category_id})\n")

            print(f"{filename} YOLO 형식으로 변환 완료 → {yolo_output_path}")

print("모든 JSON 파일 YOLO 형식으로 변환 완료!")