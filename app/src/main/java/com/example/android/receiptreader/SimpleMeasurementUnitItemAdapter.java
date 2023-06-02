package com.example.android.receiptreader;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class SimpleMeasurementUnitItemAdapter extends ArrayAdapter<String> {
    public SimpleMeasurementUnitItemAdapter(@NonNull Context context, ArrayList<String> measurementUnitItems) {
        super(context, 0, measurementUnitItems);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.simple_measurement_unit_item, parent, false);
        }

        String measurementUnit = getItem(position);
        //find the text view in the user item individual view and setting it with object name data
        TextView shoppingListName = (TextView) newItemView.findViewById(R.id.simple_measurement_unit_name);
        shoppingListName.setText(measurementUnit);
        ImageView checkMark = (ImageView) newItemView.findViewById(R.id.check_mark);
        if(Constants.currentMeasureUnit.equals(measurementUnit)){
            checkMark.setVisibility(View.VISIBLE);
        }
        else{
            checkMark.setVisibility(View.INVISIBLE);

        }

        return newItemView;

    }
}
