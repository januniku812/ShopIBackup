package com.shopi.android.receiptreader;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.ViewManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;


import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BillingManager {
    private BillingClient billingClient;
    private final Context context;

    public BillingManager(Context context){
        this.context = context;
        billingClient = BillingClient.newBuilder(context).
                setListener(new PurchasesUpdatedListener() {
                    @Override
                    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null)
                        {
                          for(Purchase purchase: purchases){
                              handlePurchase(purchase);
                          }
                        }else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                            // Handle user canceled the purchase
                            String string = "Purchase canceled";
                            Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            // Handle other errors
                            String string = "Error occurred during purchase processing";
                            Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }

                }).
                enablePendingPurchases()
                .build();

        startBillingClientConnection();
    }

    private void startBillingClientConnection(){
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                // handle disconnect
                //TO-DO: IMPLEMENT DISCONNECT HANDLE
                String string = "Purchased disconnected - check internet connection";
                Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
                toast.show();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    queryPurchases();
                }
            }
        });
    }

    public void launchBillingFlow(Activity activity, String skuId)
    {
        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(Collections.singletonList(skuId))
                .setType(BillingClient.SkuType.INAPP)
                .build();

        billingClient.querySkuDetailsAsync(params, new SkuDetailsResponseListener(){

            @Override
            public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                    if(!list.isEmpty()){
                        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                                .setSkuDetails(list.get(0))
                                .build();
                        // how to pass activity parameter
                        billingClient.launchBillingFlow(activity, billingFlowParams);
                    }
                }
            }
        });


    }

    QueryProductDetailsParams queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
                    .setProductList(
                            ImmutableList.of(
                                    QueryProductDetailsParams.Product.newBuilder()
                                            .setProductId("product_id_example")
                                            .setProductType(BillingClient.ProductType.SUBS)
                                            .build()))
                    .build();



    public void queryPurchases() {
        billingClient.queryPurchasesAsync(String.valueOf(queryProductDetailsParams), new PurchasesResponseListener() {
            @Override
            public void onQueryPurchasesResponse(@NonNull BillingResult billingResult, @NonNull List<Purchase> purchases) {
                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null){
                    for(Purchase purchase: purchases){
                        if(!purchase.getSkus().get(0).equals(Constants.skuId)) {
                            handlePurchase(purchase);
                        }
                    }
                }
            }
        });
    }

    private final AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                String string = "You're purchase has been successfully processed!";
                Toast toast = Toast.makeText(context, string, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };
    private void handlePurchase(Purchase purchase){
        if(purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED ){
            AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.getPurchaseToken())
                    .build();
            billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
        }
    }
    private boolean hasUserPurchasedSku() {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        return prefs.getBoolean("purchased_" + Constants.skuId, false);
    }
    private void savePurchaseState() {
        SharedPreferences prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("purchased_" + Constants.skuId, true);
        editor.apply();
    }
}
