package com.example.android.receiptreader;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StoreUserItemAdapter extends ArrayAdapter<StoreUserItem> {
    public StoreUserItemAdapter(@NonNull Context context, ArrayList<StoreUserItem> storeUserItems) {
        super(context, 0, storeUserItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        //get the{@link StoreUserItem} object located at this position in the list
        StoreUserItem storeUserItem = getItem(position);
        String additionalWeightUnitPriceDetail = storeUserItem.getAdditionalWeightUnitPriceDetail();
        if(newItemView == null) {
            System.out.println("ADDITIONAL WEIGHT DETAIL " +   storeUserItem.getDateOfPurchase() + storeUserItem.getStore() + storeUserItem.getItemName() + " : " + additionalWeightUnitPriceDetail);
            if(additionalWeightUnitPriceDetail != null){
                additionalWeightUnitPriceDetail = additionalWeightUnitPriceDetail.replaceAll("floz", "fl oz");

                newItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_item_with_extra_weight_detail, parent, false);
            }
            else{
              newItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
            }
        }

        //find the text view in the user item individual view and setting it with object name data
        TextView userItemStore = (TextView) newItemView.findViewById(R.id.store_name);
        userItemStore.setText(storeUserItem.getStore());

        //find the date text view in the user item individual view and setting it with object date data
        TextView userItemDate = (TextView) newItemView.findViewById(R.id.store_date);
        userItemDate.setText(storeUserItem.getDateOfPurchase());

        //find the text view in the user item individual view and setting it with object name data
        TextView userItemName = (TextView) newItemView.findViewById(R.id.item_name);
        userItemName.setText(storeUserItem.getItemName());


        //find the text view in the user item individual view and setting it with object amount paid data
        TextView userItemAmountPaid = (TextView) newItemView.findViewById(R.id.total_purchase_price);
        userItemAmountPaid.setText(storeUserItem.getAmountPaid());

        TextView extraWeighBasedUnitPrice = (TextView) newItemView.findViewById(R.id.extra_weight_based_unit_price);
        System.out.println("WANTS COMPARSION UNIT: "+ Constants.wantsPriceComparisonUnit);
        if(extraWeighBasedUnitPrice != null){System.out.println("ADDITIONAL WEIGHT DETAIL REACHED: " + additionalWeightUnitPriceDetail);
            String currentMeasureUnit = Constants.currentMeasureUnit;
            if(Constants.wantsPriceComparisonUnit && !currentMeasureUnit.isEmpty()){
                System.out.println("MADE IT!!: " + additionalWeightUnitPriceDetail + " --> " + additionalWeightUnitPriceDetail.replaceAll("[^\\d.]",""));
                Double ogActualPrice = Double.parseDouble(additionalWeightUnitPriceDetail.replaceAll("[^\\d.]",""));

                String ogMeasurementUnit = additionalWeightUnitPriceDetail.substring(additionalWeightUnitPriceDetail.indexOf("/")+1, additionalWeightUnitPriceDetail.length());
                System.out.println(ogMeasurementUnit);
                System.out.println(ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(ogMeasurementUnit));
                System.out.println("ACTUAL PRICE BEFORE:  " + ogActualPrice);
                Double actualPrice = ogActualPrice / (ItemMeasurementUnits.findRatioBetweenOgMeasurementUnitAndConversionOutcomeUnit(ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(ogMeasurementUnit), ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(currentMeasureUnit)));
                System.out.println("ACTUAL PRICE: " + actualPrice);
                DecimalFormat f = new DecimalFormat("##.00");
                actualPrice = Double.parseDouble(f.format(actualPrice));
                Integer sigFigs = 2;
                if (ogActualPrice != 0) {
                    while(actualPrice == 0.0){
                        sigFigs++;
                        String formatExpression = "##.";
                        for(int i = 0; i < sigFigs; i++) {
                            formatExpression += "0";
                        }
                        DecimalFormat newFormatter = new DecimalFormat(formatExpression);
                        actualPrice = Double.parseDouble(newFormatter.format(ogActualPrice / (ItemMeasurementUnits.findRatioBetweenOgMeasurementUnitAndConversionOutcomeUnit(ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(ogMeasurementUnit), ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(currentMeasureUnit)))));

                    }

                }
                System.out.println("ACTUAL PRICE AFTER DECIMAL FORMAT: " + actualPrice + " FOR ITEM NAME : " +userItemName);
                String newWeightBasedDetail = actualPrice + "/" + currentMeasureUnit.substring(currentMeasureUnit.indexOf("(")+1, currentMeasureUnit.indexOf(")"));
                extraWeighBasedUnitPrice.setText(newWeightBasedDetail.trim().replaceAll("\\s{2,}", " ").trim());
                System.out.println("MADE IT!! NEW ACTUAL PRICE: " + newWeightBasedDetail);

            } else{
                extraWeighBasedUnitPrice.setText(additionalWeightUnitPriceDetail.replaceAll("floz", "fl oz").replaceAll("\\s{2,}", " ").trim());
            }
        }
        return newItemView;

    }
}
