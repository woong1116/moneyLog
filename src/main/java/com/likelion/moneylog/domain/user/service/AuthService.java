package com.likelion.moneylog.domain.user.service;

import com.likelion.moneylog.domain.category.service.CategoryService;
import com.likelion.moneylog.domain.user.dto.LoginRequest;
import com.likelion.moneylog.domain.user.dto.SignupRequest;
import com.likelion.moneylog.domain.user.dto.SignupResponse;
import com.likelion.moneylog.domain.user.dto.TokenResponse;
import com.likelion.moneylog.domain.user.entity.User;
import com.likelion.moneylog.domain.user.repository.UserRepository;
import com.likelion.moneylog.global.exception.CustomException;
import com.likelion.moneylog.global.exception.ErrorCode;
import com.likelion.moneylog.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;   // 기본 카테고리 시드용 (SPEC 3장)
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입: 비밀번호는 반드시 인코딩해서 저장
    @Transactional
    public SignupResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL); // 409 → 전역 핸들러가 형식으로
        }
        User user = User.builder()
                .email(req.email())
                .password(passwordEncoder.encode(req.password())) // 평문 저장 금지!
                .nickname(req.nickname())
                .build();
        userRepository.save(user);

        // 회원가입 시 기본 카테고리 자동 생성 (식비/교통/주거/문화, 급여/용돈)
        categoryService.seedDefaultCategories(user.getId());

        return SignupResponse.from(user);
    }

    // 로그인: 검증 성공 시에만 토큰 발급
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        // 입력 비번과 저장된 해시를 비교 (평문 비교 아님!)
        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS); // 이메일/비번 구분 노출 안 함
        }

        String token = jwtProvider.createToken(user.getId(), user.getEmail());
        return TokenResponse.of(token, user.getNickname());
    }
}
