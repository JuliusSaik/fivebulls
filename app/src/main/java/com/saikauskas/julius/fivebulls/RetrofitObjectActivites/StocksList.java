package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;

public class StocksList {

    private String symbol, fullName, sector, industry;

    public StocksList(String symbol, String fullName, String sector, String industry){
        this.symbol = symbol;
        this.fullName = fullName;
        this.sector = sector;
        this.industry = industry;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSector() {
        return sector;
    }

    public String getIndustry() {
        return industry;
    }
}
