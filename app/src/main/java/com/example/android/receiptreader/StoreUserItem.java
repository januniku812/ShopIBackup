package com.example.android.receiptreader;

import java.io.Serializable;
import java.text.DecimalFormat;

public class StoreUserItem implements Serializable {
    String store;
    String dateOfPurchase;
    String itemName;
    String quantity;
    String totalAmountPaid;
    String unitPrice;

    public String getWithinPackageItemCount() {
        return withinPackageItemCount;
    }
    public void setWithinPackageItemCount(String withinPackageItemCount) {
        this.withinPackageItemCount = withinPackageItemCount;
    }

    String withinPackageItemCount;
    String additionalWeightUnitPriceDetail;

    public String getPricePerMeasurementUnit(){
        if ((additionalWeightUnitPriceDetail == null) || (additionalWeightUnitPriceDetail.isEmpty())){
            return null;
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return String.valueOf(df.format(Double.parseDouble(unitPrice.replaceAll("[^0-9.]","")) / Double.parseDouble(additionalWeightUnitPriceDetail.replaceAll("[^0-9.]",""))));
    }

    public String getAdditionalWeightUnitPriceDetail() {
        return additionalWeightUnitPriceDetail;
    }

    public void setAdditionalWeightUnitPriceDetail(String additionalWeightUnitPriceDetail) {
        this.additionalWeightUnitPriceDetail = additionalWeightUnitPriceDetail;
    }

    public StoreUserItem(String store, String dateOfPurchase, String itemName, String quantity, String totalAmountPaid, String unitPrice, String withinPackageItemCount) {
        this.store = store;
        this.dateOfPurchase = dateOfPurchase;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmountPaid = totalAmountPaid;
        this.unitPrice = unitPrice;
        this.withinPackageItemCount = withinPackageItemCount;
    }
    // weight based item
    public StoreUserItem(String store, String dateOfPurchase, String itemName, String quantity, String totalAmountPaid, String unitPrice) {
        this.store = store;
        this.dateOfPurchase = dateOfPurchase;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmountPaid = totalAmountPaid;
        this.unitPrice = unitPrice;
    }

    public StoreUserItem(String store, String dateOfPurchase, String itemName, String quantity, String totalAmountPaid, String unitPrice, String withinPackageItemCount, String additionalWeightUnitPriceDetail) {
        this.store = store;
        this.dateOfPurchase = dateOfPurchase;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmountPaid = totalAmountPaid;
        this.unitPrice = unitPrice;
        this.withinPackageItemCount = withinPackageItemCount;
        this.additionalWeightUnitPriceDetail = additionalWeightUnitPriceDetail;
    }

    public StoreUserItem(String dateOfPurchase, String unitPrice){
        this.dateOfPurchase = dateOfPurchase;
        this.unitPrice = unitPrice;
    }

    public StoreUserItem(String itemName){
        this.itemName = itemName;
    }

    public StoreUserItem() {

    }

    public String getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(String totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    // getters and setters
    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAmountPaid() {
        return totalAmountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.totalAmountPaid = amountPaid;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}
