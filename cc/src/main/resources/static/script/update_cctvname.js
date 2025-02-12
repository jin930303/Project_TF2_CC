document.addEventListener("DOMContentLoaded", function() {
    function updateCctvName() {
        var cctvSelect = document.getElementById('cctv_url');
        var selectedCctvName = cctvSelect.options[cctvSelect.selectedIndex].text;
        document.getElementById('cctv_name').value = selectedCctvName;
    }

    // CCTV 선택 시 자동으로 실행되도록 이벤트 추가
    document.getElementById('cctv_url').addEventListener('change', updateCctvName);

    // 페이지 로드 시 초기 값 설정
    updateCctvName();
});