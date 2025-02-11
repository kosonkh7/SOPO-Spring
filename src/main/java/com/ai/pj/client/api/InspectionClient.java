package com.ai.pj.client.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "inspectionClient", url = "${inspection.api.host}")
public interface InspectionClient {
    @PostMapping(value = "/predict/image/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    Map<String, String> inspectProduct(@RequestPart("file") MultipartFile file);

    // @FeignClient → FastAPI 서버 URL을 지정
    // @PostMapping → FastAPI의 /predict/image/ 엔드포인트 호출
    // @RequestPart("file") → MultipartFile을 FastAPI로 전송
    // Map<String, String> → 입력 이미지와 YOLO가 감지한 Base64 이미지 데이터 JSON으로 응답받음.
}
