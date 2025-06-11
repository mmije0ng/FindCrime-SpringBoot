package com.backend.findcrime.repository.CrimeAreaRepository;

import com.backend.findcrime.domain.Area;
import com.backend.findcrime.domain.Crime;
import com.backend.findcrime.domain.mapping.CrimeArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrimeAreaRepository extends JpaRepository<CrimeArea, Long> {
    boolean existsByCrimeAndArea(Crime crime, Area area);
    Optional<CrimeArea> findByCrimeAndArea(Crime crime, Area area);
}
