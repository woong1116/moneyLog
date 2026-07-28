package com.likelion.moneylog.domain.user.repository;

import com.likelion.moneylog.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);   // 로그인 시 사용
    boolean existsByEmail(String email);         // 회원가입 중복 체크
}