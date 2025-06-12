package com.backend.find_crime.validation.annotation;

import com.backend.find_crime.validation.validator.PostExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

// 가게 존재 검증
@Documented
@Constraint(validatedBy = PostExistValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistPost {
    String message() default "아이디와 일치하는 제보 게시글이 없습니다."; // 기본 에러 메시지

    Class<?>[] groups() default {}; // 유효성 검사 그룹

    Class<? extends Payload>[] payload() default {}; // 메타데이터 전달용
}
