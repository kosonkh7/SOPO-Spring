package com.ai.pj.restcontroller;


import com.ai.pj.dto.UserDTO;
import com.ai.pj.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> getMemberProfile(
            @Valid @RequestBody UserDTO.LoginRequest request, HttpServletResponse response
    ) {
        String [] tokens = this.authService.login(request);

        // accessToken
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        // Cookie 형식임.
//        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
//                .httpOnly(true) // 클라이언트 스크립트로 접근 불가.
////                .secure(true) // HTTPS에서만 사용.
//                .path("/") // 특정 경로에서만 쿠키가 유효
//                .maxAge(7 * 24 * 60 * 60)
//                .build();

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true) // 클라이언트 스크립트로 접근 불가.
//                .secure(true) // HTTPS에서만 사용.
                .path("/") // 특정 경로에서만 쿠키가 유효
                .maxAge( 60 * 60) // 1시간.
                .build();

        // RefreshToken은 Redis에서만 조회하는 걸로.
        // response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        return ResponseEntity.status(HttpStatus.OK)
                .body("Login successful");
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue("accessToken")String accessToken, HttpServletResponse response) {

        // 쿠키 삭제를 위해 동일한 이름으로 빈 쿠키 설정
//        Cookie cookie = new Cookie("accessToken", null);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(0); // 쿠키 즉시 만료
//        response.addCookie(cookie);

        authService.logout(accessToken);

        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);


        return ResponseEntity.ok("Logged out successfully");
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue String refreshToken) {
        String accessToken = authService.refreshToken(refreshToken);

        if (accessToken == null) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Refresh Fail");
        }

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true) // 클라이언트 스크립트로 접근 불가.
//                .secure(true) // HTTPS에서만 사용.
                .path("/") // 특정 경로에서만 쿠키가 유효
                .maxAge( 60 * 60) // 1시간
                .build();


        return ResponseEntity.status(HttpStatus.OK)
//                .header("Authorization", "Bearer " + accessToken)
                .body("Refresh Success");
    }
}
