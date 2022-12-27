package com.example.android.receiptreader;

public class ShoppingListItem {
    String store;
    String name;
    String toRecordPrice;
    String toRecordQuantity;
    String toRecordUnitPrice;

    public ShoppingListItem(String store, String name) {
        this.store = store;
        this.name = name;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToRecordPrice() {
        return toRecordPrice;
    }

    public void setToRecordPrice(String toRecordPrice) {
        this.toRecordPrice = toRecordPrice;
    }

    public String getToRecordQuantity() {
        return toRecordQuantity;
    }

    public void setToRecordQuantity(String toRecordQuantity) {
        this.toRecordQuantity = toRecordQuantity;
    }

    public String getToRecordUnitPrice() {
        return toRecordUnitPrice;
    }

    public void setToRecordUnitPrice(String toRecordUnitPrice) {
        this.toRecordUnitPrice = toRecordUnitPrice;
    }
}
