package com.example.android.receiptreader;

import java.io.Serializable;

public class StoreUserItem implements Serializable {
    String store;
    String dateOfPurchase;
    String itemName;
    String quantity;
    String totalAmountPaid;
    String unitPrice;
    public StoreUserItem(String store, String dateOfPurchase, String itemName, String quantity, String totalAmountPaid, String unitPrice) {
        this.store = store;
        this.dateOfPurchase = dateOfPurchase;
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalAmountPaid = totalAmountPaid;
        this.unitPrice = unitPrice;
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
