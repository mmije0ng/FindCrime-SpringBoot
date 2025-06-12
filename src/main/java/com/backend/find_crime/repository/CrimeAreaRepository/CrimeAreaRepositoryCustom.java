package com.backend.find_crime.repository.CrimeAreaRepository;

import com.backend.find_crime.domain.mapping.CrimeArea;

import java.util.Optional;

public interface CrimeAreaRepositoryCustom {
    Optional<CrimeArea> findWithCrimeAndArea(String crimeType, String crimeDetailType, String areaName, String areaDetailName);
}
