package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


import java.util.ArrayList;

public class EpsArrayListsObj {

    @Expose
    @SerializedName("trend")
    private ArrayList<EpsArrayListsObj> trend;

    public ArrayList<EpsArrayListsObj> getTrend() {
        return trend;
    }

    @Expose
    @SerializedName("history")
    private ArrayList<EpsArrayListsObj> history;

    public ArrayList<EpsArrayListsObj> getHistory() {
        return history;
    }

    @Expose
    @SerializedName("epsActual")
    private StockItem epsActual;

    public StockItem getEpsActual() {
        return epsActual;
    }

    @Expose
    @SerializedName("growth")
    private StockItem growth;

    public StockItem getGrowth() {
        return growth;
    }


}
