package com.ai.pj.service;


import com.ai.pj.domain.User;
import com.ai.pj.dto.UserDTO;
import com.ai.pj.mapper.UserMapper;
import com.ai.pj.repository.UserRepository;
import com.ai.pj.security.details.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User save(UserDTO dto) {
        User user = User.createUser(dto, passwordEncoder);
        return userRepository.save(user);
    }

    public Optional<User> findUserById(String id) {
        return userRepository.findUserById(id);
    }

    public boolean isExistId(String id) {
        return this.findUserById(id).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
//        User user = findUserById(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with username : " + username));
            User user = userRepository.findById(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with userIdentifier"));

        UserDTO.TokenUserInfo dto = userMapper.EntityToTokenUserInfo(user);

        // UserDetails 객체 반환
        return new CustomUserDetails(dto);
    }

}
