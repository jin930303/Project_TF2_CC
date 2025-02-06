import json
import os
from glob import glob

# JSON 파일이 있는 폴더 경로
json_folder = "dataset/annotations/Label/train"

# 모든 JSON 파일 로드
json_files = glob(os.path.join(json_folder, "*.json"))

# JSON 파일 목록 출력 (디버깅)
print(f"🔍 찾은 JSON 파일 개수: {len(json_files)}")
if not json_files:
    print("❌ JSON 파일을 찾을 수 없습니다. 경로를 확인하세요.")
    exit()

# category_name을 저장할 딕셔너리
categories_dict = {}

for json_file in json_files:
    print(f"📂 처리 중: {json_file}")  # 어떤 파일을 읽는지 확인
    with open(json_file, "r", encoding="utf-8") as f:
        data = json.load(f)

        # categories 부분 확인 (디버깅)
        if "categories" not in data or not data["categories"]:
            print(f"⚠️ 파일 {json_file} 에 'categories' 데이터가 없음.")
            continue

        # categories 부분 추출
        for category in data["categories"]:
            category_id = category.get("id")
            category_name = category.get("name")

            if category_id is None or category_name is None:
                print(f"⚠️ 파일 {json_file} 에 category_id 또는 category_name이 없음.")
                continue

            # category_id를 0부터 시작하도록 매핑
            if category_id not in categories_dict:
                categories_dict[category_id] = category_name

# category_id를 0부터 재배치
sorted_categories = {i: name for i, (cid, name) in enumerate(sorted(categories_dict.items()))}

# 출력
if not sorted_categories:
    print("❌ category 데이터가 없습니다.")
else:
    print("✅ Category mapping:")
    for new_id, name in sorted_categories.items():
        print(f"{new_id}: {name}")