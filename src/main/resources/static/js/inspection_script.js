document.addEventListener("DOMContentLoaded", function () {
    const dropZone = document.getElementById("dropZone");
    const fileInput = document.getElementById("fileInput");
    const dropZoneText = document.getElementById("dropZoneText");
    const uploadButton = document.getElementById("uploadButton");

    //  파일 드래그 앤 드롭 기능 (파일 선택만 수행)
    dropZone.addEventListener("dragover", (event) => {
        event.preventDefault();
        dropZone.classList.add("dragover");
    });

    dropZone.addEventListener("dragleave", () => {
        dropZone.classList.remove("dragover");
    });

    dropZone.addEventListener("drop", (event) => {
        event.preventDefault();
        dropZone.classList.remove("dragover");

        if (event.dataTransfer.files.length) {
            fileInput.files = event.dataTransfer.files; // 파일 저장
            displayFileName(fileInput.files[0]); // 파일 이름 표시
        }
    });

    dropZone.addEventListener("click", () => {
        fileInput.click();
    });

    fileInput.addEventListener("change", () => {
        if (fileInput.files.length > 0) {
            displayFileName(fileInput.files[0]); // 파일 이름 표시
        }
    });

    // 파일 이름을 네모 박스에 표시
    function displayFileName(file) {
        dropZoneText.textContent = file.name; // 파일 이름 표시
        uploadButton.disabled = false; // 버튼 활성화
    }

    // ✅ 업로드 처리
    document.getElementById("uploadForm").onsubmit = async function(event) {
        event.preventDefault();

        // 🔥 파일이 선택되지 않았으면 실행 중단
        if (!fileInput.files.length) {
            alert("파일을 선택해주세요!");
            return;
        }

        // 업로드 폼과 검수 버튼 숨기기
        document.getElementById("uploadForm").style.display = "none";
        document.getElementById("uploadButton").style.display = "none";

        // 🔥 기존 이미지 숨기기 (검수 시작 전에 비우기)
        document.getElementById("originalImage").style.display = "none";
        document.getElementById("detectedImage").style.display = "none";

        // 아이콘과 결과 이미지 사이의 간격 줄이기
        document.getElementById("resultContainer").style.marginTop = "-40px";

        // 검수 중 애니메이션 효과 (선택 사항)
        document.getElementById("resultContainer").style.display = "block";

        const fileInputData = document.getElementById("fileInput").files[0];
        const formData = new FormData();
        formData.append("file", fileInputData);

        const defaultIcon = document.getElementById("defaultIcon");
        const loadingIcon = document.getElementById("loadingIcon");

        // 기존 아이콘 숨기고 새 아이콘 표시
        if (defaultIcon && loadingIcon) {
            defaultIcon.style.display = "none"; // 기존 아이콘 숨기기
            loadingIcon.style.display = "block"; // 새 아이콘 보이기 (애니메이션 포함)
        }

        try {
            const response = await fetch("/inspection/inspect", {
                method: "POST",
                body: formData
            });

            if (response.ok) {
                const result = await response.json();

                // 🔥 새로운 이미지가 들어왔을 때만 표시
                document.getElementById("originalImage").src = "data:image/jpeg;base64," + result.original_image;
                document.getElementById("detectedImage").src = "data:image/jpeg;base64," + result.detected_image;

                document.getElementById("originalImage").style.display = "block";
                document.getElementById("detectedImage").style.display = "block";

                document.getElementById("resultContainer").style.display = "block";
            } else {
                alert("검수 실패!");
            }
        } catch (error) {
            alert("오류 발생!");
        } finally {
            // 기존 아이콘 다시 보이고, 새 아이콘 숨기기
            if (defaultIcon && loadingIcon) {
                defaultIcon.style.display = "block"; // 기존 아이콘 다시 보이기
                loadingIcon.style.display = "none"; // 로딩 아이콘 숨기기
            }
        }
    };

    document.getElementById("changeButton").addEventListener("click", function() {
        // 결과 영역 숨기기
        document.getElementById("resultContainer").style.display = "none";

        // 업로드 폼 보이기
        document.getElementById("uploadForm").style.display = "block";

        // 업로드 버튼 다시 활성화
        document.getElementById("uploadButton").style.display = "inline-block";

        // 🔥 파일 입력 초기화
        fileInput.value = ""; // 파일 선택 필드 초기화
        dropZoneText.textContent = "여기로 파일을 끌어오거나 클릭하여 선택하세요"; // 기존 파일명 지우고 기본 메시지로 변경

        // 🔥 기존 이미지 초기화
        document.getElementById("originalImage").src = "";
        document.getElementById("detectedImage").src = "";
    });
});