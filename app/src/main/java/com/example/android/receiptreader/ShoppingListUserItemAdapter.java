package com.example.android.receiptreader;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.ListAdapter;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ShoppingListUserItemAdapter extends ArrayAdapter<ShoppingListUserItem> {

    public ShoppingListUserItemAdapter(@NonNull Context context, ArrayList<ShoppingListUserItem> shoppingListUserItemArrayList) {
        super(context, 0, shoppingListUserItemArrayList);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_list_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        ShoppingListUserItem shoppingListUserItem = getItem(position);
        ConstraintLayout constraintLayout = newItemView.findViewById(R.id.shopping_list_user_item_card_view_cl);
        //find the text view in the user item individual view and setting it with object name data
        TextView shoppingListName = (TextView) newItemView.findViewById(R.id.shopping_list_item_name);
        shoppingListName.setText(shoppingListUserItem.getName());
        TextView lastBoughtDate = (TextView) newItemView.findViewById(R.id.last_bought_date);
        ImageView blue_check_mark = (ImageView) newItemView.findViewById(R.id.check_circle);
        try {
            String whenShoppingListUserItemLastBought = QueryUtils.getWhenShoppingListUserItemLastBought(shoppingListUserItem.getName());
            if(!whenShoppingListUserItemLastBought.equals("not previously bought")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                lastBoughtDate.setText(whenShoppingListUserItemLastBought);
                blue_check_mark.setVisibility(View.VISIBLE);
            } else{
                lastBoughtDate.setText(R.string.last_bought);
                lastBoughtDate.setVisibility(View.INVISIBLE);
                blue_check_mark.setVisibility(View.INVISIBLE);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.shopping_list_item_name, ConstraintSet.TOP, R.id.shopping_list_user_item_card_view_cl, ConstraintSet.TOP,0);
                constraintSet.connect(R.id.shopping_list_item_name, ConstraintSet.BOTTOM, R.id.shopping_list_user_item_card_view_cl, ConstraintSet.BOTTOM,0);
                constraintLayout.setConstraintSet(constraintSet);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        TextView quantity = (TextView) newItemView.findViewById(R.id.quantity_sl_item);
        quantity.setText(shoppingListUserItem.getUserQuantity());

        return newItemView;
    }
}
