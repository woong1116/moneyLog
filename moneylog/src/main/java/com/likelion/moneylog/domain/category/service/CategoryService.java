package com.likelion.moneylog.domain.category.service;

import com.likelion.moneylog.domain.category.dto.CategoryRequest;
import com.likelion.moneylog.domain.category.dto.CategoryResponse;
import com.likelion.moneylog.domain.category.entity.Category;
import com.likelion.moneylog.domain.category.entity.CategoryType;
import com.likelion.moneylog.domain.category.repository.CategoryRepository;
import com.likelion.moneylog.domain.user.repository.UserRepository;
import com.likelion.moneylog.global.exception.NotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // 등록
    @Transactional
    public CategoryResponse create(Long userId, CategoryRequest req) {
        Category category = Category.builder()
                .user(userRepository.getReferenceById(userId))
                .name(req.name())
                .type(req.type())
                .build();

        return CategoryResponse.from(categoryRepository.save(category));
    }

    // 목록 조회 — 본인 카테고리 전체
    @Transactional(readOnly = true)
    public List<CategoryResponse> getList(Long userId) {
        return categoryRepository.findByUser(userRepository.getReferenceById(userId)).stream()
                .map(CategoryResponse::from)
                .toList();
    }

    // 단건 조회
    @Transactional(readOnly = true)
    public CategoryResponse get(Long userId, Long id) {
        return CategoryResponse.from(findOwned(userId, id));
    }

    // 수정
    @Transactional
    public CategoryResponse update(Long userId, Long id, CategoryRequest req) {
        Category category = findOwned(userId, id);
        // 엔티티의 도메인 메서드로 상태 변경(더티 체킹으로 자동 반영)
        category.update(req.name(), req.type());
        return CategoryResponse.from(category);
    }

    // 삭제
    @Transactional
    public void delete(Long userId, Long id) {
        categoryRepository.delete(findOwned(userId, id));
    }

    // 본인 소유 카테고리만 조회하는 공통 헬퍼(인가의 핵심) — SPEC 표준 에러 코드 CATEGORY_NOT_FOUND
    private Category findOwned(Long userId, Long id) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NotFoundException(
                        "CATEGORY_NOT_FOUND", "존재하지 않는 카테고리입니다: " + id));
    }

    @Transactional
    public void seedDefaultCategories(Long userId) {
        if (categoryRepository.existsByUserId(userId)) return;   // 중복 시드 방지

        var user = userRepository.getReferenceById(userId);
        List<Category> defaults = List.of(
                Category.of(user, "식비", CategoryType.EXPENSE),
                Category.of(user, "교통", CategoryType.EXPENSE),
                Category.of(user, "주거", CategoryType.EXPENSE),
                Category.of(user, "문화", CategoryType.EXPENSE),
                Category.of(user, "급여", CategoryType.INCOME),
                Category.of(user, "용돈", CategoryType.INCOME)
        );
        categoryRepository.saveAll(defaults);
    }
}
