package com.ai.pj.controller;

import com.ai.pj.client.api.StockClient;
import com.ai.pj.client.dto.StockRequestDTO;
import com.ai.pj.client.dto.StockResponseDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StockController {

    private final StockClient stockClient;

    public StockController(StockClient stockClient){
        this.stockClient = stockClient;
    }

    @GetMapping("/stock")
    public String getStockPrediction(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "category_name", required = false) String categoryName,
            Model model
    )  {
        // 정렬된 location과 category_name 리스트
        List<String> locations = List.of("가락시장", "고속터미널", "군자", "길음", "김포공항", "독산", "목동",
                "불광", "석계", "신내", "신림", "양재", "온수", "용산", "창동", "천호", "화계");
        List<String> categories = List.of("food", "life", "baby", "book", "cosmetic", "digital", "fashion",
                "furniture", "goods", "other", "sports");

        // 템플릿으로 드롭다운 데이터 전달
        model.addAttribute("locations", locations);
        model.addAttribute("categories", categories);

        // FastAPI 데이터가 존재할 경우만 처리
        if (date != null && location != null && categoryName != null) {
            // RequestDTO 생성
            StockRequestDTO requestDTO = new StockRequestDTO(date, location, categoryName);
            // FeignClient 이용해서 FastAPI 호출
            StockResponseDTO responseDTO = stockClient.getStockPrediction(
                    requestDTO.getDate(),
                    requestDTO.getLocation(),
                    requestDTO.getCategoryName()
            );
            // 응답 데이터를 모델에 추가. 템플릿으로 전달
            if (responseDTO != null) {
                model.addAttribute("predictedValue", responseDTO.getPredictedValue());
                model.addAttribute("safetyStock", responseDTO.getSafetyStock());
                model.addAttribute("properStock", responseDTO.getProperStock());
            }
        }

        return "stock/stock";
    }

}