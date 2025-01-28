document.addEventListener("DOMContentLoaded", function () {
    const uploadForm = document.getElementById("uploadForm");
    const imageInput = document.getElementById("imageInput");
    const uploadedImage = document.getElementById("uploadedImage");

    uploadForm.addEventListener("submit", function (event) {
        event.preventDefault(); // 기본 폼 제출 방지

        const file = imageInput.files[0];
        if (!file) {
            alert("이미지를 선택하세요.");
            return;
        }

        const formData = new FormData();
        formData.append("file", file);

        fetch("/api/upload", {
            method: "POST",
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert("이미지 업로드 성공!");
                    uploadedImage.src = data.imageUrl; // 업로드된 이미지 경로 표시
                    uploadedImage.style.display = "block";
                } else {
                    alert("업로드 실패: " + data.message);
                }
            })
            .catch(error => console.error("Error:", error));
    });
});
