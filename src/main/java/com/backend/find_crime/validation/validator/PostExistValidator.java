package com.backend.find_crime.validation.validator;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.service.PostService.PostQueryService;
import com.backend.find_crime.validation.annotation.ExistPost;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 검증 대상은 Long
@Slf4j
@RequiredArgsConstructor
@Component
public class PostExistValidator implements ConstraintValidator<ExistPost,Long> {

    private final PostQueryService postQueryService;

    @Override
    public void initialize(ExistPost constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        // 파라미터로 넘어온 게시글 아이디가 존재하는 아이디인지 검증
        boolean isValid = postQueryService.existsPostById(value);
        log.info("ExistPost storeId: {}, isValid: {}", value, isValid);

        if(!isValid){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.POST_NOT_FOUND.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
