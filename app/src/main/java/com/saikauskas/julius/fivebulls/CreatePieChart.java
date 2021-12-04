package com.saikauskas.julius.fivebulls;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.Edits;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CreatePieChart {

    PieChart pieChart;
    Context context;


    public CreatePieChart(final PieChart pieChart, Context context){
        this.pieChart = pieChart;
        this.context = context;

    }


    //styles pie chart
    public void setupPieChart() {


        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(60.0f);
        pieChart.setUsePercentValues(true);
        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleColor(context.getResources().getColor(R.color.colorPrimary));


        pieChart.setDrawCenterText(true);
        pieChart.setCenterTextColor(context.getResources().getColor(R.color.textColorPrimary));
        pieChart.setCenterTextSize(14);
        pieChart.setCenterText("Sectors");

        Legend l = pieChart.getLegend();
        l.setEnabled(false);

    }


    public void loadPieChartData(ArrayList<String> sectors) {

        ArrayList<PieEntry> pieChartEntries = new ArrayList<>();

        ArrayList<String> noDuplicatesList = new ArrayList<>();
        for (int i = 0; i < sectors.size(); i++){

           while (!noDuplicatesList.contains(sectors.get(i))){
               noDuplicatesList.add(sectors.get(i));
           }

        }

        for (int i = 0; i < noDuplicatesList.size(); i++){

            /*
                Gets number of duplicates through method by taking String param of what to look for. (if no duplicates return 1)
                Divides number from method by the size of entire list to get percentage e.g 3 / 6 = 50%

                Second Parameter of PieEntry() is the String itself, takes in the noDuplicateList string, because index is always < sectors
                getting index of noDuplicatesList returns proper stock name, not duplicate index if they are added latter e.g ["Tech", "Tech, "Med"].

            */

            pieChartEntries.add(new PieEntry(getNumOfDuplicatesInList(sectors, noDuplicatesList.get(i)) / sectors.size(), noDuplicatesList.get(i)));

        }

        String[] colorsStringArray = context.getResources().getStringArray(R.array.pieChartColors);
        ArrayList<Integer> colors = new ArrayList<>();

        for (String s : colorsStringArray) {
            int color = Color.parseColor(s);
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(pieChartEntries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(700, Easing.EaseInOutQuad);

        setClickListener(noDuplicatesList);

    }

    private void setClickListener(final ArrayList<String> noDuplicatesList){

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int index = (int) h.getX();

                float value = e.getY();
                float percentage = Math.round(value * 100);
                pieChart.setCenterText(noDuplicatesList.get(index) + " " + String.valueOf(percentage) + "%");

            }

            @Override
            public void onNothingSelected() {

                pieChart.setCenterText("Sectors");

            }
        });

    }

    public float getNumOfDuplicatesInList(ArrayList<String> sectors, String sector){

        ArrayList<String> numOfDuplicates = new ArrayList<String>();

        for (int i = 0; i < sectors.size(); i++){
            if (sectors.get(i).equals(sector)){
                numOfDuplicates.add(sector);

            }
        }

        if (numOfDuplicates.size() != 0){
            return numOfDuplicates.size();
        } else {
            //if no duplicates means there's 1 element in list
            return 1;
        }
    }



}
