package com.example.android.receiptreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ShoppingListUserItemHistoryActivity extends AppCompatActivity {
    ArrayList<StoreUserItem> storeUserItems;
    StoreUserItemAdapter storeUserItemAdapter;
    TextView resultsForStoreUserItemsView;
    ListView storeUserItemsListView;

    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        System.out.println("made it hideSoftKeyboard");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_user_items_layout);
        storeUserItems = (ArrayList<StoreUserItem>) getIntent().getBundleExtra("BUNDLE").getSerializable("storeUserItemsHistory");
        String title = getIntent().getBundleExtra("BUNDLE").getString("title");
        String classComingFrom = getIntent().getBundleExtra("BUNDLE").getString("classComingFrom");
        storeUserItemAdapter = new StoreUserItemAdapter(this, storeUserItems);
        storeUserItemsListView = findViewById(R.id.user_items_list_view);
        storeUserItemsListView.setAdapter( storeUserItemAdapter);
        SearchView searchView = findViewById(R.id.search_bar);
        searchView.setQueryHint(getString(R.string.search_based_on_store_or_date));
        TextView titleTextView = findViewById(R.id.title);
        Toolbar toolbar = findViewById(R.id.my_toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("MADE IT setNavOnCLcikListener");
                if(classComingFrom.equals("StoreUserItemsActivity")){
                    Intent intent = new Intent(ShoppingListUserItemHistoryActivity.this, StoreUserItemsActivity.class);
                    System.out.println("data: " + getIntent().getBundleExtra("BUNDLE").getString("storeComingFrom"));
                    intent.putExtra("storeName", getIntent().getBundleExtra("BUNDLE").getString("storeComingFrom"));
                    startActivity(intent);
                }
                else
                if(classComingFrom.equals("ShoppingListUserItemsActivity")){
                    System.out.println("data: " + getIntent().getBundleExtra("BUNDLE").getString("storeComingFrom"));
                    Intent intent = new Intent(ShoppingListUserItemHistoryActivity.this, ShoppingListUserItemsActivity.class);
                    intent.putExtra("shoppingListName", getIntent().getBundleExtra("BUNDLE").getString("shoppingListName"));
                    startActivity(intent);
                }
            }
        });
        titleTextView.setText(String.format(getString(R.string.purchase_history), title));
        resultsForStoreUserItemsView = findViewById(R.id.results_for_user_item_text);
        hideSoftKeyboard(this);
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                                              @Override
                                              public boolean onQueryTextSubmit(String query) {
                                                  Integer searchQueryLength = query.length();
                                                  ArrayList<StoreUserItem> newStoreUserItemList = new ArrayList<>();
                                                  StoreUserItemAdapter storeUserItemAdapter = new StoreUserItemAdapter(getApplicationContext(), newStoreUserItemList);
                                                  for(int i = 0; i < storeUserItems.size(); i++){
                                                      StoreUserItem storeUserItem =  storeUserItems.get(i);
                                                      try{
                                                          if(storeUserItem.getItemName().substring(0,searchQueryLength).equalsIgnoreCase(query)
                                                          || storeUserItem.getDateOfPurchase().substring(0,searchQueryLength).equalsIgnoreCase(query)
                                                          || storeUserItem.getStore().substring(0,searchQueryLength).equalsIgnoreCase(query)
                                                                  || storeUserItem.getUnitPrice().substring(0,searchQueryLength).equalsIgnoreCase(query)
                                                                  || storeUserItem.getQuantity().substring(0,searchQueryLength).equalsIgnoreCase(query)
                                                                  || storeUserItem.getTotalAmountPaid().substring(0,searchQueryLength).equalsIgnoreCase(query)
                                                          ){
                                                              newStoreUserItemList.add(storeUserItem);
                                                          }

                                                          if(storeUserItem.getAdditionalWeightUnitPriceDetail() != null){
                                                              if(storeUserItem.getAdditionalWeightUnitPriceDetail().substring(0, searchQueryLength).equalsIgnoreCase(query)){
                                                                  newStoreUserItemList.add(storeUserItem);
                                                              }
                                                          }
                                                      }
                                                      catch (StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                      }

                                                  }
                                                  storeUserItemsListView.setAdapter(storeUserItemAdapter);
                                                  return false;

                                              }

                                              @Override
                                              public boolean onQueryTextChange(String newText) {
                                                  Integer searchQueryLength = newText.length();
                                                  ArrayList<StoreUserItem> newMainGodList = new ArrayList<StoreUserItem>();
                                                  StoreUserItemAdapter storeUserItemAdapter = new StoreUserItemAdapter(getApplicationContext(),newMainGodList);
                                                  for(int i = 0; i < storeUserItems.size(); i++){
                                                      StoreUserItem storeUserItem = (StoreUserItem) storeUserItems.get(i);
                                                      try{
                                                          if(storeUserItem.getItemName().substring(0,searchQueryLength).equalsIgnoreCase(newText)
                                                                  || storeUserItem.getDateOfPurchase().substring(0,searchQueryLength).equalsIgnoreCase(newText)
                                                                  || storeUserItem.getStore().substring(0,searchQueryLength).equalsIgnoreCase(newText)
                                                                  || storeUserItem.getUnitPrice().substring(0,searchQueryLength).equalsIgnoreCase(newText)
                                                                  || storeUserItem.getQuantity().substring(0,searchQueryLength).equalsIgnoreCase(newText)
                                                                  || storeUserItem.getTotalAmountPaid().substring(0,searchQueryLength).equalsIgnoreCase(newText)
                                                          ){
                                                              newMainGodList.add(storeUserItem);
                                                          }
                                                          if(storeUserItem.getAdditionalWeightUnitPriceDetail() != null){
                                                              if(storeUserItem.getAdditionalWeightUnitPriceDetail().substring(0, searchQueryLength).equalsIgnoreCase(newText)){
                                                                  newMainGodList.add(storeUserItem);
                                                              }
                                                          }
                                                      }
                                                      catch(StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                      }
                                                  }
                                                  // if user has deleted all their text
                                                  if (newText.isEmpty()) {
                                                      resultsForStoreUserItemsView.setVisibility(View.GONE);
                                                  }
                                                  else {
                                                      resultsForStoreUserItemsView.setText("Results for " + newText);
                                                      resultsForStoreUserItemsView.setVisibility(View.VISIBLE);
                                                  }
                                                  storeUserItemsListView.setAdapter(storeUserItemAdapter);
                                                  return false;
                                              }


                                          }

        );
        // filter through user_items list with user items list adapter
        searchView.setOnCloseListener( new SearchView.OnCloseListener() {
                                           @Override
                                           public boolean onClose() {
                                               return false;
                                           }
                                       }
        );
    }
}
