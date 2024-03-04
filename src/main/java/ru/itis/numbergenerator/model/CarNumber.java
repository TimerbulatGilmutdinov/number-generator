package ru.itis.numbergenerator.model;

import ru.itis.numbergenerator.constant.Region;

import java.util.Arrays;
import java.util.Objects;

public record CarNumber(char[] series, int registerNumber, Region region) {

    @Override
    public String toString() {
        return series[0] + getValidStringNumber() + series[1] + series[2] + " " + region.getRegionCode() + " " + region.getCountry();
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(series), registerNumber,region);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        CarNumber carNumber = (CarNumber) obj;

        if (registerNumber != carNumber.registerNumber) return false;
        if (!Arrays.equals(series, carNumber.series)) return false;
        return region.equals(carNumber.region);
    }

    private String getValidStringNumber() {
        if (registerNumber < 10) {
            return "00" + registerNumber;
        }
        if (registerNumber < 100) {
            return "0" + registerNumber;
        }
        return String.valueOf(registerNumber);
    }
}
