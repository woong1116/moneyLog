package com.likelion.moneylog.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.moneylog.global.exception.ErrorCode;
import com.likelion.moneylog.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

// 401 — 인증 필요(토큰 없음/위조/만료)
@Component
@RequiredArgsConstructor
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        write(response, HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED);
    }

    static void write(HttpServletResponse response, HttpStatus status, ErrorCode code) throws IOException {
        // 여기서만은 형식을 JSON으로 직접 직렬화한다(필터 단계라 @RestControllerAdvice가 못 잡음)
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ApiResponse<Void> body = ApiResponse.error(code.name(), code.getMessage());
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
