package com.likelion.moneylog.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;

// 모든 REST 응답을 감싸는 공통 응답 래퍼 클래스 (SPEC 5-1)
// null 필드(meta, code 등)는 직렬화에서 제외해 응답을 깔끔하게 유지
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Object meta,     // 목록 조회 시 PageMeta 등을 담음(없으면 미출력)
        String code      // 실패 시 기계용 에러 코드(1-8 전역 예외에서 채움)
) {
    // 성공 — 데이터만 (기본 메시지)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "요청에 성공했습니다.", data, null, null);
    }

    // 성공 — 메시지 + 데이터
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, null);
    }

    // 성공 — 메시지 + 데이터 + meta(목록 pagination 등)
    public static <T> ApiResponse<T> success(String message, T data, Object meta) {
        return new ApiResponse<>(true, message, data, meta, null);
    }

    // 실패 — 전역 예외 처리(1-8)에서 사용할 형식
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, message, null, null, code);
    }
}