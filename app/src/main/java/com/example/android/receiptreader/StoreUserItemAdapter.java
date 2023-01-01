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

public class StoreUserItemAdapter extends ArrayAdapter<StoreUserItem> {
    public StoreUserItemAdapter(@NonNull Context context, ArrayList<StoreUserItem> storeUserItems) {
        super(context, 0, storeUserItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        StoreUserItem storeUserItem = getItem(position);

        //find the text view in the user item individual view and setting it with object name data
        TextView userItemStore = (TextView) newItemView.findViewById(R.id.store_name);
        userItemStore.setText(storeUserItem.getStore());

        //find the date text view in the user item individual view and setting it with object date data
        TextView userItemDate = (TextView) newItemView.findViewById(R.id.store_date);
        userItemDate.setText(storeUserItem.getDateOfPurchase());

        //find the text view in the user item individual view and setting it with object name data
        TextView userItemName = (TextView) newItemView.findViewById(R.id.item_name);
        userItemName.setText(storeUserItem.getItemName());

        //find the text view in the user item individual view and setting it with object quantity data
        TextView userItemQuantity = (TextView) newItemView.findViewById(R.id.quantity);
        userItemQuantity.setText(storeUserItem.getQuantity());

        //find the text view in the user item individual view and setting it with object amount paid data
        TextView userItemAmountPaid = (TextView) newItemView.findViewById(R.id.amount_paid);
        userItemAmountPaid.setText(storeUserItem.getAmountPaid());

        //find the text view in the user item individual view and setting it with object amount paid data
        TextView userItemUnitPrice = (TextView) newItemView.findViewById(R.id.unit_price);
        userItemUnitPrice.setText(storeUserItem.getUnitPrice());
        return newItemView;
                                             
    }
}
