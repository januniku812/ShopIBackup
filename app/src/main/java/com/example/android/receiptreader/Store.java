package com.example.android.receiptreader;

import java.util.ArrayList;

public class Store {
    ArrayList<UserItem> userItems;
    String storeName;

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Store(ArrayList<UserItem> userItems, String storeName) {
        this.userItems = userItems;
        this.storeName = storeName;
    }

    public ArrayList<UserItem> getUserItems() {
        return userItems;
    }

    public void setUserItems(ArrayList<UserItem> userItems) {
        this.userItems = userItems;
    }
}
