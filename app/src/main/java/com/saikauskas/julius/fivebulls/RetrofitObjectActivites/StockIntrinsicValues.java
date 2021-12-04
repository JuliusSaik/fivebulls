package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

public class StockIntrinsicValues {

    //for all raw/fmt values
    @Expose
    @SerializedName("raw")
    private long raw;

    @Expose
    @SerializedName("fmt")
    private String fmt;


    //For history statements array
    @Expose
    @SerializedName("cashflowStatements")
    private ArrayList<StockIntrinsicValues> cashflowStatements;

    @Expose
    @SerializedName("incomeStatementHistory")
    private ArrayList<StockIntrinsicValues> incomeStatementHistories;

    @Expose
    @SerializedName("balanceSheetStatements")
    private ArrayList<StockIntrinsicValues> balanceSheetStatements;


    //For object values

    @Expose
    @SerializedName("endDate")
    private StockIntrinsicValues endDate;


    @Expose
    @SerializedName("totalCashFromOperatingActivities")
    private StockIntrinsicValues totalCashFromOperatingActivities;

    @Expose
    @SerializedName("capitalExpenditures")
    private StockIntrinsicValues capitalExpenditures;


    @Expose
    @SerializedName("totalRevenue")
    private StockIntrinsicValues totalRevenue;

    @Expose
    @SerializedName("netIncome")
    private StockIntrinsicValues netIncome;

    @Expose
    @SerializedName("interestExpense")
    private StockIntrinsicValues interestExpense;

    @Expose
    @SerializedName("incomeBeforeTax")
    private StockIntrinsicValues incomeBeforeTax;

    @Expose
    @SerializedName("incomeTaxExpense")
    private StockIntrinsicValues incomeTaxExpense;


    @Expose
    @SerializedName("shortLongTermDebt")
    private StockIntrinsicValues shortTermDebt;

    @Expose
    @SerializedName("longTermDebt")
    private StockIntrinsicValues longTermDebt;

    @Expose
    @SerializedName("marketCap")
    private StockIntrinsicValues marketCap;

    @Expose
    @SerializedName("sharesOutstanding")
    private StockIntrinsicValues sharesOutstanding;


    private long rawShortTermDebt, rawLongTermDebt, rawInterestExpense, rawIncomeBeforeTax, rawTaxExpense;
    private Map<String, Long> freeCashFlowData, revenueData, netIncomeData;
    public StockIntrinsicValues(Map<String, Long> freeCashFlowData, Map<String, Long> revenueData, Map<String, Long> netIncomeData, long shortTermDebt, long longTermDebt,
                                long interestExpense, long incomeBeforeTax, long taxExpense){


        this.rawShortTermDebt = shortTermDebt;
        this.rawLongTermDebt = longTermDebt;
        this.rawInterestExpense = interestExpense;
        this.rawIncomeBeforeTax = incomeBeforeTax;
        this.rawTaxExpense = taxExpense;

        this.freeCashFlowData = freeCashFlowData;
        this.revenueData = revenueData;
        this.netIncomeData = netIncomeData;

    }

    //For raw long/map saving values


    public long getRawShortTermDebt() {
        return rawShortTermDebt;
    }

    public long getRawLongTermDebt() {
        return rawLongTermDebt;
    }

    public long getRawInterestExpense() {
        return rawInterestExpense;
    }

    public long getRawIncomeBeforeTax() {
        return rawIncomeBeforeTax;
    }

    public long getRawTaxExpense() {
        return rawTaxExpense;
    }

    public Map<String, Long> getFreeCashFlowData() {
        return freeCashFlowData;
    }

    public Map<String, Long> getRevenueData() {
        return revenueData;
    }

    public Map<String, Long> getNetIncomeData() {
        return netIncomeData;
    }

    //for general values
    public long getRaw() {
        return raw;
    }

    public String getFmt() {
        return fmt;
    }


    //for object values
    public StockIntrinsicValues getEndDate() {
        return endDate;
    }

    public StockIntrinsicValues getTotalCashFromOperatingActivities() {
        return totalCashFromOperatingActivities;
    }

    public StockIntrinsicValues getCapitalExpenditures() {
        return capitalExpenditures;
    }

    public StockIntrinsicValues getTotalRevenue() {
        return totalRevenue;
    }

    public StockIntrinsicValues getNetIncome() {
        return netIncome;
    }

    public StockIntrinsicValues getInterestExpense() {
        return interestExpense;
    }

    public StockIntrinsicValues getIncomeBeforeTax() {
        return incomeBeforeTax;
    }

    public StockIntrinsicValues getIncomeTaxExpense() {
        return incomeTaxExpense;
    }

    public StockIntrinsicValues getShortTermDebt() {
        return shortTermDebt;
    }

    public StockIntrinsicValues getLongTermDebt() {
        return longTermDebt;
    }

    //for array list objects
    public ArrayList<StockIntrinsicValues> getCashflowStatements() {
        return cashflowStatements;
    }

    public ArrayList<StockIntrinsicValues> getIncomeStatementHistories() {
        return incomeStatementHistories;
    }

    public ArrayList<StockIntrinsicValues> getBalanceSheetStatements() {
        return balanceSheetStatements;
    }

    public StockIntrinsicValues getMarketCap() {
        return marketCap;
    }

    public StockIntrinsicValues getSharesOutstanding() {
        return sharesOutstanding;
    }
}
