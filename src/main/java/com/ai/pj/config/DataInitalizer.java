package com.ai.pj.config;

import com.ai.pj.domain.User;

import com.ai.pj.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitalizer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("[DataInitializer] 애플리케이션 초기 데이터 등록 시작");

        if (userRepository.findUserById("admin").isEmpty()) {
            User admin = User.builder()
                    .id("admin")
                    .email("admin@naver.com")
                    .name("관리자")
                    .password(passwordEncoder.encode("1234"))
                    .build();
            admin.setRole();
            userRepository.save(admin);
            log.info("[DataInitializer] 관리자(admin) 계정 등록 완료");
        }

        if (userRepository.findUserById("daetu01").isEmpty()) {
            User user = User.builder()
                    .id("daetu01")
                    .password(passwordEncoder.encode("1234"))
                    .name("원대안")
                    .email("daetu01@naver.com")
                    .role(User.UserRole.HOLD)
                    .build();
            userRepository.save(user);
            log.info("[DataInitializer] 일반 사용자(daetu01) 계정 등록 완료");
        }

        log.info("[DataInitializer] 초기 데이터 등록 완료");
    }
}
