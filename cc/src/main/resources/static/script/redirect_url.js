function updateCctvForm() {
    var locationSelect = document.getElementById('cctv_location');
    var hiddenField = document.getElementById('hidden_cctv_location');
    if(locationSelect && hiddenField) {
        hiddenField.value = locationSelect.value;
        console.log("Hidden field updated: " + hiddenField.value);
    } else {
        console.error("요소를 찾을 수 없습니다.");
    }
}

// DOM이 완전히 로드된 후 이벤트 설정
document.addEventListener('DOMContentLoaded', function() {
    // 초기값 설정
    updateCctvForm();

    // 지역 선택이 변경될 때마다 업데이트
    document.getElementById('cctv_location').addEventListener('change', updateCctvForm);

    // 폼 제출 전에도 업데이트 (혹시 변경사항이 반영되지 않았다면)
    document.getElementById('cctv_select').addEventListener('submit', function(event) {
        updateCctvForm();
    });
});