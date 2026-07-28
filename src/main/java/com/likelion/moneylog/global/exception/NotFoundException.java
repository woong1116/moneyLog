package com.likelion.moneylog.global.exception;

import lombok.Getter;

// 리소스를 찾을 수 없을 때 던지는 공통 예외 — SPEC 표준 에러 코드(code)를 함께 전달
@Getter
public class NotFoundException extends RuntimeException {

    private final String code;

    public NotFoundException(String code, String message) {
        super(message);
        this.code = code;
    }
}
