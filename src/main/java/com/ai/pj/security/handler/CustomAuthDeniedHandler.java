package com.ai.pj.security.handler;

import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthDeniedHandler extends RuntimeException{
    public CustomAuthDeniedHandler(String message) {
        super(message);
    }
}
