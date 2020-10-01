package com.unclepaul.uicacheprototype.entities;

public class StockColleteralViewDTO {

    public long StockColleteralId;
    public String Member; //GSEC
    public String StockSymbol; //stock symbol
    public long QtyPledged; //comes from RTC
    public long QtyBorrowed; // comes from SL
    public double Price; //comes from Pricing
    public double TotalNAV; //(QtyPledged + QtyBorrowed) * Price;

    // Return hashCode as normal, ignoring the version field...
    @Override
    public int hashCode() {
        return (int)StockColleteralId;
    }

    // Implement equals, to return true only if carIds are equal AND version fields are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockColleteralViewDTO)) return false;
        StockColleteralViewDTO other = (StockColleteralViewDTO) o;
        return this.StockColleteralId == other.StockColleteralId ;
    }
}
