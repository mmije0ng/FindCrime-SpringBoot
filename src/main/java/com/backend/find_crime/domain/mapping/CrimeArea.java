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
@Table(
        name = "crime_area",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_crime_area_crime_id_area_id",
                        columnNames = {"crime_id", "area_id"}
                )
        }
)
public class CrimeArea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crime_id", nullable = false)
    private Crime crime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;
}
