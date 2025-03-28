package com.ai.pj.security.handler;

import com.ai.pj.dto.ErrorResponseDto;
import com.ai.pj.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;


@AllArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        // AJAX 요청인지 확인 (REST API 요청인지 확인)
        String requestedWith = request.getHeader("X-Requested-With");

        if ("XMLHttpRequest".equals(requestedWith)) {
            // JSON 응답 반환 (AJAX 요청인 경우)
            ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                    HttpStatus.UNAUTHORIZED.value(),
                    authException.getMessage(),
                    LocalDateTime.now()
            );

            String responseBody = objectMapper.writeValueAsString(errorResponseDto);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseBody);
        } else {
            // 일반 브라우저 요청인 경우 로그인 페이지로 리디렉션
            response.sendRedirect("/public/login");
        }
    }
}
