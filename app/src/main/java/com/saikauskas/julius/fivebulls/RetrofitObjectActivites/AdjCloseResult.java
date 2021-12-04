package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AdjCloseResult {

    @Expose
    @SerializedName("adjclose")
    private ArrayList<Double> adjclose;

    @Expose
    @SerializedName("close")
    private ArrayList<Double> quote;

    public ArrayList<Double> getQuote() {
        return quote;
    }

    public ArrayList<Double> getAdjclose() {
        return adjclose;
    }

}
