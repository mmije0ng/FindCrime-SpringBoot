package com.backend.find_crime.repository.CrimeAreaRepository;

import com.backend.find_crime.domain.Area;
import com.backend.find_crime.domain.Crime;
import com.backend.find_crime.domain.mapping.CrimeArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrimeAreaRepository extends JpaRepository<CrimeArea, Long> {
    boolean existsByCrimeAndArea(Crime crime, Area area);
    Optional<CrimeArea> findByCrimeAndArea(Crime crime, Area area);
}
