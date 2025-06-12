package com.backend.find_crime.repository.CrimeAreaStatisticRepository;

import com.backend.find_crime.domain.CrimeAreaStatistic;
import com.backend.find_crime.domain.QArea;
import com.backend.find_crime.domain.QCrime;
import com.backend.find_crime.domain.QCrimeAreaStatistic;
import com.backend.find_crime.domain.mapping.QCrimeArea;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CrimeAreaStatisticRepositoryImpl implements CrimeAreaStatisticRepositoryCustom{
    private final JPAQueryFactory queryFactory;
    QCrimeAreaStatistic statistic = QCrimeAreaStatistic.crimeAreaStatistic;
    QCrimeArea crimeArea = QCrimeArea.crimeArea;
    QCrime crime = QCrime.crime;
    QArea area = QArea.area;

    // 카테고리(연도, 지역, 지역 디테일, 범죄대분류, 범죄소분류)별 통계 조회
    @Override
    public Optional<CrimeAreaStatistic> findStatisticWithJoins(int year, String areaName, String areaDetailName, String crimeType, String crimeDetailType) {
        return Optional.ofNullable(queryFactory
                .selectFrom(statistic)
                .join(statistic.crimeArea, crimeArea).fetchJoin()
                .join(crimeArea.crime, crime).fetchJoin()
                .join(crimeArea.area, area).fetchJoin()
                .where(
                        statistic.crimeYear.eq(year),
                        crime.crimeType.eq(crimeType),
                        crime.crimeDetailType.eq(crimeDetailType),
                        area.areaName.eq(areaName),
                        area.areaDetailName.eq(areaDetailName)
                )
                .fetchOne());
    }
}
