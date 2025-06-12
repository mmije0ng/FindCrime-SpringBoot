package com.backend.find_crime.apiPayload.code.status;

import com.backend.find_crime.apiPayload.code.BaseErrorCode;
import com.backend.find_crime.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 범죄 관련
    CRIME_NOT_FOUND(HttpStatus.BAD_REQUEST, "CRIME4001", "아이디와 일치하는 범죄가 없습니다."),
    CRIME_NOT_FOUND_BY_TYPE(HttpStatus.BAD_REQUEST, "CRIME4002", "범죄 대분류&중분류와 일치하는 범죄가 없습니다."),

    // 지역 관련
    AREA_NOT_FOUND(HttpStatus.BAD_REQUEST, "AREA4001", "아이디와 일치하는 지역이 없습니다."),
    AREA_NOT_FOUND_BY_TYPE(HttpStatus.BAD_REQUEST, "AREA4002", "지역 이름&디테일과 일치하는 지역이 없습니다."),

    // 범죄 지역 관련
    CRIME_AREA_NOT_FOUND(HttpStatus.BAD_REQUEST, "CRIME_AREA4001", "아이디와 일치하는 범죄지역이 없습니다."),
    CRIME_AREA_NOT_FOUND_BY_CRIME_AND_AREA(HttpStatus.BAD_REQUEST, "CRIME_AREA4001", "범죄&지역과 일치하는 범죄지역이 없습니다."),

    // 통계 관련
    STATISTIC_NOT_FOUND(HttpStatus.BAD_REQUEST, "STATISTIC4001","아이디와 일치하는 통계가 없습니다."),
    STATISTIC_NOT_FOUND_BY_CRIME_AREA_AND_YEAR(HttpStatus.BAD_REQUEST, "STATISTIC4002","범죄지역&연도와 일치하는 통계가 없습니다."),

    // 유저 관련
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "아이디와 일치하는 사용자가 없습니다."),

    // 게시글 관련
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, "POST4001", "아이디와 일치하는 제보 게시글이 없습니다.");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build();
    }
}