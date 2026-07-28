package com.likelion.moneylog.domain.category.controller;

import com.likelion.moneylog.domain.category.dto.CategoryRequest;
import com.likelion.moneylog.domain.category.dto.CategoryResponse;
import com.likelion.moneylog.domain.category.service.CategoryService;
import com.likelion.moneylog.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @AuthenticationPrincipal Long userId, @Valid @RequestBody CategoryRequest req) {
        CategoryResponse created = categoryService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("카테고리 등록에 성공했습니다.", created));
    }

    // 목록 조회 — 본인 카테고리 전체
    @GetMapping
    public ApiResponse<List<CategoryResponse>> list(@AuthenticationPrincipal Long userId) {
        return ApiResponse.success("카테고리 목록 조회에 성공했습니다.", categoryService.getList(userId));
    }

    // 단건 조회: 남의 것이면 403(FORBIDDEN)
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> get(@AuthenticationPrincipal Long userId,
                                              @PathVariable Long id) {
        return ApiResponse.success("카테고리 조회에 성공했습니다.", categoryService.get(userId, id));
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@AuthenticationPrincipal Long userId,
                                                 @PathVariable Long id,
                                                 @Valid @RequestBody CategoryRequest req) {
        return ApiResponse.success("카테고리 수정에 성공했습니다.",
                categoryService.update(userId, id, req));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        categoryService.delete(userId, id);
        return ApiResponse.success("카테고리 삭제에 성공했습니다.", null);
    }
}
