package com.ai.pj.service;


import com.ai.pj.domain.User;
import com.ai.pj.dto.UserDTO;
import com.ai.pj.exception.BusinessException;
import com.ai.pj.repository.UserRepository;
import com.ai.pj.security.model.UserPrincipal;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ai.pj.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(UserDTO dto) {
        User user = User.createUser(dto, passwordEncoder);
        return userRepository.save(user);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public boolean isExistId(String id) {
        return this.findById(id).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username : " + username));

        // UserDetails 객체 반환
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getId()) // 사용자 이름
                .password(user.getPassword()) // 암호화된 비밀번호
                .roles(String.valueOf(user.getRole()))        // 권한 설정
                .build();
    }

    public UserDetails loadUserByidentifier(String identifier) throws UsernameNotFoundException {
        User user = userRepository.findById(identifier)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));
        return new UserPrincipal(user);
    }
}
