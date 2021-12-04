package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class ChartApiResponse {
    @Expose
    @SerializedName("chart")
    private ChartResponse chart;

    public ChartResponse getChart(){
        return chart;
    }


}
