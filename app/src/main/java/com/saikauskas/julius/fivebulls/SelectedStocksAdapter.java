package com.saikauskas.julius.fivebulls;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saikauskas.julius.fivebulls.RetrofitObjectActivites.StockItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SelectedStocksAdapter extends RecyclerView.Adapter<SelectedStocksAdapter.ViewHolder> {

    Context context;
    private ArrayList<StockItem> stocksList;
    private static ArrayList<ImageView> starsArray;


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView stockName, stockPrice, stockTickerSymbol;
        public ImageView star1, star2, star3, star4, star5;

        String sector, recommendationKey;

        private double peRatio, dividendYield, fiveYearDiv,  payoutRatio,  earningGrowth,  quickRatio, pegRatio, currentMarketPrice,
                epsActual, epsGrowth, beta;
        private int PeRating, DivRating, PayoutRating, EarningsRating, QuickRating, PegRating, EpsRating, stockRating, stockPosition;
        private long currentTime, marketCap, sharesOut;

        private double highPrice, lowPrice, medianPrice;

        Map<String, Double> targetPrices;


        IJsonPlaceHolderAPI IJsonPlaceHolderAPI;

        TinyDB tinyDB;
        private String stockSymbol;



        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            stockName = itemView.findViewById(R.id.tvStockName);
            stockPrice = itemView.findViewById(R.id.tvStockPrice);
            stockTickerSymbol = itemView.findViewById(R.id.tvTickerSymbol);

            star1 = itemView.findViewById(R.id.ivStar1);
            star2 = itemView.findViewById(R.id.ivStar2);
            star3 = itemView.findViewById(R.id.ivStar3);
            star4 = itemView.findViewById(R.id.ivStar4);
            star5 = itemView.findViewById(R.id.ivStar5);

            List<ImageView> starsList = Arrays.asList(star1, star2, star3, star4, star5);
            starsArray = new ArrayList<>(starsList);

            currentTime = System.currentTimeMillis();


            tinyDB = new TinyDB(itemView.getContext());

            itemView.setOnClickListener(v -> {


                if (currentTime > tinyDB.getLong(stockSymbol + "dataExpiryDate")) {

                   Toast.makeText(itemView.getContext(), "Updating data to most recent quarter...", Toast.LENGTH_SHORT).show();

                    MainActivity mainActivity = new MainActivity();
                    mainActivity.getSelectedStockData(stockSymbol, stockPosition, itemView.getContext());

                    Intent intent = new Intent(itemView.getContext(), StockChartActivity.class);
                    putExtras(intent);

                    itemView.getContext().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) itemView.getContext()).toBundle());

                } else {
                    Intent intent = new Intent(itemView.getContext(), StockChartActivity.class);
                    putExtras(intent);
                    itemView.getContext().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation((Activity) itemView.getContext()).toBundle());
                }



            });




        }

        private void putExtras(Intent intent){

            intent.putExtra("stockSymbol", stockSymbol);
            intent.putExtra("stockPrice", currentMarketPrice);
            intent.putExtra("stockRating", stockRating);

            intent.putExtra("highPrice", highPrice);
            intent.putExtra("lowPrice", lowPrice);
            intent.putExtra("medianPrice", medianPrice);
            intent.putExtra("recommendation", recommendationKey);


            intent.putExtra("peRatio", peRatio);
            intent.putExtra("currentDiv", dividendYield);
            intent.putExtra("fiveYearDiv", fiveYearDiv);
            intent.putExtra("payoutRatio", payoutRatio);
            intent.putExtra("earningsGrowth", earningGrowth);
            intent.putExtra("quickRatio", quickRatio);
            intent.putExtra("pegRatio", pegRatio);
            intent.putExtra("epsGrowth", epsGrowth);
            intent.putExtra("epsActual", epsActual);


            intent.putExtra("PeRating", PeRating);
            intent.putExtra("DivRating", DivRating);
            intent.putExtra("PayoutRating", PayoutRating);
            intent.putExtra("EarningsRating", EarningsRating);
            intent.putExtra("QuickRating", QuickRating);
            intent.putExtra("PegRating", PegRating);
            intent.putExtra("EpsRating", EpsRating);

            intent.putExtra("marketCap", marketCap);
            intent.putExtra("sharesOut", sharesOut);
            intent.putExtra("beta", beta);



        }
    }


    public SelectedStocksAdapter(ArrayList<StockItem> stocksList, Context context){
        this.stocksList = stocksList;
        this.context  = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stockcardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //for regular item values like symbol
        StockItem currentStock = stocksList.get(position);
        holder.stockPosition = position;

        double marketPrice = currentStock.getRaw();

        holder.stockPrice.setText(currentStock.getShortName());
        holder.stockName.setText(currentStock.getSymbol());
        holder.stockTickerSymbol.setText("$" + String.valueOf(marketPrice));
        holder.stockRating = currentStock.getStockRating();

        holder.stockSymbol = currentStock.getSymbol();

        //TargetPrices
        holder.currentMarketPrice = marketPrice;

        holder.highPrice = currentStock.getTargetPrices().get("targetHighPrice");
        holder.lowPrice = currentStock.getTargetPrices().get("targetLowPrice");
        holder.medianPrice = currentStock.getTargetPrices().get("targetMedianPrice");
        holder.recommendationKey = currentStock.getRecommendationKey();

        //For hidden rating calculation values
        holder.peRatio = currentStock.getPeRatio();
        holder.dividendYield = currentStock.getCurrentDiv();
        holder.fiveYearDiv = currentStock.getFiveYearDiv();
        holder.payoutRatio = currentStock.getDoublePayoutRatio();
        holder.earningGrowth = currentStock.getDoubleEarningsGrowth();
        holder.quickRatio = currentStock.getDoubleQuickRatio();
        holder.pegRatio = currentStock.getDoublePegRatio();
        holder.epsActual = currentStock.getDoubleEpsActual();
        holder.epsGrowth = currentStock.getDoubleEpsGrowth();

        //for rating values in stockchartactivity
        holder.PeRating = currentStock.getPeRating();
        holder.DivRating = currentStock.getDivRating();
        holder.PayoutRating = currentStock.getPayoutRating();
        holder.EarningsRating = currentStock.getEarningsRating();
        holder.QuickRating = currentStock.getQuickRating();
        holder.PegRating = currentStock.getPegRating();
        holder.EpsRating = currentStock.getEpsRating();

        holder.marketCap = currentStock.getStockMarketCap();
        holder.sharesOut = currentStock.getStockSharesOut();
        holder.beta = currentStock.getStockBeta();



        holder.sector = currentStock.getSector();

        switch (holder.stockRating){
            case 1:
                holder.star1.setAlpha(1f);
                holder.star2.setAlpha(0.4f);
                holder.star3.setAlpha(0.4f);
                holder.star4.setAlpha(0.4f);
                holder.star5.setAlpha(0.4f);
                break;
            case 2:
                holder.star1.setAlpha(1f);
                holder.star2.setAlpha(1f);
                holder.star3.setAlpha(0.4f);
                holder.star4.setAlpha(0.4f);
                holder.star5.setAlpha(0.4f);
                break;
            case 3:
                holder.star1.setAlpha(1f);
                holder.star2.setAlpha(1f);
                holder.star3.setAlpha(1f);
                holder.star4.setAlpha(0.4f);
                holder.star5.setAlpha(0.4f);
                break;
            case 4:
                holder.star1.setAlpha(1f);
                holder.star2.setAlpha(1f);
                holder.star3.setAlpha(1f);
                holder.star4.setAlpha(1f);
                holder.star5.setAlpha(0.4f);
                break;
            case 5:
                holder.star1.setAlpha(1f);
                holder.star2.setAlpha(1f);
                holder.star3.setAlpha(1f);
                holder.star4.setAlpha(1f);
                holder.star5.setAlpha(1f);
                break;
            default:
                holder.star1.setAlpha(0.4f);
                holder.star2.setAlpha(0.4f);
                holder.star3.setAlpha(0.4f);
                holder.star4.setAlpha(0.4f);
                holder.star5.setAlpha(0.4f);
        }

    }

    @Override
    public int getItemCount() {
        return stocksList.size();
    }

}
