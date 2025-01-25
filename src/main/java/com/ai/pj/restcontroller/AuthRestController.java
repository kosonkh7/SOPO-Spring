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
        String token = this.authService.login(request);

        ResponseCookie tokenCookie = ResponseCookie.from("accessToken", token)
                .httpOnly(true) // 클라이언트 스크립트로 접근 불가.
//                .secure(true) // HTTPS에서만 사용.
                .path("/") // 특정 경로에서만 쿠키가 유효
                .maxAge(15*60)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, tokenCookie.toString());
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        // 쿠키 삭제를 위해 동일한 이름으로 빈 쿠키 설정
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 즉시 만료
        response.addCookie(cookie);

        return "You have been logged out.";
    }

}
