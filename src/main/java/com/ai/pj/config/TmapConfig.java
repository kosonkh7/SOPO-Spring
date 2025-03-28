package com.ai.pj.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TmapConfig {

    @Value("${tmap-api-key}")
    private String TMAP_API_KEY;

    public String getTmapApiKey() {
        return TMAP_API_KEY;
    }
}
