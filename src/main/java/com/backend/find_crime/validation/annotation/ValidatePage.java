package com.backend.find_crime.validation.annotation;


import jakarta.validation.Payload;

import java.lang.annotation.*;

// 페이지 번호 체크 어노테이션
@Documented // 사용자 정의 어노테이션
@Target({ElementType.PARAMETER, ElementType.FIELD}) // 어노테이션 적용 범위
@Retention(RetentionPolicy.RUNTIME) // 실행중에만
public @interface ValidatePage {
    String message() default "페이지 번호는 1 이상이어야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
