package com.likelion.moneylog.domain.transaction.dto;

import com.likelion.moneylog.domain.transaction.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

// 거래내역 등록/수정 요청 DTO
public record TransactionRequest(
        @NotNull(message = "거래 타입은 필수입니다.")
        TransactionType type,          // INCOME / EXPENSE

        @NotNull(message = "금액은 필수입니다.")
        @Positive(message = "금액은 0보다 커야 합니다.")
        Long amount,                   // 원 단위

        @NotNull(message = "카테고리는 필수입니다.")
        Long categoryId,

        @Size(max = 200, message = "설명은 200자 이하여야 합니다.")
        String description,

        @NotNull(message = "거래일은 필수입니다.")
        LocalDate transactionDate
) {}