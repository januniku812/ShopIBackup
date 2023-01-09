package com.example.android.receiptreader;

public final class JSONEditCodes { // class to help distinguish between what do with edit, delete, and add pop ups for different views
    // no save voice details code because there's only one dialog for it{@ShoppingListUserItemsActivity}
    public static final int EDIT_STORE_NAME = 1;
    public static final int EDIT_SHOPPING_LIST_NAME = 2;
    public static final int ADD_NEW_STORE = 3;
    public static final int ADD_NEW_SHOPPING_LIST = 4;
    public static final int DELETE_STORE = 5;
    public static final int DELETE_SHOPPING_LIST = 6;
    public static final int DELETE_SHOPPING_LIST_ITEM = 7;
    public static final int REORDER_SHOPPING_LIST_ITEM = 8;
}
