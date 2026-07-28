// service/TransactionService.java
package com.likelion.moneylog.domain.transaction.service;

import com.likelion.moneylog.domain.category.entity.Category;

import com.likelion.moneylog.domain.category.repository.CategoryRepository;
import com.likelion.moneylog.domain.transaction.dto.TransactionRequest;
import com.likelion.moneylog.domain.transaction.dto.TransactionResponse;
import com.likelion.moneylog.domain.transaction.entity.Transaction;
import com.likelion.moneylog.domain.transaction.entity.TransactionType;
import com.likelion.moneylog.domain.transaction.repository.TransactionRepository;
import com.likelion.moneylog.domain.user.repository.UserRepository;
import com.likelion.moneylog.global.exception.CustomException;
import com.likelion.moneylog.global.exception.ErrorCode;
import com.likelion.moneylog.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 등록 — userId는 1-8에서 로그인 사용자로 교체 예정
    @Transactional
    public TransactionResponse create(Long userId, TransactionRequest req) {
        // 카테고리 존재 + 소유자 검증 — SPEC 표준 에러 코드 CATEGORY_NOT_FOUND
        Category category = categoryRepository.findByIdAndUserId(req.categoryId(), userId)
                .orElseThrow(() -> new NotFoundException(
                        "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다: " + req.categoryId()));

        Transaction tx = Transaction.builder()
                .user(userRepository.getReferenceById(userId))
                .category(category)
                .type(req.type())
                .amount(req.amount())
                .description(req.description())
                .transactionDate(req.transactionDate())
                .build();

        return TransactionResponse.from(transactionRepository.save(tx));
    }

    // 목록: 처음부터 내 user_id 조건으로만 조회
    @Transactional(readOnly = true)
    public Page<TransactionResponse> getMyTransactions(Long userId, String yearMonth,
                                                       TransactionType type, Long categoryId,
                                                       Pageable pageable) {
        LocalDate start = null;
        LocalDate end = null;

        if (yearMonth != null && !yearMonth.isBlank()) {
            YearMonth ym = YearMonth.parse(yearMonth);   // "2026-07" 형식
            start = ym.atDay(1);
            end = ym.atEndOfMonth();
        }

        // Repository 쿼리에 userId를 반드시 포함 (아래 참고)
        return transactionRepository
                .search(userId, start, end, type, categoryId, pageable)
                .map(TransactionResponse::from);
    }

    // 단건: 조회 후 소유자 검증 → 남의 것이면 403(FORBIDDEN)
    @Transactional(readOnly = true)
    public TransactionResponse getMyTransaction(Long userId, Long id) {
        Transaction tx = findOwned(userId, id);
        return TransactionResponse.from(tx);
    }

    // 수정
    @Transactional
    public TransactionResponse update(Long userId, Long id, TransactionRequest req) {
        Transaction tx = findOwned(userId, id);
        Category category = categoryRepository.findByIdAndUserId(req.categoryId(), userId)
                .orElseThrow(() -> new NotFoundException(
                        "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다: " + req.categoryId()));
        // 엔티티의 도메인 메서드로 상태 변경(더티 체킹으로 자동 반영)
        tx.update(req.type(), req.amount(), category, req.description(), req.transactionDate());
        return TransactionResponse.from(tx);
    }

    // 삭제
    @Transactional
    public void delete(Long userId, Long id) {
        Transaction tx = findOwned(userId, id);
        transactionRepository.delete(tx);
    }

    // ★ 소유권 검증 공통 메서드: 없으면 404, 남의 것이면 403
    private Transaction findOwned(Long userId, Long id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));
        if (!tx.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN); // → HTTP 403 FORBIDDEN 형식
        }
        return tx;
    }
}