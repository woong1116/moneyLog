package com.likelion.moneylog.domain.transaction.repository;

import com.likelion.moneylog.domain.transaction.entity.Transaction;
import com.likelion.moneylog.domain.transaction.entity.TransactionType;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import com.likelion.moneylog.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 본인 데이터 + 기간(월) 필터 + 페이징
    Page<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end, Pageable pageable);

    // 통계용: 기간 내 전체 조회(페이징 없이)
    List<Transaction> findByUserAndTransactionDateBetween(
            User user, LocalDate start, LocalDate end);

    // 단건 조회 시에도 소유자 검증을 함께
    Optional<Transaction> findByIdAndUser(Long id, User user);

    // 단건 조회(userId 기준) — TransactionService에서 사용
    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    // 목록 조회 — yearMonth/type/categoryId 전부 선택(nullable) + 페이징
    @Query("SELECT t FROM Transaction t "
            + "WHERE t.user.id = :userId "
            + "AND (:start IS NULL OR t.transactionDate >= :start) "
            + "AND (:end IS NULL OR t.transactionDate <= :end) "
            + "AND (:type IS NULL OR t.type = :type) "
            + "AND (:categoryId IS NULL OR t.category.id = :categoryId)")
    Page<Transaction> search(
            @Param("userId") Long userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("type") TransactionType type,
            @Param("categoryId") Long categoryId,
            Pageable pageable);
}