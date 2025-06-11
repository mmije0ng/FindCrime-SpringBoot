package com.backend.find_crime.domain.enums;

public enum CrimeDetailType {

    // 강력범죄 세부 유형
    MURDER("살인기수"),
    ATTEMPTED_MURDER("살인미수등"),
    ROBBERY("강도"),
    RAPE("강간"),
    QUASI_RAPE("유사강간"),
    FORCIBLE_MOLESTATION("강제추행"),
    ETC_SEXUAL_CRIME("기타 강간 강제추행등"),
    ARSON("방화"),

    // 절도
    THEFT("절도범죄"),

    // 폭력범죄 세부 유형
    ASSAULT("상해"),
    BATTERY("폭행"),
    UNLAWFUL_RESTRAINT("체포감금"),
    THREATS("협박"),
    ABDUCTION("약취 유인"),
    VIOLENT_ACTS("폭력행위등"),
    EXTORTION("공갈"),
    PROPERTY_DAMAGE("손괴"),

    // 지능범죄 세부 유형
    DERELICTION_OF_DUTY("직무유기"),
    ABUSE_OF_AUTHORITY("직권남용"),
    BRIBERY("증수뢰"),
    COUNTERFEIT_CURRENCY("통화"),
    FORGERY_DOCUMENT("문서 인장"),
    FORGERY_SECURITIES("유가증권인지"),
    FRAUD("사기"),
    EMBEZZLEMENT("횡령"),
    BREACH_OF_TRUST("배임"),

    // 풍속범죄
    SEXUAL_MORALITY_CRIME("성풍속범죄"),
    GAMBLING_CRIME("도박범죄"),

    ECONOMIC_SPECIAL_CRIME("특별경제범죄"),
    DRUG_CRIME("마약범죄"),
    HEALTH_CRIME("보건범죄"),
    ENVIRONMENT_CRIME("환경범죄"),
    TRAFFIC_CRIME("교통범죄"),
    LABOR_CRIME("노동범죄"),
    SECURITY_CRIME("안보범죄"),
    ELECTION_CRIME("선거범죄"),
    MILITARY_CRIME("병역범죄"),

    // 기타
    OTHER("기타범죄");

    private final String label;

    CrimeDetailType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static CrimeDetailType fromLabel(String label) {
        for (CrimeDetailType type : values()) {
            if (type.label.equals(label)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown label: " + label);
    }
}
