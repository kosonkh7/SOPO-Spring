package com.ai.pj.security.handler;

import com.ai.pj.domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.io.IOException;
import java.net.URLEncoder;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/x-www-form-urlencoded");

        String success = "true";
        String message = "로그인 성공";// URL 인코딩

//        System.out.println("getAuth"+ authentication.getPrincipal().toString());
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        message = URLEncoder.encode(message, "UTF-8");
        response.getWriter().write("success=" + success + "&message=" + message);
    }
}
