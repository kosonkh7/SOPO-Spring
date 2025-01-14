package com.ai.pj.restcontroller;

import com.ai.pj.client.api.FastApiClient;
import com.ai.pj.client.dto.ChatbotDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final FastApiClient fastApiClient;

    @PostMapping("/chatbot")
    public String chatbot(@RequestBody ChatbotDTO.Post post) {

        String retText = fastApiClient.getText(post);

        return retText;
    }
}
