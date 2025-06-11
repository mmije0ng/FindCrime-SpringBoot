package com.backend.findcrime.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Crime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String crimeType; // 범죄 대분류

    @Column(nullable = false, length = 20)
    private String crimeDetailType; // 범죄 중분류
}
