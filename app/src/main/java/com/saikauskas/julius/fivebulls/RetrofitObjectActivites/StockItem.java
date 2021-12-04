package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;
import java.util.Map;

public class StockItem {

    //for all raw values
    @Expose
    @SerializedName("raw")
    private double raw;

    //Price targets

    @Expose
    @SerializedName("regularMarketPrice")
    private StockItem regularMarketPrice;

    @Expose
    @SerializedName("targetHighPrice")
    private StockItem targetHighPrice;

    @Expose
    @SerializedName("targetLowPrice")
    private StockItem targetLowPrice;

    @Expose
    @SerializedName("targetMedianPrice")
    private StockItem targetMedianPrice;

    @Expose
    @SerializedName("recommendationKey")
    private String recommendationKey;



    //Frontend and pie chart

    @Expose
    @SerializedName("sector")
    private String sector;

    @Expose
    @SerializedName("shortName")
    private String shortName;

    @Expose
    @SerializedName("symbol")
    private String symbol;

    int stockRating;

    //Rating Calculation

    @Expose
    @SerializedName("trailingPE")
    private StockItem trailingPE;

    @Expose
    @SerializedName("dividendYield")
    private StockItem dividendYield;

    @Expose
    @SerializedName("fiveYearAvgDividendYield")
    private StockItem fiveYearDivYield;

    @Expose
    @SerializedName("payoutRatio")
    private StockItem payoutRatio;

    @Expose
    @SerializedName("quickRatio")
    private StockItem quickRatio;

    @Expose
    @SerializedName("earningsGrowth")
    private StockItem earningsGrowth;

    @Expose
    @SerializedName("pegRatio")
    private StockItem pegRatio;

    @Expose
    @SerializedName("trend")
    private ArrayList<EpsArrayListsObj> trend;

    @Expose
    @SerializedName("growth")
    private StockItem growth;


    //For Intrinsic Value
    @Expose
    @SerializedName("beta")
    private StockItem beta;


    @Expose
    @SerializedName("marketCap")
    private StockIntrinsicValues marketCap;

    @Expose
    @SerializedName("sharesOutstanding")
    private StockIntrinsicValues sharesOutstanding;



    // for calculation backend
    double peRatio, currentDiv, fiveYearDiv, doublePayoutRatio, doubleEarningsGrowth, doubleQuickRatio, doublePegRatio, doubleEpsActual, doubleEpsGrowth;
    int PeRating, DivRating, PayoutRating, EarningsRating, QuickRating, PegRating, EpsRating;
    long stockMarketCap, stockSharesOut;
    double stockBeta;

    Map<String, Double> targetPrices;


    public StockItem(String shortName, String symbol, double rawMarketPrice, int stockRating,
                     double peRatio, double dividendYield, double fiveYearDiv, double payoutRatio, double earningGrowth, double quickRatio,
                     double pegRatio, double epsActual, double epsGrowth, int PeRating, int DivRating, int PayoutRating, int EarningsRating,
                     int QuickRating, int PegRating, int EpsRating, String sector, Map<String, Double> targetPrices, String recommendationKey,
                     long stockMarketCap, long stockSharesOut, double stockBeta) {

        //frontend showing
        this.shortName = shortName;
        this.symbol = symbol;
        this.raw = rawMarketPrice;
        this.stockRating = stockRating;

        //backend calculation and for chart data
        this.peRatio = peRatio;
        this.currentDiv = dividendYield;
        this.fiveYearDiv = fiveYearDiv;
        this.doublePayoutRatio = payoutRatio;
        this.doubleEarningsGrowth = earningGrowth;
        this.doubleQuickRatio = quickRatio;
        this.doublePegRatio = pegRatio;
        this.doubleEpsActual = epsActual;
        this.doubleEpsGrowth = epsGrowth;

        this.targetPrices = targetPrices;
        this.recommendationKey = recommendationKey;

        //for chart activity ratings
        this.PeRating = PeRating;
        this.DivRating = DivRating;
        this.PayoutRating = PayoutRating;
        this.EarningsRating = EarningsRating;
        this.QuickRating = QuickRating;
        this.PegRating = PegRating;
        this.EpsRating = EpsRating;
        this.sector = sector;

        //this.dataExpiryDate = dataExpiryDate;
        this.stockBeta = stockBeta;
        this.stockMarketCap = stockMarketCap;
        this.stockSharesOut = stockSharesOut;

    }


    //Pie Chart

    public String getSector() {
        return sector;
    }


    //For StockChartActivity rating values

    public int getPeRating() {
        return PeRating;
    }

    public int getDivRating() {
        return DivRating;
    }

    public int getPayoutRating() {
        return PayoutRating;
    }

    public int getEarningsRating() {
        return EarningsRating;
    }

    public int getQuickRating() {
        return QuickRating;
    }

    public int getPegRating() {
        return PegRating;
    }

    public int getEpsRating() {
        return EpsRating;
    }


    //For hidden calculation values to go to chart activity

    public ArrayList<EpsArrayListsObj> getTrend() {
        return trend;
    }

    public StockItem getGrowth() {
        return growth;
    }

    public double getRaw() {
        return raw;
    }

    public StockItem getTrailingPE() {
        return trailingPE;
    }

    public StockItem getDividendYield() {
        return dividendYield;
    }

    public StockItem getFiveYearDivYield() {
        return fiveYearDivYield;
    }

    public StockItem getPayoutRatio() {
        return payoutRatio;
    }

    public StockItem getRegularMarketPrice() {
        return regularMarketPrice;
    }

    public int getStockRating() {
        return stockRating;
    }

    public StockItem getEarningsGrowth() {
        return earningsGrowth;
    }

    public StockItem getQuickRatio() {
        return quickRatio;
    }

    public StockItem getPegRatio() {
        return pegRatio;
    }

    public String getShortName() {
        return shortName;
    }

    public String getSymbol() {
        return symbol;
    }

    //Intrinsic Value and chart values

    public StockItem getBeta() {
        return beta;
    }

    //loops because raw is long value(quicker than type deserilization)
    public StockIntrinsicValues getMarketCap() {
        return marketCap;
    }

    public StockIntrinsicValues getSharesOutstanding() {
        return sharesOutstanding;
    }

    //Price targets

    public StockItem getTargetHighPrice() {
        return targetHighPrice;
    }

    public StockItem getTargetLowPrice() {
        return targetLowPrice;
    }

    public StockItem getTargetMedianPrice() {
        return targetMedianPrice;
    }

    public String getRecommendationKey() {
        return recommendationKey;
    }

    public Map<String, Double> getTargetPrices() {
        return targetPrices;
    }

    //For backend calculation values
    public double getDoubleEpsActual() {
        return doubleEpsActual;
    }

    public double getDoubleEpsGrowth() {
        return doubleEpsGrowth;
    }

    public double getPeRatio() {
        return peRatio;
    }

    public double getCurrentDiv() {
        return currentDiv;
    }

    public double getFiveYearDiv() {
        return fiveYearDiv;
    }

    public double getDoublePayoutRatio() {
        return doublePayoutRatio;
    }

    public double getDoubleEarningsGrowth() {
        return doubleEarningsGrowth;
    }

    public double getDoubleQuickRatio() {
        return doubleQuickRatio;
    }

    public double getDoublePegRatio() {
        return doublePegRatio;
    }

    public double getStockBeta() {
        return stockBeta;
    }

    public long getStockMarketCap() {
        return stockMarketCap;
    }

    public long getStockSharesOut() {
        return stockSharesOut;
    }

}
