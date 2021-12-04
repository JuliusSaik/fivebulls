package com.saikauskas.julius.fivebulls;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class WACCFragment extends DialogFragment {

    private long strTotalDebt, strSharesOut, strMarketCap;
    private String strWeightOfDebt, strWeightOfEquity;
    private String strInterest, strTaxRate, strCostOfDebt, strCostOfEquity, strRiskFreeRate;

    private TextView tvMarketCap, tvTotalDebt, tvSharesOut, tvWeightOfDebt, tvWeightOfEquity;
    private TextView tvInterest, tvTaxRate, tvCostOfDebt, tvCostOfEquity, tvRiskFreeRate;

    public static WACCFragment newInstance() {
        WACCFragment fragment = new WACCFragment();
        return fragment;
    }

    public WACCFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            strMarketCap = getArguments().getLong("MarketCap");
            strTotalDebt = getArguments().getLong("TotalDebt");
            strSharesOut = getArguments().getLong("SharesOut");
            strWeightOfDebt = getArguments().getString("DebtWeight");
            strWeightOfEquity = getArguments().getString("EquityWeight");

            strRiskFreeRate = getArguments().getString("RiskFreeRate");
            strInterest = getArguments().getString("DebtInterest");
            strTaxRate = getArguments().getString("TaxRate");
            strCostOfDebt = getArguments().getString("DebtCost");
            strCostOfEquity = getArguments().getString("EquityCost");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog, container, false);

        tvMarketCap = view.findViewById(R.id.dialogMarketCap);
        tvTotalDebt = view.findViewById(R.id.dialogTotalDebt);
        tvSharesOut = view.findViewById(R.id.dialogSharesOut);
        tvWeightOfDebt = view.findViewById(R.id.dialogWeightOfDebt);
        tvWeightOfEquity = view.findViewById(R.id.dialogWeightOfEquity);

        tvRiskFreeRate = view.findViewById(R.id.dialogRiskFreeRate);
        tvInterest = view.findViewById(R.id.dialogInterest);
        tvTaxRate = view.findViewById(R.id.dialogTaxRate);
        tvCostOfDebt = view.findViewById(R.id.dialogCostOfDebt);
        tvCostOfEquity = view.findViewById(R.id.dialogCostOfEquity);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        tvMarketCap.setText(format(strMarketCap));
        tvTotalDebt.setText(format(strTotalDebt));
        tvSharesOut.setText(format(strSharesOut));
        tvWeightOfDebt.setText(strWeightOfDebt + "%");
        tvWeightOfEquity.setText(strWeightOfEquity + "%");

        tvRiskFreeRate.setText(strRiskFreeRate + "%");
        tvInterest.setText(strInterest + "%");
        tvTaxRate.setText(strTaxRate + "%");
        tvCostOfDebt.setText(strCostOfDebt + "%");
        tvCostOfEquity.setText(strCostOfEquity + "%");




        return view;
    }

    private static final NavigableMap<Long, String> suffixesFx = new TreeMap<>();
    static {
        suffixesFx.put(1_000L, "k");
        suffixesFx.put(1_000_000L, "M");
        suffixesFx.put(1_000_000_000L, "B");
        suffixesFx.put(1_000_000_000_000L, "T");
        suffixesFx.put(1_000_000_000_000_000L, "P");
        suffixesFx.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixesFx.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
}