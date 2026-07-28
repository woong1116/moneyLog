package com.likelion.moneylog.domain.user.dto;

// 로그인 성공 시 data (응답의 data 안에 들어간다)
public record TokenResponse(String accessToken, String tokenType, String nickname) {
    public static TokenResponse of(String accessToken, String nickname) {
        return new TokenResponse(accessToken, "Bearer", nickname);
    }
}
