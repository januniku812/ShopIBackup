package com.example.android.receiptreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserItemAdapter extends ArrayAdapter<UserItem> {
    public UserItemAdapter(@NonNull Context context, ArrayList<UserItem> userItems) {
        super(context, 0, userItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        UserItem userItem = getItem(position);

        //find the text view in the user item individual view and setting it with object name data
        TextView userItemStore = (TextView) newItemView.findViewById(R.id.store_name);
        userItemStore.setText(userItem.getStore());

        //find the date text view in the user item individual view and setting it with object date data
        TextView userItemDate = (TextView) newItemView.findViewById(R.id.store_date);
        userItemDate.setText(userItem.getDateOfPurchase());

        //find the text view in the user item individual view and setting it with object name data
        TextView userItemName = (TextView) newItemView.findViewById(R.id.item_name);
        userItemName.setText(userItem.getItemName());

        //find the text view in the user item individual view and setting it with object quantity data
        TextView userItemQuantity = (TextView) newItemView.findViewById(R.id.quantity);
        userItemQuantity.setText(userItem.getQuantity());

        //find the text view in the user item individual view and setting it with object amount paid data
        TextView userItemAmountPaid = (TextView) newItemView.findViewById(R.id.amount_paid);
        userItemAmountPaid.setText(userItem.getAmountPaid());

        //find the text view in the user item individual view and setting it with object amount paid data
        TextView userItemUnitPrice = (TextView) newItemView.findViewById(R.id.unit_price);
        userItemUnitPrice.setText(userItem.getUnitPrice());
        return newItemView;
                                             
    }
}
