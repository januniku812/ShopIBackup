package com.example.android.receiptreader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class AddShoppingListUserItemActivity extends AppCompatActivity {
    SimpleUserItemAdapter simpleUserItemAdapter;
    ListView simplePastUserItemsListView;
    ArrayList<StoreUserItem> userItems;
    TextInputEditText newItemEditText;
    ImageButton submitButton;
    String shoppingList;
    String lastBought = "";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_shopping_list_item_page);
        simplePastUserItemsListView = (ListView)findViewById(R.id.general_items_master);
        newItemEditText = (TextInputEditText) findViewById(R.id.new_item_edit_text);
        shoppingList = getIntent().getStringExtra("shoppingListName");
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        TextView title = findViewById(R.id.title_shopping_list_name);
        title.setText(String.format(getString(R.string.add_item), shoppingList));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddShoppingListUserItemActivity.this, ShoppingListUserItemsActivity.class);
                intent.putExtra("shoppingListName", shoppingList);
                startActivity(intent);
            }
        });
        System.out.println("SHOPPING LIST NAME: " + shoppingList);
        submitButton = (ImageButton) findViewById(R.id.submit_button);
        try {
            userItems = QueryUtils.getAllStoreUserItems();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        simpleUserItemAdapter = new SimpleUserItemAdapter(this, userItems);

        simplePastUserItemsListView.setAdapter(simpleUserItemAdapter);

        simplePastUserItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StoreUserItem storeUserItem = (StoreUserItem) simpleUserItemAdapter.getItem(i);
                String shoppingListUserItemName = storeUserItem.getItemName();
                newItemEditText.setText(shoppingListUserItemName);
                try {
                    lastBought = QueryUtils.getWhenShoppingListUserItemLastBought(shoppingListUserItemName);
                    QueryUtils.addShoppingListItem(shoppingList,newItemEditText.getText().toString().trim(), lastBought, getApplicationContext());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(lastBought.equals("not previously bought")){
                    lastBought = null;
                }
                Intent intent = new Intent(AddShoppingListUserItemActivity.this, ShoppingListUserItemsActivity.class);
                intent.putExtra("shoppingListName", shoppingList);
                startActivity(intent);
            }
        });

        newItemEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Integer searchQueryLength = charSequence.length();

                ArrayList<StoreUserItem> newUserItems = new ArrayList<StoreUserItem>();
                simpleUserItemAdapter = new SimpleUserItemAdapter(getApplicationContext(), newUserItems);
                for(int i3 = 0; i3 < userItems.size(); i3++){
                    StoreUserItem storeUserItem = (StoreUserItem) userItems.get(i3);
                    try{
                        if(storeUserItem.getItemName().substring(0, searchQueryLength).equalsIgnoreCase(charSequence.toString())){
                            newUserItems.add(storeUserItem);
                        }
                    }
                    catch(StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                    }
                }

                simplePastUserItemsListView.setAdapter(simpleUserItemAdapter);
        }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    QueryUtils.addShoppingListItem(shoppingList,newItemEditText.getText().toString().trim(), lastBought, getApplicationContext());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(AddShoppingListUserItemActivity.this, ShoppingListUserItemsActivity.class);
                intent.putExtra("shoppingListName", shoppingList);
                startActivity(intent);
            }
        });
    }
}
