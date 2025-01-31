package com.ai.pj.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class IpLogginFilter extends OncePerRequestFilter {

//    private final IpLogService ipLogService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String ipAddress = request.getRemoteAddr(); // 클라이언트 IP 가져오기
        log.info("Client IP : {}", ipAddress);

        // UserRole에 따른 log 분석?


        //
        // Ip Log 저장.
//        ipLogService.save(ipAddress, );

        filterChain.doFilter(request, response); // 다음 필터로 요청 전달.
    }
}
