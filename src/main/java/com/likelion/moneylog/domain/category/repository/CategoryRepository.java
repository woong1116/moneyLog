package com.likelion.moneylog.domain.category.repository;

import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUser(User user);// 내 카테고리 목록

    // TransactionService에서 카테고리 소유자 검증에 사용
    Optional<Category> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserId(Long userId);
}