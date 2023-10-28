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

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class RepItemAdapter extends ArrayAdapter<RepItem> {
    ArrayList<StoreUserItem> purchaseHistory;
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
        try {
            purchaseHistory = QueryUtils.getHistoryOfShoppingListItem(repItemName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(purchaseHistory != null){
            viewPurchaseHistory.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_history_24));

        } else{
            viewPurchaseHistory.setImageDrawable(getContext().getDrawable(R.drawable.ic_baseline_lighter_grey_history_24));
        }

        return newItemView;

    }
}
