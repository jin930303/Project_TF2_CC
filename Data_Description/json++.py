import os
import json

# 입력 폴더 및 출력 폴더 설정
input_root_folder = "C:/Users/shk0349/Documents/차종분류/Labeling/json_original"
output_folder = "C:/Users/shk0349/Documents/차종분류/Labeling/json_merged"

# 출력 폴더 생성
os.makedirs(output_folder, exist_ok=True)

# imagePath를 기준으로 JSON 통합 저장
merged_data = {}

# 모든 JSON 파일 검색
for root, _, files in os.walk(input_root_folder):
    for filename in files:
        if filename.endswith(".json"):
            input_path = os.path.join(root, filename)

            # JSON 파일 읽기
            with open(input_path, "r", encoding="utf-8") as file:
                data = json.load(file)

            # imagePath 값 추출
            if "imagePath" not in data:
                print(f"❌ 'imagePath' 없음: {filename}, 스킵")
                continue

            image_path = data["imagePath"]
            image_name, _ = os.path.splitext(os.path.basename(image_path))  # 파일명만 추출 (확장자 제거)

            # 동일한 imagePath를 가진 JSON 통합
            if image_name not in merged_data:
                merged_data[image_name] = {"annotations": []}

            # 기존 데이터 구조 유지하면서 통합
            merged_data[image_name]["annotations"].append(data)

# 변환된 JSON 저장
for image_name, data in merged_data.items():
    output_path = os.path.join(output_folder, f"{image_name}.json")
    with open(output_path, "w", encoding="utf-8") as out_file:
        json.dump(data, out_file, indent=4, ensure_ascii=False)
    print(f"✅ {image_name}.json 저장 완료 → {output_path}")

print("🎉 JSON 통합 작업 완료!")
