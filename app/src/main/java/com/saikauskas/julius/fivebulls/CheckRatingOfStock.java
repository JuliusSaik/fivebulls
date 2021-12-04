package com.saikauskas.julius.fivebulls;

import android.util.Range;

import java.util.List;

public class CheckRatingOfStock extends ValueLists{

    private double rawPEratio, payoutRatio, currentDiv, quickRatio, earningsGrowth, pegRatio, fiveYearDiv, epsGrowth;
    public int rating = 0;

    public CheckRatingOfStock( double rawPEratio, double payoutRatio, double currentDiv, double fiveYearDiv, double quickRatio, double earningsGrowth, double pegRatio, double epsGrowth){
        this.rawPEratio = rawPEratio;
        this.payoutRatio = payoutRatio;
        this.currentDiv = currentDiv;
        this.fiveYearDiv = fiveYearDiv;
        this.quickRatio = quickRatio;
        this.earningsGrowth = earningsGrowth;
        this.pegRatio = pegRatio;
        this.epsGrowth = epsGrowth;
    }


    public int getEpsGrowthRating(){

        if (epsGrowth > 0){

            List<Range> epsGrowthRanges = getEpsGrowthRanges();

            double uppedEpsGrowth = epsGrowth * 1000;
            double rounded = Math.round(uppedEpsGrowth);

            int intEpsGrowth = (int) rounded;

            for (int i = 0; i < epsGrowthRanges.size(); i++){
                Range range = epsGrowthRanges.get(i);

                if (range.contains(intEpsGrowth)){
                    rating = i + 1;
                    break;
                } else if (intEpsGrowth > 130){
                    rating = 5;
                }
            }

        } else {
            rating = 0;
        }

        return rating;
    }


    public int getPegRatioRating(){

        if (pegRatio > 0){

            List<Range> pegRatioRanges = getPegRatioRanges();

            double uppedPegRatio = pegRatio * 10;
            double rounded = Math.round(uppedPegRatio);

            int intPegRatio = (int) rounded;

            for (int i = 0; i < pegRatioRanges.size(); i++){
                Range range = pegRatioRanges.get(i);

                if (range.contains(intPegRatio)){
                    rating = i + 1;
                    break;
                } else if (intPegRatio > 20){
                    rating = 0;
                }
            }

        } else {
            rating = 0;
        }

        return rating;
    }


    public int getEarningsGrowthRating(){

        if (earningsGrowth > 0){

            List<Range> earningGrowthRanges = getEarningsGrowthRanges();

            double uppedEarningsRatio = earningsGrowth * 100;
            double rounded = Math.round(uppedEarningsRatio);

            int intEarningsGrowth = (int) rounded;

            for (int i = 0; i < earningGrowthRanges.size(); i++){
                Range range = earningGrowthRanges.get(i);

                if (range.contains(intEarningsGrowth)) {
                   rating = i + 1;
                } else if (intEarningsGrowth > 70){
                    rating = 5;
                }
            }


        } else {
            rating = 0;
        }

        return rating;
    }


    public int getQuickRatioRating(){

        if (quickRatio > 0){

            List<Range> quickRatioRanges = getQuickRatioRanges();

            double uppedQuickRatio = quickRatio * 10;
            double rounded = Math.round(uppedQuickRatio);

            int intQuickRatio = (int) rounded;

            for (int i = 0; i < quickRatioRanges.size(); i++){
                Range range = quickRatioRanges.get(i);

                if (range.contains(intQuickRatio)){
                    rating = i + 1;
                    break;
                } else if (intQuickRatio >= 22) {
                    rating = 5;
                    break;
                } else {
                    rating = 0;
                }
            }

        } else {
            rating = 0;
        }

        return rating;
    }

    public int getPayoutRatioRating(){

        if (payoutRatio > 0){

            List<Range> payoutRatioRanges = getPayoutRatioRanges();

            double uppedPayoutRatio = payoutRatio * 100;

            double rounded = Math.round(uppedPayoutRatio);
            int intPayoutRatio = (int) rounded;

            for (int i = 0; i < payoutRatioRanges.size(); i++) {
                Range range = payoutRatioRanges.get(i);

                if (range.contains(intPayoutRatio)) {
                    rating = i + 1;
                    break;
                } else if (intPayoutRatio > 100){
                    rating = 0;
                }
            }

        } else {
            rating = 0;
        }
        return rating;
    }

    public int getDividenDiffRating() {

        //iterates through 5 indexes and 5 ranges if its in range between e.g 10% or 20% returns the rating;
        if (currentDiv > 0 && currentDiv > fiveYearDiv) {
            for (int percentageIncrease = 0; percentageIncrease <= 4; percentageIncrease++) {

                double percentage = 1 + percentageIncrease * 0.1;
                if (currentDiv > fiveYearDiv * percentage && currentDiv < fiveYearDiv * (percentage + 0.1)) { //Creates Range but for doubles.
                    rating = percentageIncrease + 1; // + 1 because range is crated from 10%-20% altough it should start from 0%-10%
                    break;
                }
                else if (currentDiv > fiveYearDiv * 1.5){
                    rating = 5;
                    break;
                }
            }

        } else {
            rating = 0;
        }

        return rating;
    }


    public int getPeRatioRating() {

        if (rawPEratio > 0) {
            List<Range> peRatioRanges = getPeRatioRanges();

            double rounded = Math.round(rawPEratio);
            int intPERatio = (int) rounded;

            for (int i = 0; i < peRatioRanges.size(); i++) {
                Range range = peRatioRanges.get(i);

                if (range.contains(intPERatio)) {
                    rating = i + 1;
                    break;
                } else if (intPERatio > 40){
                    rating = 0;
                    break;
                }
            }

        } else {
            rating = 0;
        }
        return rating;

    }

    public int getDivStockRating(){

        double fullScore  = getDividenDiffRating() + getPayoutRatioRating() + getPeRatioRating() + getQuickRatioRating() +
                getEarningsGrowthRating() + getPegRatioRating() + getEpsGrowthRating();
        double doubleRating = fullScore / 7;

        double roundedRating = Math.round(doubleRating);
        rating = (int) roundedRating;


        return rating;
    }

    public int getNonDivStockRating(){

        double fullScore  = getPeRatioRating() + getQuickRatioRating() + getEarningsGrowthRating() + getPegRatioRating() + getEpsGrowthRating();
        double doubleRating = fullScore / 5;

        double roundedRating = Math.round(doubleRating);
        rating = (int) roundedRating;

        return rating;


    }
}
