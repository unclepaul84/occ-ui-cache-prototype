package com.unclepaul.uicacheprototype.entities;

public class StockLoanDTO {
    public long StockLoanId;
    public String StockSymbol;
    public String Lender;
    public String Borrower;
    public int LoanQty;

    // Return hashCode as normal, ignoring the version field...
    @Override
    public int hashCode() {
        return (int)StockLoanId;
    }

    // Implement equals, to return true only if carIds are equal AND version fields are equal
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockLoanDTO)) return false;
        StockLoanDTO other = (StockLoanDTO) o;
        return this.StockLoanId == other.StockLoanId;
    }
}
