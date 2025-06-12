package com.backend.find_crime.service.CrimeAreaStatisticService;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.converter.CrimeAreaStatisticConverter;
import com.backend.find_crime.domain.Area;
import com.backend.find_crime.domain.Crime;
import com.backend.find_crime.domain.CrimeAreaStatistic;
import com.backend.find_crime.domain.enums.CrimeRisk;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.dto.statistic.CrimeAreaStatisticRequest;
import com.backend.find_crime.dto.statistic.CrimeAreaStatisticResponse;
import com.backend.find_crime.repository.AreaRepository.AreaRepository;
import com.backend.find_crime.repository.CrimeAreaRepository.CrimeAreaRepository;
import com.backend.find_crime.repository.CrimeAreaStatisticRepository.CrimeAreaStatisticRepository;
import com.backend.find_crime.repository.CrimeRepository.CrimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrimeAreaStatisticQueryServiceImpl implements CrimeAreaStatisticQueryService {

    private final CrimeRepository crimeRepository;
    private final AreaRepository areaRepository;
    private final CrimeAreaRepository crimeAreaRepository;
    private final CrimeAreaStatisticRepository statisticRepository;

    // 카테고리(연도, 지역, 지역 디테일, 범죄대분류, 범죄소분류)별 통계 조회
    @Override
    public CrimeAreaStatisticResponse.StatisticResultDTO findStatisticByCategories(CrimeAreaStatisticRequest.StatisticRequestDTO requestDTO) {
        CrimeAreaStatistic statistic = statisticRepository.findStatisticWithJoins(
                requestDTO.getYear(),
                requestDTO.getAreaName(),
                requestDTO.getAreaDetailName(),
                requestDTO.getCrimeType(),
                requestDTO.getCrimeDetailType()
        ).orElseThrow(() -> new ErrorHandler(ErrorStatus.STATISTIC_NOT_FOUND_BY_CRIME_AREA_AND_YEAR));

        Integer crimeCount = statistic.getCrimeCount();
        String crimeRisk = CrimeRisk.fromCrimeCount(crimeCount).getLabel();

        log.info("카테고리(연도, 지역, 지역 디테일, 범죄대분류, 범죄소분류)별 통계 조회 완료");
        return CrimeAreaStatisticConverter.toStatisticResultDTO(crimeCount, crimeRisk);
    }

}
