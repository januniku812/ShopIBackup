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

public class StoreListAdapter extends ArrayAdapter<Store> {
    public StoreListAdapter(@NonNull Context context, ArrayList<Store> stores) {
        super(context, 0, stores);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.store_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        Store store = getItem(position);

        //find the text view in the user item individual view and setting it with object name data
        TextView storeName = (TextView) newItemView.findViewById(R.id.store_item_name);
        storeName.setText(store.getStoreName());

        return newItemView;

    }
}
