package com.saikauskas.julius.fivebulls;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements PurchasesUpdatedListener  {

    BillingClient billingClient;

    private static final String SKU_NAME = "fivebulls_subscription";
    private static final String BASE_64 =  new Secrets().gethMeoxORw("com.saikauskas.julius.fivebulls");

    private TextView bttnBuy, tvVersion;
    //FloatingActionButton tvPremium;
    private FloatingActionButton fabBack, bttnReportBug, bttnRateApp, bttnDisclaimer;
    private ImageView ivCrown;

    private SwitchCompat switchNightMode;

    private TinyDB tinyDB;


    private FrameLayout adContainer;
    private AdView bannerAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tinyDB = new TinyDB(SettingsActivity.this);

        switchNightMode = findViewById(R.id.switchDarkTheme);
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            switchNightMode.setChecked(true);
        }

        switchNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    tinyDB.putBoolean("IsNightMode", true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    tinyDB.putBoolean("IsNightMode", false);
                }

            }
        });

        bttnBuy = findViewById(R.id.bttnGetPremium);
        tvVersion = findViewById(R.id.tvVersion);
        //tvPremium = findViewById(R.id.fabSettingsBack);
        fabBack = findViewById(R.id.fabSettingsBack);

        bttnReportBug = findViewById(R.id.bttnReportBug);
        bttnRateApp = findViewById(R.id.bttnRateApp);
        bttnDisclaimer = findViewById(R.id.bttnDisclaimer);

        ivCrown = findViewById(R.id.ivCrown);
        bannerAd = findViewById(R.id.bannerAdSettings);
        adContainer = findViewById(R.id.settings_adContainer);

        fabBack.setOnClickListener(view -> onBackPressed());

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.setText("fivebulls version: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        createBillingClient();
        //establishes connection and check whether subscription is in google play cache(subscribed or not)
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(SettingsActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {

                if (isResultOK(billingResult)){

                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(SUBS);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();

                    //If purchases List is not empty means user has subscribtion
                    if (queryPurchases != null && queryPurchases.size() > 0){
                        handlePurchases(queryPurchases);
                    } else {
                        saveSubscribeValueToPref(false);
                    }
                }

            }
        });

        bttnBuy.setOnClickListener(v -> {
            subscribe();
        });

        setVisibilityValues();


        bttnReportBug.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","juliusaikauskas@gmail.com", null));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Fivebulls bug.");

            startActivity(Intent.createChooser(intent, "Send Email"));
        });

        bttnRateApp.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + getPackageName())));
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        });

        bttnDisclaimer.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsHelpActivity.class);
            startActivity(intent);
        });

    }

    private void setVisibilityValues() {

        if (hasSubscriptionFromPref()){
            ivCrown.setVisibility(View.VISIBLE);
            bttnBuy.setText("Owned");
            bttnBuy.setClickable(false);
            adContainer.setVisibility(View.GONE);


        } else {
            ivCrown.setVisibility(View.GONE);
            bttnBuy.setText("Get Premium");
            bttnBuy.setClickable(true);
            adContainer.setVisibility(View.VISIBLE);

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(@NonNull @NotNull InitializationStatus initializationStatus) {

                }
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            bannerAd.loadAd(adRequest);
        }

    }

   /* private SharedPreferences getPreferenceObject(){
        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
    }

    private SharedPreferences.Editor getPrefenceEditObject(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
        return pref.edit();
    }

    private boolean hasSubscriptionFromPref(){
        return getPreferenceObject().getBoolean("SUBS_PREMIUM", false);
    }

    private void saveSubscribeValueToPref(boolean value){
        getPrefenceEditObject().putBoolean("SUBS_PREMIUM", value).commit();
    }*/

    private boolean hasSubscriptionFromPref(){

        //premiumSession = new PremiumSession(this);
        tinyDB = new TinyDB(this);
        return tinyDB.getBoolean("PREMIUM");

    }

    private void saveSubscribeValueToPref(boolean hasPremium) {

        tinyDB = new TinyDB(this);
        tinyDB.putBoolean("PREMIUM", hasPremium);
    }

    public void createBillingClient(){
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases()
                .setListener(this)
                .build();
    }

    public boolean isResultOK(BillingResult billingResult){
        return billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK;
    }

    public void subscribe(){

        //check if service is already connected
        if (billingClient.isReady()){
            initiatePurchase();
        }
        //recconect service
        else {
           createBillingClient();

           billingClient.startConnection(new BillingClientStateListener() {
               @Override
               public void onBillingServiceDisconnected() {
                   Toast.makeText(SettingsActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
               }

               @Override
               public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                    if (isResultOK(billingResult)){
                        initiatePurchase();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Error " + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
                    }
               }
           });
        }
    }

    private void initiatePurchase() {

        List<String> skuList = new ArrayList<>();
        skuList.add(SKU_NAME);

        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);

        BillingResult billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        if (isResultOK(billingResult)){
            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> skuDetailsList) {
                            if (isResultOK(billingResult)){
                                if (skuDetailsList != null && skuDetailsList.size() > 0){

                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                            .setSkuDetails(skuDetailsList.get(0))
                                            .build();
                                    billingClient.launchBillingFlow(SettingsActivity.this, flowParams);

                                } else {
                                    Toast.makeText(SettingsActivity.this, "ID not found in database", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SettingsActivity.this, "Error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Sorry, subscription feature is not supported. Please update the play store.", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {

        //if trying to subscribe to item
        if (isResultOK(billingResult) && purchases != null){
            handlePurchases(purchases);
        }
        //if item is already subscribed to
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED){

            Purchase.PurchasesResult queryAlreadyPurchasedResult = billingClient.queryPurchases(SUBS);
            List<Purchase> alreadyPurchased = queryAlreadyPurchasedResult.getPurchasesList();

            if (alreadyPurchased != null){
                handlePurchases(alreadyPurchased);
            }
        }

        else  if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED){
            Toast.makeText(this, "Purchase canceled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error: " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private void handlePurchases(List<Purchase> purchases) {

        for (Purchase purchase : purchases){
            //if user has purchased item
            if (purchase.getSkus().contains(SKU_NAME) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {

                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    //Invalid purchase show error to user
                    Toast.makeText(this, "Error: Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }

                //if item is purchased but not yet acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams purchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();
                    billingClient.acknowledgePurchase(purchaseParams, ackPurchase);
                }

                //item is purchased and also acknowledged
                else {
                    //Grant entitlement to the user on purchase and restart activity
                    if (!hasSubscriptionFromPref()) {
                        //boolean of hasPremium or not value
                        saveSubscribeValueToPref(true);
                        setVisibilityValues();
                        Toast.makeText(this, "Succesfully Purchased!", Toast.LENGTH_SHORT).show();
                        SettingsActivity.this.recreate();
                    }
                }
            }
            //If purchase is pending
            else if (purchase.getSkus().contains(SKU_NAME) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                Toast.makeText(this, "Purchase is pending...", Toast.LENGTH_SHORT).show();
            }
            //If purchase is unknown mark false
            else if (purchase.getSkus().contains(SKU_NAME) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE){

                saveSubscribeValueToPref(false);
                setVisibilityValues();
                Toast.makeText(this, "Purchase Status is unknown", Toast.LENGTH_SHORT).show();

            }

        }

    }

    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
            if (isResultOK(billingResult)) {
                //listener acknowledges purchase and grants premium
                saveSubscribeValueToPref(true);
                setVisibilityValues();
                SettingsActivity.this.recreate();
            }
        }
    };

    private boolean verifyValidSignature(String signedData, String signature) {

        try {
            return LicenseSecurity.verifyPurchase(BASE_64, signedData, signature);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null){
            billingClient.endConnection();
        }
    }
}