package com.backend.findcrime.domain.enums;

public enum CrimeType {
    // 강력범죄
    VIOLENT_CRIME("강력범죄"),

    // 절도범죄
    THEFT_CRIME("절도범죄"),

    // 폭력범죄
    ASSAULT_CRIME("폭력범죄"),

    // 지능범죄
    INTELLIGENCE_CRIME("지능범죄"),

    // 풍속범죄
    MORALITY_CRIME("풍속범죄"),

    // 특별경제범죄
    ECONOMIC_CRIME("특별경제범죄"),

    // 마약범죄
    DRUG_CRIME("마약범죄"),

    // 보건범죄
    HEALTH_CRIME("보건범죄"),

    // 환경범죄
    ENVIRONMENT_CRIME("환경범죄"),

    // 교통범죄
    TRAFFIC_CRIME("교통범죄"),

    // 노동범죄
    LABOR_CRIME("노동범죄"),

    // 안보범죄
    SECURITY_CRIME("안보범죄"),

    // 선거범죄
    ELECTION_CRIME("선거범죄"),

    // 병역범죄
    MILITARY_SERVICE_CRIME("병역범죄"),

    // 기타범죄
    OTHER_CRIME("기타범죄");

    private final String label;

    CrimeType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // 문자열을 enum으로 변환
    public static CrimeType fromLabel(String label) {
        for (CrimeType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
