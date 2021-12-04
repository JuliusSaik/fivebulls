package com.saikauskas.julius.fivebulls;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.ApiResponse;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.QuoteSummary;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StockDetailsResult;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StockIntrinsicValues;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IntrinsicValueActivity extends AppCompatActivity {


    TextView tvStockSymbol;
    TextView tvLowValues, tvAverageValues, tvHighValues;

    TextView tvQuickIntrinsicVal, tvQuickIntrinsicMargin, tvQuickCurrentPrice;

    TextView tv2017FreeCashFlow, tv2018FreeCashFlow, tv2019FreeCashFlow, tv2020FreeCashFlow, tv2021FreeCashFlow, tv2022FreeCashFlow,
            tv2023FreeCashFlow, tv2024FreeCashFlow, tv2025FreeCashFlow, tvTerminalFreeCashFlow;

    //For dynamic DCF chart value years
    TextView tvYear0, tvYearMinus1, tvYearMinus2, tvYearMinus3, tvYearPlus1, tvYearPlus2, tvYearPlus3, tvYearPlus4, tvYearPlus5;
    TextView tv2021DiscountRate, tv2022DiscountRate, tv2023DiscountRate, tv2024DiscountRate,tv2025DiscountRate, tvTerminalDiscountRate;
    TextView tv2021PresentVal, tv2022PresentVal, tv2023PresentVal, tv2024PresentVal, tv2025PresentVal, tvTerminalPresentVal;


    TextView tvWACCReturnRate, tvPerpetualGrowthRate, tvTodaysValue, tvSharesOutstanding, tvIntrinsicValue, tvIntrinWithMargin, tvWACCError;

    FloatingActionButton fabBack;

    public static String PACKAGE_NAME;
    private static String KEY;
    //Testing Key
    //private static final String KEY = "8pCmwGLGRlaqvpG8geaZw5I8KiMRwBkG477pTNpN";

    //Goes from current year to 4 years in the past
    private long freeCashFlowYear0, freeCashFlowYearMinus1, freeCashFlowYearMinus2, freeCashFlowYearMinus3;
    private double freeCashFlowYearPlus1, freeCashFlowYearPlus2, freeCashFlowYearPlus3, freeCashFlowYearPlus4, freeCashFlowYearPlus5, freeCashFlowTerminal;
    Map<String, Long> freeCashflowData = new HashMap<>();


    private double discountedFCFYearPlus1, discountedFCFYearPlus2, discountedFCFYearPlus3, discountedFCFYearPlus4, discountedFCFYearPlus5, discountedFCFTerminal;


    private long revenueYear0, revenueYearMinus1, revenueYearMinus2, revenueYearMinus3;
    private double revenueYearPlus1, revenueYearPlus2, revenueYearPlus3, revenueYearPlus4, revenueYearPlus5; //double because of % increase
    Map<String, Long> revenueData = new HashMap<>();

    private long netIncomeYear0, netIncomeYearMinus1, netIncomeYearMinus2, netIncomeYearMinus3;
    private double netIncomeYearPlus1, netIncomeYearPlus2, netIncomeYearPlus3, netIncomeYearPlus4, netIncomeYearPlus5;
    Map<String, Long> netIncomeData = new HashMap<>();


    private long interestExpense;
    private long incomeBeforeTax;
    private long incomeTaxExpense;

    private double discountYearPlus1, discountYearPlus2, discountYearPlus3, discountYearPlus4, discountYearPlus5;

    private long shortTermDebt, longTermDebt, marketCap, sharesOut;
    private double stockBeta, stockPrice;
    private String valueHeight;

    double intrinsicValue, intrinsicValueWithMargin;
    double perpetualGrowthRate;


    DecimalFormat dfLargeNr = new DecimalFormat("#,###"); //for large numbers
    DecimalFormat dfPercentage = new DecimalFormat("0.0"); //for %

    private long strTotalDebt;
    private String strWeightOfDebt, strWeightOfEquity;
    private String strInterest, strTaxRate, strCostOfDebt, strCostOfEquity, strRiskFreeRate;

    TinyDB tinyDB;
    StockIntrinsicValues stockIntrinsicValues;

    LinearLayout WACCrateContainer;
    FrameLayout adContainer;
    AdView bannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intrinsic_value);

        PACKAGE_NAME = getApplicationContext().getPackageName();
        KEY = new Secrets().getuAnYVyWY(PACKAGE_NAME);

        tinyDB = new TinyDB(this);

        tvStockSymbol = findViewById(R.id.stockSymbolFr);

        //Buttons
        tvLowValues = findViewById(R.id.tvLowCalc);
        tvAverageValues = findViewById(R.id.tvAvarageCalc);
        tvHighValues = findViewById(R.id.tvHighCalc);
        fabBack = findViewById(R.id.fabBackIntrinsic);

        //Quick Values Top of the screen
        tvQuickIntrinsicVal = findViewById(R.id.tvQuickIntrinsicVal);
        tvQuickIntrinsicMargin = findViewById(R.id.tvQuickIntrinsicValMargin);
        tvQuickCurrentPrice = findViewById(R.id.tvQuickCurrentPrice);

        //Dynamic Years
        tvYear0 = findViewById(R.id.tv2020A);
        tvYearMinus1 = findViewById(R.id.tv2019A);
        tvYearMinus2 = findViewById(R.id.tv2018A);
        tvYearMinus3 = findViewById(R.id.tv2017A);

        tvYearPlus1 = findViewById(R.id.tv2021E);
        tvYearPlus2 = findViewById(R.id.tv2022E);
        tvYearPlus3 = findViewById(R.id.tv2023E);
        tvYearPlus4 = findViewById(R.id.tv2024E);
        tvYearPlus5 = findViewById(R.id.tv2025E);


        //Chart Values
        tv2017FreeCashFlow = findViewById(R.id.tv2017FreeCashFlow);
        tv2018FreeCashFlow = findViewById(R.id.tv2018FreeCashFlow);
        tv2019FreeCashFlow = findViewById(R.id.tv2019FreeCashFlow);
        tv2020FreeCashFlow = findViewById(R.id.tv2020FreeCashFlow);
        tv2021FreeCashFlow = findViewById(R.id.tv2021FreeCashFlow);
        tv2022FreeCashFlow = findViewById(R.id.tv2022FreeCashFlow);
        tv2023FreeCashFlow = findViewById(R.id.tv2023FreeCashFlow);
        tv2024FreeCashFlow = findViewById(R.id.tv2024FreeCashFlow);
        tv2025FreeCashFlow = findViewById(R.id.tv2025FreeCashFlow);
        tvTerminalFreeCashFlow = findViewById(R.id.tvTerminalFreeCashFlow);


        tv2021DiscountRate = findViewById(R.id.tv2021DiscR);
        tv2022DiscountRate = findViewById(R.id.tv2022DiscR);
        tv2023DiscountRate = findViewById(R.id.tv2023DiscR);
        tv2024DiscountRate = findViewById(R.id.tv2024DiscR);
        tv2025DiscountRate = findViewById(R.id.tv2025DiscR);
        tvTerminalDiscountRate = findViewById(R.id.tvTerimanlDiscR);

        tv2021PresentVal = findViewById(R.id.tv2021PresVal);
        tv2022PresentVal = findViewById(R.id.tv2022PresVal);
        tv2023PresentVal = findViewById(R.id.tv2023PresVal);
        tv2024PresentVal = findViewById(R.id.tv2024PresVal);
        tv2025PresentVal = findViewById(R.id.tv2025PresVal);
        tvTerminalPresentVal = findViewById(R.id.tvTerminalPresVal);


        //Intrinsic values
        tvWACCReturnRate = findViewById(R.id.tvWACCReturnRate);
        tvPerpetualGrowthRate = findViewById(R.id.tvPerpGrowthRate);
        tvTodaysValue = findViewById(R.id.tvTodaysValue);
        tvSharesOutstanding = findViewById(R.id.tvSharesOutstanding);
        tvIntrinsicValue = findViewById(R.id.tvIntrinsicValue);
        tvIntrinWithMargin = findViewById(R.id.tvWithMarginOfSafety);
        tvWACCError = findViewById(R.id.tvErrorWACC);

        Bundle DefaultData = getIntent().getExtras();

        String symbol = DefaultData.getString("StockSymbol");
        tvStockSymbol.setText(symbol);

        stockBeta = DefaultData.getDouble("beta");

        marketCap = DefaultData.getLong("marketCap");

        sharesOut = DefaultData.getLong("sharesOut");
        tvSharesOutstanding.setText(dfLargeNr.format(sharesOut));

        stockPrice = DefaultData.getDouble("StockPrice");
        tvQuickCurrentPrice.setText("$"+String.valueOf(stockPrice));


        if (tinyDB.getObject(symbol + "intrinsicData", StockIntrinsicValues.class) == null) {

            valueHeight = "Average";
            getAllData(symbol, this);

        } else {
            valueHeight = "Average";
            loadValues(symbol);


        }

        adContainer = findViewById(R.id.intrs_adContainer);
        bannerAd = findViewById(R.id.bannerAdIntrs);
        //check if user has premium or not
        if (!hasPremium()) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAd.loadAd(adRequest);


        } else {
            bannerAd.setVisibility(View.GONE);
        }


        tvLowValues.setOnClickListener(view -> {

            valueHeight = "Low";

            if (tinyDB.getObject(symbol + "intrinsicData", StockIntrinsicValues.class) == null) {
                getAllData(symbol, this);

            } else {
                loadValues(symbol);
            }

            tvLowValues.setTextColor(getResources().getColor(R.color.constantWhite));
            tvAverageValues.setTextColor(getResources().getColor(R.color.textColorPrimary));
            tvHighValues.setTextColor(getResources().getColor(R.color.textColorPrimary));


            tvLowValues.setBackground(getDrawable(R.drawable.iosselector_active1_ani));
            tvAverageValues.setBackground(getDrawable(R.drawable.iosselector_disabled3_ani));
            tvHighValues.setBackground(getDrawable(R.drawable.iosselector_disabled1_ani));

        });

        tvAverageValues.setOnClickListener(view -> {

            valueHeight = "Average";

            if (tinyDB.getObject(symbol + "intrinsicData", StockIntrinsicValues.class) == null) {
                getAllData(symbol, this);
            } else {
                loadValues(symbol);
            }


            tvLowValues.setTextColor(getResources().getColor(R.color.textColorPrimary));
            tvAverageValues.setTextColor(getResources().getColor(R.color.constantWhite));
            tvHighValues.setTextColor(getResources().getColor(R.color.textColorPrimary));


            tvLowValues.setBackground(getDrawable(R.drawable.iosselector_disabled2_ani));
            tvAverageValues.setBackground(getDrawable(R.drawable.iosselector_active3_ani));
            tvHighValues.setBackground(getDrawable(R.drawable.iosselector_disabled1_ani));

        });

        tvHighValues.setOnClickListener(view -> {

            valueHeight = "High";

            if (tinyDB.getObject(symbol + "intrinsicData", StockIntrinsicValues.class) == null) {
                getAllData(symbol, this);

            } else {
                loadValues(symbol);
            }
            tvLowValues.setTextColor(getResources().getColor(R.color.textColorPrimary));
            tvAverageValues.setTextColor(getResources().getColor(R.color.textColorPrimary));
            tvHighValues.setTextColor(getResources().getColor(R.color.constantWhite));


            tvLowValues.setBackground(getDrawable(R.drawable.iosselector_disabled2_ani));
            tvAverageValues.setBackground(getDrawable(R.drawable.iosselector_disabled3_ani));
            tvHighValues.setBackground(getDrawable(R.drawable.iosselector_active2_ani));

        });

        fabBack.setOnClickListener(view -> {
            onBackPressed();
        });


        WACCrateContainer = findViewById(R.id.WACCrateContainer);
        WACCrateContainer.setOnClickListener(view -> {

            FragmentTransaction ft = getFragmentManager().beginTransaction();

            Fragment prev = getFragmentManager().findFragmentByTag("DialogFragment");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);

            WACCFragment dialogFragment = WACCFragment.newInstance();

            Bundle WACCData = new Bundle();
            WACCData.putLong("MarketCap", marketCap);
            WACCData.putLong("TotalDebt", strTotalDebt);
            WACCData.putLong("SharesOut", sharesOut);
            WACCData.putString("DebtWeight", strWeightOfDebt);
            WACCData.putString("EquityWeight", strWeightOfEquity);

            WACCData.putString("RiskFreeRate", strRiskFreeRate);
            WACCData.putString("DebtInterest", strInterest);
            WACCData.putString("TaxRate", strTaxRate);
            WACCData.putString("DebtCost", strCostOfDebt);
            WACCData.putString("EquityCost", strCostOfEquity);

            dialogFragment.setArguments(WACCData);
            dialogFragment.show(ft, "DialogFragment");


        });


    }

    private void getAllData(String symbol, Context context) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://yfapi.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IJsonPlaceHolderAPI IJsonPlaceHolderAPI = retrofit.create(IJsonPlaceHolderAPI.class);

        Call<ApiResponse> call = IJsonPlaceHolderAPI.getStatementHistories(KEY, symbol);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Sorry! couldn't fetch that stock/or limit has been reached, please be patient :)", Toast.LENGTH_SHORT).show();
                } else {
                    ApiResponse list = response.body();
                    QuoteSummary quoteSummary = list.getQuoteSummary();
                    ArrayList<StockDetailsResult> results = quoteSummary.getResult();

                    if (results == null) {
                        Toast.makeText(context, "Data not found for the ticker symbol.", Toast.LENGTH_SHORT).show();
                    } else {

                        StockDetailsResult index0 = results.get(0);

                        //Cashflow statement history
                        StockIntrinsicValues cashflowHistoryResponse = index0.getCashflowStatementHistory();
                        ArrayList<StockIntrinsicValues> cashflowStatements = cashflowHistoryResponse.getCashflowStatements();

                        StockIntrinsicValues totalCashResp;
                        StockIntrinsicValues capitalExpResp;
                        StockIntrinsicValues endDate;

                                //Free Cash flow year 0
                            try {

                                StockIntrinsicValues year0Resp = cashflowStatements.get(0);

                                endDate = year0Resp.getEndDate();
                                String[] date0 = endDate.getFmt().split("-");

                                String year0 = date0[0];
                                tvYear0.setText(year0 + "A");

                                //Sets all future dates
                                setFutureDates(year0);

                                totalCashResp = year0Resp.getTotalCashFromOperatingActivities();
                                long totalCashYear0 = totalCashResp.getRaw();

                                capitalExpResp = year0Resp.getCapitalExpenditures();
                                long capitalExpedentruesYear0 = capitalExpResp.getRaw();

                                freeCashFlowYear0 = totalCashYear0 + capitalExpedentruesYear0; // add because loss values are negative and -(-) = +


                                //Free Cash flow year -1
                                StockIntrinsicValues yearMinus1Resp = cashflowStatements.get(1);

                                endDate = yearMinus1Resp.getEndDate();
                                String[] dateMinus1 = endDate.getFmt().split("-");

                                String yearMinus1 = dateMinus1[0];
                                tvYearMinus1.setText(yearMinus1 + "A");

                                totalCashResp = yearMinus1Resp.getTotalCashFromOperatingActivities();
                                long totalCashYearMinus1 = totalCashResp.getRaw();

                                capitalExpResp = yearMinus1Resp.getCapitalExpenditures();
                                long capitalExpedentruesYearMinus1 = capitalExpResp.getRaw();

                                freeCashFlowYearMinus1 = totalCashYearMinus1 + capitalExpedentruesYearMinus1;


                                //Free Cash flow year -2
                                StockIntrinsicValues yearMinus2Resp = cashflowStatements.get(2);

                                endDate = yearMinus2Resp.getEndDate();
                                String[] dateMinus2 = endDate.getFmt().split("-");

                                String yearMinus2 = dateMinus2[0];
                                tvYearMinus2.setText(yearMinus2 + "A");

                                totalCashResp = yearMinus2Resp.getTotalCashFromOperatingActivities();
                                long totalCashYearMinus2 = totalCashResp.getRaw();

                                capitalExpResp = yearMinus2Resp.getCapitalExpenditures();
                                long capitalExpedentruesYearMinus2 = capitalExpResp.getRaw();

                                freeCashFlowYearMinus2 = totalCashYearMinus2 + capitalExpedentruesYearMinus2;


                                //Free Cash flow year -3
                                StockIntrinsicValues yearMinus3Resp = cashflowStatements.get(3);

                                endDate = yearMinus3Resp.getEndDate();
                                String[] dateMinus3 = endDate.getFmt().split("-");

                                String yearMinus3 = dateMinus3[0];
                                tvYearMinus3.setText(yearMinus3 + "A");

                                totalCashResp = yearMinus3Resp.getTotalCashFromOperatingActivities();
                                long totalCashYearMinus3 = totalCashResp.getRaw();

                                capitalExpResp = yearMinus3Resp.getCapitalExpenditures();
                                long capitalExpedentruesYearMinus3 = capitalExpResp.getRaw();

                                freeCashFlowYearMinus3 = totalCashYearMinus3 + capitalExpedentruesYearMinus3;

                            } catch (Exception e) {
                                e.printStackTrace();
                                freeCashFlowYear0 = 0;
                                freeCashFlowYearMinus1 = 0;
                                freeCashFlowYearMinus2 = 0;
                                freeCashFlowYearMinus3 = 0;

                                tvYear0.setText("No Data");
                                tvYearMinus1.setText("No Data");
                                tvYearMinus2.setText("No Data");
                                tvYearMinus3.setText("No Data");

                            }


                        //Income Statement history
                        StockIntrinsicValues incomeHistoryResponse = index0.getIncomeStatementHistory();
                        ArrayList<StockIntrinsicValues> incomeStatements = incomeHistoryResponse.getIncomeStatementHistories();

                        StockIntrinsicValues totalRevenueResp;
                        StockIntrinsicValues netIncomeResp;
                        StockIntrinsicValues interestExpenseResp;
                        StockIntrinsicValues incomeBeforeTaxResp;
                        StockIntrinsicValues incomeTaxExpenseResp;

                            //year 0
                            try {

                                StockIntrinsicValues incomeYear0Resp = incomeStatements.get(0);

                                totalRevenueResp = incomeYear0Resp.getTotalRevenue();
                                revenueYear0 = totalRevenueResp.getRaw();

                                netIncomeResp = incomeYear0Resp.getNetIncome();
                                netIncomeYear0 = netIncomeResp.getRaw();

                                //only need current years tax data
                                interestExpenseResp = incomeYear0Resp.getInterestExpense();
                                interestExpense = interestExpenseResp.getRaw();

                                incomeBeforeTaxResp = incomeYear0Resp.getIncomeBeforeTax();
                                incomeBeforeTax = incomeBeforeTaxResp.getRaw();

                                incomeTaxExpenseResp = incomeYear0Resp.getIncomeTaxExpense();
                                incomeTaxExpense = incomeTaxExpenseResp.getRaw();


                                //year -1
                                StockIntrinsicValues incomeYearMinus1 = incomeStatements.get(1);

                                totalRevenueResp = incomeYearMinus1.getTotalRevenue();
                                revenueYearMinus1 = totalRevenueResp.getRaw();

                                netIncomeResp = incomeYearMinus1.getNetIncome();
                                netIncomeYearMinus1 = netIncomeResp.getRaw();


                                //year -2
                                StockIntrinsicValues incomeYearMinus2 = incomeStatements.get(2);

                                totalRevenueResp = incomeYearMinus2.getTotalRevenue();
                                revenueYearMinus2 = totalRevenueResp.getRaw();

                                netIncomeResp = incomeYearMinus2.getNetIncome();
                                netIncomeYearMinus2 = netIncomeResp.getRaw();


                                //year -3
                                StockIntrinsicValues incomeYearMinus3 = incomeStatements.get(3);

                                totalRevenueResp = incomeYearMinus3.getTotalRevenue();
                                revenueYearMinus3 = totalRevenueResp.getRaw();

                                netIncomeResp = incomeYearMinus3.getNetIncome();
                                netIncomeYearMinus3 = netIncomeResp.getRaw();

                            } catch (Exception e) {
                                e.printStackTrace();
                                revenueYear0 = 0;
                                revenueYearMinus1 = 0;
                                revenueYearMinus2 = 0;
                                revenueYearMinus3 = 0;

                                netIncomeYear0 = 0;
                                netIncomeYearMinus1 = 0;
                                netIncomeYearMinus2 = 0;
                                netIncomeYearMinus3 = 0;

                            }


                        //balance sheet for debt
                        StockIntrinsicValues balanceSheetResponse = index0.getBalanceSheetHistory();
                        ArrayList<StockIntrinsicValues> balanceSheetStatments = balanceSheetResponse.getBalanceSheetStatements();

                        StockIntrinsicValues shortTermDebtResp;
                        StockIntrinsicValues longTermDebtResp;

                            //year 0
                            try {

                                StockIntrinsicValues balanceSheetYear0 = balanceSheetStatments.get(0);

                                longTermDebtResp = balanceSheetYear0.getLongTermDebt();
                                longTermDebt = longTermDebtResp.getRaw();

                            } catch (Exception e) {
                                e.printStackTrace();
                                longTermDebt = 0;
                                Toast.makeText(IntrinsicValueActivity.this, "No data for this stock.", Toast.LENGTH_SHORT).show();
                            }

                            try {

                                StockIntrinsicValues balanceSheetYear0 = balanceSheetStatments.get(0);

                                shortTermDebtResp = balanceSheetYear0.getShortTermDebt();
                                shortTermDebt = shortTermDebtResp.getRaw();

                            } catch (Exception e) {
                                e.printStackTrace();
                                shortTermDebt = 0;

                            }


                        if (freeCashFlowYear0 == 0 && netIncomeYear0 == 0 && longTermDebt == 0){
                            Toast.makeText(IntrinsicValueActivity.this, "No data for this stock.", Toast.LENGTH_SHORT).show();
                        } else {
                            createRevenueEstimates();
                            saveValues(symbol);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }


    private boolean hasPremium() {

        tinyDB = new TinyDB(IntrinsicValueActivity.this);
        return tinyDB.getBoolean("PREMIUM");

    }


    private void saveValues(String symbol) {

        tinyDB = new TinyDB(this);

        //Map out values
        freeCashflowData.put(symbol + "FCFYear0", freeCashFlowYear0);
        freeCashflowData.put(symbol + "FCFYearMinus1", freeCashFlowYearMinus1);
        freeCashflowData.put(symbol + "FCFYearMinus2", freeCashFlowYearMinus2);
        freeCashflowData.put(symbol + "FCFYearMinus3", freeCashFlowYearMinus3);

        revenueData.put(symbol + "revYear0", revenueYear0);
        revenueData.put(symbol + "revYearMinus1", revenueYearMinus1);
        revenueData.put(symbol + "revYearMinus2", revenueYearMinus2);
        revenueData.put(symbol + "revYearMinus3", revenueYearMinus3);

        netIncomeData.put(symbol + "netIncYear0", netIncomeYear0);
        netIncomeData.put(symbol + "netIncMinus1", netIncomeYearMinus1);
        netIncomeData.put(symbol + "netIncMinus2", netIncomeYearMinus2);
        netIncomeData.put(symbol + "netIncMinus3", netIncomeYearMinus3);


        stockIntrinsicValues = new StockIntrinsicValues(freeCashflowData, revenueData, netIncomeData, shortTermDebt, longTermDebt,
                interestExpense, incomeBeforeTax, incomeTaxExpense);

        tinyDB.putObject(symbol + "intrinsicData", stockIntrinsicValues);



        long currentTimeMillis = System.currentTimeMillis();
        long intrinsicExpiryDate;

        if (hasPremium()) {
            intrinsicExpiryDate = currentTimeMillis + TimeUnit.DAYS.toMillis(180);
        } else {
            intrinsicExpiryDate = currentTimeMillis + TimeUnit.DAYS.toMillis(365);
        }

        tinyDB.putLong(symbol + "intrinsicExpiryDate", intrinsicExpiryDate);


    }

    private void loadValues(String symbol){

        tinyDB = new TinyDB(this);

        long currentTime = System.currentTimeMillis();

        if (currentTime > tinyDB.getLong(symbol + "intrinsicExpiryDate")) {
            getAllData(symbol, this);
        } else {

            stockIntrinsicValues = tinyDB.getObject(symbol + "intrinsicData", StockIntrinsicValues.class);

            freeCashflowData = stockIntrinsicValues.getFreeCashFlowData();
            if (freeCashflowData != null) {
                freeCashFlowYear0 = freeCashflowData.get(symbol + "FCFYear0");
                freeCashFlowYearMinus1 = freeCashflowData.get(symbol + "FCFYearMinus1");
                freeCashFlowYearMinus2 = freeCashflowData.get(symbol + "FCFYearMinus2");
                freeCashFlowYearMinus3 = freeCashflowData.get(symbol + "FCFYearMinus2");

            }

            revenueData = stockIntrinsicValues.getRevenueData();
            if (revenueData != null) {
                revenueYear0 = revenueData.get(symbol + "revYear0");
                revenueYearMinus1 = revenueData.get(symbol + "revYearMinus1");
                revenueYearMinus2 = revenueData.get(symbol + "revYearMinus2");
                revenueYearMinus3 = revenueData.get(symbol + "revYearMinus3");
            }

            netIncomeData = stockIntrinsicValues.getNetIncomeData();
            if (netIncomeData != null) {
                netIncomeYear0 = netIncomeData.get(symbol + "netIncYear0");
                netIncomeYearMinus1 = netIncomeData.get(symbol + "netIncMinus1");
                netIncomeYearMinus2 = netIncomeData.get(symbol + "netIncMinus2");
                netIncomeYearMinus3 = netIncomeData.get(symbol + "netIncMinus3");

            }

            longTermDebt = stockIntrinsicValues.getRawLongTermDebt();
            shortTermDebt = stockIntrinsicValues.getRawShortTermDebt();
            interestExpense = stockIntrinsicValues.getRawInterestExpense();
            incomeBeforeTax = stockIntrinsicValues.getRawIncomeBeforeTax();
            incomeTaxExpense = stockIntrinsicValues.getRawTaxExpense();

            createRevenueEstimates();

        }


    }

    private void setFutureDates(String year0) {

        int yearPlus1 =  Integer.parseInt(year0) + 1;
        int yearPlus2 =  yearPlus1 + 1;
        int yearPlus3 =  yearPlus2 + 1;
        int yearPlus4 =  yearPlus3 + 1;
        int yearPlus5 =  yearPlus4 + 1;

        tvYearPlus1.setText(yearPlus1 + "E");
        tvYearPlus2.setText(yearPlus2 + "E");
        tvYearPlus3.setText(yearPlus3 + "E");
        tvYearPlus4.setText(yearPlus4 + "E");
        tvYearPlus5.setText(yearPlus5 + "E");

    }


    private void createRevenueEstimates() {

        double revenueGrowthYearMinus2 = ((double) revenueYearMinus2 /  revenueYearMinus3);
        double revenueGrowthYearMinus1 = (double) revenueYearMinus1 / revenueYearMinus2;
        double revenueGrowthYear0 = ((double)revenueYear0 / revenueYearMinus1);


        ArrayList<Double> revenueGrowthRates = new ArrayList<>();
        revenueGrowthRates.add(revenueGrowthYear0);
        revenueGrowthRates.add(revenueGrowthYearMinus1);
        revenueGrowthRates.add(revenueGrowthYearMinus2);

        double chosenGrowthRate = 0;

        switch (valueHeight){
            case "High":

                for (Double growthRate : revenueGrowthRates){

                    for (int i = 0; i < revenueGrowthRates.size(); i++){
                        if (growthRate > revenueGrowthRates.get(i)){
                            chosenGrowthRate = growthRate;
                            break;

                        }
                    }

                }

                break;
            case "Average":

                //Growth rates
                chosenGrowthRate = (revenueGrowthYearMinus2 + revenueGrowthYearMinus1 + revenueGrowthYear0) / 3d;

                break;
            case "Low":

                chosenGrowthRate = revenueGrowthRates.get(0);

                for (int i = 1; i < revenueGrowthRates.size(); i++) {

                    if (revenueGrowthRates.get(i) < chosenGrowthRate) {
                        chosenGrowthRate = revenueGrowthRates.get(i);
                    }
                }

                break;

        }

        //creates estimates
        revenueYearPlus1 = (double) revenueYear0 * chosenGrowthRate;
        revenueYearPlus2 = revenueYearPlus1 * chosenGrowthRate;
        revenueYearPlus3 = revenueYearPlus2 * chosenGrowthRate;
        revenueYearPlus4 = revenueYearPlus3 * chosenGrowthRate;
        revenueYearPlus5 = revenueYearPlus4 * chosenGrowthRate;

        createNetIncomeMarginsEstimates();


    }

    private void createNetIncomeMarginsEstimates() {

        double netIncomeMarginYear0 = ((double) netIncomeYear0 / revenueYear0);
        double netIncomeMarginYearMinus1 = ((double) netIncomeYearMinus1 / revenueYearMinus1);
        double netIncomeMarginYearMinus2 = ((double) netIncomeYearMinus2 / revenueYearMinus2);
        double netIncomeMarginYearMinus3 = ((double) netIncomeYearMinus3 / revenueYearMinus3);

        ArrayList<Double> netIncomeMargins = new ArrayList<>();
        netIncomeMargins.add(netIncomeMarginYear0);
        netIncomeMargins.add(netIncomeMarginYearMinus1);
        netIncomeMargins.add(netIncomeMarginYearMinus2);
        netIncomeMargins.add(netIncomeMarginYearMinus3);

        double chosenNetIncomeMargin = 0;


        switch (valueHeight) {
            case "High":

                chosenNetIncomeMargin = netIncomeMargins.get(0);

                for (int i = 1; i < netIncomeMargins.size(); i++) {

                    if (netIncomeMargins.get(i) > chosenNetIncomeMargin) {
                        chosenNetIncomeMargin = netIncomeMargins.get(i);
                    }
                }
                break;

            case "Average":

                chosenNetIncomeMargin = (netIncomeMarginYear0 + netIncomeMarginYearMinus1 + netIncomeMarginYearMinus2
                        + netIncomeMarginYearMinus3) / 4d;

                break;
            case "Low":

                chosenNetIncomeMargin = netIncomeMargins.get(0);

                for (int i = 1; i < netIncomeMargins.size(); i++) {

                    if (netIncomeMargins.get(i) < chosenNetIncomeMargin) {
                        chosenNetIncomeMargin = netIncomeMargins.get(i);
                    }
                }


                break;


        }

        netIncomeYearPlus1 = revenueYearPlus1 * chosenNetIncomeMargin;
        netIncomeYearPlus2 = revenueYearPlus2 * chosenNetIncomeMargin;
        netIncomeYearPlus3 = revenueYearPlus3 * chosenNetIncomeMargin;
        netIncomeYearPlus4 = revenueYearPlus4 * chosenNetIncomeMargin;
        netIncomeYearPlus5 = revenueYearPlus5 * chosenNetIncomeMargin;

        createFCFtoNetIncomeRatios();


    }


    private void createFCFtoNetIncomeRatios() {


        double FCFtoNetRatioYear0 = ((double) freeCashFlowYear0 / netIncomeYear0);
        double FCFtoNetRatioYearMinus1 = ((double) freeCashFlowYearMinus1 / netIncomeYearMinus1);
        double FCFtoNetRatioYearMinus2 = ((double) freeCashFlowYearMinus2 / netIncomeYearMinus2);
        double FCFtoNetRatioYearMinus3 = ((double) freeCashFlowYearMinus3 / netIncomeYearMinus3);

        ArrayList<Double> FCFtoNetRatios = new ArrayList<>();
        FCFtoNetRatios.add(FCFtoNetRatioYear0);
        FCFtoNetRatios.add(FCFtoNetRatioYearMinus1);
        FCFtoNetRatios.add(FCFtoNetRatioYearMinus2);
        FCFtoNetRatios.add(FCFtoNetRatioYearMinus3);

        double chosenFCFtoNetRatio = 0;


        switch (valueHeight) {
            case "High":

                chosenFCFtoNetRatio = FCFtoNetRatios.get(0);

                for (int i = 1; i < FCFtoNetRatios.size(); i++) {

                    if (FCFtoNetRatios.get(i) > chosenFCFtoNetRatio) {
                        chosenFCFtoNetRatio = FCFtoNetRatios.get(i);
                    }
                }
                break;

            case "Average":

                chosenFCFtoNetRatio = (FCFtoNetRatioYear0 + FCFtoNetRatioYearMinus1 + FCFtoNetRatioYearMinus2 + FCFtoNetRatioYearMinus3) / 4d;

                break;
            case "Low":

                chosenFCFtoNetRatio = FCFtoNetRatios.get(0);

                for (int i = 1; i < FCFtoNetRatios.size(); i++) {

                    if (FCFtoNetRatios.get(i) < chosenFCFtoNetRatio) {
                        chosenFCFtoNetRatio = FCFtoNetRatios.get(i);
                    }
                }

                break;
        }

        freeCashFlowYearPlus1 = netIncomeYearPlus1 * chosenFCFtoNetRatio;
        freeCashFlowYearPlus2 = netIncomeYearPlus2 * chosenFCFtoNetRatio;
        freeCashFlowYearPlus3 = netIncomeYearPlus3 * chosenFCFtoNetRatio;
        freeCashFlowYearPlus4 = netIncomeYearPlus4 * chosenFCFtoNetRatio;
        freeCashFlowYearPlus5 = netIncomeYearPlus5 * chosenFCFtoNetRatio;

        setCashFlowsToChart();

    }


    private void setCashFlowsToChart() {

        //PAST
        long thsndFCFYear0 = freeCashFlowYear0 / 1000; //puts number into thousands
        tv2020FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYear0));

        long thsndFCFYearMinus1 = freeCashFlowYearMinus1 / 1000;
        tv2019FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYearMinus1));


        long thsndFCFYearMinus2 = freeCashFlowYearMinus2 / 1000;
        tv2018FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYearMinus2));


        long thsndFCFYearMinus3 = freeCashFlowYearMinus3 / 1000;
        tv2017FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYearMinus3));



        //FUTURE
        double thsndFCFYearPlus1 = freeCashFlowYearPlus1 / 1000;
        tv2021FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYearPlus1));

        double thsndFCFYearPlus2 = freeCashFlowYearPlus2 / 1000;
        tv2022FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYearPlus2));

        double thsndFCFYearPlus3 = freeCashFlowYearPlus3 / 1000;
        tv2023FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYearPlus3));

        double thsndFCFYearPlus4 = freeCashFlowYearPlus4 / 1000;
        tv2024FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYearPlus4));

        double thsndFCFYearPlus5 = freeCashFlowYearPlus5 / 1000;
        tv2025FreeCashFlow.setText("$" + dfLargeNr.format(thsndFCFYearPlus5));


        calculateWACCDiscountRate();

    }

    private void calculateWACCDiscountRate() {

        tvWACCError.setVisibility(View.GONE);
        //WACC = WdRd(1-t) + WeRe
        //Rd (Cost of debt calculation)
        double totalDebt = ((double) shortTermDebt + longTermDebt);

        strTotalDebt = (long)totalDebt;

        double interestExpense = (double) -this.interestExpense;

        double costOfDebtBTD;
        if (totalDebt == 0 || interestExpense == 0) {
            costOfDebtBTD = 0.13; //BTD - before tax deductible
         } else{
            costOfDebtBTD = interestExpense / totalDebt; //BTD - before tax deductible
        }

        strInterest = dfLargeNr.format(costOfDebtBTD * 100);

        Toast.makeText(this, "Interest rate is: " + interestExpense / totalDebt * 100, Toast.LENGTH_SHORT).show();

        //(1-t) calculation because interest is tax deductible
        double incomeBeforeTax = (double) this.incomeBeforeTax;
        double incomeTaxExpense = (double) this.incomeTaxExpense;

        double taxRate = incomeTaxExpense / incomeBeforeTax;
        strTaxRate = dfPercentage.format(taxRate * 100);

        double costOfDebt = costOfDebtBTD * (1 - taxRate);
        strCostOfDebt = dfPercentage.format(costOfDebt * 100);

        if (interestExpense == 0){
            tvWACCError.setVisibility(View.VISIBLE);
            tvWACCError.setText("Debt interest data is invalid. Use larger safety margins.");
        } else if (shortTermDebt == 0 || longTermDebt == 0){
            tvWACCError.setVisibility(View.VISIBLE);
            tvWACCError.setText("Debt data is invalid. Use larger safety margins.");
        }

        //Cost of Equity
        double costOfEquity = costOfEquityCAPM();
        strCostOfEquity = dfPercentage.format(costOfEquity * 100);

        //Weights of debt and equity
        double stockMarketCap = (double) marketCap;
        double totalEnterpriseValue = stockMarketCap + totalDebt;

        double weightOfDebt = totalDebt / totalEnterpriseValue;
        double weightOfEquity = stockMarketCap / totalEnterpriseValue;

        strWeightOfDebt = dfPercentage.format(weightOfDebt * 100);
        strWeightOfEquity = dfPercentage.format(weightOfEquity * 100) ;

        double WACCReturnRate = weightOfDebt * costOfDebt + weightOfEquity * costOfEquity;


        double WACCforTextView = WACCReturnRate * 100;
        tvWACCReturnRate.setText(dfPercentage.format(WACCforTextView)+"%");


        setDiscountRatesAndTerminalVal(WACCReturnRate);

    }


    private double costOfEquityCAPM() {

        //CAPM = Rf = B(Rm - Rf)

        //for all value heights 10%
        double avgMarketReturn = 0.10;

        switch (valueHeight) {
            case "High":
                perpetualGrowthRate = 0.03;
                break;

            case "Average":
                //hitorical avg risk free return rate 2.5%
                perpetualGrowthRate = 0.025;
                //for textview
                break;

            case "Low":
                perpetualGrowthRate = 0.02;
                break;
        }

        double hndrPerpGrowthRate = perpetualGrowthRate * 100;
        strRiskFreeRate = dfPercentage.format(hndrPerpGrowthRate);
        tvPerpetualGrowthRate.setText(strRiskFreeRate + "%");

        //Returns calculated Ra (Cost of equity/Asset risk)
        return perpetualGrowthRate + stockBeta * (avgMarketReturn - perpetualGrowthRate);

    }

    private void setDiscountRatesAndTerminalVal(double waccReturnRate) {

        //Create and set discount rates according to years
        double incrementalWACC = waccReturnRate + 1; // + 1 so i can increment it by 100% of that return rate

        discountYearPlus1 = incrementalWACC;
        discountYearPlus2 = discountYearPlus1 * incrementalWACC;
        discountYearPlus3 = discountYearPlus2 * incrementalWACC;
        discountYearPlus4 = discountYearPlus3 * incrementalWACC;
        discountYearPlus5 = discountYearPlus4 * incrementalWACC;

        //discountYear - 1 so I get real percentage value not the increase value
        tv2021DiscountRate.setText(dfLargeNr.format((discountYearPlus1 - 1) * 100) + "%");
        tv2022DiscountRate.setText(dfLargeNr.format((discountYearPlus2 - 1) * 100) + "%");
        tv2023DiscountRate.setText(dfLargeNr.format((discountYearPlus3 - 1) * 100) + "%");
        tv2024DiscountRate.setText(dfLargeNr.format((discountYearPlus4 - 1) * 100) + "%");
        tv2025DiscountRate.setText(dfLargeNr.format((discountYearPlus5 - 1) * 100) + "%");
        tvTerminalDiscountRate.setText(dfLargeNr.format((discountYearPlus5 - 1) * 100) + "%");

        //Terminal Value Calculation

        double freeCashFlowFinalYear = freeCashFlowYearPlus5 * (1 + perpetualGrowthRate);
        freeCashFlowTerminal = freeCashFlowFinalYear / (waccReturnRate - perpetualGrowthRate);

        double thsndTerminalVal = freeCashFlowTerminal / 1000;
        tvTerminalFreeCashFlow.setText("$"+ dfLargeNr.format(thsndTerminalVal));

        createAndSetDCFs();

    }

    private void createAndSetDCFs() {

        //Get discounted Cash flows
        discountedFCFYearPlus1 = freeCashFlowYearPlus1 / discountYearPlus1;
        discountedFCFYearPlus2 = freeCashFlowYearPlus2 / discountYearPlus2;
        discountedFCFYearPlus3 = freeCashFlowYearPlus3 / discountYearPlus3;
        discountedFCFYearPlus4 = freeCashFlowYearPlus4 / discountYearPlus4;
        discountedFCFYearPlus5 = freeCashFlowYearPlus5 / discountYearPlus5;
        discountedFCFTerminal = freeCashFlowTerminal / discountYearPlus5;

        //Set DCF to chart
        tv2021PresentVal.setText(dfLargeNr.format(discountedFCFYearPlus1));
        tv2022PresentVal.setText(dfLargeNr.format(freeCashFlowYearPlus2 / discountYearPlus2));
        tv2023PresentVal.setText(dfLargeNr.format(freeCashFlowYearPlus3 / discountYearPlus3));
        tv2024PresentVal.setText(dfLargeNr.format(freeCashFlowYearPlus4 / discountYearPlus4));
        tv2025PresentVal.setText(dfLargeNr.format(freeCashFlowYearPlus5 / discountYearPlus5));
        tvTerminalPresentVal.setText(dfLargeNr.format(freeCashFlowTerminal / discountYearPlus5));

        calculateIntrinsicValue();

    }

    private void calculateIntrinsicValue() {

        double FCFTotalTodaysValue = discountedFCFYearPlus1 + discountedFCFYearPlus2 + discountedFCFYearPlus3 + discountedFCFYearPlus4 +
                discountedFCFYearPlus5 + discountedFCFTerminal;

        tvTodaysValue.setText("$" + dfLargeNr.format(FCFTotalTodaysValue));

        double sharesOutstanding = (double) sharesOut;
        intrinsicValue = FCFTotalTodaysValue / sharesOutstanding;

        tvIntrinsicValue.setText("$" + dfPercentage.format(intrinsicValue));
        tvQuickIntrinsicVal.setText("$" + dfPercentage.format(intrinsicValue));

        double safetyMargin = 0;

        switch (valueHeight){
            case "High":

                safetyMargin = 0.75;

                break;
            case "Average":

                safetyMargin = 0.5;

                break;
            case "Low":

                safetyMargin = 0.25;

                break;
        }


        intrinsicValueWithMargin = intrinsicValue - (intrinsicValue * safetyMargin);

        tvIntrinWithMargin.setText("$" + dfPercentage.format(intrinsicValueWithMargin));

        double hndrSafetymargin = safetyMargin * 100;
        tvQuickIntrinsicMargin.setText((int)hndrSafetymargin + "%"  +" - " + "$" + dfPercentage.format(intrinsicValueWithMargin));



    }

}