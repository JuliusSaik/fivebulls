package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;

public class QuoteSummary {
    @Expose
    @SerializedName("result")
    private ArrayList<StockDetailsResult> detailsResults;

    public ArrayList<StockDetailsResult> getResult() {
        return detailsResults;
    }


}
