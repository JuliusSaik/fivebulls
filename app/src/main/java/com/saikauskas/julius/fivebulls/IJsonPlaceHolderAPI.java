package com.saikauskas.julius.fivebulls;

import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.ApiResponse;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.ChartApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;



public interface IJsonPlaceHolderAPI {

    @GET("v11/finance/quoteSummary/{symbol}?modules=summaryDetail%2Cprice%2CfinancialData%2CdefaultKeyStatistics%2CearningsTrend%2CearningsHistory" +
            "%2CassetProfile")
    Call<ApiResponse> getStocks(
            //@Header("x-rapidapi-host") String host,
            @Header("x-api-key") String key,
            @Path("symbol") String ticker

    );

    @GET("v8/finance/chart/{symbol}")
    Call<ChartApiResponse> getCharts(
            //@Header("x-api-host") String host,
            @Header("x-api-key") String key,
            @Path("symbol") String ticker,
            @Query("interval") String interval,
            @Query("range") String range


    );

    @GET("v11/finance/quoteSummary/{symbol}?modules=%2CincomeStatementHistory%2CcashflowStatementHistory" + "%2CbalanceSheetHistory")
    Call<ApiResponse> getStatementHistories(
            @Header("x-api-key") String key,
            @Path("symbol") String ticker

    );



}
