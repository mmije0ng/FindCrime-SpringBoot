package com.backend.find_crime.domain.mapping;

import com.backend.find_crime.domain.Area;
import com.backend.find_crime.domain.Crime;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class CrimeArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crime_id")
    private Crime crime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;
}
