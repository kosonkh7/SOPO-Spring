package com.ai.pj.security.filter;

import com.ai.pj.security.jwt.JwtUtil;
import com.ai.pj.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Iterator;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter -> 한 번 실행보장.

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = extractTokenFromCookie(request, "accessToken");

        System.out.println("AccessToken : " + accessToken);
//        String authorizationHeader = request.getHeader("Authorization");
//
//        System.out.println(authorizationHeader);
        //JWT가 헤더에 있는 경우

        if (accessToken != null) {
            if (jwtUtil.validateToken(accessToken)) {
                String identifier = jwtUtil.getUserIdentifier(accessToken);

                System.out.println("Identifier :" + identifier);
                // 유저와 토큰 일치 시 userDetails 생성
                UserDetails userDetails = userService.loadUserByUsername(identifier);

                if (userDetails != null) {
                    //UserDetails, Password, Role -> 접근권한 인증 token 생성
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        filterChain.doFilter(request, response); // 다음 필터로 넘기기
//        if ( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            String token = authorizationHeader.substring(7);
            //JWT 유효성 검증

//        }

    }

    private String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
