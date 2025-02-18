document.getElementById("cctv_Link").addEventListener("submit", function(event) {
    // 기본 submit 동작을 방지하고, 대신 원하는 동작을 처리합니다.
    event.preventDefault();

    // 여기서 필요한 작업을 추가합니다 (예: 디버깅, 로깅 등)
    console.log("폼이 제출되었습니다.");

    // 폼을 실제로 제출하려면:
    this.submit(); // 실제로 폼을 제출합니다.
});


function setDetectAvailableExit() {
    // detect_available 히든 필드의 값을 "exit"으로 변경
    document.getElementById("detect_available").value = "exit";

    // 폼 제출을 트리거 (submit 이벤트 발생)
    document.getElementById("cctv_Link").dispatchEvent(new Event('submit'));
}