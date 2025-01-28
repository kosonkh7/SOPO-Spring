package com.ai.pj.client.api;

import com.ai.pj.client.dto.StockResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "stockClient", url = "${stock.api.host}")
public interface StockClient {

    @GetMapping("/stock_predictions")
    StockResponseDTO getStockPrediction(
            @RequestParam("date") String date,
            @RequestParam("location") String location,
            @RequestParam("category_name") String categoryName
    );
}
