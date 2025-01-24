package com.ai.pj.service;

import com.ai.pj.domain.User;
import com.ai.pj.dto.UserDTO;
import com.ai.pj.mapper.UserMapper;
import com.ai.pj.repository.UserRepository;
import com.ai.pj.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;


    public String login(UserDTO.LoginRequest dto) {
        String id = dto.getId();
        String password = dto.getPassword();

        User user = userRepository.findUserById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username : " + id));

        if (!encoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다. ");
        }

        UserDTO.TokenUserInfo info = userMapper.EntityToTokenUserInfo(user);

        String accessToken = jwtUtil.createAccessToken(info);
        return accessToken;
    }
}
