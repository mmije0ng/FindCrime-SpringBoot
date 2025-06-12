package com.backend.find_crime.repository.CrimeAreaRepository;

import com.backend.find_crime.domain.QArea;
import com.backend.find_crime.domain.QCrime;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.domain.mapping.QCrimeArea;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class CrimeAreaRepositoryImpl implements CrimeAreaRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    QCrimeArea crimeArea = QCrimeArea.crimeArea;
    QCrime crime = QCrime.crime;
    QArea area = QArea.area;

    @Override
    public Optional<CrimeArea> findWithCrimeAndArea(String crimeType, String crimeDetailType, String areaName, String areaDetailName) {
        return Optional.ofNullable(
                queryFactory.selectFrom(crimeArea)
                        .join(crimeArea.crime, crime).fetchJoin()
                        .join(crimeArea.area, area).fetchJoin()
                        .where(
                                crime.crimeType.eq(crimeType),
                                crime.crimeDetailType.eq(crimeDetailType),
                                area.areaName.eq(areaName),
                                area.areaDetailName.eq(areaDetailName)
                        )
                        .fetchOne()
        );
    }
}
