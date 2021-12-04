package com.saikauskas.julius.fivebulls.RetrofitObjectActivites;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @Expose
    @SerializedName("quoteSummary")
    private QuoteSummary quoteSummary;

    public QuoteSummary getQuoteSummary() {
        return quoteSummary;
    }


}
