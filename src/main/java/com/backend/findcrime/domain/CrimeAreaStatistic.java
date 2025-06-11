package com.backend.findcrime.domain;

import com.backend.findcrime.domain.mapping.CrimeArea;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class CrimeAreaStatistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer crimeYear;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer crimeCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crime_area_id", unique = true)
    private CrimeArea crimeArea;
}
