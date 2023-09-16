package com.example.android.receiptreader;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    //    TextView resultsForStoresViews;
//    TextView resultsForShoppingListsViews;
    ShoppingListAdapter shoppingListAdapter;
    StoreListAdapter storeListAdapter;
    Observer<View> itemRepLabelObserver;
    SimpleStoreListAdapter simpleStoreListAdapter;
    ArrayList<Store> stores;
    ArrayList<ShoppingList> shoppingLists;
    String inputTextToRetrieved = "";
    Observer<String> shoppingListLaunchUpdatedObserver;
    public static MutableLiveData<String> shoppingListLaunchNeedsToBeUpdated = new MutableLiveData<>();
    Observer<String> storeLaunchUpdatedObserver;
    public static MutableLiveData<String> storeLaunchNeedsToBeUpdated = new MutableLiveData<>();
    ListView shoppingListsView;
    ListView storesListView;

    public static void updateShoppingListLaunch(String originalShoppingListName){
        shoppingListLaunchNeedsToBeUpdated.postValue(originalShoppingListName);
    }



    public void shoppingListLaunch(String originalShoppingListName) {
        Intent intent = new Intent(MainActivity.this, ShoppingListUserItemsActivity.class);
        intent.putExtra("shoppingListName",originalShoppingListName);
        startActivity(intent);
        shoppingListLaunchNeedsToBeUpdated.removeObserver(shoppingListLaunchUpdatedObserver);
    }

    public static void updateStoreLaunch(String originalStoreName){
        storeLaunchNeedsToBeUpdated.postValue(originalStoreName);
    }

    public void storeLaunch(String originalStoreName) {
        Intent intent = new Intent(MainActivity.this, StoreUserItemsActivity.class);
        intent.putExtra("storeName", originalStoreName);
        startActivity(intent);
        storeLaunchNeedsToBeUpdated.removeObserver(storeLaunchUpdatedObserver);
    }

    public boolean isVisible(final View view) {
        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return (location[1] <= getWindowManager().getDefaultDisplay().getHeight());
    }

    private void showDialog(String title, int jsonEditCode, @Nullable String originalName) {
        final int finalJsonEditCode = jsonEditCode;
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (MainActivity.this, R.style.AlertDialogCustom);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.custom_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText(title);
        ImageView icon = (ImageView) view.findViewById(R.id.imageIcon);
        TextView editText = view.findViewById(R.id.custom_dialog_edit_text);
        Button enterButton = (Button) view.findViewById(R.id.enterButton);
        if(jsonEditCode == JSONEditCodes.DELETE_STORE || jsonEditCode == JSONEditCodes.DELETE_SHOPPING_LIST){
            editText.setVisibility(View.INVISIBLE);
            icon.setMinimumHeight(50);
            icon.setMinimumWidth(50);
            enterButton.setText(R.string.yes);
            icon.setImageResource(R.drawable.delete_foreground); // making the pop up icon a trash can since by default it is the edit icon
            TextView delete_text =  (TextView) view.findViewById(R.id.delete_text);
            delete_text.setVisibility(View.VISIBLE);
            delete_text.setText(String.format("Are you sure you want to delete %s, this action is permanent and cannot be undone", originalName));
        }
        else if(finalJsonEditCode == JSONEditCodes.ADD_NEW_STORE || finalJsonEditCode == JSONEditCodes.ADD_NEW_SHOPPING_LIST){
            icon.setMinimumHeight(25);
            icon.setMinimumWidth(25);
            icon.setImageResource(R.drawable.ic_baseline_add_24); // making the pop up icon a edit symbol
            editText.setVisibility(View.VISIBLE);
            enterButton.setText(getString(R.string.enter));
            view.findViewById(R.id.delete_text).setVisibility(View.INVISIBLE); // making sure the are you sure you want to delete text is not going to show up from previous possible uses

        }
        else{ // edit functions
            icon.setMinimumHeight(25);
            icon.setMinimumWidth(25);
            icon.setImageResource(R.drawable.ic_baseline_edit_24); // making the pop up icon a edit symbol
            editText.setVisibility(View.VISIBLE);
            editText.setText(originalName);
//            editText.setHint(getString(R.string.enter_new_name));
            enterButton.setText(getString(R.string.enter));
            view.findViewById(R.id.delete_text).setVisibility(View.INVISIBLE); // making sure the are you sure you want to delete text is not going to show up from previous possible uses

        }
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        enterButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(finalJsonEditCode == JSONEditCodes.EDIT_STORE_NAME){
                    try {
                        System.out.println("EDIT TEXT VAL SUBMIT MOMENT: "+ editText.getText());
                        QueryUtils.editStoreName(originalName, String.valueOf(editText.getText()), getApplicationContext());
                        stores =  QueryUtils.getStores();
                        storeListAdapter = new StoreListAdapter(getApplicationContext(), stores);
                        storesListView.setAdapter(storeListAdapter);
                        alertDialog.dismiss();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(finalJsonEditCode == JSONEditCodes.EDIT_SHOPPING_LIST_NAME){
                    try {
                        QueryUtils.editShoppingListName(originalName, String.valueOf(editText.getText()), getApplicationContext());
                        shoppingLists =  QueryUtils.getShoppingLists();
                        shoppingListAdapter = new ShoppingListAdapter(getApplicationContext(), shoppingLists);
                        shoppingListsView.setAdapter(shoppingListAdapter);
                        alertDialog.dismiss();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(finalJsonEditCode == JSONEditCodes.ADD_NEW_STORE){ // should only be triggered by add fab button for stores
                    try {
                        String editTextVal =String.valueOf(editText.getText());
                        if(!QueryUtils.ifStoreAlreadyExists(editTextVal)) {
                            QueryUtils.addNewStore(editTextVal, getApplicationContext());
                            stores = QueryUtils.getStores();
                            storeListAdapter = new StoreListAdapter(getApplicationContext(), stores);
                            storesListView.setAdapter(storeListAdapter);
//                            View itemRepLabel = findViewById(R.id.item_repository_label);
//                            if(!isVisible(itemRepLabel)){
//                                System.out.println("NOT VISIBLE");
//                                ConstraintLayout constraintLayout = findViewById(R.id.stores_list_view_and_item_rep_label_cl);
//                                ConstraintSet constraintSet = new ConstraintSet();
//                                constraintSet.connect(R.id.stores_list_view, ConstraintSet.TOP, R.id.stores_list_view_and_item_rep_label_cl, ConstraintSet.TOP, 5);
//                                constraintSet.connect(R.id.stores_list_view, ConstraintSet.BOTTOM, R.id.item_repository_label, ConstraintSet.TOP, 5);
//                                constraintSet.connect(R.id.item_repository_label, ConstraintSet.BOTTOM, R.id.stores_list_view_and_item_rep_label_cl, ConstraintSet.BOTTOM, 15);
//                                constraintLayout.setConstraintSet(constraintSet);
//
//                            }
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, String.format(getString(R.string.store_already_exists), editTextVal), Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }

                }
                if(finalJsonEditCode == JSONEditCodes.ADD_NEW_SHOPPING_LIST){ // should only be triggered by add fab button for shopping lists
                    try {
                        String editTextVal =String.valueOf(editText.getText());
                        System.out.println("IM BEING ACCESSED @addNewShoppingList finalJsonEditCode in showDialog");
                        if(!QueryUtils.ifShoppingListAlreadyExists(editTextVal)) {
                            QueryUtils.addShoppingList(String.valueOf(editText.getText()), getApplicationContext());
                            shoppingLists = QueryUtils.getShoppingLists();
                            shoppingListAdapter = new ShoppingListAdapter(getApplicationContext(), shoppingLists);
                            shoppingListsView.setAdapter(shoppingListAdapter);
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, String.format(getString(R.string.shopping_list_already_exists), editTextVal), Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }

                }
                if(finalJsonEditCode == JSONEditCodes.DELETE_STORE){ // should only be triggered by add fab button for shopping lists
                    try {
                        QueryUtils.deleteStore(originalName, getApplicationContext());
                        ConstraintLayout constraintLayout = findViewById(R.id.stores_list_view_and_item_rep_label_cl);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.stores_list_view, ConstraintSet.TOP, R.id.stores_list_view_and_item_rep_label_cl, ConstraintSet.TOP, 5);
                        constraintSet.connect(R.id.stores_list_view_and_item_rep_label_cl, ConstraintSet.TOP, R.id.stores_label, ConstraintSet.BOTTOM, 5);
                        constraintLayout.setConstraintSet(constraintSet);
                        stores =  QueryUtils.getStores();
                        storeListAdapter = new StoreListAdapter(getApplicationContext(), stores);
                        storesListView.setAdapter(storeListAdapter);
                        alertDialog.dismiss();
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }

                }
                if(finalJsonEditCode == JSONEditCodes.DELETE_SHOPPING_LIST){ // should only be triggered by add fab button for shopping lists
                    try {
                        QueryUtils.deleteShoppingList(originalName, getApplicationContext());
                        shoppingLists =  QueryUtils.getShoppingLists();
                        shoppingListAdapter = new ShoppingListAdapter(getApplicationContext(), shoppingLists);
                        shoppingListsView.setAdapter(shoppingListAdapter);
                        alertDialog.dismiss();
                    } catch (ParseException | IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        System.out.println("made it hideSoftKeyboard");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.json_data_str = PreferenceManager.
                getDefaultSharedPreferences(this).getString("jsonData","{\n" +
                "  \"stores\": [],\n" +
                "  \"shopping_lists\":[],\n" +
                "  \"general_items_master\": []\n" +
                "}");
        Constants.currentMeasureUnit = PreferenceManager.getDefaultSharedPreferences(this).getString("measurementUnit", "");
        Constants.wantsPriceComparisonUnit = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("priceComparisonUnitOn", false);
        System.out.println("WANTS PRICE COMPARISON UNIT: " + Constants.wantsPriceComparisonUnit);
        if(Constants.currentMeasureUnit.isEmpty() && Constants.wantsPriceComparisonUnit ){
            ArrayList<String> measurementUnitsArrayList = new ArrayList<>();
            String[] measurementUnitsArray = getResources().getStringArray(R.array.measurement_units_array);
            System.out.println("MEASUREMENT UNITS: " + measurementUnitsArray.toString());
            measurementUnitsArrayList.addAll(Arrays.asList(measurementUnitsArray));
            measurementUnitsDialog(measurementUnitsArrayList);

        }
        ImageView shoppingCartIcon = findViewById(R.id.shopping_list_icon);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.side_menu);
        MenuItem checkable_measurement_item = navigationView.getMenu().findItem(R.id.measurement_units_menu_item);
        SwitchCompat mySwitch = (SwitchCompat) checkable_measurement_item.getActionView();
        mySwitch.setChecked(Constants.wantsPriceComparisonUnit);
        checkable_measurement_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ArrayList<String> measurementUnitsArrayList = new ArrayList<>();
                String[] measurementUnitsArray = getResources().getStringArray(R.array.measurement_units_array);
                System.out.println("MEASUREMENT UNITS: " + measurementUnitsArray[1]);
                measurementUnitsArrayList.addAll(Arrays.asList(measurementUnitsArray));
                System.out.println("MEASUREMENT UNITS: " +measurementUnitsArrayList);
                measurementUnitsDialog(measurementUnitsArrayList);
                return true;
            }
        });

        shoppingCartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder =
                        new androidx.appcompat.app.AlertDialog.Builder
                                (MainActivity.this, R.style.AlertDialogCustom);
                View view = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.choose_store_currently_shopping_in,
                        (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
                );
                builder.setView(view);
                ((TextView) view.findViewById(R.id.textTitle))
                        .setText(getString(R.string.choose_a_store_text_tile));
                final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(true);
                ListView chooseStoreListView = view.findViewById(R.id.chose_stores_list_view);
                simpleStoreListAdapter = new SimpleStoreListAdapter(MainActivity.this, stores);
                chooseStoreListView.setAdapter(simpleStoreListAdapter);
                chooseStoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Store selectedStore = (Store) parent.getItemAtPosition(position);
                        String originalStoreName = selectedStore.getStoreName();
                        if(Constants.storeBeingShoppedIn.isEmpty()) {
                            Constants.storeBeingShoppedIn = originalStoreName; // set the store being shopped in the store selected, and all items whose voice details are there on recorded are saved under this store
                            selectedStore.setIfHighlighted(true);
                        } else{
                            if(Constants.storeBeingShoppedIn.equals(originalStoreName)){
                                Constants.storeBeingShoppedIn = "";
                                stores = setAllStoresToNotBeingShoppedInExcept(stores, Constants.storeBeingShoppedIn);
                            }
                            else {
                                Constants.storeBeingShoppedIn = originalStoreName;
                                stores = setAllStoresToNotBeingShoppedInExcept(stores, originalStoreName);
                            }
                        }
                        simpleStoreListAdapter  = new SimpleStoreListAdapter(MainActivity.this, stores);
                        storeListAdapter = new StoreListAdapter(MainActivity.this, stores);
                        chooseStoreListView.setAdapter(simpleStoreListAdapter);
                        storesListView.setAdapter(storeListAdapter);
                    }
                });
                view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
            }
        });

        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                if(!Constants.wantsPriceComparisonUnit){
                    ArrayList<String> measurementUnitsArrayList = new ArrayList<>();
                    String[] measurementUnitsArray = getResources().getStringArray(R.array.measurement_units_array);
                    System.out.println("MEASUREMENT UNITS: " + measurementUnitsArray.toString());
                    measurementUnitsArrayList.addAll(Arrays.asList(measurementUnitsArray));
                    measurementUnitsDialog(measurementUnitsArrayList);
                }
                Constants.wantsPriceComparisonUnit = true;
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("priceComparisonUnitOn", true).apply();
            } else{
                Constants.wantsPriceComparisonUnit = false;
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("priceComparisonUnitOn", false).apply();


            }
        });
        ImageView menuImageView = (ImageView) findViewById(R.id.menu_image_view);
        menuImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
        System.out.print("JSON DATA: " + Constants.json_data_str);
        shoppingListsView = (ListView) findViewById(R.id.shopping_list_items_list_view);
        storesListView = (ListView) findViewById(R.id.stores_list_view);
/*
        TextView itemRepTv = (TextView) findViewById(R.id.item_repository_label);
*/
        ImageView itemRepImageView = (ImageView) findViewById(R.id.item_repo_icon);
        SearchView shoppingListSearchView = findViewById(R.id.shopping_list_search_bar);
        SearchView storesSearchView = findViewById(R.id.store_list_search_bars);
        storesSearchView.clearFocus();
        shoppingListSearchView.clearFocus();
        FloatingActionButton addStoreFab = (FloatingActionButton) findViewById(R.id.store_list_fab);
        FloatingActionButton addShoppingListFab = (FloatingActionButton) findViewById(R.id.shopping_list_fab);
        stores = new ArrayList<Store>();
        shoppingLists = new ArrayList<ShoppingList>();
        // retrieving data to passed to adapters
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                shoppingLists = QueryUtils.getShoppingLists();
                stores = QueryUtils.getStores();
            }
        }
        catch (Exception e) {
            System.out.println("EXCEPTION MAIN THREAD");
            e.printStackTrace();
        }
        shoppingListLaunchUpdatedObserver = new Observer<String>() {
            @Override
            public void onChanged(String newString) {
                if(!newString.equals("")){
                    shoppingListLaunchNeedsToBeUpdated.postValue("");
                    shoppingListLaunch(newString);
                    
                }
            }
        };
        shoppingListLaunchNeedsToBeUpdated.observeForever(shoppingListLaunchUpdatedObserver);
        storeLaunchUpdatedObserver = new Observer<String>() {
            @Override
            public void onChanged(String newString) {
                if(!newString.equals("")){
                    storeLaunchNeedsToBeUpdated.postValue("");
                    storeLaunch(newString);

                }
            }
        };
        storeLaunchNeedsToBeUpdated.observeForever(storeLaunchUpdatedObserver);
//        View itemRepLabel = findViewById(R.id.item_repository_label);

        itemRepImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RepItemsActivity.class);
                startActivity(intent);
                storeLaunchNeedsToBeUpdated.removeObserver(storeLaunchUpdatedObserver);
                shoppingListLaunchNeedsToBeUpdated.removeObserver(shoppingListLaunchUpdatedObserver);
            }
        });

//        itemRepLabel.setTag(itemRepLabel.getVisibility());
//        itemRepLabel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                System.out.println("TRIGGERED: " + isVisible(itemRepLabel));
//                    if(!isVisible(itemRepLabel)){
//                        System.out.println("NOT VISIBLE?!?!?!");
//                        ListView storeListView = findViewById(R.id.stores_list_view);
//                        storeListView.setLayoutParams(new ConstraintLayout.LayoutParams(storeListView.getWidth(), storeListView.getHeight()-itemRepLabel.getHeight()-10));
//                        storeListView.setClickable(true);
//ibility has changed
//            }                    }
//                //vis
//        });
        RelativeLayout shoppingListLy = findViewById(R.id.shopping_lists_ly);
        shoppingListAdapter = new ShoppingListAdapter(this, shoppingLists);
        storeListAdapter = new StoreListAdapter(this, stores);
        shoppingListsView.setAdapter(shoppingListAdapter);
        storesListView.setAdapter(storeListAdapter);
        shoppingListLy.setMinimumHeight((int) (getWindowManager().getDefaultDisplay().getHeight() * (0.1)));
        shoppingListsView.setMinimumHeight((int) (getWindowManager().getDefaultDisplay().getHeight() * (0.4)));
//        itemRepTv.setHeight((int) (getWindowManager().getDefaultDisplay().getHeight() * (0.1))); // setting item rep Tv height as 5 percent of the screen height
//        itemRepTv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                System.out.println("CURRENT PERCEN: " +storesListView.getHeight() + " WINDOW: " + getWindowManager().getDefaultDisplay().getHeight());
//                Double listVHeight = Double.parseDouble(String.valueOf(storesListView.getHeight()));
//                Double windowHeight = Double.parseDouble(String.valueOf(getWindowManager().getDefaultDisplay().getHeight()));
//                System.out.println("PERCEN: " + (listVHeight/windowHeight));
//                if((listVHeight/windowHeight) > 0.4) {
//                        System.out.println("CALLED ON GLOBALLAYOUT");
//                    }
//
//            }
//        });
        hideSoftKeyboard(this);
        // all functions for searching through the stores list view
        storesSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                    @Override
                                                    public boolean onQueryTextSubmit(String query) {
                                                        Integer searchQueryLength = query.length();
                                                        ArrayList<Store> newStoreList = new ArrayList<>();
                                                        StoreListAdapter storeListAdapter = new StoreListAdapter(getApplicationContext(), newStoreList);
                                                        try {
                                                            stores = QueryUtils.getStores();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        for(int i = 0; i < stores.size(); i++){
                                                            Store store =  stores.get(i);
                                                            try{
                                                                if(store.getStoreName().substring(0,searchQueryLength).equalsIgnoreCase(query)){
                                                                    newStoreList.add(store);
                                                                }
                                                            }
                                                            catch (StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                            }

                                                        }
                                                        storesListView.setAdapter(storeListAdapter);
                                                        return false;

                                                    }

                                                    @Override
                                                    public boolean onQueryTextChange(String newText) {
                                                        Integer searchQueryLength = newText.length();
                                                        ArrayList<Store> newStoreList = new ArrayList<Store>();
                                                        StoreListAdapter storeListAdapter = new StoreListAdapter(getApplicationContext(), newStoreList);
                                                        try {
                                                            stores = QueryUtils.getStores();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        for(int i = 0; i < stores.size(); i++){
                                                            Store store = (Store) stores.get(i);
                                                            try{
                                                                if(store.getStoreName().substring(0, searchQueryLength).equalsIgnoreCase(newText)){
                                                                    newStoreList.add(store);
                                                                }
                                                            }
                                                            catch(StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                            }
                                                        }
                                                        storesListView.setAdapter(storeListAdapter);
                                                        return false;
                                                    }


                                                }

        );
        // filter through user_items list with user items list adapter
        storesSearchView.setOnCloseListener( new SearchView.OnCloseListener() {
                                                 @Override
                                                 public boolean onClose() {
                                                     return false;
                                                 }
                                             }
        );


        // all functions for searching through the shoppingLists list view
        shoppingListSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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
        // listener for editing a specific store name
        storesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Store store = (Store) storeListAdapter.getItem(i);
                String originalStoreName = store.getStoreName();
                View selectedStoreView = storeListAdapter.getView(i, view, adapterView);
                ImageView editViewIcon = (ImageView) selectedStoreView.findViewById(R.id.edit_name_button);
                ImageView deleteButton = (ImageView) selectedStoreView.findViewById(R.id.delete_item_button);
//                ImageView shoppingCartButton = (ImageView) selectedStoreView.findViewById(R.id.select_shopping_mode);
                ConstraintLayout store_name_cl = (ConstraintLayout) selectedStoreView.findViewById(R.id.store_name_cl);
                editViewIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(getString(R.string.edit_store_name),JSONEditCodes.EDIT_STORE_NAME, originalStoreName);
                    }
                });
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(getString(R.string.delete_store),  JSONEditCodes.DELETE_STORE, originalStoreName);
                    }
                });

//                store_name_cl.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        System.out.println("MADE IT STORE historyButton @onClick");
//                        Intent intent = new Intent(MainActivity.this, StoreUserItemsActivity.class);
//                        intent.putExtra("storeName", store.getStoreName());
//                        intent.putExtra("title", store.getStoreName());
//                        startActivity(intent);
//                    }
//                });
//                shoppingCartButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if(Constants.storeBeingShoppedIn.isEmpty()) {
//                            Constants.storeBeingShoppedIn = originalStoreName; // set the store being shopped in the store selected, and all items whose voice details are there on recorded are saved under this store
//                            store.setIfHighlighted(true);
//                        } else{
//                            if(Constants.storeBeingShoppedIn.equals(originalStoreName)){
//                                Constants.storeBeingShoppedIn = "";
//                                stores = setAllStoresToNotBeingShoppedInExcept(stores, Constants.storeBeingShoppedIn);
//                            }
//                            else {
//                                Constants.storeBeingShoppedIn = originalStoreName;
//                                stores = setAllStoresToNotBeingShoppedInExcept(stores, originalStoreName);
//                            }
//                        }
//                        // update list view
//                        storeListAdapter = new StoreListAdapter(MainActivity.this, stores);
//                        storesListView.setAdapter(storeListAdapter);
//                    }
//                });

            }
        });

        shoppingListsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingList shoppingList = (ShoppingList) shoppingListAdapter.getItem(i);
                String originalShoppingListName = shoppingList.getName();
                View selectedStoreView = shoppingListAdapter.getView(i, view, adapterView);
                ImageView editViewIcon = (ImageView) selectedStoreView.findViewById(R.id.edit_name_sl_button);
                ImageView deleteButton = (ImageView) selectedStoreView.findViewById(R.id.delete_item_button);
                ConstraintLayout shopping_list_name_cl = (ConstraintLayout) selectedStoreView.findViewById(R.id.shopping_list_name_cl);
//                shopping_list_name_cl.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(MainActivity.this, ShoppingListUserItemsActivity.class);
//                        intent.putExtra("shoppingListName",originalShoppingListName);
//                        startActivity(intent);
//
//                    }
//                });
                editViewIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(getString(R.string.edit_shopping_list), JSONEditCodes.EDIT_SHOPPING_LIST_NAME, originalShoppingListName);

                    }
                });
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showDialog(getString(R.string.delete_shopping_list), JSONEditCodes.DELETE_SHOPPING_LIST, originalShoppingListName);
                    }
                });

            }

        });

        addStoreFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(getString(R.string.add_store), JSONEditCodes.ADD_NEW_STORE, null);
            }
        });

        addShoppingListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(getString(R.string.add_shopping_list), JSONEditCodes.ADD_NEW_SHOPPING_LIST, null);
            }
        });

//        itemRepTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, RepItemsActivity.class);
//                startActivity(intent);
//                storeLaunchNeedsToBeUpdated.removeObserver(storeLaunchUpdatedObserver);
//                shoppingListLaunchNeedsToBeUpdated.removeObserver(shoppingListLaunchUpdatedObserver);
//            }
//        });


    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void measurementUnitsDialog(ArrayList<String> measurementUnits) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (MainActivity.this, R.style.AlertDialogCustom);
        View view = LayoutInflater.from(MainActivity.this).inflate(
                R.layout.measurement_units_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialog)
        );
        builder.setView(view);
        ListView measurementUnitsListView = view.findViewById(R.id.simple_measurement_units_list_view);
        SimpleMeasurementUnitItemAdapter simpleMeasurementUnitItemAdapter = new SimpleMeasurementUnitItemAdapter(getApplicationContext(), measurementUnits);
        measurementUnitsListView.setAdapter(simpleMeasurementUnitItemAdapter);
        measurementUnitsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String measurementUnit =  simpleMeasurementUnitItemAdapter.getItem(position);
                int index = measurementUnitsListView.getFirstVisiblePosition();
                View v = measurementUnitsListView.getChildAt(0);
                int top = (v == null) ? 0 : v.getTop();
                Constants.currentMeasureUnit = measurementUnit;
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                        .putString("measurementUnit", measurementUnit).apply();
                measurementUnitsListView.setAdapter(simpleMeasurementUnitItemAdapter);
                measurementUnitsListView.setSelectionFromTop(index, top);
            }
        });
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public ArrayList<Store> setAllStoresToNotBeingShoppedInExcept(ArrayList<Store> stores, String storeName) {
        for(Store store: stores){
            store.setIfHighlighted(store.getStoreName().equals(storeName));
        }
        return stores;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        System.out.println("ID: " + id);
        //noinspection SimplifiableIfStatement
        if (id == R.id.measurement_units_menu_item) {
            if(item.isChecked()){
                if(!Constants.wantsPriceComparisonUnit){
                    ArrayList<String> measurementUnitsArrayList = new ArrayList<>();
                    String[] measurementUnitsArray = getResources().getStringArray(R.array.measurement_units_array);
                    System.out.println("MEASUREMENT UNITS: " + measurementUnitsArray.toString());
                    measurementUnitsArrayList.addAll(Arrays.asList(measurementUnitsArray));
                    measurementUnitsDialog(measurementUnitsArrayList);
                }
                Constants.wantsPriceComparisonUnit = true;
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("priceComparisonUnitOn", true).apply();
            } else{
                Constants.wantsPriceComparisonUnit = false;
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("priceComparisonUnitOn", false).apply();


            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Nullable
    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return super.onRetainCustomNonConfigurationInstance();
    }
}
