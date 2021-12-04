package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



import java.util.ArrayList;

public class StockChartResult {
    @Expose
    @SerializedName("timestamp")
    private ArrayList<Long> timestamp;

    @Expose
    @SerializedName("indicators")
    private StockChartResult indicators;


    //Different places but same object getters

    @Expose
    @SerializedName("adjclose")
    private ArrayList<AdjCloseResult> adjclose;

    @Expose
    @SerializedName("quote")
    private ArrayList<AdjCloseResult> quote;

    //For saving data of y and x values
    private ArrayList<Long> arrayListOfTime;

    private ArrayList<Long> arrayListOfPrice;





    public StockChartResult(ArrayList<Long> arrayListOfTime, ArrayList<Long> arrayListOfPrice){
        this.arrayListOfTime = arrayListOfTime;
        this.arrayListOfPrice = arrayListOfPrice;
    }

    public ArrayList<AdjCloseResult> getQuote() {
        return quote;
    }

    public ArrayList<AdjCloseResult> getAdjclose() {
        return adjclose;
    }

    public StockChartResult getIndicators() {
        return indicators;
    }

   public ArrayList<Long> getTimestamp() {
       return timestamp;

   }

    public ArrayList<Long> getArrayListOfPrice() {
        return arrayListOfPrice;
    }

    public ArrayList<Long> getArrayListOfTime() {
        return arrayListOfTime;
    }

}
