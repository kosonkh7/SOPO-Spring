package com.ai.pj.client.api;

import com.ai.pj.client.dto.ChatbotDTO;
import com.ai.pj.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fastApiClient", url = "${chatbot.api.host}",configuration = FeignConfig.class)
public interface FastApiClient {
    @PostMapping("/chat")
    public String getText(@RequestBody ChatbotDTO.Post post);
}
