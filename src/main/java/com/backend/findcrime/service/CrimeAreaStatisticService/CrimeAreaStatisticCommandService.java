package com.backend.findcrime.service.CrimeAreaStatisticService;

public interface CrimeAreaStatisticCommandService {

    // csv 파일을 기반으로 범죄 통계 데이터 저장
    void processCsvDataForYear(int year);
}
