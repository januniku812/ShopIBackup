package com.example.android.receiptreader;

import java.io.Serializable;
import java.util.ArrayList;

public class ShoppingList implements Serializable {
    String name;
    ArrayList<ShoppingListUserItem> shoppingListUserItemArraylist;

    public ShoppingList(String name, ArrayList<ShoppingListUserItem> shoppingListUserItemArraylist) {
        this.name = name;
        this.shoppingListUserItemArraylist = shoppingListUserItemArraylist;
    }

    public ShoppingList(String name) {
        this.name = name;
    }

    public ShoppingList() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ShoppingListUserItem> getShoppingListUserItemArraylist() {
        return shoppingListUserItemArraylist;
    }

    public void setShoppingListUserItemArraylist(ArrayList<ShoppingListUserItem> shoppingListUserItemArraylist) {
        this.shoppingListUserItemArraylist = shoppingListUserItemArraylist;
    }
}
