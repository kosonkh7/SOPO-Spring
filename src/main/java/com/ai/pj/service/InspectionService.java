package com.ai.pj.service;

import com.ai.pj.client.api.InspectionClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class InspectionService {
    private final InspectionClient inspectionClient;

    // FastAPI 호출 메서드 (FeignClient 연동)
    public Map<String, String> sendImageForInspection(MultipartFile file) {
        return inspectionClient.inspectProduct(file);
    }

}
