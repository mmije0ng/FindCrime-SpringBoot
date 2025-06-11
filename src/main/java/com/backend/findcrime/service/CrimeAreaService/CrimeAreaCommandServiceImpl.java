package com.backend.findcrime.service.CrimeAreaService;

import com.backend.findcrime.domain.Area;
import com.backend.findcrime.domain.Crime;
import com.backend.findcrime.domain.mapping.CrimeArea;
import com.backend.findcrime.repository.AreaRepository.AreaRepository;
import com.backend.findcrime.repository.CrimeAreaRepository.CrimeAreaRepository;
import com.backend.findcrime.repository.CrimeRepository.CrimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrimeAreaCommandServiceImpl implements CrimeAreaCommandService {

    private final CrimeAreaRepository crimeAreaRepository;
    private final CrimeRepository crimeRepository;
    private final AreaRepository areaRepository;

    @Transactional
    public void mapAllCrimesToAllAreas() {
        List<Crime> crimes = crimeRepository.findAll();
        List<Area> areas = areaRepository.findAll();

        List<CrimeArea> newMappings = new ArrayList<>();

        crimes.forEach(crime ->
                areas.stream()
                        .filter(area -> !crimeAreaRepository.existsByCrimeAndArea(crime, area))
                        .forEach(area -> {
                            newMappings.add(CrimeArea.builder()
                                    .crime(crime)
                                    .area(area)
                                    .build());
                        })
        );

        crimeAreaRepository.saveAll(newMappings);
        log.info("총 {}건의 범죄 - 지역 매핑 저장 완료", newMappings.size());
    }
}
