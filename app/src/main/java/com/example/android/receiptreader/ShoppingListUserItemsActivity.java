package com.example.android.receiptreader;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
//import android.content.Intent;
//import javax.measure.Measure;
//import javax.measure.UnitConverter;
//import javax.measure.quantity.Length;
//import static javax.measure.unit.NonSI.*;
//import static javax.measure.unit.SI.*;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;

import static android.os.Build.VERSION_CODES.O;
import static android.text.TextUtils.isEmpty;
import static android.view.View.GONE;

public class ShoppingListUserItemsActivity extends AppCompatActivity {
    ShoppingListUserItemAdapter shoppingListUserItemAdapter;
    TextView resultsForshoppingListUserItemsView;
    Observer<Boolean> updateObserver;
    View moreVertActionsView;
    View insightsView;
    AlertDialog insightViewAlertDialog;
    AlertDialog alertDialog;
    Date date;
    ConstraintLayout viewInsightsCl;
    SwipeMenuListView shoppingListUserItemsListView;
    String shoppingListName;
    public static MutableLiveData<Boolean> actuallyNeedsToBeUpdated = new MutableLiveData<>();
    int quantityMicrophoneState = 0;
    int unitPriceMicrophoneState = 0;
    int withinPackageMicrophoneState = 0;
    int additionalWeightMicrophoneState = 0;
    ArrayList<ShoppingListUserItem> shoppingListUserItems;
    private boolean occupied = false;

    //    static Thread updateRunnable =  new Thread(
//            new Runnable() {
//                @RequiresApi(api = O)
//                public void run() {
//                    System.out.println("ACTUALLYNEEDS TO BE UPDATED RUNNIn");
//                            try {
//                                shop                bb  bbb  bn bnnb  pingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getClass().getCOnt, shoppingListUserItems, shoppingListName, shoppingListUserItemsListView);
//                        }
//
//            });
    @RequiresApi(api = O)
    public static void update(){
        actuallyNeedsToBeUpdated.postValue(true);
    }

    public static String getFirstNotEmpty(List<String> list) {
        for (String item : list) {
            if (!isEmpty(item)) {
                return item;
            }
        }
        return null;
    }

    public void errorDialog(String message){
        if(!occupied) {
            occupied = true;
            androidx.appcompat.app.AlertDialog.Builder errorBuilder =
                    new androidx.appcompat.app.AlertDialog.Builder
                            (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom);
            View errorView = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                    R.layout.error_dialog,
                    (ConstraintLayout) findViewById(R.id.layoutDialogContainerVID)
            );
            errorBuilder.setView(errorView);
            TextView errorMessage = errorView.findViewById(R.id.error_message);
            errorMessage.setText(message);
            Button okButton = errorView.findViewById(R.id.okButton);
            final androidx.appcompat.app.AlertDialog errorAlertDialog = errorBuilder.create();
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorAlertDialog.dismiss();
                    occupied = false;
                }
            });
            errorAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    occupied = false;
                }
            });
            if (errorAlertDialog.getWindow() != null) {
                errorAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
        errorAlertDialog.show();
        }


    }
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

    private boolean eligibleForInsights(ArrayList<StoreUserItem> storeUserItemArrayList) throws java.text.ParseException {
        if(storeUserItemArrayList == null){
            return false;
        }
        boolean eligible = true;
        ConcurrentHashMap<String, ArrayList<StoreUserItem>> differentStores = new ConcurrentHashMap<String, ArrayList<StoreUserItem>>();
        // sorting into different stores
        for(StoreUserItem item: storeUserItemArrayList){
            if(item != null) {
                if (!differentStores.containsKey(item.getStore()) && item.getAdditionalWeightUnitPriceDetail() != null) {
                    differentStores.put(item.getStore(), new ArrayList<StoreUserItem>());
                }
                try {
                    differentStores.get(item.getStore()).add(item);
                } catch(Exception e){

                }
            }
        }
        // sorting by date
        for(String key: differentStores.keySet()){
            ArrayList<StoreUserItem> history = differentStores.get(key);
            StoreUserItem oldestBoughtItem = history.get(0);
            StoreUserItem newestBoughtItem = history.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            Date oldestBoughtDate = sdf.parse(oldestBoughtItem.getDateOfPurchase());
            Date newestBoughtDate = sdf.parse(newestBoughtItem.getDateOfPurchase());
            for(StoreUserItem item: history){
                Date itemDate = sdf.parse(item.getDateOfPurchase());
                if(itemDate.before(oldestBoughtDate)){
                    oldestBoughtDate = itemDate;
                }
                if(itemDate.after(newestBoughtDate)){
                    newestBoughtDate = itemDate;

                }
            }
            if(TimeUnit.DAYS.convert(newestBoughtDate.getTime() - oldestBoughtDate.getTime(), TimeUnit.MILLISECONDS) < 1){
                differentStores.remove(key);
            }

        }
        return differentStores.size() >= 1;
    }

    @RequiresApi(api = O)
    private void actionsDialog(String title, String originalName, String shoppingListName, Integer jsonEditCode) throws IOException, ParseException {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom);
        View view = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.custom_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText(title);

        ImageView icon = (ImageView) view.findViewById(R.id.imageIcon);
        TextView editText = view.findViewById(R.id.custom_dialog_edit_text);
        Button enterButton = (Button) view.findViewById(R.id.enterButton);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        if(jsonEditCode == JSONEditCodes.DELETE_SHOPPING_LIST_ITEM) {
            editText.setVisibility(View.INVISIBLE);
            icon.setMinimumHeight(50);
            icon.setMinimumWidth(50);
            enterButton.setText(R.string.yes);
            icon.setImageResource(R.drawable.delete_foreground); // making the pop up icon a trash can since by default it is the edit icon
            TextView delete_text = (TextView) view.findViewById(R.id.delete_text);
            delete_text.setVisibility(View.VISIBLE);
            delete_text.setText(String.format("Are you sure you want to delete %s, this action is permanent and cannot be undone", originalName));

            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            enterButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = O)
                @Override
                public void onClick(View view) {
                    try {
                        QueryUtils.deleteShoppingListUserItem(originalName, shoppingListName, getApplicationContext());
                        shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);
                        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    alertDialog.dismiss();
                }
            });
        }
        else if(jsonEditCode == JSONEditCodes.REORDER_SHOPPING_LIST_ITEM){
            editText.setVisibility(View.INVISIBLE);
            enterButton.setVisibility(GONE);
            ConstraintLayout constraintLayout = view.findViewById(R.id.layoutDialogContainer);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.cancel_button, ConstraintSet.START, R.id.layoutDialog, ConstraintSet.START, 65);
            constraintSet.connect(R.id.cancel_button, ConstraintSet.END, R.id.layoutDialog, ConstraintSet.END, 65);
            constraintLayout.setConstraintSet(constraintSet);
            icon.setMinimumHeight(50);
            icon.setMinimumWidth(50);
            icon.setImageResource(R.drawable.ic_baseline_arrow_circle_right_24);
            ListView shopping_list_reorder_lv = (ListView) view.findViewById(R.id.reorder_shopping_list_view);
            ArrayList<ShoppingList> shoppingListsForMoving = QueryUtils.getShoppingLists();
            shopping_list_reorder_lv.setVisibility(View.VISIBLE);
            ReorderShoppingListAdapter reorderShoppingListAdapter = new ReorderShoppingListAdapter(this, shoppingListsForMoving);
            shopping_list_reorder_lv.setAdapter(reorderShoppingListAdapter);
            shopping_list_reorder_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ShoppingList shoppingList = (ShoppingList) reorderShoppingListAdapter.getItem(i);
                    String shoppingListToMoveTo = shoppingList.getName();
                    try {
                        QueryUtils.reorderShoppingListItem(shoppingListName, shoppingListToMoveTo, originalName, getApplicationContext());
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                .putString("jsonData",Constants.json_data_str.toString()).apply();
                        shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);

                        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    alertDialog.dismiss();

                }
            });
            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });


        }
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @RequiresApi(api = O)
    private void moreVertActionsDialog(ShoppingListUserItem shoppingListUserItem) throws IOException, ParseException, java.text.ParseException {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom2);
        moreVertActionsView = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.more_vert_actions_custom_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(moreVertActionsView);
        String itemName = shoppingListUserItem.getName();
        ((TextView) moreVertActionsView.findViewById(R.id.textTitle))
                .setText(getString(R.string.actions_menu));
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);
        // extracting all the views and setting their text based on item we are running the actions for
        ImageView historyButton = (ImageView) moreVertActionsView.findViewById(R.id.history_button_image_view);
        ConstraintLayout view_history_cl = (ConstraintLayout) moreVertActionsView.findViewById(R.id.view_history_cl);
        TextView view_history_tv = (TextView) moreVertActionsView.findViewById(R.id.view_history_text_view);
        ImageView view_history_image = (ImageView) view_history_cl.getViewById(R.id.history_button_image_view);
        ConstraintLayout duplicate_indicator_cl = (ConstraintLayout) moreVertActionsView.findViewById(R.id.duplicate_indicator_cl);
        ConstraintLayout reorderCl = (ConstraintLayout) moreVertActionsView.findViewById(R.id.reorder_item_cl);
        TextView reorderItemTextView = (TextView) moreVertActionsView.findViewById(R.id.reorder_item_text_view);
        reorderItemTextView.setText(String.format(getString(R.string.reorder_item), itemName));
        ConstraintLayout deleteCl = (ConstraintLayout) moreVertActionsView.findViewById(R.id.delete_item_cl);
        TextView deleteItemTextView = (TextView) moreVertActionsView.findViewById(R.id.delete_item_text_view);
        deleteItemTextView.setText(String.format(getString(R.string.delete_item), itemName));
        viewInsightsCl = (ConstraintLayout) moreVertActionsView.findViewById(R.id.view_insights_of_item_cl);
        TextView viewInsightsTextView = (TextView) moreVertActionsView.findViewById(R.id.view_insights_of_item_text_view);

        ArrayList<ShoppingList> shoppingListsContainingSl = QueryUtils.ifShoppingListItemExistsInOtherShoppingLists(shoppingListUserItem.getName());

        if(shoppingListsContainingSl != null) {
            duplicate_indicator_cl.setVisibility(View.VISIBLE);
            TextView duplicateIndicatorTextView = duplicate_indicator_cl.findViewById(R.id.view_other_sl_with_text_view);
            duplicateIndicatorTextView.setText(String.format(getString(R.string.view_other_sl_with), itemName));
            duplicate_indicator_cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShoppingListUserItemsActivity.this, ShoppingListsHistoryActivity.class);
                    Bundle args = new Bundle();
                    args.putString("shoppingListUserItemName", itemName);
                    args.putString("shoppingListName", shoppingListName);
                    if(getIntent().getStringExtra("originalNavPathSLUTIShoppingList") != null){
                        System.out.println("ORIGINALNAVPATH NAME: " + getIntent().getStringExtra("originalNavPathSLUTIShoppingList") );
                        intent.putExtra("originalNavPathSLUTIShoppingList", getIntent().getStringExtra("originalNavPathSLUTIShoppingList"));
                    }else {
                        System.out.println("ORIGINALNAVPATH NAME REAL START: " + shoppingListName);
                        intent.putExtra("originalNavPathSLUTIShoppingList", shoppingListName);

                    }args.putSerializable("shoppingListsContainingSlItem", shoppingListsContainingSl);
                    intent.putExtra("BUNDLE", args);
                    alertDialog.dismiss();
                    startActivity(intent);
                    actuallyNeedsToBeUpdated.removeObserver(updateObserver);
                    finish();
                }
            });
        }
        else{
            duplicate_indicator_cl.setVisibility(GONE);
        }

        ArrayList<StoreUserItem> storeUserItemsHistory = QueryUtils.getHistoryOfShoppingListItem(itemName);
        if(storeUserItemsHistory != null&& storeUserItemsHistory.size() > 0){
            System.out.println(itemName + " HAS  HISTORY");
            view_history_tv.setTextColor(getResources().getColor(R.color.grey));
            ImageViewCompat.setImageTintList(view_history_image,
                    ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.grey)));
            view_history_cl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ShoppingListUserItemsActivity.this, ShoppingListUserItemHistoryActivity.class);
                    Bundle args = new Bundle();
                    args.putString("classComingFrom", "ShoppingListUserItemsActivity");
                    args.putString("title", itemName);
                    if(getIntent().getStringExtra("originalNavPathSLUTIShoppingList") != null){
                        System.out.println("ORIGINALNAVPATH NAME: " + getIntent().getStringExtra("originalNavPathSLUTIShoppingList") );
                        intent.putExtra("originalNavPathSLUTIShoppingList", getIntent().getStringExtra("originalNavPathSLUTIShoppingList"));
                    }else {
                        System.out.println("ORIGINALNAVPATH NAME REAL START: " + shoppingListName);
                        intent.putExtra("originalNavPathSLUTIShoppingList", shoppingListName);

                    }
                    args.putString("shoppingListName", shoppingListName);
                    args.putSerializable("storeUserItemsHistory", storeUserItemsHistory);
                    intent.putExtra("BUNDLE", args);
                    alertDialog.dismiss();
                    startActivity(intent);
                    actuallyNeedsToBeUpdated.removeObserver(updateObserver);
                    finish();
                }
            });

        }
        else{
            view_history_tv.setTextColor(getResources().getColor(R.color.grey_four));
            ImageViewCompat.setImageTintList(view_history_image,
                    ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.grey_four)));

        }

        if(eligibleForInsights(QueryUtils.getHistoryOfShoppingListItem(itemName)) && (Constants.wantsPriceComparisonUnit && !Constants.currentMeasureUnit.isEmpty())){
            viewInsightsCl.setVisibility(View.VISIBLE);
            viewInsightsTextView.setText(String.format(getString(R.string.view_insights_for_item), itemName));
            viewInsightsCl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        insightsDialog(itemName, storeUserItemsHistory);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        else{
            viewInsightsCl.setVisibility(GONE);
            viewInsightsTextView.setVisibility(GONE);
        }

        // functions that will always be given
        deleteCl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    showDialog(getString(R.string.delete_shopping_list_item), itemName, shoppingListName, JSONEditCodes.DELETE_SHOPPING_LIST_ITEM);
                    alertDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        reorderCl.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    showDialog(getString(R.string.reorder_shopping_list), itemName, shoppingListName, JSONEditCodes.REORDER_SHOPPING_LIST_ITEM);
                    alertDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });


        moreVertActionsView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        alertDialog.show();
    }

    //
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void insightsDialog(String itemName, ArrayList<StoreUserItem> storeUserItemHistory) throws java.text.ParseException {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom2);
        insightsView = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.insight_graph_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(insightsView);
        insightViewAlertDialog = builder.create();
        insightViewAlertDialog.setCanceledOnTouchOutside(true);
        // on below line we are initializing our graph insightsView.
        GraphView graphView = insightsView.findViewById(R.id.item_insights_graph);
        ListView storeKeyListView = insightsView.findViewById(R.id.stores_key_list_view);
        ArrayList<String> storeKeyStringArrayList = new ArrayList<>();
        Button exitButton = insightsView.findViewById(R.id.exit_button);
        TextView unitPriceTextView = insightsView.findViewById(R.id.unit_price_date_tap_tv);
        unitPriceTextView.setVisibility(GONE);


        // seperating the items history into different stores
        ConcurrentHashMap<String, ArrayList<StoreUserItem>> differentStoreHistories = new ConcurrentHashMap<>();

        for (StoreUserItem storeUserItem : storeUserItemHistory) {
            if (!differentStoreHistories.containsKey(storeUserItem.getStore())) {
                ArrayList<StoreUserItem> emptyList = new ArrayList<>();
                differentStoreHistories.put(storeUserItem.getStore(), emptyList);
            }
            System.out.println("ADDING: " + storeUserItem.getItemName() + " TO --> " + storeUserItem.getStore());
            differentStoreHistories.get(storeUserItem.getStore()).add(storeUserItem);
        }
        System.out.println("DIFFERENT STORE HISTORIES: " + differentStoreHistories.size());

        ConcurrentHashMap<String, ArrayList<StoreUserItem>> differentStoreHistoriesCopy2 =differentStoreHistories;
        Set<String> keySet = differentStoreHistories.keySet();
        System.out.println("keyset: " + keySet);
        for(String key: keySet){
            ArrayList<StoreUserItem> history = differentStoreHistoriesCopy2.get(key);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            StoreUserItem oldestBoughtItem = history.get(0);
            StoreUserItem newestBoughtItem = history.get(0);
            Date oldestBoughtDate = sdf.parse(oldestBoughtItem.getDateOfPurchase());
            Date newestBoughtDate = sdf.parse(newestBoughtItem.getDateOfPurchase());
            for(StoreUserItem item: history){
                Date itemDate = sdf.parse(item.getDateOfPurchase());
                if(itemDate.before(oldestBoughtDate)){
                    oldestBoughtDate = itemDate;
                }
                if(itemDate.after(newestBoughtDate)){
                    newestBoughtDate = itemDate;

                }
            }
            if(TimeUnit.DAYS.convert(newestBoughtDate.getTime() - oldestBoughtDate.getTime(), TimeUnit.MILLISECONDS) < 1){
                differentStoreHistoriesCopy2.remove(key);
            }

        }
        System.out.println("MADE IT: "  + differentStoreHistories.size());
        ConcurrentHashMap<String, ArrayList<StoreUserItem>> differentStoreHistoriesCopy =differentStoreHistoriesCopy2;
        for (String key: differentStoreHistories.keySet()) {
            System.out.println("KEY: " + key);
            ArrayList<StoreUserItem> history = differentStoreHistoriesCopy.get(key);
            ArrayList<StoreUserItem> newHistory = new ArrayList<>(); // history where if there are multiple purchases under one date they all get averaged and create a one-time history for that day
            HashMap<String, ArrayList<StoreUserItem>> datesOfPurchase = new HashMap<>();
            for (StoreUserItem storeUserItem : history) {
                System.out.println("history item: " + storeUserItem.getItemName());
                String dateOfPurchase = storeUserItem.getDateOfPurchase();
                if (!datesOfPurchase.keySet().contains(dateOfPurchase)) {
                    ArrayList<StoreUserItem> emptyList = new ArrayList<>();
                    datesOfPurchase.put(storeUserItem.getDateOfPurchase(), emptyList);
                }
                datesOfPurchase.get(dateOfPurchase).add(storeUserItem);
            }
            for (String dateOfPurchase : datesOfPurchase.keySet()) {
                ArrayList<StoreUserItem> dateHistory = datesOfPurchase.get(dateOfPurchase);
                Double total = 0.0;
                for (StoreUserItem storeUserItem : dateHistory) {
                    String additionalWeightUnitPriceDetail = storeUserItem.getAdditionalWeightUnitPriceDetail();
                    if(additionalWeightUnitPriceDetail != null) {
                        System.out.println("ADDITIONAL WEIGHT UNIT DETAIL FOR " + storeUserItem.getItemName() + " ON DAY OF :  " + dateOfPurchase + " : " + additionalWeightUnitPriceDetail);
                        Double ogActualPrice = Double.parseDouble(additionalWeightUnitPriceDetail.replaceAll("[^\\d.]",""));
                        String currentMeasureUnit = Constants.currentMeasureUnit;
                        String ogMeasurementUnit = additionalWeightUnitPriceDetail.substring(additionalWeightUnitPriceDetail.indexOf("/")+1);
                        Double actualPrice = ogActualPrice / (ItemMeasurementUnits.findRatioBetweenOgMeasurementUnitAndConversionOutcomeUnit(ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(ogMeasurementUnit), ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(currentMeasureUnit)));
                        System.out.println("ACTUAL PRICE: " + (ItemMeasurementUnits.findRatioBetweenOgMeasurementUnitAndConversionOutcomeUnit(ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(ogMeasurementUnit), ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(currentMeasureUnit))));
                        DecimalFormat f = new DecimalFormat("##.00");
                        actualPrice = Double.parseDouble(f.format(actualPrice));
                        Integer sigFigs = 2;
                        if (ogActualPrice != 0) {
                            while(actualPrice == 0.0){
                                sigFigs++;
                                String formatExpression = "##.";
                                for(int i = 0; i < sigFigs; i++) {
                                    formatExpression += "0";
                                }
                                DecimalFormat newFormatter = new DecimalFormat(formatExpression);
                                actualPrice = Double.parseDouble(newFormatter.format(ogActualPrice / (ItemMeasurementUnits.findRatioBetweenOgMeasurementUnitAndConversionOutcomeUnit(ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(ogMeasurementUnit), ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(currentMeasureUnit)))));

                            }

                        }
                        System.out.println("FINAL ACTUAL PRICE: " + actualPrice + "FOR ITEM: " + itemName + " ADDITIONAL WEIGHT DETAIL: " + additionalWeightUnitPriceDetail);
                        total += actualPrice;
                    }
                }

                DecimalFormat f = new DecimalFormat("##.00");
                Double finalAverage = Double.parseDouble(f.format(total / dateHistory.size()));
                System.out.print("ADDING DATE OF PURCHASE: " + dateOfPurchase + " FINAL AVG: " + finalAverage + " KEY : "  + key);
                newHistory.add(new StoreUserItem(dateOfPurchase, finalAverage.toString()));
            }
            differentStoreHistories.replace(key, newHistory);
        }

        ArrayList<Date> dates = new ArrayList<>();
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graphView);
        int totalValues = 0;
        for(String key: differentStoreHistories.keySet()){
            ArrayList<StoreUserItem> history = differentStoreHistories.get(key);
            totalValues += history.size();

        }
        ArrayList<Double> datePointXValues = new ArrayList<>();
        String[] horizontalLabels = new String[totalValues];
        int horizontalLabelsIterator = 0;
        Double biggestUnitPrice = 0.0;
        System.out.print("DIFFERENT STORE HISTORIES: " + differentStoreHistories.toString());
        for (String key : differentStoreHistories.keySet()) {
            ArrayList<StoreUserItem> history = differentStoreHistories.get(key);
            DataPoint[] dataPoints = new DataPoint[history.size()];
            int i = 0;
            PointsGraphSeries<DataPoint> pointPointsGraphSeries = new PointsGraphSeries<>(new DataPoint[]{});
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{});
            System.out.print("HISTORY "  + history.size() + " FOR " + key +" : " + Arrays.toString(history.toArray()));
            for (StoreUserItem storeUserItem : history) {
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                Date date = formatter.parse(storeUserItem.getDateOfPurchase());
                String storeUserItemPrice = storeUserItem.getUnitPrice();
                System.out.println("ADDING DATE: " + date + " FOR : " + storeUserItem.getUnitPrice());
                double doubleUnitPrice = Double.parseDouble(storeUserItemPrice.replaceAll("\\D^.", ""));
                System.out.println("PARSED DOUBLE: "+ doubleUnitPrice);
                if (doubleUnitPrice > biggestUnitPrice) {
                    biggestUnitPrice = doubleUnitPrice;
                }
                if(!dates.contains(date)){
                    dates.add(date);
                }
                DataPoint datePoint = new DataPoint(date, doubleUnitPrice);
                DataPoint[] dataPointCloneWithoutNullVals = new DataPoint[i+1];
                dataPoints[i] = (datePoint);
                datePointXValues.add(datePoint.getX());
                for(int i2 = 0; i2 < dataPoints.length; i2++){
                    if(dataPoints[i2] != null){
                        dataPointCloneWithoutNullVals[i2] = dataPoints[i2];
                    }
                }

                for(int i3 = 0; i3 < dataPointCloneWithoutNullVals.length-1; i3++){
                    DataPoint dataPoint1 = (DataPoint) dataPointCloneWithoutNullVals[i3];
                    double date1 = dataPoint1.getX();
                    DataPoint dataPoint2 = (DataPoint) dataPointCloneWithoutNullVals[i3+1];
                    double date2 = dataPoint2.getX();
                    if(date1 > date2){
                        dataPointCloneWithoutNullVals[i3+1] = dataPoint1;
                        dataPointCloneWithoutNullVals[i3] = dataPoint2;
                    }
                }
                SortedMap<Double, Double> sortedDataPointConeWithoutNullVals = new TreeMap<>();

                for(int i3 = 0; i3 < dataPointCloneWithoutNullVals.length-1; i3++){
                    DataPoint dataPoint1 = (DataPoint) dataPointCloneWithoutNullVals[i3];
                    sortedDataPointConeWithoutNullVals.put(dataPoint1.getX(), dataPoint1.getY());
                }
                int i5 = 0;
                for(Double sortedMapKey: sortedDataPointConeWithoutNullVals.keySet()){
                    Double valueForKey = sortedDataPointConeWithoutNullVals.get(sortedMapKey);
                    dataPointCloneWithoutNullVals[i5] = new DataPoint(sortedMapKey, valueForKey);
                    i5++;

                }
//                horizontalLabels[horizontalLabelsIterator] = storeUserItem.getDateOfPurchase();
                System.out.println("DATE POINT DATE: " + datePoint.getX());
                if(dataPoints.length > 1) {
                    pointPointsGraphSeries.resetData(dataPointCloneWithoutNullVals);
                }
                if(dataPoints.length > 1) {
                    series.resetData(dataPointCloneWithoutNullVals);
                }
                i++;
//                horizontalLabelsIterator++;
            }
            series.setDrawDataPoints(true);
            pointPointsGraphSeries.setShape(PointsGraphSeries.Shape.POINT);
            pointPointsGraphSeries.setSize(7);
            series.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    System.out.println("TAPPED");
                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
                    unitPriceTextView.setText( String.format(getString(R.string.data_point_tapped), itemName, key, format.format(new Date((long) dataPoint.getX())), dataPoint.getY()));
                    unitPriceTextView.setVisibility(View.VISIBLE);
                }
            });
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            series.setColor(color);
            storeKeyStringArrayList.add(key + ";" + color);
//            graphView.addSeries(pointPointsGraphSeries);
            graphView.addSeries(series);
        }
        storeKeyListView.setAdapter(new StoreKeyItemListAdapter(getApplicationContext(), storeKeyStringArrayList));
        double latestDate = datePointXValues.get(0);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        for(double doubleVal: datePointXValues){
            System.out.println("DATE POINT X VALUES : " + format.format(new Date((long) doubleVal)));
            if(doubleVal > latestDate){
                latestDate = doubleVal;
            }
        }
        long latestDateFormated = Date.parse(format.format(new Date((long) latestDate)));

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insightViewAlertDialog.dismiss();
            }
        });
        graphView.getViewport().setMinY(0.0);
        graphView.getViewport().setMaxY(biggestUnitPrice);
        System.out.println("DATES SIZE: " + dates.size());
        graphView.getGridLabelRenderer().setNumHorizontalLabels(dates.size());
        graphView.getViewport().setScrollableY(true);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);
//        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        TextView graphLabel = (TextView) insightsView.findViewById(R.id.graph_label);
        graphLabel.setText("Item price per " + Constants.currentMeasureUnit);
//        graphView.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        System.out.println("DATE X VALUES: " + datePointXValues.get(0));
//        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
//            @Override
//            public String formatLabel(double value, boolean isValueX) {
//                if(isValueX){
//                    SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
//                    System.out.println("DATE X VALUE: " + String.valueOf(value));
//                    System.out.println("Date FORMATTED: "+format.format(new Date((long) value)));
//                    System.out.println("RETURNING FORMATTED: "+format.format(new Date((long) value)));
//                    return format.format(new Date((long) value));
//
//                } else{
//                   return super.formatLabel(value, false) ;
//                }
//            }
//
//        });
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(datePointXValues.size());
        graphView.getViewport().setMaxX(latestDateFormated);
        graphView .getViewport().setXAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setHumanRounding(false, false);
        graphView.setTitleColor(getResources().getColor(R.color.blue));

        // after adding data to our line graph series.
        // on below line we are setting
        // title for our graph insightsView.
        graphView.setTitleTextSize(18);
        graphView.setTitle(String.format(getString(R.string.insights_for_itemI), itemName));

        // on below line we are setting
        // text color to our graph insightsView.
        graphView.setTitleColor(R.color.purple_200);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(40);


        if (insightViewAlertDialog.getWindow() != null) {
            insightViewAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        insightViewAlertDialog.show();
    }

    @RequiresApi(api = O)
    private void showDialog(String title, String originalName, String shoppingListName, Integer jsonEditCode) throws IOException, ParseException {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom);
        View view = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.custom_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText(title);
        ImageView icon = (ImageView) view.findViewById(R.id.imageIcon);
        TextView editText = view.findViewById(R.id.custom_dialog_edit_text);
        Button enterButton = (Button) view.findViewById(R.id.enterButton);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        if(jsonEditCode == JSONEditCodes.DELETE_SHOPPING_LIST_ITEM) {
            editText.setVisibility(View.INVISIBLE);
            icon.setMinimumHeight(50);
            icon.setMinimumWidth(50);
            enterButton.setText(R.string.yes);
            icon.setImageResource(R.drawable.delete_foreground); // making the pop up icon a trash can since by default it is the edit icon
            TextView delete_text = (TextView) view.findViewById(R.id.delete_text);
            delete_text.setVisibility(View.VISIBLE);
            delete_text.setText(String.format("Are you sure you want to delete %s, this action is permanent and cannot be undone", originalName));

            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            enterButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = O)
                @Override
                public void onClick(View view) {
                    try {System.out.println("CONSTANTS JSON BEFORE DELETE: " + PreferenceManager.
                            getDefaultSharedPreferences(getApplicationContext()).getString("jsonData", ""));
                        QueryUtils.deleteShoppingListUserItem(originalName, shoppingListName, getApplicationContext());
                        System.out.println("CONSTANTS JSON AFTER DELETE: " + PreferenceManager.
                                getDefaultSharedPreferences(getApplicationContext()).getString("jsonData", ""));
                        for(ShoppingListUserItem shoppingListUserItem: shoppingListUserItems){
                            System.out.println("SLUI NAME BEFORE: " + shoppingListUserItem.getName());
                        }
                        shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);
                        System.out.println("CONSTANTS JSON AFTER UPDATE: " + PreferenceManager.
                                getDefaultSharedPreferences(getApplicationContext()).getString("jsonData", ""));
                        for(ShoppingListUserItem shoppingListUserItem: shoppingListUserItems){
                            System.out.println("SLUI NAME: " + shoppingListUserItem.getName());
                        }
                        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    alertDialog.dismiss();
                }
            });
        }
        else if(jsonEditCode == JSONEditCodes.REORDER_SHOPPING_LIST_ITEM){
            editText.setVisibility(View.INVISIBLE);
            enterButton.setVisibility(GONE);
            ConstraintLayout constraintLayout = view.findViewById(R.id.layoutDialogContainer);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.cancel_button, ConstraintSet.START, R.id.layoutDialog, ConstraintSet.START, 65);
            constraintSet.connect(R.id.cancel_button, ConstraintSet.END, R.id.layoutDialog, ConstraintSet.END, 65);
            constraintLayout.setConstraintSet(constraintSet);
            icon.setMinimumHeight(50);
            icon.setMinimumWidth(50);
            icon.setImageResource(R.drawable.ic_baseline_arrow_circle_right_24);
            ListView shopping_list_reorder_lv = (ListView) view.findViewById(R.id.reorder_shopping_list_view);
            ArrayList<ShoppingList> shoppingListsForMoving = QueryUtils.getShoppingLists();
            shopping_list_reorder_lv.setVisibility(View.VISIBLE);
            ReorderShoppingListAdapter reorderShoppingListAdapter = new ReorderShoppingListAdapter(this, shoppingListsForMoving);
            shopping_list_reorder_lv.setAdapter(reorderShoppingListAdapter);
            shopping_list_reorder_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ShoppingList shoppingList = (ShoppingList) reorderShoppingListAdapter.getItem(i);
                    String shoppingListToMoveTo = shoppingList.getName();
                    try {
                        QueryUtils.reorderShoppingListItem(shoppingListName, shoppingListToMoveTo, originalName, getApplicationContext());
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                .putString("jsonData",Constants.json_data_str.toString()).apply();
                        shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);

                        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    alertDialog.dismiss();

                }
            });
            view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });


        }
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("@onactivityresult: " + requestCode);
    }

    // method to replace all common issues in voice to speech text in different areas
    private String replaceAllCommonIssues(String s) {
        return removeLeadingZeros(s).replaceAll("\\s+", " ").replaceAll("fluidounces", "fl oz").replaceAll("fluid ounces", "fl oz").replaceAll("G", "g").replaceAll(" per ", " /").replaceAll("per", " /").replaceAll(" a ", " /").
                replaceAll("pound", "lb").replaceAll("pounds", "lb")
                .replaceAll("gram", "g").replaceAll("grams", "g")
                .replaceAll("kilogram", "kg").replaceAll("kilograms", "kg")
                .replaceAll("ounces", "oz").replaceAll("ounce", "oz")
                .replaceAll("pints", "pt").replaceAll("pint", "pt")
                .replaceAll("gallons", "gl").replaceAll("gallon", "gl")
                .replaceAll("milliliters", "ml").replaceAll("milliliter", "ml")
                .replaceAll("liters", "l").replaceAll("liter", "l")
                .replaceAll(" /", "/")
                .replaceAll("/", " /")
                .replaceAll("profound", "per lb")
                ;

    }

    // method to remove leading zeros in a string of digits
    private String removeLeadingZeros(String str){
        String newstr = "";
        int ind = 0;
        for (int i = 0; i < str.length(); i++) {
            char p = str.charAt(i);
            if (p != '0') {
                ind = i;
                break;
            }
        }
        newstr = str.substring(ind, str.length());
        return str;
    }
    @RequiresApi(O)
    private void speakWithVoiceDialog(String shoppingListUserItemName, boolean ifStoreProvided) {
        System.out.println("ACESSED");
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom);
        View view = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.voice_input_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainerVID)
        );
        final boolean[] ifQuantityIsIndividualPackageBased = {true};

        // following layouts are for package based purchase details
        ConstraintLayout additional_weight_cl = view.findViewById(R.id.additional_weight_cl);
        ConstraintLayout within_package_item_count_cl = view.findViewById(R.id.within_package_item_count_ly);
        // following layouts are for weight based purchase details and package based details
        ConstraintLayout quanity_detail_cl = view.findViewById(R.id.quantity_detail_ly);
        ConstraintLayout unit_price_detail_cl = view.findViewById(R.id.unit_price_detail_ly);
        TextView choseStoreTextView = view.findViewById(R.id.chose_a_store_text_view);
        ListView choseStoreListView = view.findViewById(R.id.chose_stores_list_view);
        TextView recordDetailsTextView = (TextView) view.findViewById(R.id.textTitle);
        recordDetailsTextView.setText(String.format(getString(R.string.record_details_for_item), shoppingListUserItemName));
        ImageView calendar =  view.findViewById(R.id.calendar_view);
        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarDialog();
            }
        });
        Display display = getWindowManager().getDefaultDisplay();
        ToggleButton toggleButton = view.findViewById(R.id.eachWeightToggleButton);
        int width = display.getWidth();
        view.setMinimumWidth(width/2);
        choseStoreTextView.setVisibility(GONE);
        choseStoreListView.setVisibility(GONE); // by default the listview that will be populated should be gone
        builder.setView(view);
        Button cancel_button = view.findViewById(R.id.cancel_button);
        Button enter_button = view.findViewById(R.id.enterButton);
        EditText withinPackageItemCountEditText = view.findViewById(R.id.within_package_item_count_edit_text);
        EditText quantityEditText = view.findViewById(R.id.quantity_edit_text);
        EditText unitPriceEditText = view.findViewById(R.id.unit_price_edit_text);
        // setting default values of quantity and within package item count ad 1
        quantityEditText.setText(getString(R.string.default_total_packages_and_items_within_package_purchased));
        withinPackageItemCountEditText.setText(getString(R.string.default_total_packages_and_items_within_package_purchased));
        EditText additionalWeightEditText = view.findViewById(R.id.additional_weight_edit_text);
        ImageView quantityMicrophone = view.findViewById(R.id.quantity_microphone);
        ImageView unitPriceMicrophone = view.findViewById(R.id.unit_price_microphone);
        ImageView additionalWeightMicrophone = view.findViewById(R.id.additional_weight_microphone);
        ImageView withinPackageItemCountMicrophone = view.findViewById(R.id.within_package_item_count_microphone);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            TextView quantity_label = (TextView) quanity_detail_cl.findViewById(R.id.quantity_edit_text_label);
            TextView unit_price_label = (TextView) unit_price_detail_cl.findViewById(R.id.unit_price_edit_text_label);
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    ifQuantityIsIndividualPackageBased[0] = true;
                    // The toggle is enabled
                    additional_weight_cl.setVisibility(View.VISIBLE);
                    within_package_item_count_cl.setVisibility(View.VISIBLE);quantity_label.setText(getString(R.string.total_packages_purchased));
                    unit_price_label.setText(getString(R.string.package_price));
                } else {
                    // The toggle is disabled
                    ifQuantityIsIndividualPackageBased[0] = false;
                    additional_weight_cl.setVisibility(GONE);
                    within_package_item_count_cl.setVisibility(GONE);
                    quantity_label.setText(getString(R.string.quantity_unit));
                    unit_price_label.setText(getString(R.string.unit_price_by_weight));
                }
            }

        });
        SpeechRecognizer additionalWeightSr = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent additionalWeightSrIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        additionalWeightSrIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        additionalWeightSrIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS , 800);
        additionalWeightSrIntent .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        RecognitionListener additionalWeightRL = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Toast.makeText(ShoppingListUserItemsActivity.this, "Listening...", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                System.out.println("@onEndOfSpeech - additionalWeight");
                additionalWeightMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - additionalWeight: " + result + "english version: " + EnglishWordsToNumbers.replaceNumbers(result));
                    if (result.matches(".*[a-zA-Z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = "";
                        if(!result.contains("fl oz")) {
                            justAlpha = getFirstNotEmpty(Arrays.asList(removeNumberRelated(result).split(" "))); // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                            System.out.println("JUST ALPHA: " + justAlpha);
                        }
                        else{
                            justAlpha = "fl oz";
                        }
                        if (!isMeasurementUnit(justAlpha)){
                            errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                            justAlpha = "";
                        }
                        result = result.replaceAll("[a-zA-Z]", "");
                        try {
                            Double.parseDouble(result);
                            additionalWeightEditText.setText(result + " " + justAlpha);
                        }
                        catch(Exception e){
                            e.printStackTrace();
                            errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                        }
                    } else {
                        additionalWeightEditText.setText(result);
                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION: " );
                    e.printStackTrace();
                    additionalWeightMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            additionalWeightSr.stopListening();
                            System.out.println("MADE IT");
                        }
                    };
                    stopListeningRunnable.run();
                    additionalWeightMicrophoneState = 0;
                }
            }
            @Override
            public void onPartialResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - additionalWeight: " + result + "english version: " + EnglishWordsToNumbers.replaceNumbers(result));
                    if (result.matches(".*[a-zA-Z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = "";
                        if(!result.contains("fl oz")) {
                            justAlpha = getFirstNotEmpty(Arrays.asList(removeNumberRelated(result).split(" "))); // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                            System.out.println("JUST ALPHA: " + justAlpha);
                        }
                        else{
                            justAlpha = "fl oz";
                        }
                        if (!isMeasurementUnit(justAlpha)){
//                            errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                            justAlpha = "";
                        }
                        result = result.replaceAll("[a-zA-Z]", "");
                        try {
                            Double.parseDouble(result);
                            additionalWeightEditText.setText(result + " " + justAlpha);
                        }
                        catch(Exception e){
                            e.printStackTrace();
//                            errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                        }
                    } else {
                        additionalWeightEditText.setText(result);
                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION: " );
                    e.printStackTrace();
                    additionalWeightMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            additionalWeightSr.stopListening();
                            System.out.println("MADE IT");
                        }
                    };
                    stopListeningRunnable.run();
                    additionalWeightMicrophoneState = 0;
                }
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - additionalWeight: " + result + "english version: " + EnglishWordsToNumbers.replaceNumbers(result));
                    if (result.matches(".*[a-zA-Z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = "";
                        if(!result.contains("fl oz")) {
                            justAlpha = getFirstNotEmpty(Arrays.asList(removeNumberRelated(result).split(" "))); // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                            System.out.println("JUST ALPHA: " + justAlpha);
                        }
                        else{
                            justAlpha = "fl oz";
                        }
                        if (!isMeasurementUnit(justAlpha)){
//                            errorDialog(getString(R.string.by_packge_couldnt_rec_value_additional_weight));
                            justAlpha = "";
                        }
                        result = result.replaceAll("[a-zA-Z]", "");
                        try {
                            Double.parseDouble(result);
                            additionalWeightEditText.setText(result + " " + justAlpha);
                        }
                        catch(Exception e){
                            e.printStackTrace();
//                            errorDialog(getString(R.string.by_packge_couldnt_rec_value_additional_weight));
                        }
                    } else {
                        additionalWeightEditText.setText(result);
                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION: " );
                    e.printStackTrace();
                    additionalWeightMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            additionalWeightSr.stopListening();
                            System.out.println("MADE IT");
                        }
                    };
                    stopListeningRunnable.run();
                    additionalWeightMicrophoneState = 0;
                }
            }
        };
        additionalWeightSr.setRecognitionListener(additionalWeightRL);
        additionalWeightMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(additionalWeightMicrophoneState == 0){
                    if(quantityMicrophoneState == 1){ // if microphone for recording quantity details is on, then whe need to turn it off both in speech recognition service and color/state indication
                        quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                        final Runnable stopListeningRunnable = new Runnable() {
                            @Override
                            public void run() {
                                additionalWeightSr.stopListening();
                            }
                        };
                        stopListeningRunnable.run();
                        quantityMicrophoneState = 0;
                    }
                    if(ContextCompat.checkSelfPermission(ShoppingListUserItemsActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                        System.out.println("ACCESS DENIED");
                        ActivityCompat.requestPermissions(ShoppingListUserItemsActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

                    } else {
                        additionalWeightMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                        final Runnable startListeningRunnable = new Runnable() {
                            @Override
                            public void run() {
                                additionalWeightSr.startListening(additionalWeightSrIntent);
                            }
                        };
                        startListeningRunnable.run();
                        additionalWeightMicrophoneState = 1;
                    }
                }
                else if(additionalWeightMicrophoneState == 1){
                    additionalWeightMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            additionalWeightSr.stopListening();
                        }
                    };
                    stopListeningRunnable.run();
                    additionalWeightMicrophoneState = 0;

                }

            }
        });
        SpeechRecognizer quantitySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        SpeechRecognizer unitPriceSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        SpeechRecognizer withinPackageItemCountRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent quantitySpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        quantitySpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        quantitySpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS , 800);
        quantitySpeechRecognizerIntent .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        final Intent unitPriceRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        unitPriceRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS , 800);
        // when the users click on the textviews next to the edit texts for hold details, listening starts
        final Intent withinPackageItemCountRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        unitPriceRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS , 800);

        RecognitionListener quantityRecognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Toast.makeText(ShoppingListUserItemsActivity.this, "Listening...", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                System.out.println("@onEndOfSpeech - quantity");
                quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - quantity 34455: " + result);
                    if (result.matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = "";
                        if(!result.contains("fl oz")) {
                             justAlpha = removeNumberRelated(result).split(" ")[0]; // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                        }
                        else{
                            justAlpha = "fl oz";
                        }
                        if(!ifQuantityIsIndividualPackageBased[0]) {
                            try {
                                if (result.matches(".*[a-z].*")) {
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    if (!justAlpha.replaceAll(" ", "").isEmpty()){
                                        errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                        justAlpha = "";
                                    }
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
                                            errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                        }
                                    }
                                    quantityEditText.setText(result.replaceAll(justAlpha, ""));
                                } else {
                                    quantityEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                final Runnable stopListeningRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        quantitySpeechRecognizer.stopListening();
                                        System.out.println("MADE IT");
                                    }
                                };
                                stopListeningRunnable.run();
                                quantityMicrophoneState = 0;
                            }
                        }
                        else{
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in like 'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                            }
                            quantityEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
                            errorDialog(getString(R.string.by_weight_only_greater_than_0));
                        } else {
                            quantityEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    e.printStackTrace();
                    quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            quantitySpeechRecognizer.stopListening();
                        }
                    };
                    stopListeningRunnable.run();
                    quantityMicrophoneState = 0;
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                System.out.println("@onPartialResults - quantity");
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - quantity 34455: " + result);
                    if (result.matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = "";
                        if(!result.contains("fl oz")) {
                            justAlpha = removeNumberRelated(result).split(" ")[0]; // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                        }
                        else{
                            justAlpha = "fl oz";
                        }
                        if(!ifQuantityIsIndividualPackageBased[0]) {
                            try {
                                if (result.matches(".*[a-z].*")) {
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    if (!justAlpha.replaceAll(" ", "").isEmpty()){
//                                        errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                        justAlpha = "";
                                    }
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
//                                            errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                        }
                                    }
                                    quantityEditText.setText(result.replaceAll(justAlpha, ""));
                                } else {
                                    quantityEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                final Runnable stopListeningRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        quantitySpeechRecognizer.stopListening();
                                        System.out.println("MADE IT");
                                    }
                                };
                                stopListeningRunnable.run();
                                quantityMicrophoneState = 0;
                            }
                        }
                        else{
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in like 'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
//                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                            }
                            quantityEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
//                            errorDialog(getString(R.string.by_weight_only_greater_than_0));
                        } else {
                            quantityEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            quantitySpeechRecognizer.stopListening();
                        }
                    };
                    stopListeningRunnable.run();
                    quantityMicrophoneState = 0;
                }

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - quantity 34455: " + result);
                    if (result.matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = "";
                        if(!result.contains("fl oz")) {
                            justAlpha = removeNumberRelated(result).split(" ")[0]; // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                        }
                        else{
                            justAlpha = "fl oz";
                        }
                        if(!ifQuantityIsIndividualPackageBased[0]) {
                            try {
                                if (result.matches(".*[a-z].*")) {
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    if (!justAlpha.replaceAll(" ", "").isEmpty()){
                                        errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                        justAlpha = "";
                                    }
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        }  // the only exception it will be catching is a double parsing exception
                                        //                                            errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));

                                    }
                                    quantityEditText.setText(result.replaceAll(justAlpha, ""));
                                } else {
                                    quantityEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                final Runnable stopListeningRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        quantitySpeechRecognizer.stopListening();
                                        System.out.println("MADE IT");
                                    }
                                };
                                stopListeningRunnable.run();
                                quantityMicrophoneState = 0;
                            }
                        }
                        else{
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in like 'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                            }
                            quantityEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
//                            errorDialog(getString(R.string.by_weight_only_greater_than_0));
                        } else {
                            quantityEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){

                    e.printStackTrace();
                    quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            quantitySpeechRecognizer.stopListening();
                        }
                    };
                    stopListeningRunnable.run();
                    quantityMicrophoneState = 0;
                }
            }
        };
        quantitySpeechRecognizer.setRecognitionListener(quantityRecognitionListener);
        quantityMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantityMicrophoneState == 0){
                    if(ContextCompat.checkSelfPermission(ShoppingListUserItemsActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                        System.out.println("ACCESS DENIED");
                        ActivityCompat.requestPermissions(ShoppingListUserItemsActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

                    } else {
                        quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                        final Runnable startListeningRunnable = new Runnable() {
                            @Override
                            public void run() {
                                quantitySpeechRecognizer.startListening(quantitySpeechRecognizerIntent);
                            }
                        };
                        startListeningRunnable.run();
                        quantityMicrophoneState = 1;
                    }
                }
                else if(quantityMicrophoneState == 1){
                    quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    final Handler handler = new Handler();
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            quantitySpeechRecognizer.stopListening();
                            System.out.println("MADE IT");
                        }
                    };
                    stopListeningRunnable.run();
                    quantityMicrophoneState = 0;

                }
            }
        });

        RecognitionListener unitPriceRecognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Toast.makeText(ShoppingListUserItemsActivity.this, "Listening...", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(unitPriceSpeechRecognizer.RESULTS_RECOGNITION);
                System.out.println("@onResults - unitPrice: " + data.get(0));
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    System.out.println("RESULT: " + result + " FOR DATA: " + data.get(0));
                    String ogResult = result;
                    if(ifQuantityIsIndividualPackageBased[0]) {
                        if (result.matches(".*[a-z].*")) {
                            String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                            System.out.println("REPLACE NUMBERS : " + replaceNumbers);
                            if (!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                result = replaceNumbers;
                                unitPriceEditText.setText(result);
                            } else {
                                errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                                unitPriceEditText.setText(result.replaceAll(".*[a-z].*", ""));

                            }
                        } else {
                            try {
                                if (Double.parseDouble(result) <= 0) {
                                    errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                                } else {
                                    unitPriceEditText.setText(result);
                                }
                            } catch(Exception e){
                                errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));

                            }

                        }
                    }
                    else{
                        if (result.matches(".*[a-z].*")) {
                            String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result);
                            String justAlpha = removeNumberRelated(result).split(" ")[0].replaceAll("/", "");
                            System.out.println("REPLACE NUMBERS : " + replaceNumbers + " JUST ALPH: " + justAlpha);
                            String resultWithoutAlphaCharsOrSlash = result.replaceAll(justAlpha, "").replaceAll("/", "");
                            if(!isMeasurementUnit(justAlpha)){
                                System.out.println("TRIGGERED 1");
                                errorDialog(getString(R.string.unit_price_by_weight_guidelines));
                                unitPriceEditText.setText(resultWithoutAlphaCharsOrSlash);
                            } else{
                                unitPriceEditText.setText(resultWithoutAlphaCharsOrSlash + " /" + justAlpha);
                            }
                            System.out.println("DOUBLE PARSED: " + Double.parseDouble(replaceNumbers));
                            if (!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                result = replaceNumbers;
                                unitPriceEditText.setText(result);
                            }
                            try {
                                if (Double.parseDouble(resultWithoutAlphaCharsOrSlash) <= 0) {
                                    System.out.println("TRIGGERED 2");
                                    errorDialog(getString(R.string.unit_price_by_weight_guidelines));
                                }
                            } catch(Exception e){

                            }
                        } else {
                           errorDialog(getString(R.string.by_weight_items_unit_price_needs_to_have_measurement_unit));
                           try {
                               if (Double.parseDouble(result) <= 0) {
                                   errorDialog(getString(R.string.unit_price_val_have_to_be_greater_than_0));
                               }
                           } catch(Exception e){

                           }

                        }

                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION");
                    e.printStackTrace();
                    unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    errorDialog(getString(R.string.couldnt_rec_voice_input));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            unitPriceSpeechRecognizer.stopListening();
                            System.out.println("MADE IT - stop UPSR");
                        }
                    };
                    stopListeningRunnable.run();
                    unitPriceMicrophoneState = 0;
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(unitPriceSpeechRecognizer.RESULTS_RECOGNITION);
                System.out.println("@onPartialResults - unitPrice: " + data.get(0));
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    System.out.println("RESULT: " + result + " FOR DATA: " + data.get(0));
                    String ogResult = result;
                    if(ifQuantityIsIndividualPackageBased[0]) {
                        if (result.matches(".*[a-z].*")) {
                            String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                            System.out.println("REPLACE NUMBERS : " + replaceNumbers);
                            if (!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                result = replaceNumbers;
                                unitPriceEditText.setText(result);
                            } else {
//                                errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                                unitPriceEditText.setText(result.replaceAll(".*[a-z].*", ""));

                            }
                        } else {
                            if (!(Double.parseDouble(result) <= 0)) {
                                unitPriceEditText.setText(result);
                            }

                        }
                    }
                    else{
                        if (result.matches(".*[a-z].*")) {
                            String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result);
                            String justAlpha = removeNumberRelated(result).split(" ")[0].replaceAll("/", "");
                            System.out.println("REPLACE NUMBERS : " + replaceNumbers + " JUST ALPH: " + justAlpha);
                            String resultWithoutAlphaCharsOrSlash = result.replaceAll(justAlpha, "").replaceAll("/", "");
                            if(!isMeasurementUnit(justAlpha)){
//                                errorDialog(getString(R.string.unit_price_by_weight_guidelines));
                                unitPriceEditText.setText(resultWithoutAlphaCharsOrSlash);
                            } else{
                                unitPriceEditText.setText(resultWithoutAlphaCharsOrSlash + " /" + justAlpha);
                            }
                            System.out.println("DOUBLE PARSED: " + Double.parseDouble(replaceNumbers));
                            if (!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                result = replaceNumbers;
                                unitPriceEditText.setText(result);
                            }
//                            if (Double.parseDouble(resultWithoutAlphaCharsOrSlash) <= 0) {
////                                errorDialog(getString(R.string.unit_price_by_weight_guidelines));
//                            }
                        } else {
//                            errorDialog(getString(R.string.by_weight_items_unit_price_needs_to_have_measurement_unit));

                        }

                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION");
                    e.printStackTrace();
                    unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    errorDialog(getString(R.string.couldnt_rec_voice_input));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            unitPriceSpeechRecognizer.stopListening();
                            System.out.println("MADE IT - stop UPSR");
                        }
                    };
                    stopListeningRunnable.run();
                    unitPriceMicrophoneState = 0;
                }

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(unitPriceSpeechRecognizer.RESULTS_RECOGNITION);
                System.out.println("@onPartialResults - unitPrice: " + data.get(0));
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    System.out.println("RESULT: " + result + " FOR DATA: " + data.get(0));
                    String ogResult = result;
                    if(ifQuantityIsIndividualPackageBased[0]) {
                        if (result.matches(".*[a-z].*")) {
                            String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                            System.out.println("REPLACE NUMBERS : " + replaceNumbers);
                            if (!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                result = replaceNumbers;
                                unitPriceEditText.setText(result);
                            } else {
//                                errorDialog(getString(R.string.by_package_unit_price_couldnt_rec_value));
                                unitPriceEditText.setText(result.replaceAll(".*[a-z].*", ""));

                            }
                        } else {
                            if (!(Double.parseDouble(result) <= 0)) {
                                unitPriceEditText.setText(result);
                            }

                        }
                    }
                    else{
                        if (result.matches(".*[a-z].*")) {
                            String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result);
                            String justAlpha = removeNumberRelated(result).split(" ")[0].replaceAll("/", "");
                            System.out.println("REPLACE NUMBERS : " + replaceNumbers + " JUST ALPH: " + justAlpha);
                            String resultWithoutAlphaCharsOrSlash = result.replaceAll(justAlpha, "").replaceAll("/", "");
                            if(!isMeasurementUnit(justAlpha)){
//                                errorDialog(getString(R.string.unit_price_by_weight_guidelines));
                                unitPriceEditText.setText(resultWithoutAlphaCharsOrSlash);
                            } else{
                                unitPriceEditText.setText(resultWithoutAlphaCharsOrSlash + " /" + justAlpha);
                            }
                            System.out.println("DOUBLE PARSED: " + Double.parseDouble(replaceNumbers));
                            if (!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                result = replaceNumbers;
                                unitPriceEditText.setText(result);
                            }
                            if (Double.parseDouble(resultWithoutAlphaCharsOrSlash) <= 0) {
//                                errorDialog(getString(R.string.unit_price_by_weight_guidelines));
                            }
                        }  //                            errorDialog(getString(R.string.by_weight_items_unit_price_needs_to_have_measurement_unit));
                        //                            if (Double.parseDouble(result) <= 0) {
                        ////                                errorDialog( getString(R.string.unit_price_val_have_to_be_greater_than_0));
                        //                            }


                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION");
                    e.printStackTrace();
                    unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    errorDialog(getString(R.string.couldnt_rec_voice_input));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            unitPriceSpeechRecognizer.stopListening();
                            System.out.println("MADE IT - stop UPSR");
                        }
                    };
                    stopListeningRunnable.run();
                    unitPriceMicrophoneState = 0;
                }

            }
        };
        unitPriceSpeechRecognizer.setRecognitionListener(unitPriceRecognitionListener);
        unitPriceMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(unitPriceMicrophoneState == 0){
                    if(quantityMicrophoneState == 1){ // if microphone for recording quantity details is on, then whe need to turn it off both in speech recognition service and color/state indication
                        quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                        final Runnable stopListeningRunnable = new Runnable() {
                            @Override
                            public void run() {
                                quantitySpeechRecognizer.stopListening();
                            }
                        };
                        stopListeningRunnable.run();
                        quantityMicrophoneState = 0;
                    }
                    if(ContextCompat.checkSelfPermission(ShoppingListUserItemsActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                        System.out.println("ACCESS DENIED");
                        ActivityCompat.requestPermissions(ShoppingListUserItemsActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

                    } else {
                        unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                        final Runnable startListeningRunnable = new Runnable() {
                            @Override
                            public void run() {
                                unitPriceSpeechRecognizer.startListening(unitPriceRecognizerIntent);
                            }
                        };
                        startListeningRunnable.run();
                        unitPriceMicrophoneState = 1;
                    }
                }
                else if(unitPriceMicrophoneState == 1){
                    unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            unitPriceSpeechRecognizer.stopListening();
                        }
                    };
                    stopListeningRunnable.run();
                    unitPriceMicrophoneState = 0;

                }

            }
        });

        RecognitionListener withinPackageItemCountRecognitionListener = new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Toast.makeText(ShoppingListUserItemsActivity.this, "Listening...", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                System.out.println("@onEndOfSpeech - quantity");
                withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - quantity 34455: " + result);
                    if (result.matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = removeNumberRelated(result).split(" ")[0]; // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                        if(!ifQuantityIsIndividualPackageBased[0]) {
                            try {
                                if (result.matches(".*[a-z].*")) {
                                    System.out.println("A_Z CONTIANING ONEc234325315131351353: " + result);
                                    System.out.println("JUST ALPHA: " + justAlpha);
                                    if (!isMeasurementUnit(justAlpha)){
                                        Toast.makeText(ShoppingListUserItemsActivity.this,getString(R.string.add_proper_unit_of_weight_after_numeric_value_2), Toast.LENGTH_SHORT).show();
                                        justAlpha = "";
                                        System.out.println("JUST ALPHA 2: " + result);
                                    }
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    System.out.println("Wo A_Z CONTIANING ONE: " + result);
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
                                            errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                        }
                                    }
                                    withinPackageItemCountEditText.setText(result + " " + justAlpha);
                                } else {
                                    withinPackageItemCountEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                e.printStackTrace();
                                withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                final Runnable stopListeningRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        withinPackageItemCountRecognizer.stopListening();
                                        System.out.println("MADE IT");
                                    }
                                };
                                stopListeningRunnable.run();
                                withinPackageMicrophoneState = 0;
                            }
                        }
                        else{
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in lik e'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            System.out.println("REPLACED PFJDAOFDA:  " + replaceNumbers);
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
                                errorDialog(getString(R.string.only_whole_numbers_within_package_item_count_purchased));
                            }
                            withinPackageItemCountEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
                            errorDialog(getString(R.string.by_package_within_package_item_count_only_whole_numbers));
                        } else {
                            withinPackageItemCountEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION");
                    withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    errorDialog(getString(R.string.couldnt_rec_value_within_package_item));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            withinPackageItemCountRecognizer.stopListening();
                            System.out.println("MADE IT");
                        }
                    };
                    stopListeningRunnable.run();
                    withinPackageMicrophoneState = 0;
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                System.out.println("@onPartialResults - quantity");
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - quantity 34455: " + result);
                    if (result.matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = removeNumberRelated(result).split(" ")[0]; // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                        if(!ifQuantityIsIndividualPackageBased[0]) {
                            try {
                                if (result.matches(".*[a-z].*")) {
                                    System.out.println("A_Z CONTIANING ONEc234325315131351353: " + result);
                                    System.out.println("JUST ALPHA: " + justAlpha);
                                    if (!isMeasurementUnit(justAlpha)){
                                        Toast.makeText(ShoppingListUserItemsActivity.this,getString(R.string.add_proper_unit_of_weight_after_numeric_value_2), Toast.LENGTH_SHORT).show();
                                        justAlpha = "";
                                        System.out.println("JUST ALPHA 2: " + result);
                                    }
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    System.out.println("Wo A_Z CONTIANING ONE: " + result);
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
//                                            errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                        }
                                    }
                                    withinPackageItemCountEditText.setText(result + " " + justAlpha);
                                } else {
                                    withinPackageItemCountEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                System.out.println("EXCEPTION");
                                withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                final Runnable stopListeningRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        withinPackageItemCountRecognizer.stopListening();
                                        System.out.println("MADE IT");
                                    }
                                };
                                stopListeningRunnable.run();
                                withinPackageMicrophoneState = 0;
                            }
                        }
                        else{
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in lik e'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            System.out.println("REPLACED PFJDAOFDA:  " + replaceNumbers);
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
//                                errorDialog(getString(R.string.only_whole_numbers_within_package_item_count_purchased));
                            }
                            withinPackageItemCountEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
//                            errorDialog(getString(R.string.by_package_within_package_item_count_only_whole_numbers));
                        } else {
                            withinPackageItemCountEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION");
                    withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    errorDialog(getString(R.string.couldnt_rec_value_within_package_item));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            withinPackageItemCountRecognizer.stopListening();
                            System.out.println("MADE IT");
                        }
                    };
                    stopListeningRunnable.run();
                    withinPackageMicrophoneState = 0;
                }
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - quantity 34455: " + result);
                    if (result.matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = removeNumberRelated(result).split(" ")[0]; // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                        if(!ifQuantityIsIndividualPackageBased[0]) {
                            try {
                                if (result.matches(".*[a-z].*")) {
                                    System.out.println("A_Z CONTIANING ONEc234325315131351353: " + result);
                                    System.out.println("JUST ALPHA: " + justAlpha);
                                    if (!isMeasurementUnit(justAlpha)){
                                        Toast.makeText(ShoppingListUserItemsActivity.this,getString(R.string.add_proper_unit_of_weight_after_numeric_value_2), Toast.LENGTH_SHORT).show();
                                        justAlpha = "";
                                        System.out.println("JUST ALPHA 2: " + result);
                                    }
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    System.out.println("Wo A_Z CONTIANING ONE: " + result);
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        System.out.println("EXCEPTION");
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
//                                            errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                        }
                                    }
                                    withinPackageItemCountEditText.setText(result + " " + justAlpha);
                                } else {
                                    withinPackageItemCountEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                System.out.println("EXCEPTION");
                                withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                                errorDialog(getString(R.string.by_package_item_count_couldnt_rec_value));
                                final Runnable stopListeningRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        withinPackageItemCountRecognizer.stopListening();
                                        System.out.println("MADE IT");
                                    }
                                };
                                stopListeningRunnable.run();
                                withinPackageMicrophoneState = 0;
                            }
                        }
                        else{
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in lik e'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            System.out.println("REPLACED PFJDAOFDA:  " + replaceNumbers);
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
//                                errorDialog(getString(R.string.only_whole_numbers_within_package_item_count_purchased));
                            }
                            withinPackageItemCountEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
//                            errorDialog(getString(R.string.by_package_within_package_item_count_only_whole_numbers));
                        } else {
                            withinPackageItemCountEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
                    withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
//                    errorDialog(getString(R.string.couldnt_rec_value_within_package_item));
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            withinPackageItemCountRecognizer.stopListening();
                            System.out.println("MADE IT");
                        }
                    };
                    stopListeningRunnable.run();
                    withinPackageMicrophoneState = 0;
                }
            }
        };
        withinPackageItemCountRecognizer.setRecognitionListener(withinPackageItemCountRecognitionListener);
        withinPackageItemCountMicrophone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(withinPackageMicrophoneState == 0){
                    if(ContextCompat.checkSelfPermission(ShoppingListUserItemsActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
                        System.out.println("ACCESS DENIED");
                        ActivityCompat.requestPermissions(ShoppingListUserItemsActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

                    } else {
                        withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                        final Runnable startListeningRunnable = new Runnable() {
                            @Override
                            public void run() {
                                withinPackageItemCountRecognizer.startListening(quantitySpeechRecognizerIntent);
                            }
                        };
                        startListeningRunnable.run();
                        withinPackageMicrophoneState = 1;
                    }
                }
                else if(withinPackageMicrophoneState == 1){
                    withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    final Handler handler = new Handler();
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            withinPackageItemCountRecognizer.stopListening();
                            System.out.println("MADE IT");
                        }
                    };
                    stopListeningRunnable.run();
                    withinPackageMicrophoneState = 0;

                }
            }
        });

        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        enter_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = O)
            @Override
            public void onClick(View view) {

                if(ifQuantityIsIndividualPackageBased[0]) {
                    String quantityToPass = quantityEditText.getText().toString();
                    System.out.println("quantityToPass: " + quantityToPass);
                    String withinPackageItemCountToPass = withinPackageItemCountEditText.getText().toString();
                    String unitPriceToPass = unitPriceEditText.getText().toString();
                    String additionalWeightToPass = additionalWeightEditText.getText().toString();
                    if (quantityToPass.isEmpty()) {
                        quantityToPass = "1";
                    }
                    if (withinPackageItemCountToPass.isEmpty()) {
                        withinPackageItemCountToPass = "1";
                    }
                    Double doubleParsedUnitPrice;
                    Double doubleParsedPackageQuantity;
                    Double doubleParsedPackageWithinPackageItemCount;
                    Double doubleParsedAdditionalWeight = null;
                    try {
                        doubleParsedUnitPrice = Double.parseDouble(unitPriceToPass);
                    } catch(Exception e){
                        doubleParsedUnitPrice = 0.0;
                    }
                    try {
                        doubleParsedPackageQuantity = Double.parseDouble(quantityToPass);
                    } catch(Exception e){
                        doubleParsedPackageQuantity = 0.0;
                    }
                    try {
                        doubleParsedPackageWithinPackageItemCount = Double.parseDouble(withinPackageItemCountToPass);
                    } catch(Exception e){
                        doubleParsedPackageWithinPackageItemCount = 0.0;
                    }
                    System.out.println("ADDITIONAL WEIGHT PASS: " + additionalWeightToPass + " IS TRUE: " + !additionalWeightToPass.replaceAll("\\s+","").isEmpty());
                    try {
                        if(!additionalWeightToPass.replaceAll("\\s+","").isEmpty()) {
                            System.out.println("PREPARSE: " + additionalWeightToPass.replaceAll("[^0-9.]", ""));
                            doubleParsedAdditionalWeight = Double.parseDouble(additionalWeightToPass.replaceAll("[^0-9.]", ""));
                            System.out.println("PARSED: " + doubleParsedAdditionalWeight);
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        doubleParsedAdditionalWeight = null;
                    }
                    if(unitPriceToPass.isEmpty()){
                        errorDialog(getString(R.string.package_price_required_details));
                    }
                    else if(doubleParsedUnitPrice <= 0){ //
                        errorDialog(getString(R.string.only_number_value_more_than_0_by_package_item_unit_price));

                    }
                    else if(Math.floor(doubleParsedPackageQuantity) != doubleParsedPackageQuantity){
                        errorDialog(getString(R.string.only_whole_numbers_total_package_purchased));
                    }
                    else if(Math.floor(doubleParsedPackageWithinPackageItemCount) != doubleParsedPackageWithinPackageItemCount){
                        errorDialog(getString(R.string.by_package_within_package_item_count_only_whole_numbers));
                    }
                    else if(!additionalWeightToPass.replaceAll("\\s+","").isEmpty() && doubleParsedAdditionalWeight == null){
                        System.out.println("TRIGGERED HERE 4");
                        errorDialog(getString(R.string.by_package_weight_guidelines));
                    }
                    else {
                        if(date == null){
                            date = new Date();
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String dateStr = dateFormat.format(date);
                        if (!ifStoreProvided) {
                            System.out.println("IF PROVIDED: " + ifStoreProvided);
                            ArrayList<Store> storesForChosing = null;
                            try {
                                if (Build.VERSION.SDK_INT >= O) {
                                    storesForChosing = QueryUtils.getStores(getApplicationContext());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            SimpleStoreListAdapter simpleStoreListAdapter = new SimpleStoreListAdapter(ShoppingListUserItemsActivity.this, storesForChosing);
                            choseStoreListView.setAdapter(simpleStoreListAdapter);
                            choseStoreTextView.setText(String.format(getString(R.string.chose_a_store), shoppingListUserItemName));
                            choseStoreTextView.setVisibility(View.VISIBLE);
                            choseStoreListView.setVisibility(View.VISIBLE);
                            view.setVisibility(View.VISIBLE);
                            System.out.println("CALLED 2");

                            final String[] finalAdditionalWeightToPass = {additionalWeightToPass};
                            choseStoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    View selectedStoreView = simpleStoreListAdapter.getView(i, view, adapterView);
                                    Store selectedStore = simpleStoreListAdapter.getItem(i);
                                    try {
                                        String quantityToPass = quantityEditText.getText().toString();
                                        System.out.println("new quantityToPass: " + quantityToPass);
                                        String withinPackageItemCountToPass = withinPackageItemCountEditText.getText().toString();
                                        String unitPriceToPass = unitPriceEditText.getText().toString();
                                        String additionalWeightToPass = additionalWeightEditText.getText().toString();
                                        if (quantityToPass.isEmpty()) {
                                            quantityToPass = "1";
                                        }
                                        if (withinPackageItemCountToPass.isEmpty()) {
                                            withinPackageItemCountToPass = "1";
                                        }
                                        Double doubleParsedUnitPrice;
                                        Double doubleParsedPackageQuantity;
                                        Double doubleParsedPackageWithinPackageItemCount;
                                        Double doubleParsedAdditionalWeight = null;
                                        try {
                                            doubleParsedUnitPrice = Double.parseDouble(unitPriceToPass);
                                        } catch(Exception e){
                                            doubleParsedUnitPrice = 0.0;
                                        }
                                        try {
                                            doubleParsedPackageQuantity = Double.parseDouble(quantityToPass);
                                        } catch(Exception e){
                                            doubleParsedPackageQuantity = 0.0;
                                        }
                                        try {
                                            doubleParsedPackageWithinPackageItemCount = Double.parseDouble(withinPackageItemCountToPass);
                                        } catch(Exception e){
                                            doubleParsedPackageWithinPackageItemCount = 0.0;
                                        }
                                        try {
                                            if(!additionalWeightToPass.isEmpty()) {
                                                doubleParsedAdditionalWeight = Double.parseDouble(additionalWeightToPass.replaceAll("[^0-9.]", ""));
                                            }
                                        } catch(Exception e){
                                            doubleParsedAdditionalWeight = null;
                                        }
                                        if(unitPriceToPass.isEmpty()){
                                            errorDialog(getString(R.string.package_price_required_details));
                                        }
                                        else if(doubleParsedUnitPrice <= 0){ //
                                            errorDialog(getString(R.string.only_number_value_more_than_0_by_package_item_unit_price));

                                        }
                                        else if(Math.floor(doubleParsedPackageQuantity) != doubleParsedPackageQuantity){
                                            errorDialog(getString(R.string.only_whole_numbers_total_package_purchased));
                                        }
                                        else if(!additionalWeightToPass.replaceAll("\\s+","").isEmpty() && doubleParsedAdditionalWeight == null){
                                            System.out.println("TRIGGERED HERE 1");
                                            errorDialog(getString(R.string.by_package_weight_guidelines));
                                        }
                                        else if(Math.floor(doubleParsedPackageWithinPackageItemCount) != doubleParsedPackageWithinPackageItemCount){
                                            errorDialog(getString(R.string.by_package_within_package_item_count_only_whole_numbers));
                                        }
                                        else {

                                            if (!additionalWeightToPass.replaceAll("\\s+","").isEmpty() && !containsMeasurementUnit(finalAdditionalWeightToPass[0])) {
                                                System.out.println("TRIGGERED HERE 2");
                                                errorDialog(getString(R.string.by_package_weight_guidelines));
                                            }

                                            else {
                                                System.out.println("CALLED CHOSE STORE LIST VIEW QUANTITY IS INDIVIDUAL + FINAL WEIGHT: " + finalAdditionalWeightToPass[0]);
                                                QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, selectedStore.getStoreName(), dateStr,
                                                        quantityEditText.getText().toString(), // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                                        unitPriceEditText.getText().toString(), "ea", finalAdditionalWeightToPass[0], withinPackageItemCountEditText.getText().toString(), getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                                        .putString("jsonData", Constants.json_data_str.toString()).apply();
                                                alertDialog.dismiss();
                                            }
                                            shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);
                                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems, shoppingListName);
                                            shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        } else {
                            System.out.println("STORE FINAL PASS: " + Constants.storeBeingShoppedIn);
                            try {
                                String finalAdditionalWeightToPass = additionalWeightEditText.getText().toString();
                                if (!finalAdditionalWeightToPass.replaceAll("\\s+","").isEmpty() && !containsMeasurementUnit(finalAdditionalWeightToPass)) {
                                    System.out.println("TRIGGERED HERE 3: " + finalAdditionalWeightToPass);
                                    errorDialog(getString(R.string.by_package_weight_guidelines));
                                } else if (finalAdditionalWeightToPass.isEmpty()) {
                                    System.out.println("CALLED LIST VIEW QUANTITY IS INDIVIDUAL NO ADDITIONAL WEIGHT DETAIL");
                                    QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, Constants.storeBeingShoppedIn, dateStr,
                                            quantityEditText.getText().toString(), // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                            unitPriceEditText.getText().toString(), "/ea", null, quantityEditText.getText().toString(), getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss

                                    alertDialog.dismiss();
                                } else if (!finalAdditionalWeightToPass.isEmpty() && containsMeasurementUnit(finalAdditionalWeightToPass)) {
                                    QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, Constants.storeBeingShoppedIn, dateStr,
                                            quantityEditText.getText().toString(), // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                            unitPriceEditText.getText().toString(), "/ea", additionalWeightToPass, quantityEditText.getText().toString(), getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss

                                    alertDialog.dismiss();
                                }

                                shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);

                                shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems, shoppingListName);
                                shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else{

                    String quantityToPass = quantityEditText.getText().toString();
                    System.out.println("quantityToPass 2: " + quantityToPass);
                    String unitPriceToPass = replaceAllCommonIssues(unitPriceEditText.getText().toString());
                    if (quantityToPass.isEmpty()) {
                        quantityToPass = "1";
                    }
                    if(unitPriceToPass.isEmpty()){
                        errorDialog(getString(R.string.add_up_by_weight));
                    }
                    else {
                        if(date == null){
                            date = new Date();
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        String dateStr = dateFormat.format(date);
                        if (!ifStoreProvided) {
                            System.out.println("IF PROVIDED: " + ifStoreProvided);
                            ArrayList<Store> storesForChosing = null;
                            try {
                                if (Build.VERSION.SDK_INT >= O) {
                                    storesForChosing = QueryUtils.getStores(getApplicationContext());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            SimpleStoreListAdapter simpleStoreListAdapter = new SimpleStoreListAdapter(ShoppingListUserItemsActivity.this, storesForChosing);
                            choseStoreListView.setAdapter(simpleStoreListAdapter);
                            choseStoreTextView.setText(String.format(getString(R.string.chose_a_store), shoppingListUserItemName));
                            choseStoreTextView.setVisibility(View.VISIBLE);
                            choseStoreListView.setVisibility(View.VISIBLE);
                            view.setVisibility(View.VISIBLE);
                            System.out.println("CALLED 2");

                            choseStoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    View selectedStoreView = simpleStoreListAdapter.getView(i, view, adapterView);
                                    Store selectedStore = simpleStoreListAdapter.getItem(i);
                                    try {
                                        System.out.println("CALLED 1");
                                        String quantityToPass = quantityEditText.getText().toString();
                                        System.out.println("quantityToPass 2: " + quantityToPass);
                                        String unitPriceToPass = replaceAllCommonIssues(unitPriceEditText.getText().toString());
                                        if (quantityToPass.isEmpty()) {
                                            quantityToPass = "1";
                                        }
                                        if (!unitPriceToPass.isEmpty()) {
                                            System.out.println("UNIT PRICE TO PASS: " + unitPriceToPass);
                                            String numberRelatedRemoved = removeNumberRelated(replaceAllCommonIssues(unitPriceToPass));
                                            String numberRelatedRemovedWithoutSlash = numberRelatedRemoved.replaceAll("/", "");
                                            if (isMeasurementUnit(numberRelatedRemovedWithoutSlash) && unitPriceToPass.contains("/") & Double.parseDouble(unitPriceToPass.replaceAll(numberRelatedRemoved, "")) > 0) {
                                                System.out.println("CALLED NOT STORE VIEW QUANTITY IS NOT IND");
                                                QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, selectedStore.getStoreName(), dateStr,
                                                        quantityToPass + numberRelatedRemovedWithoutSlash, // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                                        unitPriceToPass.replaceAll(numberRelatedRemoved, ""), numberRelatedRemovedWithoutSlash, null, null, getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                                alertDialog.dismiss();
                                            } else {
                                                errorDialog(getString(R.string.manual_unit_price_guidelines));

                                            }
                                            shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);

                                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems, shoppingListName);
                                            shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                        } else {
                                            errorDialog(getString(R.string.add_up_by_weight));

                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        errorDialog(getString(R.string.manual_unit_price_guidelines));
                                    }

                                }
                            });
                        } else {
                            try {
                                if (!unitPriceToPass.isEmpty()) {
                                    System.out.println("UNIT PRICE TO PASS: " + unitPriceToPass);
                                    String numberRelatedRemoved = removeNumberRelated(replaceAllCommonIssues(unitPriceToPass));
                                    String numberRelatedRemovedWithoutSlash = numberRelatedRemoved.replaceAll("/", "");
                                    if (isMeasurementUnit(numberRelatedRemovedWithoutSlash) && unitPriceToPass.contains("/") & Double.parseDouble(unitPriceToPass.replaceAll(numberRelatedRemoved, "")) > 0) {
                                        System.out.println("CALLED NOT STORE VIEW QUANTITY IS NOT IND");
                                        QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, Constants.storeBeingShoppedIn, dateStr,
                                                quantityToPass + numberRelatedRemovedWithoutSlash, // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                                unitPriceToPass.replaceAll(numberRelatedRemoved, ""), numberRelatedRemovedWithoutSlash, null, null, getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                        alertDialog.dismiss();
                                    } else {
                                        errorDialog(getString(R.string.manual_unit_price_guidelines));

                                    }
                                    shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);

                                    shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems, shoppingListName);
                                    shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                } else {
                                    errorDialog(getString(R.string.add_up_by_weight));
                                }
                            } catch (Exception e) {
                                errorDialog(getString(R.string.unit_price_by_weight_guidelines));
                            }
                        }
                    }

                }


            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                date = null;
            }
        });
        alertDialog.setCanceledOnTouchOutside(true);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private void calendarDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom);
        View view = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.calendar_layout,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        CalendarView calendarView = view.findViewById(R.id.calendar_view);
        calendarView.setDate(System.currentTimeMillis(),false,true); // setting date to current day
        alertDialog.setCanceledOnTouchOutside(true);
        if(date != null){
            calendarView.setDate(date.getTime());
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                date = new Date((year-1900), month, dayOfMonth);
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }

        alertDialog.show();
    }

    private String convertWithEnglishWordsToNumbers(String result) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] stringSplitUp = result.split(" ");
        for(String s: stringSplitUp){
            String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(s);
            if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                stringBuilder.append(replaceNumbers);
            } else{
                stringBuilder.append(" " + s);
            }
        }
        System.out.println("CONVETED: " + stringBuilder.toString() );
        return stringBuilder.toString();
    }

    private boolean containsMeasurementUnit(String string) {
        String[] stringSplitUp = string.split(" ");
        for (String s : stringSplitUp) {
            if (isMeasurementUnit(s)) {
                return true;
            }
        }
        return false;
    }

    private String removeNumberRelated(String result) {
        StringBuilder returnStr =new StringBuilder();
        String[] resultSplitUp = result.toString().split(" ");
        for(int i = 0; i < resultSplitUp.length; i++){
            System.out.println("RESULT SPLIT UP i : " + resultSplitUp[i]);
            if(!resultSplitUp[i].isEmpty()) {
                if(!resultSplitUp[i].matches("[0-9]")){
                    try {
                        Double.parseDouble(resultSplitUp[i]);
                    } catch (Exception e) {
                        String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(resultSplitUp[i].replaceAll(" ", ""));
                        System.out.println("DOUBLE PARSED: " + Double.parseDouble(replaceNumbers));
                        if (Double.parseDouble(replaceNumbers) == 0.0) {
                            // if even the replaceNumbers() func can't convert for strings like 'three' which are number related, then it is definitely not number related and we append it
                            returnStr.append(resultSplitUp[i]).append(" ");
                        }

                    }
                }
            }

        }
        System.out.println("RETURN: " + returnStr.toString().trim());
        return returnStr.toString().trim();
    }

    public void resetAdapter(){
        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
    }

    private boolean isMeasurementUnit(String string) {
        ArrayList<String> measurementUnitsArrayList = new ArrayList<>();
        String[] measurementUnitsArray = getResources().getStringArray(R.array.measurement_units_array);
        for(int i = 0; i < measurementUnitsArray.length; i++){
            String item =measurementUnitsArray[i];
            measurementUnitsArrayList.add(item.substring(item.indexOf("(")+1, item.indexOf(")")));
        }
        System.out.println("MEASUREMENTS ARRAY: " + measurementUnitsArrayList.toString());
        System.out.println("IS : " + string + " MEASUREMENT TYPE: " + measurementUnitsArrayList.contains(string.toLowerCase()));
        return measurementUnitsArrayList.contains(string.toLowerCase());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, getString(R.string.audio_permission_granted), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, getString(R.string.cant_use_this_feature), Toast.LENGTH_SHORT).show();

            }
        }
    }


    @RequiresApi(api = O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_items_layout);
        shoppingListName = getIntent().getStringExtra("shoppingListName");
        shoppingListUserItemsListView = findViewById(R.id.shopping_list_user_items_list_view);
        resultsForshoppingListUserItemsView = findViewById(R.id.results_for_user_item_text);
        SearchView searchView = findViewById(R.id.search_bar);
        Toolbar toolBar = findViewById(R.id.my_toolbar);
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(shoppingListName);
        updateObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    int index = shoppingListUserItemsListView.getFirstVisiblePosition();
                    View v = shoppingListUserItemsListView.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    System.out.println("RUNNING ONCHANGED SHOPPING LIST ITEMS BEFORE: " + shoppingListUserItems.size());
                    for (ShoppingListUserItem item: shoppingListUserItems
                    ) {
                        System.out.println(item.getName() + item.getUserQuantity() + item.getLastBought());
                    }
                    try {
                        shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);
                    } catch (ParseException  e) {
                        System.out.println("RUNNING ONCHANGED  EXCEPTION");
                        e.printStackTrace();
                    }
                    System.out.println("RUNNING ONCHANGED SHOPPING LIST ITEMS AFTER: " + shoppingListUserItems.size());

                    shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems, shoppingListName);
                    shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                    shoppingListUserItemsListView.setSelectionFromTop(index, top);
                    System.out.println("SET ADAPTER: " + shoppingListUserItemAdapter.getCount());
                    actuallyNeedsToBeUpdated.setValue(false);
                }
            }
        };
        actuallyNeedsToBeUpdated.observeForever(updateObserver);
        ImageButton removeAllGreenTickMarksButton = findViewById(R.id.remove_check_marks_button);
        removeAllGreenTickMarksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(ShoppingListUserItem item: shoppingListUserItems){
                    try {
                        QueryUtils.setItemNotGreenTickMarked(item.getName(), shoppingListName, getApplicationContext());

                        QueryUtils.setShoppingListItemToNotSavedForLater(item.getName(), shoppingListName, getApplicationContext());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                update();
            }
        });
        toolBar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if(getIntent().getStringExtra("classComingFrom") != null && getIntent().getStringExtra("classComingFrom").equals("ShoppingListsHistoryActivity")){
                    intent =  new Intent(ShoppingListUserItemsActivity.this, ShoppingListsHistoryActivity.class);
                    Bundle args = new Bundle();
                    if(getIntent().getStringExtra("originalNavPathSLUTIShoppingList") != null){
                        System.out.println("ORIGINALNAVPATH NAME: " + getIntent().getStringExtra("originalNavPathSLUTIShoppingList") );
                        intent.putExtra("originalNavPathSLUTIShoppingList", getIntent().getStringExtra("originalNavPathSLUTIShoppingList"));
                    }else {
                        System.out.println("ORIGINALNAVPATH NAME REAL START: " + shoppingListName);
                        intent.putExtra("originalNavPathSLUTIShoppingList", shoppingListName);

                    }
                    args.putString("shoppingListUserItemName", getIntent().getStringExtra("shoppingListUserItemName"));
                    args.putSerializable("shoppingListsContainingSlItem", getIntent().getBundleExtra("BUNDLE").getSerializable("shoppingListsContainingSlItem"));
                    intent.putExtra("BUNDLE", args);
                }
                else{

                    intent = new Intent(ShoppingListUserItemsActivity.this, MainActivity.class);


                }
                startActivity(intent);
                actuallyNeedsToBeUpdated.removeObserver(updateObserver);
                finish();
            }
        });
        ImageButton add_item_button = findViewById(R.id.add_image_button);
        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShoppingListUserItemsActivity.this, AddShoppingListUserItemActivity.class);
                intent.putExtra("shoppingListName", shoppingListName);
                startActivity(intent);
                actuallyNeedsToBeUpdated.removeObserver(updateObserver);
                finish();
            }
        });
        shoppingListUserItems = null;
        try {
            System.out.println("JSON DATE WHEN OPENED: " + PreferenceManager.
                    getDefaultSharedPreferences(getApplicationContext()).getString("jsonData", ""));
            shoppingListUserItems = QueryUtils.getShoppingListUserItems(shoppingListName);
            System.out.println("ITEMSSSS @onCreate");
            for (ShoppingListUserItem item: shoppingListUserItems
            ) {
                System.out.println(item.getName() + item.getUserQuantity() + item.getLastBought());
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
        if(!(shoppingListUserItemAdapter.getCount() < 1)) {
            shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);

        }
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem item = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                item.setBackground(new ColorDrawable(Color.TRANSPARENT));
                // set item width
                item.setWidth(100);
                // set a icon
                item.setIcon(R.drawable.ic_baseline_bookmark_border_24);
                // add to menu
                menu.addMenuItem(item);
            }
        };
        shoppingListUserItemsListView.setMenuCreator(creator);

        shoppingListUserItemsListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                ShoppingListUserItem shoppingListUserItem = shoppingListUserItemAdapter.getItem(position);
                if(shoppingListUserItem.getIfSavedForLater()){
                    try {
                        QueryUtils.setShoppingListItemToNotSavedForLater(shoppingListUserItem.getName(), shoppingListName, getApplicationContext());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else{
                    try {
                        QueryUtils.setShoppingListItemToSavedForLater(shoppingListUserItem.getName(), shoppingListName, getApplicationContext());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                update();
                return true;
            }
        });

        shoppingListUserItemAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                System.out.println("data register observer @onChanged");
            }

            @Override
            public void onInvalidated() {
                super.onInvalidated();
                System.out.println("data register observer @onInvalidated");
            }
        });

        hideSoftKeyboard(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                                              @Override
                                              public boolean onQueryTextSubmit(String query) {
                                                  Integer searchQueryLength = query.length();
                                                  try {
                                                      shoppingListUserItems = QueryUtils.getShoppingListUserItems(shoppingListName);
                                                  } catch (IOException e) {
                                                      e.printStackTrace();
                                                  } catch (ParseException e) {
                                                      e.printStackTrace();
                                                  }
                                                  ArrayList<ShoppingListUserItem> newItems = new ArrayList<>();
                                                  for(ShoppingListUserItem item: shoppingListUserItems){
                                                      if(!(query.length() > item.getName().length())){
                                                          if(item.getName().substring(0, searchQueryLength).equalsIgnoreCase(query)){
                                                              newItems.add(item);
                                                          }
                                                      }
                                                      else if(query.length() >= 3){
                                                          if(item.getName().toLowerCase().contains(query.toLowerCase())){
                                                              newItems.add(item);
                                                          }
                                                      }
                                                  }
                                                  shoppingListUserItems = newItems;

                                                  shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems, shoppingListName);
                                                  try{
                                                      shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                                  } catch(Exception e){
                                                      shoppingListUserItemsListView.removeAllViewsInLayout();
                                                  }
                                                  return false;

                                              }

                                              @Override
                                              public boolean onQueryTextChange(String query) {

                                                  Integer searchQueryLength = query.length();
                                                  try {
                                                      shoppingListUserItems = QueryUtils.getShoppingListUserItems(shoppingListName);
                                                  } catch (IOException e) {
                                                      e.printStackTrace();
                                                  } catch (ParseException e) {
                                                      e.printStackTrace();
                                                  }
                                                  ArrayList<ShoppingListUserItem> newItems = new ArrayList<>();
                                                  for(ShoppingListUserItem item: shoppingListUserItems){
                                                      if(!(query.length() > item.getName().length())){
                                                          if(item.getName().substring(0, searchQueryLength).equalsIgnoreCase(query)){
                                                              newItems.add(item);
                                                          }
                                                      }
                                                      else if(query.length() >= 3){
                                                          if(item.getName().toLowerCase().contains(query.toLowerCase())){
                                                              System.out.print("FOR QUERY : " + query + " ADDING ITEM: " + item.getName());
                                                              newItems.add(item);
                                                          }
                                                      }
                                                  }
                                                  shoppingListUserItems = newItems;
                                                  System.out.println("NEW SHOPPING LIST USER ITEMS @onQuereyTExtChange: " + shoppingListUserItems);

                                                  // if user has deleted all their text
                                                  if (query.isEmpty()) {
                                                      resultsForshoppingListUserItemsView.setVisibility(GONE);
                                                  }
                                                  else {
                                                      resultsForshoppingListUserItemsView.setText("Results for " + query);
                                                      resultsForshoppingListUserItemsView.setVisibility(View.VISIBLE);
                                                  }

                                                  shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                                                  try{
                                                      shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                                  } catch(Exception e){
                                                      shoppingListUserItemsListView.removeAllViewsInLayout();
                                                  }
                                                  return false;
                                              }


                                          }

        );
        // filter through user_items list with user items list adapter
        searchView.setOnCloseListener( new SearchView.OnCloseListener() {
                                           @Override
                                           public boolean onClose() {

                                               try {
                                                   shoppingListUserItems = QueryUtils.getShoppingListUserItems(shoppingListName);
                                               } catch (IOException e) {
                                                   e.printStackTrace();
                                               } catch (ParseException e) {
                                                   e.printStackTrace();
                                               }
                                               return false;
                                           }
                                       }
        );

        shoppingListUserItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingListUserItem shoppingListUserItem = (ShoppingListUserItem) shoppingListUserItemAdapter.getItem(i);
                String shoppingListUserItemName = shoppingListUserItem.getName();
                View selectedStoreView = shoppingListUserItemAdapter.getView(i, view, adapterView);
                selectedStoreView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        System.out.println("LONG CLICKED");
                        try {
                            TextView name = (TextView) v.findViewById(R.id.shopping_list_item_name);
                            if (!shoppingListUserItem.isIfGreenMarked()) {
                                QueryUtils.setItemGreenTickMarked(name.getText().toString(), shoppingListName, getApplicationContext());
                                System.out.println("SET ITEM GREEN TICK MARKED: " + shoppingListUserItemName);
                            } else {
                                QueryUtils.setItemNotGreenTickMarked(name.getText().toString(), shoppingListName, getApplicationContext());
                                System.out.println("SET ITEM GREEN NOT TICK MARKED: " + shoppingListUserItemName);
                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        ShoppingListUserItemsActivity.update();
                        return true;
                    }

                });
                ImageView microphoneButton = (ImageView) selectedStoreView.findViewById(R.id.record_details_button);
                ImageView eyeImageView = (ImageView) selectedStoreView.findViewById(R.id.more_vert_actions_item_button);
                eyeImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            moreVertActionsDialog(shoppingListUserItem);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
                microphoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            System.out.println("STORE BEING SHOPPED IN: " + Constants.storeBeingShoppedIn);
                            speakWithVoiceDialog(shoppingListUserItemName, !Constants.storeBeingShoppedIn.isEmpty());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


            }




        });

        if(getIntent().getBooleanExtra("isTour", false) && PreferenceManager.getDefaultSharedPreferences(ShoppingListUserItemsActivity.this).getBoolean("isFirstTimeRun", true)){
            try {
                firstRunTour();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, findViewById(R.id.user_item_card_view), listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void firstRunTour() throws ParseException, IOException {
        System.out.println("FIRST RUN TOUR");
        QueryUtils.addShoppingListItem(shoppingListName,getString(R.string.granola_bar), null, getApplicationContext());
        updateUserItems();
        ImageButton tourGuideNavButton = findViewById(R.id.shopping_list_tour_guide_nav_button);
        tourGuideNavButton.setVisibility(View.VISIBLE);
        shoppingListUserItemsListView.setSelection(0);
        View exampleShoppingListUserItemView2 = getViewByPosition(shoppingListUserItemsListView.getFirstVisiblePosition(), shoppingListUserItemsListView);
        int[] point = new int[2];
        System.out.println("SHOPPING LIST USER VIEW: " + point);
        View exampleShoppingListUserItemView = getViewByPosition(shoppingListUserItemsListView.getFirstVisiblePosition(), shoppingListUserItemsListView);
        System.out.println("EXAMPLE TIME X : " + exampleShoppingListUserItemView.getX());
        final int[] clickNum = {PreferenceManager.getDefaultSharedPreferences(this).getInt("shoppingListPageTourClickNum", 0)};
        GuideView shoppingListItemNameGuideView = new GuideView.Builder(this)
                .setTitle("Shopping List User Item Name")
                .setContentText("This is the unique name of your item that is saved in a larger items repository")
                .setTargetView(exampleShoppingListUserItemView.getRootView().findViewById(R.id.shopping_list_item_name))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        GuideView shoppingListUserItemQuantityGuideView = new GuideView.Builder(this)
                .setTitle("Shopping List User Item Quantity")
                .setContentText("This is the quantity of your shopping list item that you can toggle with the plus/minus buttons")
                .setTargetView(exampleShoppingListUserItemView.findViewById(R.id.quantity_sl_item))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        GuideView recordPurchaseDetails = new GuideView.Builder(this)
                .setTitle("Record Purchase Details")
                .setContentText("The microphone button triggers the record purchase details pop-up of a item. This is where you record the purchase of your shopping list item. Purchases can either be by package or by weight, mainly for produce, based on the type of item you buy. If you are recording for a by purchase item then you need to fill the the unit price per package, note that the quantity and within item count fields are defaulted to 1.The additional weight detail is optional but is needed to create unit price per a measurement of weight for that item, later used in price analytics. For by weight items your quantity is the amount of the item in some weight measurement unit such as pounds (lb) but you don't need to add the unit of weight there as it is inferred from the unit price. The unit price field is mandatory and it is the price per the weight measurement unit for the item, an example input for by weight items would be 1.99/lb pronounced as 1.99 per pound. All fields can be filled using the voice functionality or typed and please note that if you use the voice functionality you must start your numbers with a zero, for example 1.99 will be said 01.99, and to indicate a decimal place say point. You can set what date you are recording the purchase for by clicking the calendar icon defaulted to the current date.")
                .setTargetView(exampleShoppingListUserItemView.findViewById(R.id.record_details_button))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        GuideView greenTickMarkGuideView = new GuideView.Builder(this)
                .setTitle("Green Tick Mark")
                .setContentText("A green tick mark appears next to any shopping list item you short-long double tap and is way to keep track of what you've bought without having to record purchase details")
                .setTargetView(exampleShoppingListUserItemView.findViewById(R.id.check_circle))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView


        GuideView remove_all_green_tick_mark = new GuideView.Builder(this)
                .setTitle("Remove All Green Tick Mark")
                .setContentText("Clicking this button removes all green tick marks from all items that have a green tick mark in the shopping list.")
                .setTargetView(exampleShoppingListUserItemView.findViewById(R.id.remove_check_marks_button))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView


        GuideView bookmark_for_later = new GuideView.Builder(this)
                .setTitle("Save For Later")
                .setContentText("You can swipe right on any shopping list item and hit the bookmark icon to mark the item blue as saved for another trip or later.")
                .setTargetView(exampleShoppingListUserItemView)
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        GuideView blueTickMark = new GuideView.Builder(this)
                .setTitle("Blue Item Name")
                .setContentText("The shopping list item name is written in blue if you have recorded purchase details for it at least once.")
                .setTargetView(exampleShoppingListUserItemView.findViewById(R.id.shopping_list_item_name))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView


        GuideView mostRecentPurchaseDateGuideView = new GuideView.Builder(this)
                .setTitle("Most Recent Purchase Date")
                .setContentText("The most recent date you recorded purchase details for this item that appears alongside the blue item name.")
                .setTargetView(exampleShoppingListUserItemView.findViewById(R.id.last_bought_date))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        GuideView moreOptionsGuideView = new GuideView.Builder(this)
                .setTitle("More Options")
                .setContentText("Here are other options, marked by 3 vertical dots,  such as the purchase history of the item, moving the item to another list, deleting item from list, and the insights graph. ")
                .setTargetView(exampleShoppingListUserItemView.findViewById(R.id.more_vert_actions_item_button))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView
        moreVertActionsView = findViewById(R.id.search_bar);
        insightsView = findViewById(R.id.search_bar);
        GuideView moreOptionsGuideViewExplained = new GuideView.Builder(this)
                .setTitle("More Options Explained")
                .setContentText("The item purchase history is the collection of all recorded purchases for this item across stores with the date, the amount you spent at the time for the whole purchase, and the price per your unit comparison unit if you have the option toggled on from the home page. You can move the item to another shopping list and delete it from your shopping list entirely. The insights graph helps compare price over time. Purchase history and insights graph options are greyed out if there is not enough data for the item.")
                .setTargetView(moreVertActionsView)
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        GuideView insightsGraphExplained = new GuideView.Builder(this)
                .setTitle("Insights Graph Explained")
                .setContentText("The insights graph compares the price of an item against a common measurement of weight(e.g. lb) over time for each store it has recorded history for in a line graph. Some choices will be greyed out such as purchase history and insights graph based on the recorded data for the item. Insights graph is only available if you record purchase details for the item on at least 2 different dates and have your price comparison setting toggled on from the main page. Note that dates with multiple purchases in one day have the unit-price for that day averaged. You can press each data point for more details.")
                .setTargetView(insightsView)
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        AppCompatImageButton add_button = (AppCompatImageButton) findViewById(R.id.add_image_button);
        GuideView add_button_guide_view =  new GuideView.Builder(this)
                .setTitle("Add a shopping list item")
                .setContentText("This button allows you to add a shopping list item from the item repository or of a new name to the current shopping list.")
                .setTargetView(add_button)
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        SearchView shopping_list_search_view = (SearchView) findViewById(R.id.search_bar);
        GuideView shopping_list_search_view_guide_view =  new GuideView.Builder(this)
                .setTitle("Search in the Shopping List")
                .setContentText("This is a search bar for searching within all the items in the shopping list by name.")
                .setTargetView(shopping_list_search_view)
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        GuideView toolbar_guideview =  new GuideView.Builder(this)
                .setTitle("Navigation Arrow")
                .setContentText("At the top of each page is the title of your store, shopping list, purchase history or other component of this app. Next to it is the back button and please use this back arrow to navigate throughout the app instead of the in-built arrows at the bottom of your device.")
                .setTargetView(findViewById(R.id.my_toolbar))
                .setContentTextSize(12)//optional
                .setTitleTextSize(14)//optional
                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                .setDismissType(DismissType.outside)
                .build(); //optional - default dismissible by TargetView

        ArrayList<GuideView> tourGuideViewArrayList = new ArrayList<>();
        tourGuideViewArrayList.add(shoppingListItemNameGuideView);
        tourGuideViewArrayList.add(shoppingListUserItemQuantityGuideView);
        tourGuideViewArrayList.add(recordPurchaseDetails);
        tourGuideViewArrayList.add(greenTickMarkGuideView);
        tourGuideViewArrayList.add(remove_all_green_tick_mark);
        tourGuideViewArrayList.add(bookmark_for_later);
        tourGuideViewArrayList.add(blueTickMark);
        tourGuideViewArrayList.add(mostRecentPurchaseDateGuideView);
        tourGuideViewArrayList.add(moreOptionsGuideView);
        tourGuideViewArrayList.add(moreOptionsGuideViewExplained);
        tourGuideViewArrayList.add(insightsGraphExplained);
        tourGuideViewArrayList.add(insightsGraphExplained); // filler #9
        tourGuideViewArrayList.add(add_button_guide_view);
        tourGuideViewArrayList.add(shopping_list_search_view_guide_view);
        tourGuideViewArrayList.add(toolbar_guideview);
        ShoppingListUserItem shoppingListUserItemExample = shoppingListUserItemAdapter.getItem(0);
        QueryUtils.setItemNotGreenTickMarked(shoppingListUserItemExample.getName(), shoppingListName, getApplicationContext());
        updateUserItems();
        TextView last_bought_date = exampleShoppingListUserItemView.findViewById(R.id.last_bought_date);
        ImageView check_mark = (ImageView) exampleShoppingListUserItemView.findViewById(R.id.check_mark);
        tourGuideViewArrayList.get(clickNum[0]).show();
        tourGuideNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("CLICK NUM: " + clickNum[0]);
                if(clickNum[0] < tourGuideViewArrayList.size()){
                    tourGuideViewArrayList.get(clickNum[0]).dismiss();
                    clickNum[0]++;
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt("shoppingListPageTourClickNum", clickNum[0]).apply();
                    if(clickNum[0] == 3){
                        try {
                            QueryUtils.setItemGreenTickMarked(shoppingListUserItemExample.getName(), shoppingListName, getApplicationContext());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            updateUserItems();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        update();
                    }
                    if(clickNum[0] == 6){
                        try {
                            QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemExample.getName(), getString(R.string.empty_store), "10/7/2023", "1", "3.50", "ea", "50g", "1", getApplicationContext());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemExample.getName(), getString(R.string.empty_store), "10/9/2023", "1", "4.00", "ea", "50g", "1", getApplicationContext());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        try {
                            updateUserItems();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    if(clickNum[0] == 7){
                        last_bought_date.setVisibility(View.VISIBLE);
                        last_bought_date.setText(getString(R.string.placeholder_last_bought_date));
                    }


                    if(clickNum[0] == 9){
                        try {
                            moreVertActionsDialog(shoppingListUserItemExample);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        GuideView more_options_explained = new GuideView.Builder(ShoppingListUserItemsActivity.this)
                                .setTitle("More Options Explained")
                                .setContentText("The item purchase history is the collection of all recorded purchases for this item across stores with the date, the amount you spent at the time for the whole purchase, and the price per your unit comparison unit if you have the option toggled on from the home page. You can move the item to another shopping list and delete it from your shopping list entirely. The insights graph helps compare price over time. Purchase history and insights graph options are greyed out if there is not enough data for the item.")
                                .setTargetView(moreVertActionsView)
                                .setContentTextSize(12)//optional
                                .setTitleTextSize(14)//optional
                                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                                .setDismissType(DismissType.outside)
                                .build();
                        alertDialog.getWindow().setDimAmount(0f);
                        tourGuideViewArrayList.set(clickNum[0], more_options_explained);
                    }
                    if(clickNum[0] == 10){
                        try {
                            moreVertActionsDialog(shoppingListUserItemExample);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        GuideView insights_graph_explained_new = new GuideView.Builder(ShoppingListUserItemsActivity.this)
                                .setTitle("Insights Graph Explained")
                                .setContentText("The insights graph compares the price of an item against a common measurement of weight(e.g. lb) over time for each store it has recorded history for in a line graph. Some choices will be greyed out such as purchase history and insights graph based on the recorded data for the item. Insights graph is only available if you record purchase details for the item on at least 2 different dates and have your price comparison setting toggled on from the main page. Note that dates with multiple purchases in one day have the unit-price for that day averaged. You can press each data point for more details.")
                                .setTargetView(moreVertActionsView)
                                .setContentTextSize(10)//optional
                                .setTitleTextSize(12)//optional
                                .setTitleTypeFace(Typeface.defaultFromStyle(Typeface.BOLD))
                                .setDismissType(DismissType.outside)
                                .build();
                        alertDialog.getWindow().setDimAmount(0f);
                        tourGuideViewArrayList.set(clickNum[0], insights_graph_explained_new);
                    }
                    if(clickNum[0] == 11){
                        try {
                            insightsDialog(shoppingListUserItemExample.getName(), QueryUtils.getHistoryOfShoppingListItem(shoppingListUserItemExample.getName()));
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    if(clickNum[0] < tourGuideViewArrayList.size() && (clickNum[0] != 11)) {
                        tourGuideViewArrayList.get(clickNum[0]).show();
                    }
                    if(clickNum[0] == tourGuideViewArrayList.size()){
                        tourGuideNavButton.setVisibility(GONE);
                        PreferenceManager.getDefaultSharedPreferences(ShoppingListUserItemsActivity.this).edit().putBoolean("firstTimeRun", false).apply();

                        try {
                            QueryUtils.deleteShoppingList(shoppingListName, getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            QueryUtils.deleteStore(getString(R.string.empty_store), getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            QueryUtils.deleteRepItem(getString(R.string.granola_bar), getApplicationContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        PreferenceManager.getDefaultSharedPreferences(ShoppingListUserItemsActivity.this).edit().putBoolean("priceComparisonUnitOn", false).apply();
                        Constants.wantsPriceComparisonUnit = false;
                        Intent intent = new Intent(ShoppingListUserItemsActivity.this, MainActivity.class);
                        startActivity(intent);


                    }
                }
            }
        });
        System.out.println("DONE");

    }

    @RequiresApi(api = O)
    private void updateUserItems() throws IOException, ParseException {
        shoppingListUserItems = QueryUtils.getShoppingListUserItems(shoppingListName);
        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems, shoppingListName);
        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
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

    @Override
    public void finish() {
        super.finish();
    }

}