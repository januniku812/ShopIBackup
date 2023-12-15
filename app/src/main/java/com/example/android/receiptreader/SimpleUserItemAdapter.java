package com.example.android.receiptreader;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class SimpleUserItemAdapter extends ArrayAdapter<StoreUserItem> {
    ArrayList<ShoppingListUserItem> shoppingListUserItems;
    public SimpleUserItemAdapter(@NonNull Context context, ArrayList<StoreUserItem> storeUserItems, ArrayList<ShoppingListUserItem> shoppingListUserItems) {
        super(context, 0, storeUserItems);
        this.shoppingListUserItems = shoppingListUserItems;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View newItemView = convertView;
        if(newItemView == null) {
            newItemView = LayoutInflater.from(getContext()).inflate(R.layout.simple_user_item, parent, false);
        }

        //get the{@link Deity} object located at this position in the list
        StoreUserItem storeUserItem = getItem(position);

        //find the text view in the user item individual view and setting it with object name data
        String name = storeUserItem.getItemName();
        TextView userItemName = (TextView) newItemView.findViewById(R.id.simple_user_item_name);
        TextView includedInShoppingList = (TextView) newItemView.findViewById(R.id.included_in_shopping_list);
        userItemName.setText(name);

        System.out.println("SHOPPING LIST USER ITEMS: ");
        ArrayList<String> shoppingListUserItemNames = new ArrayList<>();
        for(ShoppingListUserItem shoppingListUserItem: shoppingListUserItems){
            shoppingListUserItemNames.add(shoppingListUserItem.getName().replaceAll(" ", "").trim());
            System.out.println(shoppingListUserItem.getName());
        }
        for(String itemName: shoppingListUserItemNames){
            System.out.println("COMPARING: " + name + " WITH: " + itemName.replaceAll(" ", "").trim());
            if(itemName.replaceAll(" ", "").trim().equalsIgnoreCase(name.replaceAll(" ", ""))){
                System.out.println("CONTAINS");
                includedInShoppingList.setVisibility(View.VISIBLE);
                newItemView.findViewById(R.id.user_item_card_view).setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.light_blue_2)));
                return newItemView;
            }
        }
        includedInShoppingList.setVisibility(View.INVISIBLE);
        newItemView.findViewById(R.id.user_item_card_view).setBackgroundTintList(ColorStateList.valueOf(getContext().getColor(R.color.white)));
        newItemView.findViewById(R.id.user_item_card_view).setBackgroundResource(R.drawable.white_background_card);
        return newItemView;
    }
}
