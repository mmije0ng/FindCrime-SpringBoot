package com.backend.find_crime.domain;

import com.backend.find_crime.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Area extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String areaName; // 구

    @Column(nullable = false, length = 20)
    private String areaDetailName; // 시/군
}
