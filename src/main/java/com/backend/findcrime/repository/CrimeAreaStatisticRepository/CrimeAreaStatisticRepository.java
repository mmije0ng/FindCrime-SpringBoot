package com.backend.findcrime.repository.CrimeAreaStatisticRepository;

import com.backend.findcrime.domain.CrimeAreaStatistic;
import com.backend.findcrime.domain.mapping.CrimeArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CrimeAreaStatisticRepository extends JpaRepository<CrimeAreaStatistic, Long> {
    Optional<CrimeAreaStatistic> findByCrimeAreaAndCrimeYear(CrimeArea crimeArea, int crimeYear);

    List<CrimeAreaStatistic> findAllByCrimeYear(int crimeYear);
}
