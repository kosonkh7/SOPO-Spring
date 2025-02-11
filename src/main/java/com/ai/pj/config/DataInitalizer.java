package com.ai.pj.config;

import com.ai.pj.domain.User;

import com.ai.pj.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
public class DataInitalizer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        User admin = User.builder()
                .id("admin")
                .email("adfasdf@asdfa.com")
                .name("sadfasd")
                .password(passwordEncoder.encode("1234"))
                .build();

        admin.setRole();

        if (userRepository.findUserById("admin").isEmpty()) {
            userRepository.save(admin);
        }

        User user = User.builder()
                .id("daetu01")
                .password(passwordEncoder.encode("1234"))
                .name("fasdf")
                .email("sadfasdf@asdfanas.asdf")
                .role(User.UserRole.HOLD)
                .build();

        if (userRepository.findUserById("daetu01").isEmpty()) {
            userRepository.save(user);
        }



    }
}
