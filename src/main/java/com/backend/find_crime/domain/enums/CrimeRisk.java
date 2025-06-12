package com.backend.find_crime.domain.enums;

import java.util.Arrays;

public enum CrimeRisk {
    SAFE("안전", 0, 50),
    NORMAL("보통", 50, 100),
    DANGEROUS("위험", 100, Integer.MAX_VALUE);

    private final String label;
    private final int min;
    private final int max;

    CrimeRisk(String label, int min, int max) {
        this.label = label;
        this.min = min;
        this.max = max;
    }

    public String getLabel() {
        return label;
    }

    public static CrimeRisk fromCrimeCount(int count) {
        return Arrays.stream(values())
                .filter(risk -> count >= risk.min && count < risk.max)
                .findFirst()
                .orElse(DANGEROUS); // fallback
    }
}
