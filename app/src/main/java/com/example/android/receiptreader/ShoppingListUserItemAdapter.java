package com.example.android.receiptreader;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ListAdapter;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import static android.provider.Settings.System.getString;

public class ShoppingListUserItemAdapter extends ArrayAdapter<ShoppingListUserItem> {
    ListView shoppingListUserItemsListView;
    String shoppingListNameStr;
    ArrayList<ShoppingListUserItem> shoppingListUserItems;

    public ArrayList<ShoppingListUserItem> getShoppingListUserItems() {
        return shoppingListUserItems;
    }

    public void setShoppingListUserItems(ArrayList<ShoppingListUserItem> shoppingListUserItems) {
        this.shoppingListUserItems = shoppingListUserItems;
    }

    public ShoppingListUserItemAdapter(@NonNull Context context, ArrayList<ShoppingListUserItem> shoppingListUserItemArrayList, String shoppingListName) {
        super(context, 0, shoppingListUserItemArrayList);
        System.out.println("ITEMSSSS @ShoppingListUserItemAdapter");
        for (ShoppingListUserItem item: shoppingListUserItemArrayList
        ) {
            System.out.println(item.getName() + item.getUserQuantity() + " LAST BOUGHT: "  + item.getLastBought());
        }
        shoppingListUserItems = shoppingListUserItemArrayList;
        this.shoppingListNameStr = shoppingListName;

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
        System.out.println("RUNNING @getView in @ShoppingListUserItemAdapter for : "+ shoppingListUserItemName + shoppingListUserItem.getUserQuantity());
        shoppingListName.setText(shoppingListUserItem.getName());
//        ImageView duplicateIndicator = (ImageView) newItemView.findViewById(R.id.duplicate_indicator_image_view);
        TextView lastBoughtDate = (TextView) newItemView.findViewById(R.id.last_bought_date);
//        ImageView historyButton = (ImageView) newItemView.findViewById(R.id.history_button_image_view);
        ImageView check_mark = (ImageView) newItemView.findViewById(R.id.check_circle);
        check_mark.setVisibility(View.INVISIBLE);
        if (shoppingListUserItem.getLastBought().equals("")) { // sometimes the user might manually type a name of an item already bought and it will not have last bought saved in it
            try {
                String whenShoppingListUserItemLastBought = QueryUtils.getWhenShoppingListUserItemLastBought(shoppingListUserItemName);
                if (!whenShoppingListUserItemLastBought.equals("not previously bought")) {
                    shoppingListName.setTextColor(getContext().getColor(R.color.blue));
                    System.out.println("MADE IT DOES HAVE LAST BOUGHT :" + position + shoppingListUserItemName + " SET TEXT " + whenShoppingListUserItemLastBought);
                    lastBoughtDate.setText(whenShoppingListUserItemLastBought);
                    lastBoughtDate.setVisibility(View.VISIBLE);
                    shoppingListUserItem.setLastBought(whenShoppingListUserItemLastBought); // will help when running search view updated adapters
                    if (shoppingListUserItem.isIfGreenMarked()) {
                        System.out.println("GREEN MARKED: " + shoppingListUserItemName);
                        check_mark.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.green)));
                        check_mark.setVisibility(View.VISIBLE);
                    } else{
                        check_mark.setVisibility(View.INVISIBLE);
                    }
                } else {
                    System.out.println("MADE IT DOESNT HAVE LAST BOUGHT:" + shoppingListUserItemName);
                    if (shoppingListUserItem.isIfGreenMarked()) {
                        System.out.println("GREEN MARKED: " + shoppingListUserItemName);

                        check_mark.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.green)));
                        check_mark.setVisibility(View.VISIBLE);
                    } else {
                        check_mark.setVisibility(View.INVISIBLE);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.shopping_list_item_name, ConstraintSet.TOP, R.id.shopping_list_user_item_card_view_cl, ConstraintSet.TOP, 0);
                        constraintSet.connect(R.id.shopping_list_item_name, ConstraintSet.BOTTOM, R.id.shopping_list_user_item_card_view_cl, ConstraintSet.BOTTOM, 0);
                        constraintLayout.setConstraintSet(constraintSet);
                    }
                    lastBoughtDate.setVisibility(View.GONE);
                    lastBoughtDate.setText(R.string.last_bought);
                    System.out.println("MADE IT DOESNT HAVE LAST BOUGHT 4:" + shoppingListUserItemName);
                }
            }
             catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        else{ // sometimes the item will be previously bought or the item's last bought will be updated in line 55 as above for search view adapter efficiency
            System.out.println("OTHER HAS BOUGHT: " + shoppingListUserItem.getName() + " " + shoppingListUserItem.getLastBought());

            System.out.println("SET BLUE CHEK MARK FOR " + shoppingListUserItemName);
            lastBoughtDate.setText(shoppingListUserItem.getLastBought());
            lastBoughtDate.setVisibility(View.VISIBLE);
            shoppingListName.setTextColor(getContext().getColor(R.color.blue));
        }
//        try {
//            ArrayList<ShoppingList> otherShoppingListsSlExistsIn =  QueryUtils.ifShoppingListItemExistsInOtherShoppingLists(shoppingListUserItem.getName());
//
//            if(otherShoppingListsSlExistsIn != null){
//                duplicateIndicator.setImageResource(R.drawable.ic_round_content_copy_24);
//            }
//            else{
//
//                duplicateIndicator.setImageResource(R.drawable.ic_baseline_grey_content_copy_24);
//            }
//            shoppingListUserItem.setOtherShoppingListsExistingIn(otherShoppingListsSlExistsIn);
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        try {
//            ArrayList<StoreUserItem> storeUserItemsHistory = QueryUtils.getHistoryOfShoppingListItem(shoppingListUserItemName);
//            if(storeUserItemsHistory == null){
//                historyButton.setImageResource(R.drawable.ic_baseline_grey_history_24);
//            }
//            else{
//                historyButton.setImageResource(R.drawable.ic_baseline_history_24);
//            }
//            shoppingListUserItem.setStoreUserItemsHistory(storeUserItemsHistory);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        TextView quantity = (TextView) newItemView.findViewById(R.id.quantity_sl_item);
        quantity.setText(shoppingListUserItem.getUserQuantity());

        ImageView increaseQuantityAppCompatImageButton =  newItemView.findViewById(R.id.quantity_add_button);
        ImageView decreaseQuantityAppCompatImageButton = newItemView.findViewById(R.id.quantity_minus_button);
        increaseQuantityAppCompatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    System.out.println("IVE BEEN CLICKED STORE ITEM ADAPTER");
                    if(!shoppingListUserItem.isIfGreenMarked()) {
                        QueryUtils.increaseShoppingListItemQuantity(shoppingListNameStr, shoppingListUserItemName, getContext());
                        ShoppingListUserItemsActivity.update();
                    } else{
                        Toast.makeText(getContext(), "Bought item quantity cannot be changed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        decreaseQuantityAppCompatImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    System.out.println("IVE BEEN CLICKED ITEM ADAPTER");
                    if(Integer.parseInt(shoppingListUserItem.getUserQuantity()) > 1 && !shoppingListUserItem.isIfGreenMarked()) {
                        QueryUtils.decreaseShoppingListItemQuantity(shoppingListNameStr, shoppingListUserItemName, getContext());
                        ShoppingListUserItemsActivity.update();
                    } else if(shoppingListUserItem.isIfGreenMarked()){
                        Toast.makeText(getContext(), "Bought item quantity cannot be changed", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        return newItemView;
    }

    @Override
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        System.out.println("@getCount CALLED: " + super.getCount());
        return super.getCount();
    }

    @Nullable
    @Override
    public ShoppingListUserItem getItem(int position) {
        System.out.println("@getItem CALLED: " + position);
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        System.out.println("@getItemId CALLED: " + position);
        return super.getItemId(position);
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public int getViewTypeCount() {
        if (getCount() > 0) {
            return getCount();
        } else {
            return super.getViewTypeCount();
        }
    }
}