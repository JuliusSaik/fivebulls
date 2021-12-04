package com.saikauskas.julius.fivebulls;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.opencsv.CSVReader;
import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StocksList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class StockSearchActivity extends AppCompatActivity {

    //Reycler View Values
    private RecyclerView recyclerView;
    private StocksListAdapter adapter;

    //Regular xml values
    EditText searchStocks;

    private ArrayList<StocksList> stocksList = new ArrayList<>();
    private ArrayList<StocksList> filteredList = new ArrayList<>();

    FloatingActionButton fabBack;

    String nasdaqSymbol, nasdaqFullName, sector, industry;

    TinyDB tinyDB;
    FrameLayout adContainer;
    AdView bannerAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_search);


        searchStocks = findViewById(R.id.etSearch);
        searchStocks.requestFocus();
        fabBack = findViewById(R.id.fabSearchBack);

        recyclerView = findViewById(R.id.stockSearchRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        getAllStocks();

        searchStocks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                adapter.getFilter().filter(editable.toString().toLowerCase());


            }
        });


        adapter.setOnItemClickListener(new StocksListAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int pos) {


                String querySymbol = stocksList.get(pos).getSymbol();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("symbol", querySymbol);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();


            }
        });

        tinyDB = new TinyDB(StockSearchActivity.this);

        bannerAd = findViewById(R.id.bannerAdSearch);
        adContainer = findViewById(R.id.searchAdContainer);
        if (!hasPremium()){
            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAd.loadAd(adRequest);

        } else {
            bannerAd.setVisibility(View.GONE);
            //checkPremiumSetConstraints(false);
        }

        fabBack.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    private boolean hasPremium() {
        tinyDB = new TinyDB(StockSearchActivity.this);
        return tinyDB.getBoolean("PREMIUM");
    }


    private void checkPremiumSetConstraints(boolean isAdLoaded){

        ConstraintLayout constraintLayout = findViewById(R.id.searchConsraintLayout);
        ConstraintSet constraintSet = new ConstraintSet();

        if (hasPremium()){

            adContainer.setVisibility(View.GONE);

            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.stockSearchRecycler, ConstraintSet.TOP, R.id.etSearch, ConstraintSet.BOTTOM, 300);
            constraintSet.applyTo(constraintLayout);

        } else {

            if (isAdLoaded) {

                adContainer.setVisibility(View.VISIBLE);

                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.stockSearchRecycler, ConstraintSet.TOP, R.id.searchAdContainer, ConstraintSet.BOTTOM, 620);
                constraintSet.applyTo(constraintLayout);

            } else {

                adContainer.setVisibility(View.GONE);

                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.stockSearchRecycler, ConstraintSet.TOP, R.id.etSearch, ConstraintSet.BOTTOM, 300);
                constraintSet.applyTo(constraintLayout);
            }

        }
    }



    private void getAllStocks() {

        InputStream stocks_list = getResources().openRawResource(R.raw.allstocks_list);
        CSVReader stocksReader = new CSVReader(new InputStreamReader(stocks_list));

        try {

            String[] stocksLine;

           while ((stocksLine = stocksReader.readNext()) != null){

                nasdaqSymbol = stocksLine[0];
                nasdaqFullName = stocksLine[1];

                sector = stocksLine[2];
                industry = stocksLine[3];

                if (sector.isEmpty() && industry.isEmpty()){
                    sector = "N/A";
                    industry = "N/A";
                }

                stocksList.add(new StocksList(nasdaqSymbol, nasdaqFullName, sector, industry));

           }


            adapter = new StocksListAdapter(stocksList);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /*
    private void Filter(String searched) {

        for (StocksList stock : stocksList ){
            if (stock.getSymbol().toLowerCase().contains(searched) || stock.getFullName().toLowerCase().contains(searched)
                    || stock.getSector().toLowerCase().contains(searched)){
                filteredList.add(stock);
            }
        }
        //set adapter.onCLick here and use for loop of getting filtered list symbol and size
        adapter = new StocksListAdapter(filteredList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

     */
}