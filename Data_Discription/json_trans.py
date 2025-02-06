import os
import json
import shutil

# 입력 폴더 및 이미지 폴더 경로 설정
input_root_folder = "C:/Users/shk0349/Documents/차종분류/Labeling/json_merged"
image_folder = "C:/Users/shk0349/Documents/차종분류/Image/Image_2"
output_folder = "C:/Users/shk0349/Documents/차종분류/Labeling/json_trans"

# 출력 폴더 생성
os.makedirs(output_folder, exist_ok=True)

# 이미지 파일 리스트 (확장자 제거한 이름 기준으로 저장)
image_files = {os.path.splitext(f)[0] for f in os.listdir(image_folder)}  # 확장자 없이 이름만 저장

# 모든 JSON 파일 검색
for root, _, files in os.walk(input_root_folder):
    for filename in files:
        if filename.endswith(".json"):
            input_path = os.path.join(root, filename)

            # JSON 파일 읽기
            with open(input_path, "r", encoding="utf-8") as file:
                data = json.load(file)

            # JSON에서 imagePath 값 추출
            if "imagePath" not in data:
                print(f"❌ 'imagePath' 없음: {filename}, 스킵")
                continue

            # JSON 파일명에서 확장자 제외한 이름을 추출
            json_name, _ = os.path.splitext(filename)

            # 이미지가 존재하는지 확인 (이미지 파일 리스트에서 해당 이름을 검색)
            if json_name in image_files:
                # JSON 파일 이동
                destination_path = os.path.join(output_folder, filename)
                shutil.move(input_path, destination_path)
                print(f"✅ JSON 이동 완료: {filename} → {output_folder}")
            else:
                print(f"🚫 해당 이미지 없음: {json_name}, JSON 이동 안 함")

print("🎉 JSON 이동 작업 완료!")
