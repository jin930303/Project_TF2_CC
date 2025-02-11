# 입력 파일 경로
input_file = 'C:/Users/shk0349/Documents/차종분류/Labeling/category_info.txt'
# 출력 파일 경로
output_file = 'C:/Users/shk0349/Documents/차종분류/Labeling/car_info.txt'

# 파일 읽기
with open(input_file, 'r', encoding='utf-8') as f:
    lines = f.readlines()

# 'Category Name: '을 없애고, ID와 함께 이름만 추출
categories = set()
for line in lines:
    category_name = line.replace('Category Name: ', '').strip()
    categories.add(category_name)

# 중복 제거된 카테고리 리스트로 변환
categories = list(categories)

# 결과 저장할 형식으로 변환
result = {
    'names': categories
}

# 파일에 저장
with open(output_file, 'w', encoding='utf-8') as f:
    f.write(f"names : {categories}\n")

print(f"파일이 {output_file}에 저장되었습니다.")
