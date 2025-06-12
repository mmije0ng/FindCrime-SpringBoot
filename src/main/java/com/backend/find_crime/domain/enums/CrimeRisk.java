package com.backend.find_crime.domain.enums;

public enum CrimeRisk {
    DANGEROUS("위험"),
    NORMAL("보통"),
    SAFE("안전");

    private final String label;

    CrimeRisk(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
