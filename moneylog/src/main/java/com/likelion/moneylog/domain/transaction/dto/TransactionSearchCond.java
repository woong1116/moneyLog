package com.likelion.moneylog.domain.transaction.dto;

import com.likelion.moneylog.domain.transaction.entity.TransactionType;

// 목록 조회 검색 조건(yearMonth 필수, 나머지 선택)
public record TransactionSearchCond(
        String yearMonth,           // "2026-07"
        TransactionType type,       // nullable
        Long categoryId             // nullable
) {}
