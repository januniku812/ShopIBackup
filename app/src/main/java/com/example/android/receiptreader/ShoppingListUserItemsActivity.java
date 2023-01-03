package com.example.android.receiptreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class ShoppingListUserItemsActivity extends AppCompatActivity {

    TextView resultsForshoppingListUserItemsView;
    ListView shoppingListUserItemsListView;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_items_layout);
        shoppingListUserItemsListView = findViewById(R.id.shopping_list_user_items_list_view);
        resultsForshoppingListUserItemsView = findViewById(R.id.results_for_user_item_text);
        SearchView searchView = findViewById(R.id.search_bar);
        ImageView historyButton = (ImageView) findViewById(R.id.history_button_sl_item);
        FloatingActionButton add_fab = findViewById(R.id.sl_fab);
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShoppingListUserItemsActivity.this, AddShoppingListUserItemActivity.class);
                startActivity(intent);

            }
        });
        String shoppingListName = getIntent().getStringExtra("shoppingListName");
        ArrayList<ShoppingListUserItem> shoppingListUserItems = null;
        try {
            shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ShoppingListUserItemAdapter shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(this, shoppingListUserItems);
        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);

        hideSoftKeyboard(this);
        searchView.setIconified(false);

        ArrayList<ShoppingListUserItem> finalshoppingListUserItems = shoppingListUserItems;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                                              @Override
                                              public boolean onQueryTextSubmit(String query) {
                                                  Integer searchQueryLength = query.length();
                                                  ArrayList<ShoppingListUserItem> newshoppingListUserItemList = new ArrayList<>();
                                                  ShoppingListUserItemAdapter shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), newshoppingListUserItemList);
                                                  for(int i = 0; i < finalshoppingListUserItems.size(); i++){
                                                      ShoppingListUserItem shoppingListUserItem =  finalshoppingListUserItems.get(i);
                                                      try{
                                                          if(shoppingListUserItem.getName().substring(0,searchQueryLength).equalsIgnoreCase(query)){
                                                              newshoppingListUserItemList.add(shoppingListUserItem);
                                                          }
                                                      }
                                                      catch (StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                      }

                                                  }
                                                  shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                                  return false;

                                              }

                                              @Override
                                              public boolean onQueryTextChange(String newText) {
                                                  Integer searchQueryLength = newText.length();
                                                  ArrayList<ShoppingListUserItem> newMainGodList = new ArrayList<ShoppingListUserItem>();
                                                  ShoppingListUserItemAdapter shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(),newMainGodList);
                                                  for(int i = 0; i < finalshoppingListUserItems.size(); i++){
                                                      ShoppingListUserItem shoppingListUserItem = (ShoppingListUserItem) finalshoppingListUserItems.get(i);
                                                      try{
                                                          if(shoppingListUserItem.getName().substring(0, searchQueryLength).equalsIgnoreCase(newText)){
                                                              newMainGodList.add(shoppingListUserItem);
                                                          }
                                                      }
                                                      catch(StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                      }
                                                  }
                                                  // if user has deleted all their text
                                                  if (newText.isEmpty()) {
                                                      resultsForshoppingListUserItemsView.setVisibility(View.GONE);
                                                  }
                                                  else {
                                                      resultsForshoppingListUserItemsView.setText("Results for " + newText);
                                                      resultsForshoppingListUserItemsView.setVisibility(View.VISIBLE);
                                                  }
                                                  shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
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
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
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
