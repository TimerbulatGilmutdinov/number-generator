package ru.itis.numbergenerator.constant;

public enum Region {
    TATARSTAN(116, "RUS");

    private final int regionCode;
    private final String country;

    Region(int regionCode, String country) {
        this.regionCode = regionCode;
        this.country = country;
    }

    public String getCountry() {
        return this.country;
    }

    public int getRegionCode() {
        return this.regionCode;
    }
}
