package com.example.android.receiptreader;

import java.util.ArrayList;

public class ShoppingListUserItem {
    ArrayList<ShoppingList> otherShoppingListsExistingIn;
    ArrayList<StoreUserItem> storeUserItemsHistory;
    String name;

    public ArrayList<ShoppingList> getOtherShoppingListsExistingIn() {
        return otherShoppingListsExistingIn;
    }

    public void setOtherShoppingListsExistingIn(ArrayList<ShoppingList> otherShoppingListsExistingIn) {
        this.otherShoppingListsExistingIn = otherShoppingListsExistingIn;
    }

    public ArrayList<StoreUserItem> getStoreUserItemsHistory() {
        return storeUserItemsHistory;
    }

    public void setStoreUserItemsHistory(ArrayList<StoreUserItem> storeUserItemsHistory) {
        this.storeUserItemsHistory = storeUserItemsHistory;
    }

    String lastBought;
    String userQuantity;
    String toRecordPrice;
    String toRecordQuantity;
    String toRecordUnitPrice;
    String toRecordStore;

    public ShoppingListUserItem(String name, String lastBought, String userQuantity) {
        this.name = name;
        this.lastBought = lastBought;
        this.userQuantity = userQuantity;
    }

    public ShoppingListUserItem(String name, String lastBought, String userQuantity, String toRecordPrice, String toRecordQuantity, String toRecordUnitPrice, String toRecordStore) {
        this.name = name;
        this.lastBought = lastBought;
        this.userQuantity = userQuantity;
        this.toRecordPrice = toRecordPrice;
        this.toRecordQuantity = toRecordQuantity;
        this.toRecordUnitPrice = toRecordUnitPrice;
        this.toRecordStore = toRecordStore;
    }

    public ShoppingListUserItem(){

    }

    public String getLastBought() {
        return lastBought;
    }

    public void setLastBought(String lastBought) {
        this.lastBought = lastBought;
    }

    public String getUserQuantity() {
        return userQuantity;
    }

    public void setUserQuantity(String userQuantity) {
        this.userQuantity = userQuantity;
    }

    public String getToRecordStore() {
        return toRecordStore;
    }

    public void setToRecordStore(String toRecordStore) {
        this.toRecordStore = toRecordStore;
    }

    public String getStore() {
        return toRecordStore;
    }

    public void setStore(String store) {
        this.toRecordStore = store;
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
