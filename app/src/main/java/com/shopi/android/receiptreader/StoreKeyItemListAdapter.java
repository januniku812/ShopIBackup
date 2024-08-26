package com.shopi.android.receiptreader;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class StoreKeyItemListAdapter extends ArrayAdapter<String> {

    public StoreKeyItemListAdapter(@NonNull Context context, ArrayList<String> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.store_key_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        String storeKeyWithColor = getItem(position);

        //find the text view in the user item individual view and setting it with object name data
        TextView storeName = (TextView) newItemView.findViewById(R.id.store_item_name);
        storeName.setText(storeKeyWithColor.substring(0, storeKeyWithColor.indexOf(";")));


        Button colorButton = (Button) newItemView.findViewById(R.id.color_button);
        colorButton.setBackgroundTintList(ColorStateList.valueOf(Integer.parseInt(storeKeyWithColor.substring(storeKeyWithColor.indexOf(";")+1))));

        return newItemView;

    }
}
