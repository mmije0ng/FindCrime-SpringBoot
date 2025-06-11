package com.backend.findcrime.repository.CrimeRepository;

import com.backend.findcrime.domain.Crime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrimeRepository extends JpaRepository<Crime, Long> {
    Optional<Crime> findByCrimeTypeAndCrimeDetailType(String crimeType, String crimeDetailType);
}
