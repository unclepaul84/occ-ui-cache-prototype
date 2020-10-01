package com.unclepaul.uicacheprototype.entities;

public class EquityPriceDTO {

    public long EquityId;
    public String Symbol;
    public double Price;

    // Return hashCode as normal, ignoring the version field...
    @Override
    public int hashCode() {
        return (int)EquityId;
    }

    // Implement equals, to return true only if carIds are equal AND version fields are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EquityPriceDTO)) return false;
        EquityPriceDTO other = (EquityPriceDTO) o;
        return this.EquityId == other.EquityId ;
    }
}
