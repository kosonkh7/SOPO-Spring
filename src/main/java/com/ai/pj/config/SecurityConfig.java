package com.ai.pj.config;

import com.ai.pj.security.authentication.RoleBasedAuthenticationProvider;
import com.ai.pj.security.filter.JwtAuthFilter;
import com.ai.pj.security.handler.CustomAccessDeniedHandler;
import com.ai.pj.security.handler.CustomAuthenticationEntryPoint;

import com.ai.pj.security.jwt.JwtUtil;
import com.ai.pj.service.AuthService;
import com.ai.pj.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfig {

    private final UserService userService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtUtil jwtUtil;

    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**", "/img/**", "/css/**", "/js/**", "/public/login"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf
                                .ignoringRequestMatchers(PathRequest.toH2Console())
                                .disable()
                        )
                //
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .formLogin(form -> form
                        .loginPage("/public/login") // 사용자 정의 로그인 페이지
                        .disable())
                .addFilterBefore(new JwtAuthFilter(userService, authService, jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionHandling) -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
//                       .anyRequest().permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
//                        .anyRequest().fullyAuthenticated()
                        // 권한에 따른 로그인 다 잡기
                        .anyRequest().permitAll() // 모든 요청 허용
                )

//                        .loginProcessingUrl("/public/loginProc")
//                        .successHandler(new CustomAuthenticationSuccessHandler())
//                        .failureHandler(new CustomAuthenticationFailureHandler()))
//                        .defaultSuccessUrl("/board/", true)) // 로그인 성공 시 이동할 페이지
                .logout(logout -> logout
//                        .logoutUrl("/logout")
//                        .logoutSuccessUrl("/public/home")
                        .disable())

                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .build();
    }


    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        System.out.println(1010);
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(new RoleBasedAuthenticationProvider(userService, passwordEncoder))
                .build();
    }
}
