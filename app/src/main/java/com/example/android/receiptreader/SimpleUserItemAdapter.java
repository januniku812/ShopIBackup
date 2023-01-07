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

public class SimpleUserItemAdapter extends ArrayAdapter<StoreUserItem> {
    public SimpleUserItemAdapter(@NonNull Context context, ArrayList<StoreUserItem> storeUserItems) {
        super(context, 0, storeUserItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.simple_user_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        StoreUserItem storeUserItem = getItem(position);

        //find the text view in the user item individual view and setting it with object name data
        TextView userItemName = (TextView) newItemView.findViewById(R.id.simple_user_item_name);
        userItemName.setText(storeUserItem.getItemName());

        return newItemView;

    }
}
