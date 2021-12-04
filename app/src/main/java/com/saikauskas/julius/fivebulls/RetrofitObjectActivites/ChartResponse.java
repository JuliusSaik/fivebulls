package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



import java.util.ArrayList;

public class ChartResponse {

  @Expose
    @SerializedName("result")
    private ArrayList<StockChartResult> chartResult;

    public ArrayList<StockChartResult> getChartResult(){
        return chartResult;
    }

}
