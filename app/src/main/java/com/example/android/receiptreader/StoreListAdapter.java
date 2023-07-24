package com.example.android.receiptreader;

import android.content.Context;
import android.content.res.Resources;
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

        View cardView = newItemView.findViewById(R.id.store_item_card_view);
        if(store.isIfHighlighted()){ // set color to blue if it is the store being shopped in
            cardView.setBackgroundResource(R.drawable.light_blue_card_view_bkg);
        }else{
            cardView.setBackgroundResource(R.drawable.white_background_card);
        }
        newItemView.findViewById(R.id.store_name_cl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.updateStoreLaunch(store.getStoreName());
            }

        });

        return newItemView;

    }
}
