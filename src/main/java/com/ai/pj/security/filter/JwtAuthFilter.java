package com.ai.pj.security.filter;

import com.ai.pj.security.jwt.JwtUtil;
import com.ai.pj.service.AuthService;
import com.ai.pj.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter { // OncePerRequestFilter -> 한 번 실행보장.

    private final UserService userService;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //JWT가 헤더에 있는 경우
//        String authorizationHeader = request.getHeader("Authorization");
//
//        System.out.println(authorizationHeader);
//
//        if ( authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            String token = authorizationHeader.substring(7);
////            JWT 유효성 검증
//            if (jwtUtil.validateToken(token)) {
//                String identifier = jwtUtil.getUserIdentifier(token);
//
//                System.out.println("Identifier :" + identifier);
//                // 유저와 토큰 일치 시 userDetails 생성
//                UserDetails userDetails = userService.loadUserByUsername(identifier);
//
//                if (userDetails != null) {
//                    //UserDetails, Password, Role -> 접근권한 인증 token 생성
//                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                }
//            }
//        }
//
//        filterChain.doFilter(request, response);



         // *  쿠키로 토큰 전송.
           String accessToken = extractTokenFromCookie(request, "accessToken");

           System.out.println("AccessToken : " + accessToken);
        if (accessToken != null) {
            try {
                if (jwtUtil.validateToken(accessToken)) {
                    String identifier = jwtUtil.getUserIdentifier(accessToken);

                    // 만약에 에러 로그가 있는 상태로 왔다?
                    if (log.isErrorEnabled()) {
                        System.out.println("EXPIREDDDDD".repeat(3));
                        String newAccessToken = null;
                        newAccessToken = authService.refreshToken(identifier);
                        // AccessToken 갱신 및 response에 쿠키 담아서 주기. .

                        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                                .httpOnly(true) // 클라이언트 스크립트로 접근 불가.
    //                .secure(true) // HTTPS에서만 사용.
                                .path("/") // 특정 경로에서만 쿠키가 유효
                                .maxAge( 60 * 60) // 1시간
                                .build();

                        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
                    }
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
            } catch (UsernameNotFoundException e) {
                Cookie cookie = new Cookie("accessToken", null);
                cookie.setHttpOnly(true);
                cookie.setSecure(true);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);

                filterChain.doFilter(request, response);
            }
        }

        filterChain.doFilter(request, response); // 다음 필터로 넘기기
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
