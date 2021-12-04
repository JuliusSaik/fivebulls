package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class StockDetailsResult {


    //Eps Array
    @Expose
    @SerializedName("earningsTrend")
    private EpsArrayListsObj earningsTrend;

    @Expose
    @SerializedName("earningsHistory")
    private EpsArrayListsObj earningsHistory;

    //StockItem for rating calculation
    @Expose
    @SerializedName("assetProfile")
    private StockItem assetProfile;

    @Expose
    @SerializedName("summaryDetail")
    private StockItem summaryDetail;

    @Expose
    @SerializedName("price")
    private StockItem price;

    @Expose
    @SerializedName("financialData")
    private StockItem financialData;

    @Expose
    @SerializedName("defaultKeyStatistics")
    private StockItem defaultKeyStatistics;


    //StockIntrinsiValues for Intrinsic value activity
    @Expose
    @SerializedName("cashflowStatementHistory")
    private StockIntrinsicValues cashflowStatementHistory;

    @Expose
    @SerializedName("incomeStatementHistory")
    private StockIntrinsicValues incomeStatementHistory;

    @Expose
    @SerializedName("balanceSheetHistory")
    private StockIntrinsicValues balanceSheetHistory;


    //Eps
    public EpsArrayListsObj getEarningsTrend() {
        return earningsTrend;
    }

    public EpsArrayListsObj getEarningsHistory() {
        return earningsHistory;
    }

    //StockItem
    public StockItem getAssetProfile() {
        return assetProfile;
    }

    public StockItem getSummaryDetail() {
        return summaryDetail;
    }

    public StockItem getPrice() {
        return price;
    }

    public StockItem getFinancialData() {
        return financialData;
    }

    public StockItem getDefaultKeyStatistics() {
        return defaultKeyStatistics;
    }


    //StockIntrinsicValue
    public StockIntrinsicValues getCashflowStatementHistory() {
        return cashflowStatementHistory;
    }

    public StockIntrinsicValues getIncomeStatementHistory() {
        return incomeStatementHistory;
    }

    public StockIntrinsicValues getBalanceSheetHistory() {
        return balanceSheetHistory;
    }
}
