package com.ai.pj.security.service;

import com.ai.pj.domain.Token;
import com.ai.pj.domain.User;
import com.ai.pj.exception.BusinessException;
import com.ai.pj.exception.ErrorCode;
import com.ai.pj.security.jwt.JwtGenerator;
import com.ai.pj.security.jwt.JwtRule;
import com.ai.pj.security.jwt.JwtUtil;
import com.ai.pj.security.jwt.TokenStatus;
import com.ai.pj.security.repository.TokenRepository;
import com.ai.pj.service.UserService;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;

import static com.ai.pj.exception.ErrorCode.JWT_TOKEN_NOT_FOUND;
import static com.ai.pj.exception.ErrorCode.NOT_AUTHENTICATED_USER;
import static com.ai.pj.security.jwt.JwtRule.*;

@Service
@Transactional(readOnly = true)
public class JwtService {
    private final TokenRepository tokenRepository;

    private final UserService userService;
    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;
    private final Key ACCESS_SECRET_KEY;
    private final Key REFRESH_SECRET_KEY;
    private final long ACCESS_EXPIRATION;
    private final long REFRESH_EXPIRATION;


    public JwtService(UserService userService, JwtGenerator jwtGenerator,
                      JwtUtil jwtUtil, TokenRepository tokenRepository
//                      @Value("${jwt.access-secret}") String ACCESS_SECRET_KEY,
//                      @Value("${jwt.refresh-secret") String REFRESH_SECRET_KEY,
//                      @Value("${jwt.access-expiration}") long ACCESS_EXPIRATION,
//                      @Value("${jwt.refresh-expiration}") long REFRESH_EXPIRATION
    ){

        this.userService = userService;
        this.tokenRepository = tokenRepository;
        this.jwtGenerator = jwtGenerator;
        this.jwtUtil = jwtUtil;
        this.ACCESS_SECRET_KEY = jwtUtil.getSignKey("l$Nn8x3f@9Z*P1o&uK4R6^T#mGwQ%7Yv");
        this.REFRESH_SECRET_KEY = jwtUtil.getSignKey("mA*9R6T#yP2Z$L&Nn3vG^X1Kw%8oG7Qf");
        this.ACCESS_EXPIRATION = 600000;
        this.REFRESH_EXPIRATION = 10800000;

    }

    public void validateUser(User requestUser) {
        if (requestUser.getRole() == User.UserRole.HOLD)  {
            throw new BusinessException(NOT_AUTHENTICATED_USER);
        }
    }

    @Transactional
    public String generateAccessToken(HttpServletResponse response, User requestUser) {
        String accessToken = jwtGenerator.generateAccessToken(ACCESS_SECRET_KEY, ACCESS_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(ACCESS_PREFIX.getValue(), accessToken, ACCESS_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());

        return accessToken;
    }

    public String generateRefreshToken(HttpServletResponse response, User requestUser) {
        String refreshToken = jwtGenerator.generateRefreshToken(REFRESH_SECRET_KEY, REFRESH_EXPIRATION, requestUser);
        ResponseCookie cookie = setTokenToCookie(REFRESH_PREFIX.getValue(), refreshToken, REFRESH_EXPIRATION / 1000);
        response.addHeader(JWT_ISSUE_HEADER.getValue(), cookie.toString());

        tokenRepository.save(new Token(requestUser.getNum(), refreshToken));
        return refreshToken;
    }


    private ResponseCookie setTokenToCookie(String tokenPrefix, String token, long maxAgeSeconds) {
        return ResponseCookie.from(tokenPrefix, token)
                .path("/")
                .maxAge(maxAgeSeconds)
                .httpOnly(true)
                .sameSite("None")
                .secure(true)
                .build();
    }

    public boolean validateAccessToken(String token) {
        return jwtUtil.getTokenStatus(token, ACCESS_SECRET_KEY) == TokenStatus.AUTHENTICATED;
    }

    public boolean validateRefreshToken(String token, String identifier) {
        boolean isRefreshValid = jwtUtil.getTokenStatus(token, REFRESH_SECRET_KEY) == TokenStatus.AUTHENTICATED;

        Token storedToken = tokenRepository.findById(identifier)
                .orElseThrow(() -> new BusinessException(ErrorCode.JWT_TOKEN_NOT_FOUND));
        return storedToken.getRefreshToken().equals(token);
    }

    public String resolveTokenFromCookie(HttpServletRequest request, JwtRule tokenPrefix) {
        Cookie [] cookies = request.getCookies();

        if (cookies == null) {
            throw new BusinessException(JWT_TOKEN_NOT_FOUND);
        }
        return jwtUtil.resolveTokenFromCookie(cookies, tokenPrefix);
    }

    public Authentication getAuthentication(String token) {
        UserDetails principal = userService.loadUserByidentifier(getUserPk(token, ACCESS_SECRET_KEY));
        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
    }

    private String getUserPk(String token, Key secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJwt(token)
                .getBody()
                .getSubject();
    }

    public String getIdentifierFromRefresh(String refreshToken){
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(REFRESH_SECRET_KEY)
                    .build()
                    .parseClaimsJwt(refreshToken)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_JWT);
        }
    }

    public void logout(User requestUser, HttpServletResponse response) {
        tokenRepository.deleteById(requestUser.getNum());

        Cookie accessCookie = jwtUtil.resetToken(ACCESS_PREFIX);
        Cookie refreshCookie = jwtUtil.resetToken(REFRESH_PREFIX);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

}
