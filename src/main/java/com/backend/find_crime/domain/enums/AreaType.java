package com.backend.find_crime.domain.enums;

public enum AreaType {
    GANGWON("강원"),
    GYEONGGI("경기"),
    GYEONGNAM("경남"),
    GYEONGBUK("경북"),
    GWANGJU("광주"),
    DAEGU("대구"),
    DAEJEON("대전"),
    BUSAN("부산"),
    SEOUL("서울"),
    SEJONG("세종"),
    ULSAN("울산"),
    INCHEON("인천"),
    JEONNAM("전남"),
    JEONBUK("전북"),
    JEJU("제주"),
    CHUNGNAM("충남"),
    CHUNGBUK("충북");

    private final String label;

    AreaType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
