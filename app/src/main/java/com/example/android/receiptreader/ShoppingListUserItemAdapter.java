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
        if (newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.shopping_list_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        ShoppingListUserItem shoppingListUserItem = getItem(position);
        String shoppingListUserItemName = shoppingListUserItem.getName();
        ConstraintLayout constraintLayout = newItemView.findViewById(R.id.shopping_list_user_item_card_view_cl);
        //find the text view in the user item individual view and setting it with object name data
        TextView shoppingListName = (TextView) newItemView.findViewById(R.id.shopping_list_item_name);
        shoppingListName.setText(shoppingListUserItem.getName());
        ImageView duplicateIndicator = (ImageView) newItemView.findViewById(R.id.duplicate_indicator);
        TextView lastBoughtDate = (TextView) newItemView.findViewById(R.id.last_bought_date);
        ImageView historyButton = (ImageView) newItemView.findViewById(R.id.history_button_sl_item);
        ImageView blue_check_mark = (ImageView) newItemView.findViewById(R.id.check_circle);
        if (shoppingListUserItem.getLastBought().equals("")) { // sometimes the user might manually type a name of an item already bought and it will not have last bought saved in it
            try {
                String whenShoppingListUserItemLastBought = QueryUtils.getWhenShoppingListUserItemLastBought(shoppingListUserItemName);
                if (!whenShoppingListUserItemLastBought.equals("not previously bought")) {
                    blue_check_mark.setVisibility(View.VISIBLE);
                    System.out.println("MADE IT grstrstrststrstrstrstrstr :" + position + shoppingListUserItemName + " SET TEXT " + whenShoppingListUserItemLastBought);
                    lastBoughtDate.setText(whenShoppingListUserItemLastBought);
                    shoppingListUserItem.setLastBought(whenShoppingListUserItemLastBought); // will help when running search view updated adapters
                } else {
                    System.out.println("MADE IT 2:" + shoppingListUserItemName);
                    lastBoughtDate.setText(R.string.last_bought);
                    lastBoughtDate.setVisibility(View.INVISIBLE);
                    blue_check_mark.setVisibility(View.INVISIBLE);
                    ConstraintSet constraintSet = new ConstraintSet();
                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(R.id.shopping_list_item_name, ConstraintSet.TOP, R.id.shopping_list_user_item_card_view_cl, ConstraintSet.TOP, 0);
                    constraintSet.connect(R.id.shopping_list_item_name, ConstraintSet.BOTTOM, R.id.shopping_list_user_item_card_view_cl, ConstraintSet.BOTTOM, 0);
                    constraintLayout.setConstraintSet(constraintSet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else{ // sometimes the item will be previously bought or the item's last bought will be updated in line 55 as above for search view adapter efficiency
            lastBoughtDate.setText(shoppingListUserItem.getLastBought());
        }
        try {
                ArrayList<ShoppingList> otherShoppingListsSlExistsIn =  QueryUtils.ifShoppingListItemExistsInOtherShoppingLists(shoppingListUserItem.getName());
                if (otherShoppingListsSlExistsIn == null) {
                    System.out.println("OTHER SHOPPING LISTS: " + otherShoppingListsSlExistsIn.size());
                    duplicateIndicator.setImageResource(R.drawable.ic_baseline_grey_content_copy_24);
                }
                else{
                    duplicateIndicator.setImageResource(R.drawable.ic_round_content_copy_24);
                }
                shoppingListUserItem.setOtherShoppingListsExistingIn(otherShoppingListsSlExistsIn);
            }
            catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        try {
            ArrayList<StoreUserItem> storeUserItemsHistory = QueryUtils.getHistoryOfShoppingListItem(shoppingListUserItemName);
            if(storeUserItemsHistory == null){
                historyButton.setImageResource(R.drawable.ic_baseline_grey_history_24);
            }
            else{
                historyButton.setImageResource(R.drawable.ic_baseline_history_24);
            }
            shoppingListUserItem.setStoreUserItemsHistory(storeUserItemsHistory);
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