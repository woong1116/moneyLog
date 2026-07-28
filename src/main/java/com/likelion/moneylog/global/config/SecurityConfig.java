package com.likelion.moneylog.global.config;

import com.likelion.moneylog.global.security.JwtAuthenticationFilter;
import com.likelion.moneylog.global.security.RestAccessDeniedHandler;
import com.likelion.moneylog.global.security.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint; // 401 형식
    private final RestAccessDeniedHandler accessDeniedHandler;           // 403 형식

    // 인증 없이 접근 허용할 경로 (화이트리스트)
    private static final String[] WHITELIST = {
            "/api/auth/signup",
            "/api/auth/login",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/login.html",
            "/transactions.html",
            "/api.js"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {})   // 위에서 등록한 CorsConfigurationSource 빈이 자동 사용됨
                // JWT는 브라우저 쿠키가 아니라 헤더로 오므로 CSRF 불필요
                .csrf(csrf -> csrf.disable())
                // 세션을 만들지 않는 무상태 방식
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST).permitAll()  // 회원가입/로그인/Swagger는 공개
                        .anyRequest().authenticated()            // 나머지는 전부 인증 필요
                )
                // ★ 시큐리티가 던지는 인증/인가 예외도 공통 응답 형식으로 내려준다
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint) // 401 UNAUTHORIZED
                        .accessDeniedHandler(accessDeniedHandler)           // 403 FORBIDDEN
                )
                // 우리가 만든 JWT 필터를 기본 인증 필터 앞에 끼운다
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();   // 비밀번호는 BCrypt로만 저장
    }
}