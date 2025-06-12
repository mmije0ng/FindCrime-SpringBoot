package com.backend.find_crime.validation.validator;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.validation.annotation.ValidatePage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@Component
public class PageValidator implements HandlerMethodArgumentResolver {

    // resolver 실행 여부 검증
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @ValidatePage Integer 타입 파라미터만 처리
        boolean hasCheckPageAnnotation = parameter.hasParameterAnnotation(ValidatePage.class);
        boolean isIntegerType = parameter.getParameterType().equals(Integer.class);

        log.info("supportsParameter 호출 - @ValidatePage: {}, 타입: {}",
                hasCheckPageAnnotation, isIntegerType);

        return hasCheckPageAnnotation && isIntegerType;
    }

    // 새로운 값 바인딩
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String pageParam = webRequest.getParameter("page");
        log.info("받은 page 파라미터: {}", pageParam);

        if (pageParam == null || pageParam.isEmpty())
            throw new ErrorHandler(ErrorStatus.PAGE_NOT_FOUND); // null 또는 비어있는 경우 에러

        int page = Integer.parseInt(pageParam);
        if (page < 1)
            throw new ErrorHandler(ErrorStatus.INVALID_PAGE_NUMBER); // 1보다 작은 경우 에러

        log.info("바인딩 된 page: {}", page-1);

        return page - 1;
    }
}
