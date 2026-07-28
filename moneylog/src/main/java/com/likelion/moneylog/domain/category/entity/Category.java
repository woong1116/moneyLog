package com.likelion.moneylog.domain.category.entity;

import com.likelion.moneylog.domain.user.entity.User;
import com.likelion.moneylog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private CategoryType type;     // INCOME / EXPENSE

    @Builder
    public Category(User user, String name, CategoryType type) {
        this.user = user;
        this.name = name;
        this.type = type;
    }

    // 시드 등 간단 생성용 정적 팩토리
    public static Category of(User user, String name, CategoryType type) {
        return new Category(user, name, type);
    }

    // 수정은 Setter가 아닌 의도가 드러나는 메서드로
    public void update(String name, CategoryType type) {
        this.name = name;
        this.type = type;
    }
}