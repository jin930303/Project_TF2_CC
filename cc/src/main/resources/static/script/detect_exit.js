    function setDetectAvailableExit() {
        // detect_available 히든 필드의 값을 "exit"으로 변경
        document.getElementById("detect_available").value = "exit";
        
        // 폼 제출
        document.getElementById("cctv_Link").submit();
    }
