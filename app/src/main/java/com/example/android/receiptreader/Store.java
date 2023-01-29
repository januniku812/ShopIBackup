package com.example.android.receiptreader;

import java.util.ArrayList;

public class Store {
    ArrayList<StoreUserItem> storeUserItems;
    String storeName;
    boolean ifHighlighted = false;

    public boolean isIfHighlighted() {
        return ifHighlighted;
    }

    public void setIfHighlighted(boolean ifHighlighted) {
        this.ifHighlighted = ifHighlighted;
    }

    public Store() {

    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Store(String storeName, ArrayList<StoreUserItem> storeUserItems) {
        this.storeName = storeName;
        this.storeUserItems = storeUserItems;
    }

    public ArrayList<StoreUserItem> getStoreUserItems() {
        return storeUserItems;
    }

    public void setStoreUserItems(ArrayList<StoreUserItem> storeUserItems) {
        this.storeUserItems = storeUserItems;
    }
}
