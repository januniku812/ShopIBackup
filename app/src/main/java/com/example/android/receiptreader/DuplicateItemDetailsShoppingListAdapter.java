package com.example.android.receiptreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DuplicateItemDetailsShoppingListAdapter extends ArrayAdapter<ShoppingList> {
    ArrayList<ShoppingList> shoppingLists;
    public DuplicateItemDetailsShoppingListAdapter(@NonNull Context context, ArrayList<ShoppingList> shoppingLists) {
        super(context, 0, shoppingLists);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.duplicate_item_details_page_shopping_list, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        ShoppingList shoppingList = getItem(position);

        //find the text view in the user item individual view and setting it with object name data
        TextView shoppingListName = (TextView) newItemView.findViewById(R.id.shopping_list_name);
        shoppingListName.setText(shoppingList.getName());

        return newItemView;

    }
}
