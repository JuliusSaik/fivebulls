package com.saikauskas.julius.fivebulls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.PieChart;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.ApiResponse;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.EpsArrayListsObj;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.QuoteSummary;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StockDetailsResult;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StockIntrinsicValues;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StockItem;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.facebook.ads.*;

public class MainActivity extends AppCompatActivity {

    Context context = MainActivity.this;
    public static String PACKAGE_NAME;

    private static final int GET_STOCK = 1;

    FloatingActionButton bttnAddStock, bttnSettings;

    private RecyclerView recyclerView;
    private SelectedStocksAdapter adapter;

    private ArrayList<StockItem> stockItems = new ArrayList<>();
    private ArrayList<String> sectors = new ArrayList<>();


    private static String KEY;
    //Testing Key
    //private static final String KEY = "8pCmwGLGRlaqvpG8geaZw5I8KiMRwBkG477pTNpN";


    private double rawPEratio, payoutRatio, fiveYearDiv, currentDiv, regulMarketPrice, quickRatio, earningsGrowth, pegRatio, epsGrowth, epsActual;
    private int PeRating, DivRating, PayoutRating, EarningsRating, QuickRating, PegRating, EpsRating;
    long dataExpiryDate;

    private String recommendationKey;
    private double targetHighPrice, targetLowPrice, targetMedianPrice;
    private Map<String, Double> targetPrices = new HashMap<>();

    private long marketCap, sharesOutstanding;
    private double beta;

    private String shortName, symbol, sector;
    private TextView tvEmptyListText;

    private LottieAnimationView emptyListAni;


    //private IJsonPlaceHolderAPI IJsonPlaceHolderAPI;

    private CheckRatingOfStock checkRatingOfStock;
    private CreatePieChart createPieChart;
    private PieChart pieChart;

    private TinyDB tinyDB;

    private AdView bannerAd;
    private FrameLayout adContainer;
    private boolean isAdLoaded = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinyDB = new TinyDB(MainActivity.this);


        if(tinyDB.getBoolean("IsNightMode")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
        setContentView(R.layout.activity_main);
        setupWindowAnimations();

        PACKAGE_NAME = getApplicationContext().getPackageName();
        KEY = new Secrets().getuAnYVyWY(PACKAGE_NAME);

        recyclerView = findViewById(R.id.stockDisplayRecycler);
        tvEmptyListText = findViewById(R.id.emptydialogtv);
        emptyListAni = findViewById(R.id.emptylistani);
        bttnAddStock = findViewById(R.id.bttnAddStock);
        pieChart = findViewById(R.id.sectorChart);
        bttnSettings = findViewById(R.id.bttnSettings);
        adContainer = findViewById(R.id.ad_container);

        loadItems(MainActivity.this);

        setReyclerViewValues();
        setEmptyView(adapter.getItemCount() == 0);

        bttnAddStock.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, StockSearchActivity.class);
            startActivityForResult(intent, GET_STOCK);

        });

        bttnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });


        createPieChart = new CreatePieChart(pieChart, this);
        createPieChart.setupPieChart();
        createPieChart.loadPieChartData(sectors);


        bannerAd = findViewById(R.id.bannerAdMain);
        adContainer = findViewById(R.id.ad_container);
        //check if user has premium or not
        if (!hasPremium()) {
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAd.loadAd(adRequest);
            isAdLoaded = false;
            setEmptyView(adapter.getItemCount() == 0);

            bannerAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    isAdLoaded = true;
                    setEmptyView(adapter.getItemCount() == 0);
                    super.onAdLoaded();
                }
            });

        } else {
            adContainer.setVisibility(View.GONE);

        }

    }

    private void setupWindowAnimations() {

        Window window = getWindow();
        Slide slide = new Slide();

        slide.setInterpolator(new FastOutSlowInInterpolator());
        slide.setSlideEdge(Gravity.TOP);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        slide.excludeTarget(R.id.bttnAddStock, true);
        slide.setDuration(400);

        window.setExitTransition(slide);
        window.setReenterTransition(slide);
        window.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));

    }


    private boolean hasPremium() {

        tinyDB = new TinyDB(MainActivity.this);
        return tinyDB.getBoolean("PREMIUM");
    }


    private void setEmptyView(boolean isRecyclerEmpty) {

        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);
        ConstraintSet constraintSet = new ConstraintSet();

        if (hasPremium()) {

            if (isRecyclerEmpty) {

                tvEmptyListText.setVisibility(View.VISIBLE);
                emptyListAni.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                adContainer.setVisibility(View.GONE);

            } else {

                tvEmptyListText.setVisibility(View.GONE);
                emptyListAni.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                adContainer.setVisibility(View.GONE);

                constraintSet.clone(constraintLayout);

                constraintSet.connect(R.id.stockDisplayRecycler, ConstraintSet.TOP, R.id.sectorChart, ConstraintSet.BOTTOM, 861);
                constraintSet.applyTo(constraintLayout);
            }

        } else {

            if (isRecyclerEmpty) {

                tvEmptyListText.setVisibility(View.VISIBLE);
                emptyListAni.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                adContainer.setVisibility(View.VISIBLE);

                constraintSet.clone(constraintLayout);

                constraintSet.connect(R.id.ad_container, ConstraintSet.TOP, R.id.bttnSettings, ConstraintSet.BOTTOM, 31);
                constraintSet.applyTo(constraintLayout);

            } else {

                tvEmptyListText.setVisibility(View.GONE);
                emptyListAni.setVisibility(View.GONE);

                pieChart.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);


                if (isAdLoaded) {

                    adContainer.setVisibility(View.VISIBLE);
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.ad_container, ConstraintSet.TOP, R.id.sectorSizingSpace, ConstraintSet.BOTTOM, 90);
                    constraintSet.connect(R.id.stockDisplayRecycler, ConstraintSet.TOP, R.id.ad_container, ConstraintSet.BOTTOM, 1156);

                } else {

                    adContainer.setVisibility(View.GONE);
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.stockDisplayRecycler, ConstraintSet.TOP, R.id.sectorSizingSpace, ConstraintSet.BOTTOM, 891);

                }


                constraintSet.applyTo(constraintLayout);

            }

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_STOCK) {
            if (resultCode == Activity.RESULT_OK) {

                String querySymbol = null;
                if (data != null) {
                    querySymbol = data.getStringExtra("symbol");
                }
                getSelectedStockData(querySymbol, 0, this);//default pos value because it won't be used unless data is expired
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
    }


    public void getSelectedStockData(final String querySymbol, int position, Context context) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://yfapi.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IJsonPlaceHolderAPI IJsonPlaceHolderAPI = retrofit.create(IJsonPlaceHolderAPI.class);

        Call<ApiResponse> call = IJsonPlaceHolderAPI.getStocks(KEY, querySymbol);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(@NotNull Call<ApiResponse> call, @NotNull retrofit2.Response<ApiResponse> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "Sorry! couldn't fetch that stock/or limit has been reached, please be patient :)", Toast.LENGTH_SHORT).show();
                } else {

                    ApiResponse list = response.body();
                    QuoteSummary quoteSummary = list.getQuoteSummary();
                    ArrayList<StockDetailsResult> results = quoteSummary.getResult();

                    if (results == null) {
                        Toast.makeText(context, "Data not found for the symbol.", Toast.LENGTH_SHORT).show();
                    } else {
                        StockDetailsResult index0 = results.get(0);


                        //priceResponse
                        StockItem priceResponse = index0.getPrice();
                        StockItem marketPriceResp = priceResponse.getRegularMarketPrice();

                        shortName = priceResponse.getShortName();
                        symbol = priceResponse.getSymbol();
                        regulMarketPrice = marketPriceResp.getRaw();

                        //For Pie Chart sectors

                        StockItem assetProfileResp = index0.getAssetProfile();
                        try {
                            sector = assetProfileResp.getSector();
                        } catch (Exception e) {
                            e.printStackTrace();
                            sector = "n/a";
                        }
                        //only add sector to list if stock hasn't been added because of not initialized list if requerying data
                        if (!isItemAlreadyAdded(context, symbol)) {
                            sectors.add(sector);
                        }

                        //earningsHistory for actual eps
                        try {
                            EpsArrayListsObj earningsHistoryResp = index0.getEarningsHistory();
                            ArrayList<EpsArrayListsObj> historyList = earningsHistoryResp.getHistory();
                            EpsArrayListsObj latestEps = historyList.get(3);

                            StockItem eps = latestEps.getEpsActual();
                            epsActual = eps.getRaw();
                        } catch (Exception e) {
                            e.printStackTrace();
                            epsActual = 0;
                        }


                        //earningsTrend for eps growth
                        try {
                            EpsArrayListsObj earningsTrendResp = index0.getEarningsTrend();
                            ArrayList<EpsArrayListsObj> trendList = earningsTrendResp.getTrend();
                            EpsArrayListsObj trendIndex5 = trendList.get(5);

                            StockItem growth = trendIndex5.getGrowth();
                            epsGrowth = growth.getRaw();
                        } catch (Exception e) {
                            e.printStackTrace();
                            epsGrowth = 0;
                        }
                        //summaryDetail
                        StockItem summaryDetailResponse = index0.getSummaryDetail();

                        try {
                            StockItem payoutRatioObj = summaryDetailResponse.getPayoutRatio();
                            payoutRatio = payoutRatioObj.getRaw();

                        } catch (Exception e) {
                            e.printStackTrace();
                            payoutRatio = 0;
                        }

                        try {
                            StockItem dividendYield = summaryDetailResponse.getDividendYield();
                            currentDiv = dividendYield.getRaw();

                            StockItem fiveYearDivYield = summaryDetailResponse.getFiveYearDivYield();
                            fiveYearDiv = fiveYearDivYield.getRaw() / 100;

                        } catch (Exception e) {
                            e.printStackTrace();
                            currentDiv = 0;
                            fiveYearDiv = 0;
                        }

                        try {

                            StockItem betaResp = summaryDetailResponse.getBeta();
                            beta = betaResp.getRaw();

                            StockIntrinsicValues marketCapResp = summaryDetailResponse.getMarketCap();
                            marketCap = marketCapResp.getRaw();

                        } catch (Exception e) {
                            e.printStackTrace();
                            beta = 0;
                            marketCap = 0;

                        }

                        //financialData
                        StockItem financialDataResponse = index0.getFinancialData();

                        try {
                            StockItem quickRatioResp = financialDataResponse.getQuickRatio();
                            quickRatio = quickRatioResp.getRaw();
                        } catch (Exception e) {
                            e.printStackTrace();
                            quickRatio = 0;
                        }

                        try {
                            StockItem highPrice = financialDataResponse.getTargetHighPrice();
                            StockItem lowPrice = financialDataResponse.getTargetLowPrice();
                            StockItem medianPrice = financialDataResponse.getTargetMedianPrice();
                            recommendationKey = financialDataResponse.getRecommendationKey();

                            targetHighPrice = highPrice.getRaw();
                            targetLowPrice = lowPrice.getRaw();
                            targetMedianPrice = medianPrice.getRaw();


                        } catch (Exception e) {
                            e.printStackTrace();

                            targetHighPrice = 0;
                            targetLowPrice = 0;
                            targetMedianPrice = 0;
                            recommendationKey = "none";
                        }


                        targetPrices.put("targetHighPrice", targetHighPrice);
                        targetPrices.put("targetLowPrice", targetLowPrice);
                        targetPrices.put("targetMedianPrice", targetMedianPrice);

                        try {
                            StockItem earningsGrowthResp = financialDataResponse.getEarningsGrowth();
                            earningsGrowth = earningsGrowthResp.getRaw();
                        } catch (Exception e) {
                            e.printStackTrace();
                            earningsGrowth = 0;
                        }

                        //defaultKeyStatistics

                        StockItem defaultKeyStatistics = index0.getDefaultKeyStatistics();

                        try {
                            StockItem pegRatioResp = defaultKeyStatistics.getPegRatio();
                            pegRatio = pegRatioResp.getRaw();
                        } catch (Exception e) {
                            e.printStackTrace();
                            pegRatio = 0;
                        }

                        try {

                            StockIntrinsicValues sharesResp = defaultKeyStatistics.getSharesOutstanding();
                            sharesOutstanding = sharesResp.getRaw();
                        } catch (Exception e) {
                            e.printStackTrace();

                            sharesOutstanding = 0;
                        }


                        try {

                            StockItem trailingPEratio = summaryDetailResponse.getTrailingPE();
                            rawPEratio = trailingPEratio.getRaw();

                        } catch (NullPointerException nullPoint) {
                            rawPEratio = 0;
                        }

                        if (!isItemAlreadyAdded(context, symbol)) {

                            long currentTimeMillis = System.currentTimeMillis();
                            dataExpiryDate = currentTimeMillis + TimeUnit.DAYS.toMillis(90);

                            tinyDB.putLong(symbol + "dataExpiryDate", dataExpiryDate);

                        }


                        rateStock(position, context); //context for when data is expired and adapter handles intent

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isItemAlreadyAdded(Context context, String symbol){
        tinyDB = new TinyDB(context);
        return tinyDB.getBoolean( symbol + "isItemAdded");
    }


    private void rateStock(int position, Context context) {

        checkRatingOfStock = new CheckRatingOfStock(rawPEratio, payoutRatio, currentDiv, fiveYearDiv, quickRatio, earningsGrowth, pegRatio, epsGrowth);

        PeRating = checkRatingOfStock.getPeRatioRating();
        PayoutRating = checkRatingOfStock.getPayoutRatioRating();
        DivRating = checkRatingOfStock.getDividenDiffRating();
        QuickRating = checkRatingOfStock.getQuickRatioRating();
        EarningsRating = checkRatingOfStock.getEarningsGrowthRating();
        PegRating = checkRatingOfStock.getPegRatioRating();
        EpsRating = checkRatingOfStock.getEpsGrowthRating();


        if (currentDiv == 0) {
            int ratingNonDiv = checkRatingOfStock.getNonDivStockRating();
            setDataAndAdd(ratingNonDiv, symbol, position, context);
        } else {
            int ratingDiv = checkRatingOfStock.getDivStockRating();
            setDataAndAdd(ratingDiv, symbol, position, context);
        }


    }


    public void setDataAndAdd(int starNumber, String symbol, int itemPosition, Context context) {

        long currentTime = System.currentTimeMillis();

        if (isItemAlreadyAdded(context, symbol)){

            //if data is expired redo call and add new item remove old one
            if (currentTime > tinyDB.getLong(symbol + "dataExpiryDate")) {

                loadItems(context);
                adapter = new SelectedStocksAdapter(stockItems, context);

                long newDataExpiryDate = currentTime + TimeUnit.DAYS.toMillis(90);
                tinyDB.putLong(symbol + "dataExpiryDate", newDataExpiryDate);

                stockItems.add(new StockItem(shortName, symbol, regulMarketPrice, starNumber,
                        rawPEratio, currentDiv, fiveYearDiv, payoutRatio, earningsGrowth, quickRatio, pegRatio, epsActual, epsGrowth,
                        PeRating, DivRating, PayoutRating, EarningsRating, QuickRating, PegRating, EpsRating, sector, targetPrices, recommendationKey,
                        marketCap, sharesOutstanding, beta));

                stockItems.remove(itemPosition);
                adapter.notifyItemRemoved(itemPosition);
                adapter.notifyItemInserted(itemPosition);

                sectors.remove(itemPosition);
                sectors.add(sector);

                saveItems(context);

            } else {
                Toast.makeText(context, symbol + " is already in your list" , Toast.LENGTH_SHORT).show();

            }
        }
        else if(!isItemAlreadyAdded(MainActivity.this, symbol)) {

            stockItems.add(new StockItem(shortName, symbol, regulMarketPrice, starNumber,
                    rawPEratio, currentDiv, fiveYearDiv, payoutRatio, earningsGrowth, quickRatio, pegRatio, epsActual, epsGrowth,
                    PeRating, DivRating, PayoutRating, EarningsRating, QuickRating, PegRating, EpsRating, sector, targetPrices, recommendationKey,
                    marketCap, sharesOutstanding, beta));


            CreatePieChart createPieChart = new CreatePieChart(pieChart, MainActivity.this);
            createPieChart.loadPieChartData(sectors);

            adapter.notifyItemInserted(adapter.getItemCount() + 1);
            tinyDB.putBoolean(symbol + "isItemAdded", true);
            saveItems(MainActivity.this);

            setEmptyView(false);
        }

    }


    private void saveItems(Context context) {

        tinyDB = new TinyDB(context);
        tinyDB.putListObject("stocksRecyclerList", stockItems);
        tinyDB.putListString("sectorsList", sectors);

    }

    private void loadItems(Context context) {

        tinyDB = new TinyDB(context);
        stockItems = tinyDB.getListObject("stocksRecyclerList", StockItem.class);
        sectors = tinyDB.getListString("sectorsList");

    }

    private void setReyclerViewValues() {
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(false);
        adapter = new SelectedStocksAdapter(stockItems, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getBindingAdapterPosition();

                tinyDB.remove(stockItems.get(position).getSymbol() + "isItemAdded");
                tinyDB.remove(stockItems.get(position).getSymbol() + "intrinsicData");

                //remove chart data so to not cram useless info of old stocks
                tinyDB.remove(stockItems.get(position).getSymbol() + "time5y");
                tinyDB.remove(stockItems.get(position).getSymbol() + "price5y");

                tinyDB.remove(stockItems.get(position).getSymbol() + "time1y");
                tinyDB.remove(stockItems.get(position).getSymbol() + "price1y");

                tinyDB.remove(stockItems.get(position).getSymbol() + "time6mo");
                tinyDB.remove(stockItems.get(position).getSymbol() + "price6mo");

                tinyDB.remove(stockItems.get(position).getSymbol() + "time3mo");
                tinyDB.remove(stockItems.get(position).getSymbol() + "price3mo");

                tinyDB.remove(stockItems.get(position).getSymbol() + "time5d");
                tinyDB.remove(stockItems.get(position).getSymbol() + "price5d");

                tinyDB.remove(stockItems.get(position).getSymbol() + "time1d");
                tinyDB.remove(stockItems.get(position).getSymbol() + "price1d");


                sectors.remove(position);
                stockItems.remove(position);


                createPieChart.loadPieChartData(sectors);
                adapter.notifyItemRemoved(position);

                setEmptyView(adapter.getItemCount() == 0);
                saveItems(MainActivity.this);

            }


        }).attachToRecyclerView(recyclerView);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}