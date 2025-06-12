package com.backend.find_crime.repository.CrimeAreaStatisticRepository;

import com.backend.find_crime.domain.CrimeAreaStatistic;
import com.backend.find_crime.domain.mapping.CrimeArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrimeAreaStatisticRepository extends JpaRepository<CrimeAreaStatistic, Long>, CrimeAreaStatisticRepositoryCustom {
    Optional<CrimeAreaStatistic> findByCrimeAreaAndCrimeYear(CrimeArea crimeArea, int crimeYear);

    List<CrimeAreaStatistic> findAllByCrimeYear(int crimeYear);
}
