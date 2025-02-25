package com.ai.pj.service;

import com.ai.pj.domain.Token;
import com.ai.pj.domain.User;
import com.ai.pj.dto.UserDTO;
import com.ai.pj.mapper.UserMapper;
import com.ai.pj.repository.UserRepository;
import com.ai.pj.security.handler.CustomAccessDeniedHandler;
import com.ai.pj.security.handler.CustomAuthDeniedHandler;
import com.ai.pj.security.jwt.JwtUtil;
import com.ai.pj.security.repository.CustomTokenRepository;
import com.ai.pj.security.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationNotSupportedException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final TokenRepository tokenRepository;
    private final LogMarkerService logMarkerService;


    public String [] login(UserDTO.LoginRequest dto) {
        String id = dto.getId();
        String password = dto.getPassword();

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username : " + id));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        System.out.println(user.getRole());
        if (user.getRole().toString().equals("HOLD")) {
            System.out.println("들어옴");
            throw new CustomAuthDeniedHandler("회원가입을 요청 중입니다. 잠시만 기다려 주세요!");
        }

        if (checkDuplicate(user.getIdentifier())) {
            throw new CustomAuthDeniedHandler("이미 로그인 중인 아이디입니다.");
        }



        UserDTO.TokenUserInfo info = userMapper.EntityToTokenUserInfo(user);

        // accessToken 생성.
        String accessToken = jwtUtil.createAccessToken(info);
        String refreshToken = jwtUtil.createRefreshToken(info);

        tokenRepository.save(new Token(user.getIdentifier(), refreshToken, "use", 7*24*60*60L) );

        return new String[]{accessToken, refreshToken};
    }

    public void logout(String refreshToken) {
        String identifier = jwtUtil.getUserIdentifier(refreshToken);

        // redis에 저장된 거 제거해야됨.
        tokenRepository.deleteById(identifier);
    }

    public boolean checkDuplicate(String identifier) {
       return tokenRepository.findByusable(identifier);
    }



    public String refreshToken (String identifier) {
        System.out.println("TOKEN IDENTIFIER: : " + identifier);
        // Redis에서 identifier로 Refresh토큰이 존재하는 지 확인 및 가져오기
        Token token = tokenRepository.findById(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 토큰입니다."));

        User user = userRepository.findById(identifier)
                        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다. "));


        UserDTO.TokenUserInfo tokenUserInfo = userMapper.EntityToTokenUserInfo(user);


        // 존재 시, 갱신 및 AccessToken 재발급.
        return jwtUtil.createAccessToken(tokenUserInfo);
    }



    /*
    public String refreshToken(String refreshToken) {
        String username = jwtUtil.getUserIdentifier(refreshToken);

        Optional<Token> storedToken = tokenRepository.findById(username) ;

        if (storedToken.isEmpty() || !storedToken.get().getRefreshToken().equals(refreshToken)) {
            return null ;
        }
        Claims claims = jwtUtil.parseClaims(refreshToken);
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);
        UserDTO.TokenUserInfo tempus = UserDTO.TokenUserInfo.builder()
                .identifier(username)
                .email(email)
                .role(User.UserRole.valueOf(role))
                .build();
        String newAccessToken = jwtUtil.createAccessToken(tempus) ;

        return newAccessToken;

    }*/
}
