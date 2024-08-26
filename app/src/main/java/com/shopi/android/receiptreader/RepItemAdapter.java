package com.shopi.android.receiptreader;

import android.content.Context;
import android.content.res.ColorStateList;
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
import androidx.core.content.ContextCompat;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class RepItemAdapter extends ArrayAdapter<RepItem> {
    public RepItemAdapter(@NonNull Context context, ArrayList<RepItem> repItems) {
        super(context, 0, repItems);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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
        String repItemName = repItem.getName();
        TextView repItemTv = (TextView) newItemView.findViewById(R.id.rep_item_name);
        repItemTv.setText(repItemName);

        ImageView viewPurchaseHistory = newItemView.findViewById(R.id.item_rep_purchase_history);
        ArrayList<StoreUserItem> purchaseHistory = new ArrayList<>();
        try {
            purchaseHistory = QueryUtils.getHistoryOfShoppingListItem(repItemName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(!purchaseHistory.isEmpty()){
            viewPurchaseHistory.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.light_blue)));

        } else{
            viewPurchaseHistory.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.grey_four)));
        }

        return newItemView;

    }
}
