package com.example.android.receiptreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.android.receiptreader.camera.SecondCameraActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class StoreUserItemsActivity extends AppCompatActivity {
    StoreUserItemAdapter storeUserItemAdapter;
    TextView resultsForStoreUserItemsView;
    ListView storeUserItemsListView;
    ArrayList<StoreUserItem> storeUserItems;
    String storeName;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update(){
        int index = storeUserItemsListView.getFirstVisiblePosition();
        View v = storeUserItemsListView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        try {
            storeUserItems = QueryUtils.getStoreUserItems(storeName);
        } catch (Exception  e) {
            e.printStackTrace();
        }

        storeUserItemAdapter = new StoreUserItemAdapter(getApplicationContext(), storeUserItems);
        storeUserItemsListView.setSelectionFromTop(index, top);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_user_items_layout);
        storeUserItemsListView = findViewById(R.id.user_items_list_view);
        resultsForStoreUserItemsView = findViewById(R.id.results_for_user_item_text);
        SearchView searchView = findViewById(R.id.search_bar);

        searchView.setQueryHint(getString(R.string.search_for_item));
        TextView titleTextView = findViewById(R.id.title);
         storeName = getIntent().getStringExtra("storeName");
        titleTextView.setText(String.format(getString(R.string.purchase_history_name), storeName));
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StoreUserItemsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ArrayList<StoreUserItem> storeUserItems = null;
        try {
            storeUserItems = QueryUtils.getStoreUserItems(storeName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        storeUserItemAdapter = new StoreUserItemAdapter(this, storeUserItems);
        storeUserItemsListView.setAdapter(storeUserItemAdapter);

        hideSoftKeyboard(this);

        ArrayList<StoreUserItem> finalStoreUserItems = storeUserItems;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                                              @Override
                                              public boolean onQueryTextSubmit(String query) {
                                                  Integer searchQueryLength = query.length();
                                                  ArrayList<StoreUserItem> newStoreUserItemList = new ArrayList<>();
                                                  StoreUserItemAdapter storeUserItemAdapter = new StoreUserItemAdapter(getApplicationContext(), newStoreUserItemList);
                                                  for(int i = 0; i < finalStoreUserItems.size(); i++){
                                                      StoreUserItem storeUserItem =  finalStoreUserItems.get(i);
                                                      try{
                                                          if(storeUserItem.getItemName().substring(0,searchQueryLength).equalsIgnoreCase(query)
                                                          || storeUserItem.getDateOfPurchase().substring(0,searchQueryLength).equalsIgnoreCase(query)){
                                                              newStoreUserItemList.add(storeUserItem);
                                                          }
                                                          else if(query.length() >= 3 && (storeUserItem.getItemName().toLowerCase().contains(query.toLowerCase())
                                                                  || storeUserItem.getDateOfPurchase().toLowerCase().contains(query))) {
                                                              newStoreUserItemList.add(storeUserItem);

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
                                                  ArrayList<StoreUserItem> newStoreUserItemList = new ArrayList<StoreUserItem>();
                                                  StoreUserItemAdapter storeUserItemAdapter = new StoreUserItemAdapter(getApplicationContext(),newStoreUserItemList);
                                                  for(int i = 0; i < finalStoreUserItems.size(); i++){
                                                      StoreUserItem storeUserItem = (StoreUserItem) finalStoreUserItems.get(i);
                                                      try{

                                                          if(storeUserItem.getItemName().substring(0,searchQueryLength).equalsIgnoreCase(newText)
                                                                  || storeUserItem.getDateOfPurchase().substring(0,searchQueryLength).equalsIgnoreCase(newText)){
                                                              newStoreUserItemList.add(storeUserItem);
                                                          }
                                                          else if(newText.length() >= 3 && (storeUserItem.getItemName().toLowerCase().contains(newText.toLowerCase())
                                                                  || storeUserItem.getDateOfPurchase().toLowerCase().contains(newText))) {
                                                              newStoreUserItemList.add(storeUserItem);

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
        storeUserItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    StoreUserItem storeUserItem = (StoreUserItem) storeUserItemAdapter.getItem(i);
                    String nameToPass = storeUserItem.getItemName();
                    View selectedStoreView = storeUserItemAdapter.getView(i, view, adapterView);
                    ConstraintLayout storeUserItemNameCl = selectedStoreView.findViewById(R.id.item_name_cl);
//                    storeUserItemNameCl.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
                            // reusing the shopping list user item history as it can work for giving the history of a record of an item saved in a store as well
                            Intent intent = new Intent(StoreUserItemsActivity.this, ShoppingListUserItemHistoryActivity.class);
                            ArrayList<StoreUserItem> storeUserItemsToPass = new ArrayList<>();
                            try {
                                 storeUserItemsToPass = QueryUtils.getHistoryOfShoppingListItem(nameToPass);
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Bundle args = new Bundle();
                            args.putString("classComingFrom", "StoreUserItemsActivity");
                            args.putString("title", nameToPass);
                            args.putString("storeComingFrom", storeName);
                            args.putSerializable("storeUserItemsHistory", storeUserItemsToPass);
                            intent.putExtra("BUNDLE", args);
                            startActivity(intent);
//                        }
//                    });
                }
        }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}