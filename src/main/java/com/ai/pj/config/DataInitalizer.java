package com.ai.pj.config;

import com.ai.pj.domain.User;

import com.ai.pj.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class DataInitalizer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        User admin = User.builder()
                .id("admin")
                .password(passwordEncoder.encode("1234"))
                .build();

        admin.setRole();

        if (userRepository.findById("admin").isEmpty()) {
            userRepository.save(admin);
        }
    }
}
