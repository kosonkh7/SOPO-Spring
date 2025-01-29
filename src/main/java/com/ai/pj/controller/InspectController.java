package com.ai.pj.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/inspect")
public class InspectController {

    private static final String UPLOAD_DIR = "uploads/"; // 이미지 저장 폴더

    @GetMapping
    public String redirectToInspect() {
        return "redirect:/inspect/";
    }

    @GetMapping("/")
    public String showInspectPage(Model model) {
        return "inspect/inspect";
    }

    /**  이미지 업로드 처리 **/
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("{\"success\": false, \"message\": \"파일이 없습니다.\"}");
        }

        try {
            // 고유 파일명 생성
            String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + filename);

            // 디렉토리 없으면 생성
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 파일 저장
            file.transferTo(filePath.toFile());

            // 저장된 파일 URL 반환
            String imageUrl = "/inspect/images/" + filename;
            return ResponseEntity.ok("{\"success\": true, \"imageUrl\": \"" + imageUrl + "\"}");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("{\"success\": false, \"message\": \"업로드 실패.\"}");
        }
    }

    /**  업로드된 이미지 제공 **/
    @GetMapping("/images/{filename}")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR + filename);
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok().body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
