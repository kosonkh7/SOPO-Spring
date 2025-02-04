package com.ai.pj.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class TmapConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public String getTmapApiKey() {
        return dotenv.get("TMAP_API_KEY");
    }
}
