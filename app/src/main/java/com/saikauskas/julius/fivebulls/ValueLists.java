package com.saikauskas.julius.fivebulls;

import android.util.Range;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ValueLists {

    public List<Range> getEpsGrowthRanges() {

        //0.084 = 8.4% so x 1000 = no doubles
        Range range1 = new Range(10, 30);
        Range range2 = new Range(40, 70);
        Range range3 = new Range(80, 90);
        Range range4 = new Range(100, 110);
        Range range5 = new Range(120, 130);

        List<Range> list = new LinkedList<>(Arrays.asList(range1, range2, range3, range4, range5));
        return list;
    }

    public List<Range> getPegRatioRanges() {

        Range range9 = new Range(1, 9);
        Range range10 = new Range(10, 11);
        Range range12 = new Range(12, 13);
        Range range14 = new Range(14, 16);
        Range range17 = new Range(17, 20);

        List<Range> list = new LinkedList<>(Arrays.asList(range17, range14, range12, range10, range9));
        return list;
    }


    public List<Range> getEarningsGrowthRanges() {

        Range range10 = new Range(1, 10);
        Range range15 = new Range(11, 25);
        Range range25 = new Range(30, 40);
        Range range35 = new Range(41, 55);
        Range range50 = new Range(56, 70);

        List<Range> list = new LinkedList<>(Arrays.asList(range10, range15, range25, range35, range50));
        return list;
    }

    public List<Range> getQuickRatioRanges() {

        Range range20 = new Range(8, 9);
        Range range25 = new Range(10, 11);
        Range range30 = new Range(12, 13);
        Range range35 = new Range(14, 16);
        Range range40 = new Range(17, 22);

        List<Range> list = new LinkedList<>(Arrays.asList(range20, range25, range30, range35, range40));
        return list;
    }

    public List<Range> getPeRatioRanges() {

        Range range20 = new Range(1, 20);
        Range range25 = new Range(21, 25);
        Range range30 = new Range(26, 30);
        Range range35 = new Range(31, 35);
        Range range40 = new Range(36, 40);

        List<Range> list = new LinkedList<>(Arrays.asList(range40, range35, range30, range25, range20)); // add in reverse order cuz index = rating
        return list;
    }

    public List<Range> getPayoutRatioRanges() {

        //purely here for quality control
        Range range25 = new Range(1, 5);
        Range range45 = new Range(76, 100);
        Range range55 = new Range(6, 45);
        Range range65 = new Range(46, 65);
        Range range75 = new Range(66, 75);

        List<Range> list = new LinkedList<>(Arrays.asList(range25, range45, range55, range65, range75));
        return list;
    }

}
