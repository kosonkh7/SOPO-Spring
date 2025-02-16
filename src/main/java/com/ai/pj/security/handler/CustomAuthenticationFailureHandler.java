//package com.ai.pj.security.handler;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//
//import java.io.IOException;
//import java.net.URLEncoder;
//
//public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
//
//        System.out.println("123131");
//        response.setContentType("application/x-www-form-urlencoded");
//        response.setContentType("application/x-www-form-urlencoded");
//
//        String success = "false";
//        String message = URLEncoder.encode(exception.getMessage(), "UTF-8"); // URL 인코딩
//
//        response.setHeader("Cache-Control", "no-store");
//        response.setHeader("Pragma", "no-cache");
//
//        response.getWriter().write("success=" + success + "&message=" + message);
//
//    }
//}
