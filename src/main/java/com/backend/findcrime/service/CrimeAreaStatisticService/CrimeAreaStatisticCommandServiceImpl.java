package com.backend.findcrime.service.CrimeAreaStatisticService;

import com.backend.findcrime.domain.Area;
import com.backend.findcrime.domain.Crime;
import com.backend.findcrime.domain.CrimeAreaStatistic;
import com.backend.findcrime.domain.mapping.CrimeArea;
import com.backend.findcrime.repository.AreaRepository.AreaRepository;
import com.backend.findcrime.repository.CrimeAreaRepository.CrimeAreaRepository;
import com.backend.findcrime.repository.CrimeAreaStatisticRepository.CrimeAreaStatisticRepository;
import com.backend.findcrime.repository.CrimeRepository.CrimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrimeAreaStatisticCommandServiceImpl implements CrimeAreaStatisticCommandService {

    private final CrimeAreaRepository crimeAreaRepository;
    private final CrimeRepository crimeRepository;
    private final AreaRepository areaRepository;
    private final CrimeAreaStatisticRepository statisticRepository;

    @Transactional
    public void processCsvDataForYear(int year) {
        try (InputStream is = new FileInputStream("data/경찰청_범죄 발생 지역별 통계_20231231.csv");
             InputStreamReader reader = new InputStreamReader(is, Charset.forName("MS949"))) {

            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .parse(reader);

            List<Area> allAreas = areaRepository.findAll();
            Map<String, List<Area>> areaGroupedByName = allAreas.stream()
                    .collect(Collectors.groupingBy(Area::getAreaName));

            parser.forEach(record -> {
                String crimeType = record.get("범죄대분류");
                String crimeDetailType = record.get("범죄중분류");

                Crime crime = crimeRepository.findByCrimeTypeAndCrimeDetailType(crimeType, crimeDetailType)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 범죄: " + crimeType + "/" + crimeDetailType));

                record.toMap().entrySet().stream()
                        .filter(entry -> !entry.getKey().equals("범죄대분류") && !entry.getKey().equals("범죄중분류"))
                        .forEach(entry -> processAreaRegion(entry.getKey().trim(), parseInt(entry.getValue()), crime, year, areaGroupedByName));

                saveNationalTotal(crime, year);
            });

        } catch (Exception e) {
            throw new RuntimeException("CSV 처리 중 오류", e);
        }
    }

    private void processAreaRegion(String region, int count, Crime crime, int year, Map<String, List<Area>> areaGroupedByName) {
        // 단일 지역
        areaRepository.findByAreaFullName(region).ifPresentOrElse(
                area -> saveStatistic(crime, area, year, count),
                () -> {
                    // 전체 지역 (예: "경기도전체")
                    if (region.endsWith("전체")) {
                        String areaName = region.replace("전체", "");
                        List<Area> subAreas = areaGroupedByName.getOrDefault(areaName, List.of());

                        int total = subAreas.stream()
                                .map(area -> crimeAreaRepository.findByCrimeAndArea(crime, area)
                                        .flatMap(ca -> statisticRepository.findByCrimeAreaAndCrimeYear(ca, year))
                                        .map(CrimeAreaStatistic::getCrimeCount)
                                        .orElse(0))
                                .mapToInt(Integer::intValue)
                                .sum();

                        areaRepository.findByAreaFullName(region)
                                .ifPresent(totalArea -> saveStatistic(crime, totalArea, year, total));
                    }
                }
        );
    }

    private void saveNationalTotal(Crime crime, int year) {
        areaRepository.findByAreaFullName("전국전체").ifPresent(nationalArea -> {
            int nationalTotal = statisticRepository
                    .findAllByCrimeYear(year).stream()
                    .filter(stat -> stat.getCrimeArea().getCrime().equals(crime))
                    .mapToInt(CrimeAreaStatistic::getCrimeCount)
                    .sum();

            saveStatistic(crime, nationalArea, year, nationalTotal);
        });
    }

    private void saveStatistic(Crime crime, Area area, int year, int count) {
        CrimeArea crimeArea = crimeAreaRepository.findByCrimeAndArea(crime, area)
                .orElseThrow(() -> new IllegalStateException("CrimeArea 없음: " + crime.getId() + "/" + area.getId()));

        statisticRepository.findByCrimeAreaAndCrimeYear(crimeArea, year).ifPresentOrElse(
                existing -> {
                    existing.setCrimeCount(count);
                    statisticRepository.save(existing);
                },
                () -> {
                    CrimeAreaStatistic stat = CrimeAreaStatistic.builder()
                            .crimeArea(crimeArea)
                            .crimeYear(year)
                            .crimeCount(count)
                            .build();
                    statisticRepository.save(stat);
                }
        );
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }
}
