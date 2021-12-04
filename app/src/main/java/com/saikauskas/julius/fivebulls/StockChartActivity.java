package com.saikauskas.julius.fivebulls;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentContainerView;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.AdjCloseResult;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.ChartApiResponse;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.ChartResponse;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StockChartResult;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class StockChartActivity extends AppCompatActivity {

    double defaultIntentValue = 0;
    int defaultIntValue = 0;

    LineChart lineChart;
    LineDataSet lineDataSet1;

    IJsonPlaceHolderAPI IJsonPlaceHolderAPI;

    //private static final String KEY = new Secrets().getuAnYVyWY("com.saikauskas.julius.fivebulls");
    //Testing Key
    private static final String KEY = "8pCmwGLGRlaqvpG8geaZw5I8KiMRwBkG477pTNpN";


    private String querySymbol, recommendation;

    Group ratingsGroup, statisticsGroup;

    private TextView tvStockDate, tvStockPrice, tvSymbol, tvDay, tvWeek, tv3Month, tv6Month, tvYear, tv5Year, tvMax, tvStatistics, tvRatings;

    private double peRatio, currentDiv, fiveYearDiv,  payoutRatio,  earningGrowth,  quickRatio, pegRatio, stockPrice, epsActual, epsGrowth, beta;
    private double targetHighPrice, targetLowPrice, targetMedianPrice;

    private long marketCap, sharesOut;

    private TextView tvPeRatio, tvDividendYield, tvFiveYearDiv,  tvPayoutRatio,  tvEarningGrowth,  tvQuickRatio, tvPegRatio, tvEpsGrowth,
            tvPeRating, tvDivRating, tvPayoutRating, tvEarningsRating, tvQuickRating, tvPegRating, tvEpsRating;

    private TextView tvHighPrice, tvLowPrice,tvMedianPrice, tvCurrentPrice, tvRecommendation, tvInstrinsicValue;

    int PeRating, DivRating, PayoutRating, EarningsRating, QuickRating, PegRating, EpsRating, stockRating;
    long dataExpiryDate;

    FragmentContainerView fragmentContainerView;

    ImageView star1, star2, star3, star4, star5;

    ArrayList<Entry> dataVals = new ArrayList<Entry>();

    TinyDB tinyDB;

    AdView bannerAd;
    FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stockchart);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://yfapi.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IJsonPlaceHolderAPI = retrofit.create(IJsonPlaceHolderAPI.class);



        lineChart = findViewById(R.id.priceChart);
        lineChart.setNoDataText("Loading chart data...");
        lineChart.setNoDataTextColor(getResources().getColor(R.color.textColorPrimary));

        tvStockDate = findViewById(R.id.tvStockDate);
        tvStockPrice = findViewById(R.id.tvStockPrice);
        tvSymbol = findViewById(R.id.tvSymbol);

        tvDay = findViewById(R.id.tvDay);
        tvWeek = findViewById(R.id.tvWeek);
        tv3Month = findViewById(R.id.tv3Month);
        tv6Month = findViewById(R.id.tv6Months);
        tvYear = findViewById(R.id.tvYear);
        tv5Year = findViewById(R.id.tv5Year);
        tvMax = findViewById(R.id.tvMax);


        tvPeRatio = findViewById(R.id.tvPeRatio);
        tvDividendYield = findViewById(R.id.tvCurrentDiv);
        tvFiveYearDiv = findViewById(R.id.tvFiveYearDiv);
        tvPayoutRatio = findViewById(R.id.tvPayoutRatio);
        tvEarningGrowth = findViewById(R.id.tvEarningGrowth);
        tvQuickRatio = findViewById(R.id.tvQuickRatio);
        tvPegRatio = findViewById(R.id.tvPegRatio);
        //tvEpsActual = findViewById(R.id.tvEpsActual);
        tvEpsGrowth = findViewById(R.id.tvEpsGrowth);

        tvHighPrice = findViewById(R.id.tvHighPrice);
        tvLowPrice = findViewById(R.id.tvLowPrice);
        tvMedianPrice = findViewById(R.id.tvMedianPrice);
        tvCurrentPrice = findViewById(R.id.tvCurrentPrice);
        tvRecommendation = findViewById(R.id.tvRecommendation);


        tvPeRating = findViewById(R.id.tvPeRating);
        tvDivRating = findViewById(R.id.tvDivRating);
        tvPayoutRating = findViewById(R.id.tvPayoutRatioRating);
        tvEarningsRating = findViewById(R.id.tvEarningsGrowthRating);
        tvQuickRating = findViewById(R.id.tvQuickRatioRating);
        tvPegRating = findViewById(R.id.tvPegRating);
        tvEpsRating = findViewById(R.id.tvEpsGrowthRating);

        tvInstrinsicValue = findViewById(R.id.tvIntrinsicValueCalc);

        star1 = findViewById(R.id.ivStar1);
        star2 = findViewById(R.id.ivStar2);
        star3 = findViewById(R.id.ivStar3);
        star4 = findViewById(R.id.ivStar4);
        star5 = findViewById(R.id.ivStar5);

        tvStatistics = findViewById(R.id.tvKeyStatistics);
        tvRatings = findViewById(R.id.tvRatings);
        statisticsGroup = findViewById(R.id.statisticsGroup);
        ratingsGroup = findViewById(R.id.ratingsGroup);

        tvStatistics.setOnClickListener(v -> {

            statisticsGroup.setVisibility(View.VISIBLE);
            ratingsGroup.setVisibility(View.GONE);

            tvStatistics.setTextColor(getResources().getColor(R.color.constantWhite));
            tvRatings.setTextColor(getResources().getColor(R.color.textColorPrimary));

            tvStatistics.setBackground(getDrawable(R.drawable.iosselector_active1_ani));
            tvRatings.setBackground(getDrawable(R.drawable.iosselector_disabled1_ani));

        });

        tvRatings.setOnClickListener(v -> {

            statisticsGroup.setVisibility(View.GONE);
            ratingsGroup.setVisibility(View.VISIBLE);

            tvRatings.setTextColor(getResources().getColor(R.color.constantWhite));
            tvStatistics.setTextColor(getResources().getColor(R.color.textColorPrimary));

            tvRatings.setBackground(getDrawable(R.drawable.iosselector_active2_ani));
            tvStatistics.setBackground(getDrawable(R.drawable.iosselector_disabled2_ani));


        });

        tvInstrinsicValue.setOnClickListener(view -> {

            Intent intent = new Intent(StockChartActivity.this, IntrinsicValueActivity.class);
            Bundle data = new Bundle();

            data.putDouble("StockPrice", stockPrice);
            data.putString("StockSymbol", querySymbol);
            data.putDouble("beta", beta);
            data.putLong("marketCap", marketCap);
            data.putLong("sharesOut", sharesOut);

            intent.putExtras(data);
            startActivity(intent);

        });



        FloatingActionButton fabBack = findViewById(R.id.fabBack);
        fabBack.setOnClickListener(v ->{

            setWindowAnimations();
            Intent intent = new Intent(StockChartActivity.this, MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

        });


        getIntentAndSetValues();

        tinyDB = new TinyDB(StockChartActivity.this);

        if (tinyDB.getListLong(querySymbol + "price1y").isEmpty()){
            getStockChart(querySymbol, "1d", "1y");
            List<TextView> listDay = new LinkedList<>(Arrays.asList(tvDay, tvWeek, tv3Month, tv6Month, tv5Year, tvMax));
            checkStatesAndSetStyle(tvYear, listDay);

        } else {
            loadChartData(querySymbol, "1y");
            List<TextView> listDay = new LinkedList<>(Arrays.asList(tvDay, tvWeek, tv3Month, tv6Month, tv5Year, tvMax));
            checkStatesAndSetStyle(tvYear, listDay);
        }


        tvDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tinyDB.getListLong(querySymbol +"price1d").isEmpty()){
                    getStockChart(querySymbol, "5m", "1d");
                } else {
                    loadChartData(querySymbol, "1d");
                }
                List<TextView> listDay = new LinkedList<>(Arrays.asList(tvWeek, tv3Month, tv6Month, tvYear, tv5Year, tvMax));
                checkStatesAndSetStyle(tvDay, listDay);


            }
        });

        tvWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tinyDB.getListLong(querySymbol +"price5d").isEmpty()){
                    getStockChart(querySymbol, "1d", "5d");
                } else {
                    loadChartData(querySymbol, "5d");
                }
                List<TextView> listDay = new LinkedList<>(Arrays.asList(tvDay, tv3Month, tv6Month, tvYear, tv5Year, tvMax));
                checkStatesAndSetStyle(tvWeek, listDay);
            }
        });

        tv3Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tinyDB.getListLong(querySymbol +"price3mo").isEmpty()){
                    getStockChart(querySymbol, "1d", "3mo");
                } else {
                    loadChartData(querySymbol, "3mo");
                }
                List<TextView> listDay = new LinkedList<>(Arrays.asList(tvDay, tvWeek, tv6Month, tvYear, tv5Year, tvMax));
                checkStatesAndSetStyle(tv3Month, listDay);
            }
        });

        tv6Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tinyDB.getListLong(querySymbol +"price6mo").isEmpty()){
                    getStockChart(querySymbol, "1wk", "6mo");
                } else {
                    loadChartData(querySymbol, "6mo");
                }
                List<TextView> listDay = new LinkedList<>(Arrays.asList(tvDay, tvWeek, tv3Month, tvYear, tv5Year, tvMax));
                checkStatesAndSetStyle(tv6Month, listDay);
            }
        });

        tvYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tinyDB.getListLong(querySymbol +"price1y").isEmpty()){
                    getStockChart(querySymbol, "1d", "1y");
                } else {
                    loadChartData(querySymbol, "1y");

                }
                List<TextView> listDay = new LinkedList<>(Arrays.asList(tvDay, tvWeek, tv3Month, tv6Month, tv5Year, tvMax));
                checkStatesAndSetStyle(tvYear, listDay);
            }
        });

        tv5Year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tinyDB.getListLong(querySymbol +"price5y").isEmpty()){
                    getStockChart(querySymbol, "1mo", "5y");
                } else {
                    loadChartData(querySymbol, "5y");

                }
                List<TextView> listDay = new LinkedList<>(Arrays.asList(tvDay, tvWeek, tv3Month, tv6Month, tvYear, tvMax));
                checkStatesAndSetStyle(tv5Year, listDay);
            }
        });

        tvMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tinyDB.getListLong(querySymbol +"priceMax").isEmpty() || tinyDB.getListLong(querySymbol + "priceMax") != null){
                    getStockChart(querySymbol, "1mo", "max");
                } else {
                    loadChartData(querySymbol, "max");
                }
                List<TextView> listDay = new LinkedList<>(Arrays.asList(tvDay, tvWeek, tv3Month, tv6Month, tvYear, tv5Year));
                checkStatesAndSetStyle(tvMax, listDay);
            }
        });


        adContainer = findViewById(R.id.chart_adContainer);
        bannerAd = findViewById(R.id.bannerAdChart);
        //check if user has premium or not
        if (hasPremium()) {
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



        //OnChartValueSelectedListener
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                Float timeFloat = e.getX();
                long dateToLong = timeFloat.longValue();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm yyyy");
                Calendar cal = Calendar.getInstance(Locale.US);
                cal.setTimeInMillis(dateToLong * 1000L);



                tvStockPrice.setText("$" + String.valueOf(e.getY()));
                tvStockDate.setText(dateFormat.format(cal.getTime()));

            }

            @Override
            public void onNothingSelected() {


                Float maxFloat = lineDataSet1.getXMax();
                long maxToLong = maxFloat.longValue();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd HH:mm yyyy");
                Calendar cal = Calendar.getInstance(Locale.US);
                cal.setTimeInMillis(maxToLong * 1000L);

                tvStockPrice.setText("$" + String.valueOf(lineDataSet1.getYMax()));
                tvStockDate.setText(dateFormat.format(cal.getTime()));

            }
        });


    }

    private void setWindowAnimations() {


        Window window = getWindow();
        Slide slide = new Slide();

        slide.setInterpolator(new FastOutSlowInInterpolator());
        slide.setSlideEdge(Gravity.BOTTOM);
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        slide.excludeTarget(R.id.bttnAddStock, true);
        slide.setDuration(400);

        window.setExitTransition(slide);
        window.setReturnTransition(slide);
        window.setBackgroundDrawable(getResources().getDrawable(R.color.colorPrimary));

    }

    @Override
    public void onBackPressed(){

        if (getSupportFragmentManager().getBackStackEntryCount() != 0){
            fragmentContainerView.setVisibility(View.GONE);
        }
        {
            setWindowAnimations();
            Intent intent = new Intent(StockChartActivity.this, MainActivity.class);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());

            finish();
        }
    }

    private boolean hasPremium() {

        tinyDB = new TinyDB(StockChartActivity.this);
        return tinyDB.getBoolean("PREMIUM");

    }


   /* private void checkPremiumSetConstraints(){

        ConstraintLayout constraintLayout = findViewById(R.id.mainChartLayout);
        ConstraintSet constraintSet = new ConstraintSet();

        if (hasPremium()){

            adContainer.setVisibility(View.GONE);

            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.statisticsRatingsLayout, ConstraintSet.TOP, R.id.ivDivider, ConstraintSet.BOTTOM, 136);
            constraintSet.applyTo(constraintLayout);


        } else {

            adContainer.setVisibility(View.VISIBLE);
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.statisticsRatingsLayout, ConstraintSet.TOP, R.id.chart_adContainer, ConstraintSet.BOTTOM, 108);
            constraintSet.applyTo(constraintLayout);

        }
    }*/

    public void checkStatesAndSetStyle(TextView textToChange, List<TextView> textToKeep){

        textToChange.setAlpha(1.0f);
        textToChange.setBackground(getDrawable(R.drawable.datechange_animation));
        //TransitionDrawable transition = (TransitionDrawable) textToChange.getBackground();
        //transition.startTransition(500);

        for (TextView date : textToKeep) {
            date.setAlpha(0.7f);
            date.setBackground(null);
            //transition.reverseTransition(500);
        }


    }


    public void getIntentAndSetValues(){

        //TODO CHANGE FOR CLEANER CODE
        Bundle bundleData = getIntent().getExtras();

        querySymbol = getIntent().getStringExtra("stockSymbol");
        stockPrice = getIntent().getDoubleExtra("stockPrice", defaultIntentValue);
        stockRating = getIntent().getIntExtra("stockRating", defaultIntValue);

        peRatio = getIntent().getDoubleExtra("peRatio", defaultIntentValue);
        currentDiv = getIntent().getDoubleExtra("currentDiv", defaultIntentValue);
        fiveYearDiv = getIntent().getDoubleExtra("fiveYearDiv", defaultIntentValue);
        payoutRatio = getIntent().getDoubleExtra("payoutRatio", defaultIntentValue);
        earningGrowth = getIntent().getDoubleExtra("earningsGrowth", defaultIntentValue);
        quickRatio = getIntent().getDoubleExtra("quickRatio", defaultIntentValue);
        pegRatio = getIntent().getDoubleExtra("pegRatio", defaultIntentValue);
        epsActual = getIntent().getDoubleExtra("epsActual", defaultIntentValue);
        epsGrowth = getIntent().getDoubleExtra("epsGrowth", defaultIntentValue);


        targetHighPrice = getIntent().getDoubleExtra("highPrice", defaultIntentValue);
        targetLowPrice = getIntent().getDoubleExtra("lowPrice", defaultIntentValue);
        targetMedianPrice = getIntent().getDoubleExtra("medianPrice", defaultIntentValue);
        recommendation = getIntent().getStringExtra("recommendation");

        marketCap = getIntent().getLongExtra("marketCap", 0);
        sharesOut = getIntent().getLongExtra("sharesOut", 0);
        beta = getIntent().getDoubleExtra("beta", 0);



        tvSymbol.setText(querySymbol);
        tvStockPrice.setText("$" + String.format("%.2f", stockPrice));

        tvPeRatio.setText(String.format("%.2f", peRatio));
        tvDividendYield.setText(String.format("%.2f", currentDiv * 100 ) + "%");
        tvFiveYearDiv.setText(String.format("%.2f", fiveYearDiv * 100 ) + "%");
        tvPayoutRatio.setText(String.format("%.2f", payoutRatio * 100) + "%");
        tvEarningGrowth.setText(String.format("%.2f", earningGrowth * 100)  + "%");
        tvQuickRatio.setText(String.format("%.2f", quickRatio));
        tvPegRatio.setText(String.format("%.2f", pegRatio));
        tvEpsGrowth.setText(String.format("%.2f", epsGrowth * 100) + "%");
        //tvEpsActual.setText("$" + String.format("%.2f", epsActual));

        tvHighPrice.setText("$" + String.format("%.2f", targetHighPrice));
        tvLowPrice.setText("$" + String.format("%.2f", targetLowPrice));
        tvMedianPrice.setText("$" + String.format("%.2f", targetMedianPrice));
        tvCurrentPrice.setText("$" + String.format("%.2f", stockPrice));
        //tvRecommendation.setText("Price Recomm. " + recommendation.toUpperCase());


        PeRating = getIntent().getIntExtra("PeRating", defaultIntValue);
        DivRating = getIntent().getIntExtra("DivRating", defaultIntValue);
        PayoutRating = getIntent().getIntExtra("PayoutRating", defaultIntValue);
        EarningsRating = getIntent().getIntExtra("EarningsRating", defaultIntValue);
        QuickRating = getIntent().getIntExtra("QuickRating", defaultIntValue);
        PegRating = getIntent().getIntExtra("PegRating", defaultIntValue);
        EpsRating = getIntent().getIntExtra("EpsRating", defaultIntValue);

        tvPeRating.setText(String.valueOf(PeRating) + "/5");

        if (currentDiv == 0) {
            tvDivRating.setText("Doesn't pay div.");
        } else {
            tvDivRating.setText(String.valueOf(DivRating) + "/5");
        }

        if (currentDiv == 0) {
            tvPayoutRating.setText("Doesn't pay div.");
        } else {
            tvPayoutRating.setText(String.valueOf(PayoutRating) + "/5");
        }

        tvEarningsRating.setText(String.valueOf(EarningsRating) + "/5");
        tvQuickRating.setText(String.valueOf(QuickRating) + "/5");
        tvPegRating.setText(String.valueOf(PegRating) + "/5");
        tvPegRating.setText(String.valueOf(PegRating) + "/5");
        tvEpsRating.setText(String.valueOf(EpsRating) + "/5"); //problem

        switch (stockRating){
            case 0:
                List<ImageView> listDark0 = new LinkedList<>(Arrays.asList(star1, star2, star3, star4, star5));
                setStarRatingOpacities(null, listDark0);
                break;
            case 1:
                List<ImageView> listLight1 = new LinkedList<>(Arrays.asList(star1));
                List<ImageView> listDark1 = new LinkedList<>(Arrays.asList(star2, star3, star4, star5));
                setStarRatingOpacities(listLight1, listDark1);
                break;
            case 2:
                List<ImageView> listLight2 = new LinkedList<>(Arrays.asList(star1, star2));
                List<ImageView> listDark2 = new LinkedList<>(Arrays.asList(star3, star4, star5));
                setStarRatingOpacities(listLight2, listDark2);
                break;
            case 3:
                List<ImageView> listLight3 = new LinkedList<>(Arrays.asList(star1, star2, star3));
                List<ImageView> listDark3 = new LinkedList<>(Arrays.asList(star4, star5));
                setStarRatingOpacities(listLight3, listDark3);
                break;
            case 4:
                List<ImageView> listLight4 = new LinkedList<>(Arrays.asList(star1, star2, star3, star4));
                List<ImageView> listDark4 = new LinkedList<>(Arrays.asList(star5));
                setStarRatingOpacities(listLight4, listDark4);
                break;
            case 5:
                List<ImageView> listLight5 = new LinkedList<>(Arrays.asList(star1, star2, star3, star4, star5));
                setStarRatingOpacities(listLight5, null);
                break;


        }


    }

    public void setStarRatingOpacities(List<ImageView> imagesToChange, List<ImageView> imagesToKeep){

        if (imagesToChange != null) {
            for (ImageView image : imagesToChange) {
                image.setAlpha(1.0f);
            }
        } else {
            Log.d("Crash", "No rating for this stock.");
        }

        if (imagesToKeep != null) {
            for (ImageView image : imagesToKeep) {
                image.setAlpha(0.4f);
            }

        } else {
            Log.d("Crash", "No rating for this stock.");
        }


    }

    private void getStockChart(final String symbol, String interval, final String range){

        Call<ChartApiResponse> call = IJsonPlaceHolderAPI.getCharts(KEY, symbol, interval, range);

        call.enqueue(new Callback<ChartApiResponse>() {
            @Override
            public void onResponse(Call<ChartApiResponse> call, Response<ChartApiResponse> response) {

                if (!response.isSuccessful()){
                    lineChart.invalidate();
                    lineChart.setNoDataText("Sorry! Couldn't find this stock's data");
                } else {

                    ChartApiResponse chart = response.body();
                    ChartResponse chartResponse = chart.getChart();
                    ArrayList<StockChartResult> results = chartResponse.getChartResult();
                    StockChartResult resultIndex0 = results.get(0);

                    StockChartResult indicators = resultIndex0.getIndicators();


                    ArrayList<Double> stockPriceList;

                    //for price values going y
                    try {
                        ArrayList<AdjCloseResult> adjclose = indicators.getAdjclose();
                        AdjCloseResult adjcloseIndex0 = adjclose.get(0);
                        stockPriceList = adjcloseIndex0.getAdjclose();
                    } catch (Exception e){
                        ArrayList<AdjCloseResult> quote = indicators.getQuote();
                        AdjCloseResult quoteIndex0 = quote.get(0);
                        stockPriceList = quoteIndex0.getQuote();

                    }
                    //for time values going x
                    ArrayList<Long> timestamps = resultIndex0.getTimestamp();


                    if (timestamps != null && stockPriceList != null) {
                        if (timestamps.size() == stockPriceList.size()) {
                            createChart(timestamps, stockPriceList, symbol, range);
                        }
                    } else {
                        Toast.makeText(StockChartActivity.this, "Couldn't create chart, because data is invalid.", Toast.LENGTH_SHORT).show();
                        lineChart.setNoDataText("Sorry! Stock chart data is invalid.");

                    }

                }


            }

            @Override
            public void onFailure(Call<ChartApiResponse> call, Throwable t) {
                lineChart.invalidate();
                lineChart.setNoDataText("Sorry! Couldn't find this stock's data");
            }
        });


    }

    private void createChart(ArrayList<Long> timestamps, ArrayList<Double> prices, String stockSymbol, String range){

        ArrayList<Long> pricesToLong = new ArrayList<>();

        //Converts Doubles arrayList to Longs arrayList so It will match the x axis data
        for(int i = 0; i < prices.size(); i++){

            Double d = prices.get(i);

            if(d != null){
                pricesToLong.add(d.longValue());
            } else {

                //If first input is null
                if(i == 0){

                   for(int loopPrice = i + 1; i < prices.size(); i++) {

                       if (prices.get(loopPrice) != null) {
                           pricesToLong.add(prices.get(loopPrice).longValue());
                           break;
                       }

                   }

                    //if starting from 0 all inputs are null, and doesn't break out of loop. stop everything

                } else {
                    //If any other input is null (beyond first)
                    Double previousPrice = prices.get(i - 1);
                    pricesToLong.add(previousPrice.longValue());
                    Toast.makeText(this, "Due to API missinputs, some prices may be incorrect.", Toast.LENGTH_SHORT).show();
                }
            }


        }

        if (!dataVals.isEmpty()) {
            dataVals.clear();

        }

        for (int i = 0; i < timestamps.size(); i++) {
            if (timestamps.get(i) != null && pricesToLong.get(i) != null) {
                dataVals.add(new Entry(timestamps.get(i), pricesToLong.get(i)));
            } else{
                lineChart.setNoDataText("Sorry, data is unavailable.");
                break;
            }
        }



        lineDataSet1 = new LineDataSet(dataVals, "stockChart");
        LineData data = new LineData(lineDataSet1);

        lineChart.setData(data);
        setChartStyles();
        lineChart.invalidate();

        saveChartData(timestamps, pricesToLong ,stockSymbol, range);

    }

    public void saveChartData(ArrayList<Long> arrayListOfTime, ArrayList<Long> arrayListOfPrice,String stockSymbol, String range){

            tinyDB = new TinyDB(StockChartActivity.this);

            long currentTimeMillis = System.currentTimeMillis();

            switch (range){
               case "1d":

                   tinyDB.putListLong(stockSymbol + "time1d", arrayListOfTime);
                   tinyDB.putListLong( stockSymbol + "price1d", arrayListOfPrice);

                   if (hasPremium()) {

                       long expiryDate1dPremium = currentTimeMillis + TimeUnit.HOURS.toMillis(1);
                       tinyDB.putLong(stockSymbol + "expiryDate1d", expiryDate1dPremium);

                   } else {

                       long expiryDate1d = currentTimeMillis + TimeUnit.DAYS.toMillis(1);
                       tinyDB.putLong(stockSymbol + "expiryDate1d", expiryDate1d);

                   }

                   break;
               case "5d":

                   tinyDB.putListLong(stockSymbol + "time5d", arrayListOfTime);
                   tinyDB.putListLong( stockSymbol + "price5d", arrayListOfPrice);

                   if (hasPremium()){

                       long expiryDate5dPremium = currentTimeMillis + TimeUnit.DAYS.toMillis(1);
                       tinyDB.putLong(stockSymbol + "expiryDate5d", expiryDate5dPremium);

                   } else {

                       long expiryDate5d = currentTimeMillis + TimeUnit.DAYS.toMillis(3);
                       tinyDB.putLong(stockSymbol + "expiryDate5d", expiryDate5d);

                   }

                   break;
               case "3mo":

                   tinyDB.putListLong(stockSymbol + "time3mo", arrayListOfTime);
                   tinyDB.putListLong( stockSymbol + "price3mo", arrayListOfPrice);

                   if (hasPremium()){

                       long expiryDate3moPremium = currentTimeMillis + TimeUnit.DAYS.toMillis(3);
                       tinyDB.putLong(stockSymbol + "expiryDate3mo", expiryDate3moPremium);

                   } else {

                       long expiryDate3mo = currentTimeMillis + TimeUnit.DAYS.toMillis(10);
                       tinyDB.putLong(stockSymbol + "expiryDate3mo", expiryDate3mo);

                   }

                   break;
               case "6mo":

                   tinyDB.putListLong(stockSymbol + "time6mo", arrayListOfTime);
                   tinyDB.putListLong( stockSymbol + "price6mo", arrayListOfPrice);

                   if (hasPremium()){

                       long expiryDate6moPremium = currentTimeMillis + TimeUnit.DAYS.toMillis(3);
                       tinyDB.putLong(stockSymbol + "expiryDate6mo", expiryDate6moPremium);

                   } else {

                       long expiryDate6mo = currentTimeMillis + TimeUnit.DAYS.toMillis(14);
                       tinyDB.putLong(stockSymbol + "expiryDate6mo", expiryDate6mo);

                   }

                   break;
               case "1y":

                   tinyDB.putListLong(stockSymbol + "time1y", arrayListOfTime);
                   tinyDB.putListLong( stockSymbol + "price1y", arrayListOfPrice);

                   if (hasPremium()){
                       long expiryDate1yPremium = currentTimeMillis + TimeUnit.DAYS.toMillis(3);
                       tinyDB.putLong(stockSymbol + "expiryDate1y", expiryDate1yPremium);

                   } else {
                       long expiryDate1y = currentTimeMillis + TimeUnit.DAYS.toMillis(14);
                       tinyDB.putLong(stockSymbol + "expiryDate1y", expiryDate1y);

                   }

                   break;
               case "5y":

                   tinyDB.putListLong(stockSymbol + "time5y", arrayListOfTime);
                   tinyDB.putListLong( stockSymbol + "price5y", arrayListOfPrice);

                   if (hasPremium()){

                       long expiryDate5yPremium = currentTimeMillis + TimeUnit.DAYS.toMillis(3);
                       tinyDB.putLong(stockSymbol + "expiryDate5y", expiryDate5yPremium);

                   } else {

                       long expiryDate5y = currentTimeMillis + TimeUnit.DAYS.toMillis(14);
                       tinyDB.putLong(stockSymbol + "expiryDate5y", expiryDate5y);

                   }

                   break;
               case "max":

                   tinyDB.putListLong(stockSymbol + "timeMax", arrayListOfTime);
                   tinyDB.putListLong( stockSymbol + "priceMax", arrayListOfPrice);

                   if (hasPremium()){

                       long expiryDateMaxPremium = currentTimeMillis + TimeUnit.DAYS.toMillis(3);
                       tinyDB.putLong(stockSymbol + "expiryDateMax", expiryDateMaxPremium);

                   } else {

                       long expiryDateMax = currentTimeMillis + TimeUnit.DAYS.toMillis(14);
                       tinyDB.putLong(stockSymbol + "expiryDate5y", expiryDateMax);

                   }

                   break;
           }


    }



    private void loadChartData(String stockSymbol, String range) {

        ArrayList<Long> arrayListOfPrice = new ArrayList<>();
        ArrayList<Long> arrayListOfTime = new ArrayList<>();


        tinyDB = new TinyDB(StockChartActivity.this);

        dataVals.clear();

        long currentTime = System.currentTimeMillis();

        switch (range) {
            case "1d":

                if (currentTime > tinyDB.getLong(stockSymbol + "expiryDate1d")) {

                    getStockChart(stockSymbol, "5m", "1d");

                } else {

                    arrayListOfTime = tinyDB.getListLong(stockSymbol + "time1d");
                    arrayListOfPrice = tinyDB.getListLong(stockSymbol + "price1d");

                }
                break;

            case "5d":

                if (currentTime > tinyDB.getLong(stockSymbol + "expiryDate5d")) {

                    getStockChart(stockSymbol, "1d", "5d");

                } else {

                    arrayListOfTime = tinyDB.getListLong(stockSymbol + "time5d");
                    arrayListOfPrice = tinyDB.getListLong(stockSymbol + "price5d");

                }
                break;
            case "3mo":
                if (currentTime > tinyDB.getLong(stockSymbol + "expiryDate3mo")) {

                    getStockChart(stockSymbol, "1d", "3mo");

                } else {
                    arrayListOfTime = tinyDB.getListLong(stockSymbol + "time3mo");
                    arrayListOfPrice = tinyDB.getListLong(stockSymbol + "price3mo");
                }
                break;
            case "6mo":

                if (currentTime > tinyDB.getLong(stockSymbol + "expiryDate6mo")) {

                    getStockChart(stockSymbol, "1wk", "6mo");

                } else {

                    arrayListOfTime = tinyDB.getListLong(stockSymbol + "time6mo");
                    arrayListOfPrice = tinyDB.getListLong(stockSymbol + "price6mo");

                }

                break;
            case "1y":

                if (currentTime > tinyDB.getLong(stockSymbol + "expiryDate1y")) {

                    getStockChart(stockSymbol, "1wk", "1y");

                } else {

                    arrayListOfTime = tinyDB.getListLong(stockSymbol + "time1y");
                    arrayListOfPrice = tinyDB.getListLong(stockSymbol + "price1y");

                }
                break;
            case "5y":

                if (currentTime > tinyDB.getLong(stockSymbol + "expiryDate5y")) {

                    getStockChart(querySymbol, "1mo", "5y");

                }else {

                    arrayListOfTime = tinyDB.getListLong(stockSymbol + "time5y");
                    arrayListOfPrice = tinyDB.getListLong(stockSymbol + "price5y");

                }
                break;
            case "max":

                if (currentTime > tinyDB.getLong(stockSymbol + "expiryDateMax")) {

                    getStockChart(querySymbol, "1mo", "max");


                } else {

                    arrayListOfTime = tinyDB.getListLong(stockSymbol + "timeMax");
                    arrayListOfPrice = tinyDB.getListLong(stockSymbol + "priceMax");

                }
                break;

        }

        for (int i = 0; i < arrayListOfTime.size(); i++) {
            dataVals.add(new Entry(arrayListOfTime.get(i), arrayListOfPrice.get(i)));
        }


        lineDataSet1 = new LineDataSet(dataVals, "stockChart");
        LineData data = new LineData(lineDataSet1);
        lineChart.setData(data);
        setChartStyles();

        lineChart.invalidate();


    }
    private void setChartStyles(){


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(18);
        xAxis.setTextColor(getResources().getColor(R.color.textColorPrimary));
        xAxis.setEnabled(false);

        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisLeft.setEnabled(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setTextSize(18);
        yAxisRight.setTextColor(getResources().getColor(R.color.textColorPrimary));
        yAxisRight.setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);

        Description description = lineChart.getDescription();
        description.setEnabled(false);

        lineChart.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.setExtraBottomOffset(5);
        lineChart.setDragDecelerationEnabled(true);
        lineChart.setDragDecelerationFrictionCoef(1);

        lineDataSet1.setColor(getResources().getColor(R.color.blue));
        lineDataSet1.setLineWidth(3.5f);
        lineDataSet1.setHighlightLineWidth(1);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setValueTextSize(0.0f);
        lineDataSet1.setHighLightColor(getResources().getColor(R.color.monochrom));
        lineDataSet1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        lineChart.animateX(250, Easing.Linear);
        lineChart.invalidate();

    }

}