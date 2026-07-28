package com.likelion.moneylog.domain.transaction.dto;

import com.likelion.moneylog.domain.transaction.entity.Transaction;
import com.likelion.moneylog.domain.transaction.entity.TransactionType;

import java.time.LocalDate;
import java.time.LocalDateTime;

// 거래내역 응답 DTO
public record TransactionResponse(
        Long id,
        TransactionType type,
        Long amount,
        Long categoryId,
        String categoryName,           // 화면 편의를 위해 카테고리명 포함
        String description,
        LocalDate transactionDate,
        LocalDateTime createdAt
) {
    // 엔티티 → 응답 DTO 변환 정적 팩토리
    public static TransactionResponse from(Transaction t) {
        return new TransactionResponse(
                t.getId(),
                t.getType(),
                t.getAmount(),
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getDescription(),
                t.getTransactionDate(),
                t.getCreatedAt()
        );
    }
}