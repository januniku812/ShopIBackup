package com.shopi.android.receiptreader;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class ShoppingListsHistoryActivity extends AppCompatActivity {
    ArrayList<ShoppingList> shoppingLists;
    DuplicateItemDetailsShoppingListAdapter duplicateItemDetailsShoppingListAdapter;
    ListView shoppingListsView;
    String shoppingListUserItemName;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.duplicate_item_details_page);
        shoppingListsView = findViewById(R.id.sl_list_view);
        shoppingLists = (ArrayList<ShoppingList>) getIntent().getBundleExtra("BUNDLE").getSerializable("shoppingListsContainingSlItem");
        shoppingListUserItemName = getIntent().getBundleExtra("BUNDLE").getString("shoppingListUserItemName");
        duplicateItemDetailsShoppingListAdapter = new DuplicateItemDetailsShoppingListAdapter(this, shoppingLists);
        shoppingListsView.setAdapter(duplicateItemDetailsShoppingListAdapter);
        SearchView shoppingListSearchView = findViewById(R.id.duplicate_item_details_page_search_bar);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShoppingListsHistoryActivity.this, ShoppingListUserItemsActivity.class);
                System.out.println("ORIGINALNAVPATH NAME FINAL: " + getIntent().getStringExtra("originalNavPathSLUTIShoppingList") );
                intent.putExtra("shoppingListName", getIntent().getStringExtra("originalNavPathSLUTIShoppingList"));
                startActivity(intent);

            }
        });
        TextView title = (TextView) findViewById(R.id.textTitle);
        title.setText(String.format(getString(R.string.shopping_lists_with), shoppingListUserItemName));
        // all functions for searching through the shoppingLists list view
        shoppingListSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                                                          @RequiresApi(api = Build.VERSION_CODES.O)
                                                          @Override
                                                          public boolean onQueryTextSubmit(String query) {
                                                              Integer searchQueryLength = query.length();
                                                              ArrayList<ShoppingList> newShoppingListList = new ArrayList<>();
                                                              ShoppingListAdapter ShoppingListListAdapter = new ShoppingListAdapter(getApplicationContext(), newShoppingListList);
                                                              // updating shopping list
                                                              try {
                                                                  shoppingLists = QueryUtils.getShoppingLists();
                                                              } catch (IOException e) {
                                                                  e.printStackTrace();
                                                              } catch (ParseException e) {
                                                                  e.printStackTrace();
                                                              }
                                                              for(int i = 0; i < shoppingLists.size(); i++){
                                                                  ShoppingList shoppingList =  shoppingLists.get(i);
                                                                  try{
                                                                      if(shoppingList.getName().substring(0,searchQueryLength).equalsIgnoreCase(query)){
                                                                          newShoppingListList.add(shoppingList);
                                                                      }
                                                                  }
                                                                  catch (StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                                  }

                                                              }
                                                              shoppingListsView.setAdapter(ShoppingListListAdapter);
                                                              return false;

                                                          }

                                                          @RequiresApi(api = Build.VERSION_CODES.O)
                                                          @Override
                                                          public boolean onQueryTextChange(String newText) {
                                                              Integer searchQueryLength = newText.length();
                                                              ArrayList<ShoppingList> newShoppingListList = new ArrayList<ShoppingList>();
                                                              ShoppingListAdapter ShoppingListListAdapter = new ShoppingListAdapter(getApplicationContext(), newShoppingListList);
                                                              // updating shopping list
                                                              try {
                                                                  shoppingLists = QueryUtils.getShoppingLists();
                                                              } catch (IOException e) {
                                                                  e.printStackTrace();
                                                              } catch (ParseException e) {
                                                                  e.printStackTrace();
                                                              }
                                                              for(int i = 0; i < shoppingLists.size(); i++){
                                                                  ShoppingList shoppingList = (ShoppingList) shoppingLists.get(i);
                                                                  try{
                                                                      if(shoppingList.getName().substring(0, searchQueryLength).equalsIgnoreCase(newText)){
                                                                          newShoppingListList.add(shoppingList);
                                                                      }
                                                                  }
                                                                  catch(StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                                  }
                                                              }
                                                              shoppingListsView.setAdapter(ShoppingListListAdapter);
                                                              return false;
                                                          }


                                                      }

        );
        // filter through user_items list with user items list adapter
        shoppingListSearchView.setOnCloseListener( new SearchView.OnCloseListener() {
                                                       @Override
                                                       public boolean onClose() {
                                                           return false;
                                                       }
                                                   }
        );
        shoppingListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingList shoppingList = (ShoppingList) duplicateItemDetailsShoppingListAdapter.getItem(i);
                String originalShoppingListName = shoppingList.getName();
                View selectedStoreView = duplicateItemDetailsShoppingListAdapter.getView(i, view, adapterView);
                ImageView editViewIcon = (ImageView) selectedStoreView.findViewById(R.id.edit_name_sl_button);
                ImageView deleteButton = (ImageView) selectedStoreView.findViewById(R.id.delete_item_button);
                Intent intent = new Intent(ShoppingListsHistoryActivity.this, ShoppingListUserItemsActivity.class);
                intent.putExtra("shoppingListName",shoppingList.getName());
                intent.putExtra("shoppingListUserItemName",shoppingListUserItemName);
                System.out.println("ORIGINALNAVPATH NAME: " + getIntent().getStringExtra("originalNavPathSLUTIShoppingList") );
                intent.putExtra("originalNavPathSLUTIShoppingList",getIntent().getStringExtra("originalNavPathSLUTIShoppingList"));
                intent.putExtra("classComingFrom","ShoppingListsHistoryActivity");
                Bundle args = new Bundle();
                args.putSerializable("shoppingListsContainingSlItem", shoppingLists);
                intent.putExtra("BUNDLE", args);
                startActivity(intent);
            }

        });

    }
}
