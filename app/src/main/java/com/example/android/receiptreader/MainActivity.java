package com.example.android.receiptreader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
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
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void navigationTutorial(int guideViewNum) throws InterruptedException, ParseException, IOException {

        // Get current version code

        // Get saved version code
        boolean isFirstTimeRun = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("firstTimeRun", true);
        System.out.println("IS FIRST TIME RUN: " + isFirstTimeRun);
        int mainPageTourClickNum = PreferenceManager.getDefaultSharedPreferences(this).getInt("mainPageTourClickNum", 0);
        final int[] mainClick = new int[1];
        mainClick[0] = mainPageTourClickNum;
        // Check for first run or upgrade

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean("priceComparisonUnitOn", true).apply();
        Constants.wantsPriceComparisonUnit = true;
        Constants.currentMeasureUnit = "grams (g)";
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                    .putString("measurementUnit", "grams (g)").apply();
        System.out.println("APPLIED MEASUREMENT UNIT FIRST RUN: " + Constants.currentMeasureUnit);

        shoppingLists = QueryUtils.getShoppingLists();
        shoppingListAdapter = new ShoppingListAdapter(getApplicationContext(), shoppingLists);
        shoppingListsView.setAdapter(shoppingListAdapter);
        stores = QueryUtils.getStores(getApplicationContext());
        storeListAdapter = new StoreListAdapter(getApplicationContext(), stores);
        storesListView.setAdapter(storeListAdapter);

//            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("firstTimeRun", false).apply();
//        System.out.println("UPDATED PREF: "+PreferenceManager.getDefaultSharedPreferences(this).getBoolean("firstTimeRun", true));
//        ArrayList<GuideView> tourGuideViewArrayList = new ArrayList<>();
//        View tourGuideNavButton = findViewById(R.id.tour_guide_nav_button);
//        tourGuideNavButton.setVisibility(View.VISIBLE);
//            tourGuideNavButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    tourGuideNavButtonClicked[0] = true;
//                    tourGuideNavButton.notify();
//                }
//            });
            // This is just a normal run
        GuideView shoppingListGuideView = new GuideView.Builder(this)
                    .setTitle("Shopping Lists")
                    .setContentText("This is where your shopping lists are and you can edit names and delete them as needed")
                    .setTargetView(findViewById(R.id.shopping_list_items_list_view))
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside)
                    .build(); //optional - default dismissible by TargetView

        GuideView shoppingListAddButtonGuideView = new GuideView.Builder(this)
                    .setTitle("Shopping Lists Add Button")
                    .setContentText("This is where you add new shopping lists")
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional'
                    .setTargetView(findViewById(R.id.shopping_list_fab))
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside)//optional - default dismissible by TargetView
                    .build();

        GuideView storeListViewGuideView = new GuideView.Builder(this)
                    .setTitle("Shopping Lists Search View")
                    .setContentText("This is where you can search within your shopping lists")
                    .setTargetView(findViewById(R.id.shopping_list_search_bar))
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build();

        GuideView storesListGuideView = new GuideView.Builder(this)
                    .setTitle("Store Lists")
                    .setContentText("This is where your stores are and you can edit names and delete them as needed")
                    .setTargetView(findViewById(R.id.stores_list_view))
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build();

        GuideView storesAddListButtonGuide = new GuideView.Builder(this)
                    .setTitle("Stores List Add Button")
                    .setContentText("This is where you add new stores")
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional'
                    .setTargetView(findViewById(R.id.store_list_fab))
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside)//optional - default dismissible by TargetView
                    .build();

        GuideView storesListSearchBarGuideView = new GuideView.Builder(this)
                    .setTitle("Stores List Search View")
                    .setContentText("This is where you can search within your stores ")
                    .setTargetView(findViewById(R.id.store_list_search_bars))
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build();


        GuideView shoppingCartView = new GuideView.Builder(this)
                    .setTitle("Shopping Cart")
                    .setContentText("This is where you can set where you are currently shopping for recorded purchases to be stored automatically. The store you chose here will be highlighted in blue below.")
                    .setTargetView(findViewById(R.id.shopping_list_icon))
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build();

        GuideView itemsRepGuideView = new GuideView.Builder(this)
                    .setTitle("Item Repository")
                    .setContentText("This is where your items that you add to shopping lists are stored. Adding an item to this repository adds it as an option as a possible item to add to shopping lists. Editing an item name here does not change any purchase details with the item's name it just updates it for future use in adding items to a shopping list. Deleting an item from the item repository does not delete history purchase details of the item, it will stop showing up as an option in adding items to shopping lists.")
                    .setTargetView(findViewById(R.id.item_repo_icon))
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build();
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.side_menu);
        View priceComparisonUnitView =  navigationView.getMenu().findItem(R.id.measurement_units_menu_item).getActionView();
        SwitchCompat mySwitch = (SwitchCompat) priceComparisonUnitView;
        mySwitch.setChecked(true);
        GuideView priceComparisonUnitGuideView = new GuideView.Builder(this)
                    .setTitle("Price Comparison Unit Functionality")
                    .setContentText("This functionality allows you to compare all items with purchases that have weight details in different measurement units, such as lb or kg, against each other in a common unit. Currently we have it set to grams for tutorial demonstration and after tutorial it will be toggled off. Toggling this on converts weight details to a common unit and allows you to compare prices across items, stores, and over time. Toggling this option on allows you to have insight graphs(price over time graphs) for shopping list items - this is further explained later in the tutorial.")
                    .setTargetView(priceComparisonUnitView)
                    .setContentTextSize(12)//optional
                    .setTitleTextSize(14)//optional
                    .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                    .setDismissType(DismissType.outside) //optional - default dismissible by TargetView
                    .build();

        if (guideViewNum == 1) {
            itemsRepGuideView.show();
        }
        else if(guideViewNum == 2){
            storesAddListButtonGuide.show();
        }

        else if(guideViewNum == 3){
            shoppingListAddButtonGuideView.show();
        }
        else if(guideViewNum == 4){
            if(!QueryUtils.ifShoppingListAlreadyExists(getString(R.string.empty_shoping_list))) {
                QueryUtils.addShoppingList(getString(R.string.empty_shoping_list), getApplicationContext());
            }
            if(!QueryUtils.ifStoreAlreadyExists(getString(R.string.empty_store))) {
                QueryUtils.addNewStore(getString(R.string.empty_store), getApplicationContext());
            }
            Intent intent = new Intent(MainActivity.this, ShoppingListUserItemsActivity.class);
            intent.putExtra("shoppingListName",getString(R.string.empty_shoping_list));
            intent.putExtra("isTour", true);
            intent.putExtra("tourSection", "addItemsToShoppingList");
            startActivity(intent);
        }
        else if(guideViewNum == 5){
            shoppingCartView.show();
        }
        else if(guideViewNum == 6){
            if(!QueryUtils.ifShoppingListAlreadyExists(getString(R.string.empty_shoping_list))) {
                QueryUtils.addShoppingList(getString(R.string.empty_shoping_list), getApplicationContext());
            }
            if(!QueryUtils.ifStoreAlreadyExists(getString(R.string.empty_store))) {
                QueryUtils.addNewStore(getString(R.string.empty_store), getApplicationContext());
            }
            Intent intent = new Intent(MainActivity.this, ShoppingListUserItemsActivity.class);
            intent.putExtra("shoppingListName",getString(R.string.empty_shoping_list));
            intent.putExtra("isTour", true);
            intent.putExtra("tourSection", "shopping");
            startActivity(intent);
        }
        else if(guideViewNum == 7){
            priceComparisonUnitGuideView.show();
        }
//
//            tourGuideViewArrayList.add(shoppingListGuideView);
//            tourGuideViewArrayList.add(shoppingListAddButtonGuideView);
//            tourGuideViewArrayList.add(storeListViewGuideView);
//            tourGuideViewArrayList.add(storesListGuideView);
//            tourGuideViewArrayList.add(storesAddListButtonGuide);
//            tourGuideViewArrayList.add(storesListSearchBarGuideView);
//            tourGuideViewArrayList.add(shoppingCartView);
//            tourGuideViewArrayList.add(itemsRepGuideView);
//            tourGuideViewArrayList.add(priceComparisonUnitGuideView);
//
//            if(mainClick[0] == 0) {
//                tourGuideViewArrayList.get(mainClick[0]).show();
//            }

//            tourGuideNavButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    System.out.println("MAIN CLICK RIGHT NOW: " + mainClick[0]);
//                    if(mainClick[0] < tourGuideViewArrayList.size()){
//                        tourGuideViewArrayList.get(mainClick[0]).dismiss();
//
//                        if(mainClick[0] == 8){
//                            drawerLayout.openDrawer(Gravity.RIGHT);
//                            navigationView.setVisibility(View.VISIBLE);
//
//
//                        }
//                        if(mainClick[0] < tourGuideViewArrayList.size()) {
//                            tourGuideViewArrayList.get(mainClick[0]).show();
//                        }
//                        mainClick[0]++;
//                        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putInt("mainPageTourClickNum", mainClick[0]).apply();
//
//
//                    }
//                    else if(mainClick[0] == tourGuideViewArrayList.size()){
//                        Intent intent = new Intent(MainActivity.this, ShoppingListUserItemsActivity.class);
//                        intent.putExtra("shoppingListName",getString(R.string.empty_shoping_list));
//                        intent.putExtra("isTour", true);
//                        startActivity(intent);
//                    }
//                }
//            });


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

                        stores = QueryUtils.getStores(getApplicationContext());
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

                            stores = QueryUtils.getStores(getApplicationContext());
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

                        stores = QueryUtils.getStores(getApplicationContext());
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
        Constants.storeBeingShoppedIn = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("selectedStore", "");
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
        MenuItem navigationTutorial = navigationView.findViewById(R.id.navigation_tutorial_menu);
        MenuItem checkable_measurement_item = navigationView.getMenu().findItem(R.id.measurement_units_menu_item);
        MenuItem addItemsToItemRepository = navigationView.getMenu().findItem(R.id.add_items_to_item_repository);
        MenuItem defineStore = navigationView.getMenu().findItem(R.id.define_store);
        MenuItem defineShoppingList = navigationView.getMenu().findItem(R.id.define_shopping_list);
        MenuItem addItemsToShoppingList = navigationView.getMenu().findItem(R.id.add_items_to_shopping_list);
        MenuItem setStoreForShopping = navigationView.getMenu().findItem(R.id.set_store_for_shopping);
        MenuItem shopping = navigationView.getMenu().findItem(R.id.shopping);
        MenuItem priceComparisonUnit = navigationView.getMenu().findItem(R.id.price_comparison_unit);
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
        addItemsToItemRepository.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    drawerLayout.closeDrawer(navigationView);
                    navigationTutorial(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        defineStore.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    drawerLayout.closeDrawer(navigationView);
                    navigationTutorial(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        defineShoppingList.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    drawerLayout.closeDrawer(navigationView);
                    navigationTutorial(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        addItemsToShoppingList.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    drawerLayout.closeDrawer(navigationView);
                    navigationTutorial(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        setStoreForShopping.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    drawerLayout.closeDrawer(navigationView);
                    navigationTutorial(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        shopping.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    drawerLayout.closeDrawer(navigationView);
                    navigationTutorial(6);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        priceComparisonUnit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                try {
                    navigationTutorial(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("selectedStore", originalStoreName).apply();
                            stores = refactorStoresOrder(stores, Constants.storeBeingShoppedIn);

                        } else{
                            if(Constants.storeBeingShoppedIn.equals(originalStoreName)){
                                Constants.storeBeingShoppedIn = "";
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("selectedStore", "").apply();
                                stores = refactorStoresOrder(stores, Constants.storeBeingShoppedIn);
                            }
                            else {
                                Constants.storeBeingShoppedIn = originalStoreName;
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("selectedStore", originalStoreName).apply();
                                stores = refactorStoresOrder(stores, originalStoreName);
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

                stores = QueryUtils.getStores(getApplicationContext());
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

        RelativeLayout shoppingListLy = findViewById(R.id.shopping_lists_ly);
        shoppingListAdapter = new ShoppingListAdapter(this, shoppingLists);
        storeListAdapter = new StoreListAdapter(this, stores);
        shoppingListsView.setAdapter(shoppingListAdapter);
        storesListView.setAdapter(storeListAdapter);
        shoppingListLy.setMinimumHeight((int) (getWindowManager().getDefaultDisplay().getHeight() * (0.1)));
        shoppingListsView.setMinimumHeight((int) (getWindowManager().getDefaultDisplay().getHeight() * (0.4)));

        hideSoftKeyboard(this);
        // all functions for searching through the stores list view
        storesSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                    @Override
                                                    public boolean onQueryTextSubmit(String query) {
                                                        Integer searchQueryLength = query.length();
                                                        ArrayList<Store> newStoreList = new ArrayList<>();
                                                        storeListAdapter = new StoreListAdapter(getApplicationContext(), newStoreList);
                                                        try {

                                                            stores = QueryUtils.getStores(getApplicationContext());
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        for(int i = 0; i < stores.size(); i++){
                                                            Store store =  stores.get(i);
                                                            try{
                                                                if(store.getStoreName().toLowerCase().contains(query.toLowerCase())){
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
                                                        storeListAdapter = new StoreListAdapter(getApplicationContext(), newStoreList);
                                                        try {

                                                            stores = QueryUtils.getStores(getApplicationContext());
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        for(int i = 0; i < stores.size(); i++){
                                                            Store store = (Store) stores.get(i);
                                                            try{
                                                                if(store.getStoreName().toLowerCase().contains(newText.toLowerCase())){
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
                                                              shoppingListAdapter = new ShoppingListAdapter(getApplicationContext(), newShoppingListList);
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
                                                                      if(shoppingList.getName().toLowerCase().contains(query.toLowerCase())){
                                                                          newShoppingListList.add(shoppingList);
                                                                      }
                                                                  }
                                                                  catch (StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                                  }

                                                              }
                                                              shoppingListsView.setAdapter(shoppingListAdapter);
                                                              return false;

                                                          }

                                                          @Override
                                                          public boolean onQueryTextChange(String newText) {
                                                              Integer searchQueryLength = newText.length();
                                                              ArrayList<ShoppingList> newShoppingListList = new ArrayList<ShoppingList>();
                                                              shoppingListAdapter = new ShoppingListAdapter(getApplicationContext(), newShoppingListList);
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
                                                                      if(shoppingList.getName().toLowerCase().contains(newText.toLowerCase())){
                                                                          newShoppingListList.add(shoppingList);
                                                                      }
                                                                  }
                                                                  catch(StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                                  }
                                                              }
                                                              shoppingListsView.setAdapter(shoppingListAdapter);
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
                (ConstraintLayout) findViewById(R.id.moreVertActionsLayoutDialog)
        );
        builder.setView(view);
        ListView measurementUnitsListView = view.findViewById(R.id.simple_measurement_units_list_view);
        Button cancelButton = view.findViewById(R.id.close_button);
        SimpleMeasurementUnitItemAdapter simpleMeasurementUnitItemAdapter = new SimpleMeasurementUnitItemAdapter(getApplicationContext(), measurementUnits);
        measurementUnitsListView.setAdapter(simpleMeasurementUnitItemAdapter);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
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
                System.out.println("APPLIED MEASUREMENT UNIT THROUGH CHOSEN MEANS: " + Constants.currentMeasureUnit);
                measurementUnitsListView.setAdapter(simpleMeasurementUnitItemAdapter);
                measurementUnitsListView.setSelectionFromTop(index, top);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setCanceledOnTouchOutside(true);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    public ArrayList<Store> refactorStoresOrder(ArrayList<Store> stores, String storeName) {
        int position = 0;
        Store specifiedStore = null;
        for(Store store: stores){
            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("selectedStore", "").equalsIgnoreCase(store.getStoreName())){
                specifiedStore = store;
                position = stores.indexOf(store);
            }
        }
        if(!(specifiedStore == null)){
            stores.remove(position);
            stores.add(0, specifiedStore);
        }
        System.out.println("REFACTORED STORES");
        for(Store store: stores){
            System.out.println("NAME: " + store.getStoreName());
        }
        return stores;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.app_menu, menu);
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
