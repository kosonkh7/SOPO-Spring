package com.ai.pj.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                                .ignoringRequestMatchers(PathRequest.toH2Console())
                                .disable()
                        )
                .authorizeHttpRequests(request -> request
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
//                        .requestMatchers("/public/**", "/login").permitAll()
//                        .anyRequest().authenticated()
                        .anyRequest().permitAll() // 모든 요청 허용

                )
                .formLogin(form -> form
                        .loginPage("/public/login") // 사용자 정의 로그인 페이지
                        .loginProcessingUrl("/public/loginProc")
                        .defaultSuccessUrl("/board/", true)) // 로그인 성공 시 이동할 페이지
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/public/home"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 비밀번호 암호화
    }
}
