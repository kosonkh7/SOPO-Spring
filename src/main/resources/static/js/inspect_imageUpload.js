document.addEventListener("DOMContentLoaded", function () {
    const uploadForm = document.getElementById("uploadForm");
    const imageInput = document.getElementById("imageInput");
    const uploadedImage = document.getElementById("uploadedImage");
    const detectionsList = document.getElementById("detections");

    uploadForm.addEventListener("submit", function (event) {
        event.preventDefault(); // 기본 폼 제출 방지

        const file = imageInput.files[0];
        if (!file) {
            alert("이미지를 선택하세요.");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);

        fetch("/upload/", {
            method: "POST",
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("이미지 업로드 및 탐지 완료!");

                    // 이미지 표시
                    uploadedImage.src = data.image_url;
                    uploadedImage.style.display = "block";

                    // 탐지된 객체 목록 업데이트
                    detectionsList.innerHTML = "";
                    data.detections.forEach(item => {
                        const li = document.createElement("li");
                        li.textContent = `${item.label} (신뢰도: ${(item.confidence * 100).toFixed(2)}%)`;
                        detectionsList.appendChild(li);
                    });
                } else {
                    alert("탐지 실패: " + data.message);
                }
            })
            .catch(error => console.error("Error:", error));
    });
});
