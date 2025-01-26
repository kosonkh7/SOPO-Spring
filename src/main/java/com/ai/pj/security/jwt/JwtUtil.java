package com.ai.pj.security.jwt;

import com.ai.pj.dto.UserDTO;
import com.ai.pj.service.LogMarkerService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

import java.time.ZonedDateTime;
import java.util.Date;


/**
 *  [JWT 관련 메서드를 제공하는 클래스]
 */
@Component
@Slf4j
public class JwtUtil {
    private final Key key;
    private final long ACCESS_TOKEN_EXPIRATION; // 15분 토큰 만료 시간.]
    private final long REFRESH_TOKEN_EXPIRATION; // 7일

    @Autowired
    private LogMarkerService logMarkerService;



    public JwtUtil(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_token_expiration_time}") long accessTokenExpTime,
            @Value("${jwt.refresh_token_expiration_time}") long refreshTokenExpTime
    ) {
//        String secretKey = "lKNn8x3fA9ZAP1o1uK4R67T1mGwQZ7Yv";
//        long accessTokenExpTime = 600000;
        byte [] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.ACCESS_TOKEN_EXPIRATION = accessTokenExpTime;
        this.REFRESH_TOKEN_EXPIRATION = refreshTokenExpTime;
    }

    /**
     * Access Token 생성
     * @param member
     * @return Access Token String
     */
    public String createAccessToken(UserDTO.TokenUserInfo member) {
        return createToken(member, ACCESS_TOKEN_EXPIRATION);
    }

    /**
     *
     * @param member
     * @return refreshToken
     */
    public String createRefreshToken(UserDTO.TokenUserInfo member){
        return Jwts.builder()
                .setId(member.getIdentifier())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 생성
     * @param member
     * @param expireTime
     * @return JWT string
     */
    private String createToken(UserDTO.TokenUserInfo member, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("memberIdentifier", member.getIdentifier());
        claims.put("email", member.getEmail());
        claims.put("role", member.getRole());

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime tokenValidity = now.plusSeconds(expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(now.toInstant()))
                .setExpiration(Date.from(tokenValidity.toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Token에서 User ID 추출
     * @param token
     * @return User Identifier
     */
    public String getUserIdentifier(String token) {
        return parseClaims(token).get("memberIdentifier", String.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info(logMarkerService.getInvalidTokenMarker(),"Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            // token refresh 후.  true 값 리턴.
            log.info(logMarkerService.getExpiredTokenMarker(), "Expired JWT Token");
            return true;
        } catch (UnsupportedJwtException e) {
            log.info(logMarkerService.getInvalidTokenMarker(),"Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info(logMarkerService.getInvalidTokenMarker(),"JWT claims string is empty.", e);
        }
        return false;
    }



    /**
     * JWT Claims 추출
     * @param accessToken
     * @return JWT Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e){
            return e.getClaims();
        }
    }
}
