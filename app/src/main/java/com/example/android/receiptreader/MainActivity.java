package com.example.android.receiptreader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView resultsForUserItemsView;
    ListView userItemsListView;
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
        setContentView(R.layout.activity_main);
        userItemsListView = findViewById(R.id.user_items_list_view);
        resultsForUserItemsView = findViewById(R.id.results_for_user_item_text);
        SearchView searchView = findViewById(R.id.search_bar);
        FloatingActionButton add_fab = findViewById(R.id.fab);
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SecondCameraActivity.class);
                startActivity(intent);

            }
        });

        ArrayList<UserItem> userItems = new ArrayList<UserItem>();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                userItems = QueryUtils.getUserItems();
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION MAIN THREAD");
            e.printStackTrace();
        }


        userItemsListView.setAdapter(new UserItemAdapter(this, userItems));

        hideSoftKeyboard(this);
        searchView.setIconified(false);

        ArrayList<UserItem> finalUserItems = userItems;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Integer searchQueryLength = query.length();
                    ArrayList<UserItem> newUserItemList = new ArrayList<>();
                    UserItemAdapter userItemAdapter = new UserItemAdapter(getApplicationContext(), newUserItemList);
                    for(int i = 0; i < finalUserItems.size(); i++){
                        UserItem userItem =  finalUserItems.get(i);
                        try{
                            if(userItem.getItemName().substring(0,searchQueryLength).equalsIgnoreCase(query)){
                                newUserItemList.add(userItem);
                            }
                        }
                        catch (StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                        }

                    }
                    userItemsListView.setAdapter(userItemAdapter);
                    return false;

                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    Integer searchQueryLength = newText.length();
                    ArrayList<UserItem> newMainGodList = new ArrayList<UserItem>();
                    UserItemAdapter userItemAdapter = new UserItemAdapter(getApplicationContext(),newMainGodList);
                    for(int i = 0; i < finalUserItems.size(); i++){
                        UserItem userItem = (UserItem) finalUserItems.get(i);
                        try{
                            if(userItem.getItemName().substring(0, searchQueryLength).equalsIgnoreCase(newText)){
                                newMainGodList.add(userItem);
                            }
                        }
                        catch(StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                        }
                    }
                    // if user has deleted all their text
                    if (newText.isEmpty()) {
                        resultsForUserItemsView.setVisibility(View.GONE);
                    }
                    else {
                        resultsForUserItemsView.setText("Results for " + newText);
                        resultsForUserItemsView.setVisibility(View.VISIBLE);
                    }
                    userItemsListView.setAdapter(userItemAdapter);
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