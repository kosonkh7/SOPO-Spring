package com.ai.pj.controller;

import com.ai.pj.service.InspectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/inspection")
public class InspectionController {
    private final InspectionService inspectionService;

    // 검수 페이지 렌더링 (GET)
    @GetMapping("/inspect")
    public String showInspectionPage() {
        return "inspect/inspection";  // Mustache 템플릿 반환
    }

    // 상품 검수 요청 (POST) - FastAPI 호출 후 Mustache에 이미지 전달
    @PostMapping("/inspect")
    public ResponseEntity<Map<String, String>> inspectProduct(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = inspectionService.sendImageForInspection(file);
        return ResponseEntity.ok(response);
    }
}
