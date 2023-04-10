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

public class RepItemAdapter extends ArrayAdapter<RepItem> {
    public RepItemAdapter(@NonNull Context context, ArrayList<RepItem> repItems) {
        super(context, 0, repItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.rep_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        RepItem repItem = getItem(position);

        //find the text view in the user item individual view and setting it with object name data
        TextView repItemTv = (TextView) newItemView.findViewById(R.id.rep_item_name);
        repItemTv.setText(repItem.getName());

        return newItemView;

    }
}
