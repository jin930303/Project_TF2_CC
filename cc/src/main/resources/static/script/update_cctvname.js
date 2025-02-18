document.addEventListener("DOMContentLoaded", function () {
    // CCTV URL select box와 숨겨진 CCTV name input을 가져옴
    const cctvSelect = document.getElementById("cctv_url");
    const hiddenCctvName = document.getElementById("cctv_name");

    // 첫 번째 로드 시, 초기 값으로 hiddenCctvName 설정
    hiddenCctvName.value = cctvSelect.options[cctvSelect.selectedIndex].text;

    // CCTV URL이 변경될 때마다 cctv_name 값을 업데이트
    cctvSelect.addEventListener("change", function () {
        // 선택된 option의 텍스트 값(cctv_name)을 가져와 숨겨진 input에 설정
        hiddenCctvName.value = cctvSelect.options[cctvSelect.selectedIndex].text;
    });
});
