package com.example.android.receiptreader;

import android.Manifest;
import android.app.Activity;
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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import static android.os.Build.VERSION_CODES.O;
import static android.text.TextUtils.isEmpty;

public class ShoppingListUserItemsActivity extends AppCompatActivity {
    ShoppingListUserItemAdapter shoppingListUserItemAdapter;
    TextView resultsForshoppingListUserItemsView;
    Observer<Boolean> updateObserver;
    ListView shoppingListUserItemsListView;
    String shoppingListName;
    public static MutableLiveData<Boolean> actuallyNeedsToBeUpdated = new MutableLiveData<>();
    int quantityMicrophoneState = 0;
    int unitPriceMicrophoneState = 0;
    int withinPackageMicrophoneState = 0;
    int additionalWeightMicrophoneState = 0;
    ArrayList<ShoppingListUserItem> shoppingListUserItems;
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
        ConcurrentHashMap<String, ArrayList<StoreUserItem>> differentStores = new   ConcurrentHashMap<String, ArrayList<StoreUserItem>>();
        // sorting into different stores
        for(StoreUserItem item: storeUserItemArrayList){
            if(!differentStores.containsKey(item.getStore())){
                differentStores.put(item.getStore(), new ArrayList<StoreUserItem>());
            }
            differentStores.get(item.getStore()).add(item);
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
            enterButton.setVisibility(View.GONE);
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
        View view = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.more_vert_actions_custom_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        String itemName = shoppingListUserItem.getName();
        ((TextView) view.findViewById(R.id.textTitle))
                .setText(String.format(getString(R.string.actions_shopping_list_user_item), itemName));
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        // extracting all the views and setting their text based on item we are running the actions for
        ImageView historyButton = (ImageView) view.findViewById(R.id.history_button_image_view);
        ConstraintLayout view_history_cl = (ConstraintLayout) view.findViewById(R.id.view_history_cl);
        TextView view_history_tv = (TextView) view.findViewById(R.id.view_history_text_view);
        ImageView view_history_image = (ImageView) view_history_cl.getViewById(R.id.history_button_image_view);
        ConstraintLayout duplicate_indicator_cl = (ConstraintLayout) view.findViewById(R.id.duplicate_indicator_cl);
        ConstraintLayout reorderCl = (ConstraintLayout) view.findViewById(R.id.reorder_item_cl);
        TextView reorderItemTextView = (TextView) view.findViewById(R.id.reorder_item_text_view);
        reorderItemTextView.setText(String.format(getString(R.string.reorder_item), itemName));
        ConstraintLayout deleteCl = (ConstraintLayout) view.findViewById(R.id.delete_item_cl);
        TextView deleteItemTextView = (TextView) view.findViewById(R.id.delete_item_text_view);
        deleteItemTextView.setText(String.format(getString(R.string.delete_item), itemName));
        ConstraintLayout viewInsightsCl = (ConstraintLayout) view.findViewById(R.id.view_insights_of_item_cl);
        TextView viewInsightsTextView = (TextView) view.findViewById(R.id.view_insights_of_item_text_view);

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
            duplicate_indicator_cl.setVisibility(View.GONE);
        }

        ArrayList<StoreUserItem> storeUserItemsHistory = QueryUtils.getHistoryOfShoppingListItem(itemName);
        if(storeUserItemsHistory != null){
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
            viewInsightsCl.setVisibility(View.GONE);
            viewInsightsTextView.setVisibility(View.GONE);
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

    //
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void insightsDialog(String itemName, ArrayList<StoreUserItem> storeUserItemHistory) throws java.text.ParseException {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom2);
        View view = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.insight_graph_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        // on below line we are initializing our graph view.
        GraphView graphView = view.findViewById(R.id.item_insights_graph);
        ListView storeKeyListView = view.findViewById(R.id.stores_key_list_view);
        ArrayList<String> storeKeyStringArrayList = new ArrayList<>();
        Button exitButton = view.findViewById(R.id.exit_button);
        TextView unitPriceTextView = view.findViewById(R.id.unit_price_date_tap_tv);
        unitPriceTextView.setVisibility(View.GONE);


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
                alertDialog.dismiss();
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
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Item price per " + Constants.currentMeasureUnit);
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
        // title for our graph view.
        graphView.setTitleTextSize(18);
        graphView.setTitle(String.format(getString(R.string.insights_for_itemI), itemName));

        // on below line we are setting
        // text color to our graph view.
        graphView.setTitleColor(R.color.purple_200);

        // on below line we are setting
        // our title text size.
        graphView.setTitleTextSize(40);


        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
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
            enterButton.setVisibility(View.GONE);
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
        return s.replaceAll("fluidounces", "fl oz").replaceAll("fluid ounces", "fl oz").replaceAll("G", "g");
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

        Display display = getWindowManager().getDefaultDisplay();
        ToggleButton toggleButton = view.findViewById(R.id.eachWeightToggleButton);
        int width = display.getWidth();
        view.setMinimumWidth(width/2);
        choseStoreTextView.setVisibility(View.GONE);
        choseStoreListView.setVisibility(View.GONE); // by default the listview that will be populated should be gone
        builder.setView(view);
        Button cancel_button = view.findViewById(R.id.cancel_button);
        Button enter_button = view.findViewById(R.id.enterButton);
        EditText withinPackageItemCountEditText = view.findViewById(R.id.within_package_item_count_edit_text);
        EditText quantityEditText = view.findViewById(R.id.quantity_edit_text);
        EditText unitPriceEditText = view.findViewById(R.id.unit_price_edit_text);
        EditText additionalWeightEditText = view.findViewById(R.id.additional_weight_edit_text);
        EditText withinPackageItemCount = view.findViewById(R.id.within_package_item_count_edit_text);
        ImageView quantityMicrophone = view.findViewById(R.id.quantity_microphone);
        ImageView unitPriceMicrophone = view.findViewById(R.id.unit_price_microphone);
        ImageView additionalWeightMicrophone = view.findViewById(R.id.additional_weight_microphone);
        ImageView withinPackageItemCountMicrophone = view.findViewById(R.id.within_package_item_count_microphone);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (!isChecked) {
                    ifQuantityIsIndividualPackageBased[0] = true;
                    // The toggle is enabled
                    additional_weight_cl.setVisibility(View.VISIBLE);
                    within_package_item_count_cl.setVisibility(View.VISIBLE);
                    unitPriceEditText.setHint(getString(R.string.price_per_package));
                } else {
                    // The toggle is disabled
                    ifQuantityIsIndividualPackageBased[0] = false;
                    additional_weight_cl.setVisibility(View.GONE);
                    within_package_item_count_cl.setVisibility(View.GONE);
                    unitPriceEditText.setHint(getString(R.string.price_per_pound));
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
                    if (result.matches(".*[a-z].*")) {
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
                            Toast.makeText(ShoppingListUserItemsActivity.this,getString(R.string.add_proper_unit_of_weight_after_numeric_value), Toast.LENGTH_SHORT).show();
                            justAlpha = "";
                        }
                        result = result.replaceAll("[a-z]", "");
                        try {
                            Double.parseDouble(result);
                        }
                        catch(Exception e){
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                        }
                        additionalWeightEditText.setText(result + " " + justAlpha);
                    } else {
                        additionalWeightEditText.setText(result);
                    }
                }
                catch (Exception e){
                    System.out.println("EXCEPTION: " );
                    e.printStackTrace();
                    additionalWeightMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                System.out.println("@onPartialResults - additionalWeight");
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - additionalWeight: " + result + "english version: " + EnglishWordsToNumbers.replaceNumbers(result));
                    if (result.matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = "";
                        if(!result.contains("fl oz")) {
                            justAlpha = removeNumberRelated(result).split(" ")[0]; // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                        }
                        else{
                            justAlpha = "fl oz";
                        } if (!isMeasurementUnit(justAlpha)){
                            Toast.makeText(ShoppingListUserItemsActivity.this,getString(R.string.add_proper_unit_of_weight_after_numeric_value), Toast.LENGTH_SHORT).show();
                            justAlpha = "";
                        }
                        result = result.replaceAll("[a-z]", "");
                        try {
                            Double.parseDouble(result);
                        }
                        catch(Exception e){
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                        }
                        additionalWeightEditText.setText(result + " " + justAlpha);
                    } else {
                        additionalWeightEditText.setText(result);
                    }
                }
                catch (Exception e){
                }

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    String ogResult = result;
                    System.out.println("@onResults - additionalWeight: " + result + "english version: " + EnglishWordsToNumbers.replaceNumbers(result));
                    if (result.matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + result);
                        String justAlpha = "";
                        if(!result.contains("fl oz")) {
                            justAlpha = removeNumberRelated(result).split(" ")[0]; // making sure if they are saying multple things after numeric value like 3.99 pounds pounds
                        }
                        else{
                            justAlpha = "fl oz";
                        }if (!isMeasurementUnit(justAlpha)){
                            Toast.makeText(ShoppingListUserItemsActivity.this,getString(R.string.add_proper_unit_of_weight_after_numeric_value), Toast.LENGTH_SHORT).show();
                            justAlpha = "";
                        }
                        result = result.replaceAll("[a-z]", "");
                        try {
                            Double.parseDouble(result);
                        }
                        catch(Exception e){
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                        }
                        additionalWeightEditText.setText(result + " " + justAlpha);
                    } else {
                        additionalWeightEditText.setText(result);
                    }
                }
                catch (Exception e){
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
                                    System.out.println("A_Z CONTIANING ONEc234325315131351353: " + result);
                                    System.out.println("JUST ALPHA: " + justAlpha);
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    if (!isMeasurementUnit(justAlpha)){
                                        Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.add_proper_unit_of_weight_after_numeric_value_2), result), Toast.LENGTH_SHORT).show();
                                        justAlpha = "";
                                        System.out.println("JUST ALPHA 2: " + result);
                                    }
                                    System.out.println("Wo A_Z CONTIANING ONE: " + result);
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
                                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    quantityEditText.setText(result + " " + justAlpha);
                                } else {
                                    quantityEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                System.out.println("QUANITY EXCEPTION: " + e.toString());
                                quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in lik e'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            System.out.println("REPLACED PFJDAOFDA:  " + replaceNumbers);
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.please_only_stand_alone_values_for_individuals), Toast.LENGTH_SHORT ).show();
                            }
                            quantityEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.quantity_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            quantityEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
                    System.out.println("QUANITY EXCEPTION: " + e.toString());
                    quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                        } if(!ifQuantityIsIndividualPackageBased[0]) {
                            try {
                                if (result.matches(".*[a-z].*")) {
                                    System.out.println("A_Z CONTIANING ONEc234325315131351353: " + result);
                                    System.out.println("JUST ALPHA: " + justAlpha);
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    if (!isMeasurementUnit(justAlpha)){
                                        Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.add_proper_unit_of_weight_after_numeric_value_2), result), Toast.LENGTH_SHORT).show();
                                        justAlpha = "";
                                        System.out.println("JUST ALPHA 2: " + result);
                                    }
                                    System.out.println("Wo A_Z CONTIANING ONE: " + result);
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
                                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    quantityEditText.setText(result + " " + justAlpha);
                                } else {
                                    quantityEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                System.out.println("QUANITY EXCEPTION: " + e.toString());
                                quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in lik e'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            System.out.println("REPLACED PFJDAOFDA:  " + replaceNumbers);
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.please_only_stand_alone_values_for_individuals), Toast.LENGTH_SHORT ).show();
                            }
                            quantityEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.quantity_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            quantityEditText.setText(result);
                        }

                    }
                }


                catch (Exception e){
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
                        }if(!ifQuantityIsIndividualPackageBased[0]) {
                            try {
                                if (result.matches(".*[a-z].*")) {
                                    System.out.println("A_Z CONTIANING ONEc234325315131351353: " + result);
                                    System.out.println("JUST ALPHA: " + justAlpha);
                                    result = convertWithEnglishWordsToNumbers(result).replaceAll("[a-z]", "");
                                    if (!isMeasurementUnit(justAlpha)){
                                        Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.add_proper_unit_of_weight_after_numeric_value_2), result), Toast.LENGTH_SHORT).show();
                                        justAlpha = "";
                                        System.out.println("JUST ALPHA 2: " + result);
                                    }
                                    System.out.println("Wo A_Z CONTIANING ONE: " + result);
                                    try {
                                        Double.parseDouble(result);
                                    }
                                    catch(Exception e){
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
                                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    quantityEditText.setText(result + " " + justAlpha);
                                } else {
                                    quantityEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                System.out.println("QUANITY EXCEPTION: " + e.toString());
                                quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                            String replaceNumbers = convertWithEnglishWordsToNumbers( result); // removing and converting any numeric values that were written in lik e'three' and 'five' and if there are still alphanumeric values, telling the user that they should only put stand only values in individual pricing setting
                            System.out.println("REPLACED PFJDAOFDA:  " + replaceNumbers);
                            String justAlpha2 = replaceNumbers.toLowerCase().replaceAll("[^a-z]", "");
                            if(!justAlpha2.equals("")) {
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.please_only_stand_alone_values_for_individuals), Toast.LENGTH_SHORT ).show();
                            }
                            quantityEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.quantity_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            quantityEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
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
                    String ogResult = result;
                    if (result.matches(".*[a-z].*")) {
                        String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                        System.out.println("REPLACE NUMBERS : " + replaceNumbers);
                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                            result = replaceNumbers;
                            unitPriceEditText.setText(result);
                        }else  {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT).show();
                            unitPriceEditText.setText(result.replaceAll(".*[a-z].*",""));

                        }
                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.unit_price_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            unitPriceEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
                    unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    if (result.matches(".*[a-z].*")) {
                        String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                            result = replaceNumbers;
                            unitPriceEditText.setText(result);
                        }else  {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT).show();
                            unitPriceEditText.setText(result.replaceAll(".*[a-z].*",""));

                        }
                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.unit_price_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            unitPriceEditText.setText(result);
                        }


                    }
                }
                catch (Exception e){

                }

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(unitPriceSpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    String result = replaceAllCommonIssues(data.get(0));
                    if (result.matches(".*[a-z].*")) {
                        String replaceNumbers = EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                            result = replaceNumbers;
                            unitPriceEditText.setText(result);
                        }else  {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT).show();
                            unitPriceEditText.setText(result.replaceAll(".*[a-z].*",""));

                        }
                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.unit_price_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            unitPriceEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){

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
                                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    withinPackageItemCountEditText.setText(result + " " + justAlpha);
                                } else {
                                    withinPackageItemCountEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.please_only_stand_alone_values_for_individuals), Toast.LENGTH_SHORT ).show();
                            }
                            withinPackageItemCountEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.quantity_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            withinPackageItemCountEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
                    withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    withinPackageItemCountEditText.setText(result + " " + justAlpha);
                                } else {
                                    withinPackageItemCountEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.please_only_stand_alone_values_for_individuals), Toast.LENGTH_SHORT ).show();
                            }
                            withinPackageItemCountEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.quantity_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            withinPackageItemCountEditText.setText(result);
                        }

                    }
                }


                catch (Exception e){
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
                                        String replaceNumbers =EnglishWordsToNumbers.replaceNumbers(result.replaceAll(" ", ""));
                                        if(!(Double.parseDouble(replaceNumbers) == 0.0)) {
                                            result = replaceNumbers;
                                        } else { // the only exception it will be catching is a double parsing exception
                                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), ogResult), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    withinPackageItemCountEditText.setText(result + " " + justAlpha);
                                } else {
                                    withinPackageItemCountEditText.setText(result);
                                }
                            }
                            catch (Exception e){
                                withinPackageItemCountMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_proper_input_detected), Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.please_only_stand_alone_values_for_individuals), Toast.LENGTH_SHORT ).show();
                            }
                            withinPackageItemCountEditText.setText(replaceNumbers.replaceAll("[a-z]", ""));
                        }

                    } else {
                        if(Double.parseDouble(result) <= 0){
                            Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.quantity_val_have_to_be_greater_than_0), Toast.LENGTH_SHORT).show();
                        } else {
                            withinPackageItemCountEditText.setText(result);
                        }

                    }
                }
                catch (Exception e){
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
                        quantityToPass = "not filled";
                    }
                    if (withinPackageItemCountToPass.isEmpty()) {
                        withinPackageItemCountToPass = "not filled";
                    }
                    if (unitPriceToPass.isEmpty()) {
                        unitPriceToPass = "not filled";
                    }
                    if (additionalWeightToPass.isEmpty()) {
                        additionalWeightToPass = null;
                    }
                    Long date = System.currentTimeMillis();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String dateStr = dateFormat.format(date);
                    if (!ifStoreProvided) {
                        System.out.println("IF PROVIDED: " + ifStoreProvided);
                        ArrayList<Store> storesForChosing = null;
                        try {
                            if (Build.VERSION.SDK_INT >= O) {
                                storesForChosing = QueryUtils.getStores();
                            }
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleStoreListAdapter simpleStoreListAdapter = new SimpleStoreListAdapter(ShoppingListUserItemsActivity.this, storesForChosing);
                        choseStoreListView.setAdapter(simpleStoreListAdapter);
                        choseStoreTextView.setText(String.format(getString(R.string.chose_a_store), shoppingListUserItemName));
                        choseStoreTextView.setVisibility(View.VISIBLE);
                        choseStoreListView.setVisibility(View.VISIBLE);
                        view.setVisibility(View.VISIBLE);
                        System.out.println("CALLED 2");

                        String finalAdditionalWeightToPass = additionalWeightToPass;
                        choseStoreListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                View selectedStoreView = simpleStoreListAdapter.getView(i, view, adapterView);
                                Store selectedStore = simpleStoreListAdapter.getItem(i);
                                try {

                                    if(finalAdditionalWeightToPass != null && !containsMeasurementUnit(finalAdditionalWeightToPass)){
                                        Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.add_weight_ms), Toast.LENGTH_LONG).show();

                                    }else{
                                        System.out.println("CALLED CHOSE STORE LIST VIEW QUANTITY IS INDIVIDUAL + FINAL WEIGHT: " + finalAdditionalWeightToPass);
                                        QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, selectedStore.getStoreName(), dateStr,
                                                quantityEditText.getText().toString(), // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                                unitPriceEditText.getText().toString(), "ea", finalAdditionalWeightToPass.trim(), withinPackageItemCountEditText.getText().toString(), getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                                                .putString("jsonData",Constants.json_data_str.toString()).apply();
                                        alertDialog.dismiss();
                                    }
                                    shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);
                                    shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                                    shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);;

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                    else {
                        System.out.println("STORE FINAL PASS: " + Constants.storeBeingShoppedIn);
                        try {
                            String finalAdditionalWeightToPass = additionalWeightEditText.getText().toString();
                            if(!finalAdditionalWeightToPass.isEmpty() && !containsMeasurementUnit(finalAdditionalWeightToPass)){
                                Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.add_weight_ms), Toast.LENGTH_LONG).show();

                            }else{
                                System.out.println("CALLED LIST VIEW QUANTITY IS INDIVIDUAL");
                                QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, Constants.storeBeingShoppedIn, dateStr,
                                        quantityEditText.getText().toString(), // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                        unitPriceEditText.getText().toString(), "/ea", null, quantityEditText.getText().toString(), getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss

                                alertDialog.dismiss();
                            }

                            shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);

                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                            shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{

                    String quantityToPass = quantityEditText.getText().toString();
                    System.out.println("quantityToPass 2: " + quantityToPass);
                    String unitPriceToPass = unitPriceEditText.getText().toString();
                    if (quantityToPass.isEmpty()) {
                        quantityToPass = "not filled";
                    }
                    if (unitPriceToPass.isEmpty()) {
                        unitPriceToPass = "not filled";
                    }

                    Long date = System.currentTimeMillis();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String dateStr = dateFormat.format(date);
                    if (!ifStoreProvided) {
                        System.out.println("IF PROVIDED: " + ifStoreProvided);
                        ArrayList<Store> storesForChosing = null;
                        try {
                            if (Build.VERSION.SDK_INT >= O) {
                                storesForChosing = QueryUtils.getStores();
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
                                    String numberRelatedRemoved = removeNumberRelated(quantityEditText.getText().toString());
                                    if(numberRelatedRemoved.replaceAll(" ", "").isEmpty()) {
                                        Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.add_proper_unit_of_weight_after_numeric_value_2), quantityEditText.getText().toString()), Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        System.out.println("CALLED CHOSE STORE LIST VIEW QUANTITY IS NOT IND");
                                        QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, selectedStore.getStoreName(), dateStr,
                                                quantityEditText.getText().toString(), // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                                unitPriceEditText.getText().toString(), numberRelatedRemoved, null, null, getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                        alertDialog.dismiss();
                                    }
                                    shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);

                                    shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                                    shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                    else {
                        try {
                            String numberRelatedRemoved = removeNumberRelated(quantityEditText.getText().toString());
                            if(!isMeasurementUnit(numberRelatedRemoved.replaceAll(" ", ""))) {
                                Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.add_proper_unit_of_weight_after_numeric_value_2), quantityEditText.getText().toString()), Toast.LENGTH_SHORT).show();
                            }else{
                                System.out.println("CALLED NOT STORE VIEW QUANTITY IS NOT IND");
                                QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, Constants.storeBeingShoppedIn, dateStr,
                                        quantityEditText.getText().toString(), // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                        unitPriceEditText.getText().toString(), numberRelatedRemoved, null, null, getApplicationContext()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                alertDialog.dismiss();
                            }
                            shoppingListUserItems = QueryUtils.updateShoppingListUserItems(shoppingListUserItems, shoppingListName);

                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems,  shoppingListName);
                            shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }


            }
        });
        alertDialog.setCanceledOnTouchOutside(true);
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
                    resetAdapter();
                    shoppingListUserItemsListView.setSelectionFromTop(index, top);
                    System.out.println("SET ADAPTER: " + shoppingListUserItemAdapter.getCount());
                    actuallyNeedsToBeUpdated.setValue(false);
                }
            }
        };
        actuallyNeedsToBeUpdated.observeForever(updateObserver);
//        new Thread(
//            new Runnable() {
//                public void run() {
//                    System.out.println("ACTUALLYNEEDS TO BE UPDATED RUNNIn");
//                    if(actuallyNeedsToBeUpdated.getValue() != null) {
//                        if (actuallyNeedsToBeUpdated.getValue()) {
//                            System.out.println("ACTUALLYNEEDS TO BE UPDATED RUNNIn 2");
//                            try {
//                                shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } catch (ParseException e) {
//                                e.printStackTrace();
//                            }
//
//                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(ShoppingListUserItemsActivity.this, shoppingListUserItems, shoppingListName, shoppingListUserItemsListView);
//                        }
//                    }
//
//                }
//        }).start();
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
                                                  }
                                                  shoppingListUserItems = newItems;
                                                  //                                                  ArrayList<ShoppingListUserItem> newshoppingListUserItemList = new ArrayList<>();
//                                                  for(int i = 0; i < shoppingListUserItems.size(); i++){
//                                                      ShoppingListUserItem shoppingListUserItem =  shoppingListUserItems.get(i);
//                                                      try{
//                                                          if(!shoppingListUserItem.getName().substring(0,searchQueryLength).equalsIgnoreCase(query)){
//                                                              System.out.println("REMOVING " + shoppingListUserItem.getName() + " FOR " + query);
//                                                              shoppingListUserItems.remove(i);
//                                                          }
//                                                          else{
//
//                                                          }
//                                                      }
//                                                      catch (StringIndexOutOfBoundsException exception){
////                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
//                                                      }
//
//                                                  }
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
                                                  }
                                                  shoppingListUserItems = newItems;
                                                  System.out.println("NEW SHOPPING LIST USER ITEMS @onQuereyTExtChange: " + shoppingListUserItems);
                                                  //
//                                                  for(int i = 0; i < shoppingListUserItems.size(); i++){
//                                                      ShoppingListUserItem shoppingListUserItem =  shoppingListUserItems.get(i);
//                                                      try{
//                                                          if(!shoppingListUserItem.getName().substring(0,searchQueryLength).equalsIgnoreCase(query)){
//                                                              System.out.println("REMOVING " + shoppingListUserItem.getName() + " FOR " + query);
//
//                                                              shoppingListUserItems.remove(i);
//                                                          }
//                                                      }
//                                                      catch (StringIndexOutOfBoundsException exception){
////                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
//                                                      }
//
//                                                  }
                                                  // if user has deleted all their text
                                                  if (query.isEmpty()) {
                                                      resultsForshoppingListUserItemsView.setVisibility(View.GONE);
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
                            } else {
                                QueryUtils.setItemNotGreenTickMarked(name.getText().toString(), shoppingListName, getApplicationContext());


                            }
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                        ShoppingListUserItemsActivity.update();
                        return true;
                    }

                });
                selectedStoreView.setOnTouchListener(new OnSwipeTouchListener(ShoppingListUserItemsActivity.this) {
                    public void onSwipeLeft() {
                        System.out.println("SWIPED - LEFT");
                        try {
                            QueryUtils.setItemGreenTickMarked(shoppingListUserItemName, shoppingListName, getApplicationContext());
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        update();

                    }

                    public void onSwipeRight() {
                        System.out.println("SWIPED - RIGHT");
                        try {
                            QueryUtils.setItemGreenTickMarked(shoppingListUserItemName, shoppingListName, getApplicationContext());
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                        update();
                    }

                    public void onSwipeBottom() {

                    }

                    public void onSwipeTop() {

                    }

                    public void onDownTouch() {

                    }
                });
//                ImageView historyButton = (ImageView) selectedStoreView.findViewById(R.id.history_button_sl_item);
//                ImageView duplicateIndicator = (ImageView) selectedStoreView.findViewById(R.id.duplicate_indicator);
                ImageView microphoneButton = (ImageView) selectedStoreView.findViewById(R.id.record_details_button);
//                ImageView reorderButton = (ImageView) selectedStoreView.findViewById(R.id.reorder_item_button);
//                ImageView deleteButton = (ImageView) selectedStoreView.findViewById(R.id.delete_item_button);
//                ImageView increaseQuantityButton = (ImageView) selectedStoreView.findViewById(R.id.quantity_add_button);
//                ImageView decreasedQuantityButton = (ImageView) selectedStoreView.findViewById(R.id.quantity_minus_button);
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