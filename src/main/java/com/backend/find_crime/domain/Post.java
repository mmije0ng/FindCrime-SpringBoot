package com.backend.find_crime.domain;

import com.backend.find_crime.domain.mapping.CrimeArea;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String postTitle;

    @Column(nullable = false)
    private String postContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crime_area_id")
    private CrimeArea crimeArea;
}
