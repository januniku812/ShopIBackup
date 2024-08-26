package com.shopi.android.receiptreader;
import com.shopi.android.receiptreader.MainActivity;


import android.content.Intent;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.Editable;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import android.content.Context;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class QueryUtils  {
    public static String strip(String string){
        return string.replaceAll("\\s+", " ");
    }
    @RequiresApi
    public static ArrayList<ShoppingListUserItem>  updateShoppingListUserItems(ArrayList<ShoppingListUserItem> shoppingListUserItems, String shoppingListName) throws ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        System.out.println("CALLED AT @updateShoppingListUserItems");
        ArrayList<ShoppingListUserItem> shoppingListUserItemsToReturn = new ArrayList<>(); // initializing array list to return

        // finding the shopping list data we are dealing with
        JSONObject jsonObject = (JSONObject) object;
        JSONArray shopping_list_user_items = null;
        JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
        for(int i = 0; i < shopping_lists.size(); i++){
            JSONObject shopping_list = (JSONObject) shopping_lists.get(i);
            System.out.println("SHOPPING LIST NAME i: " + shopping_list.get("shopping_list_name") + " OUR SHOPPINF LIST NAME : " + shoppingListName);
            if(shopping_list.get("shopping_list_name").toString().equalsIgnoreCase(shoppingListName)){
                System.out.println("REACHED");
                try {
                    shopping_list_user_items = (JSONArray) shopping_list.get("shopping_list_user_items");
                } catch(Exception e){
                    shopping_list_user_items = (JSONArray) new JSONParser().parse(String.valueOf(shopping_list.get("shopping_list_user_items")));
                }
            }
        }
        System.out.println("SHOPING LIST USER ITEMS ASFJASKJSHAKAJHAKJHA: " + shopping_list_user_items);
        for(ShoppingListUserItem shoppingListUserItem: shoppingListUserItems){
            try{
                // parse through its data in the shopping list json array
                for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                    JSONObject userItemObject = (JSONObject) shopping_list_user_items.get(i2);
                    System.out.println("USer ITEM OBJ: " + userItemObject);
                    ShoppingListUserItem userItemToAdd = new ShoppingListUserItem();
                    if(strip(userItemObject.get("shopping_list_item_name").toString()).equalsIgnoreCase(strip(shoppingListUserItem.getName()))) { // getting data only for
                        String userItemName = (String) userItemObject.get("shopping_list_item_name");
                        String userItemDate = (String) userItemObject.get("shopping_list_item_last_bought");
                        String userItemQuantity = (String) userItemObject.get("shopping_list_item_quantity");
                        boolean ifGreenTickMarked = false;
                        try {
                            ifGreenTickMarked = Boolean.parseBoolean(userItemObject.get("if_green_tick_marked").toString());
                        } catch(Exception e){
                            ifGreenTickMarked = false;
                        }
                        if(userItemObject.get("shopping_list_item_quantity") == null){
                            userItemQuantity = "";
                        }
                        boolean ifSavedForLater = false;
                        try {
                            ifSavedForLater = Boolean.parseBoolean(userItemObject.get("if_saved_for_later").toString());
                        } catch(Exception e){
                            ifSavedForLater = false;
                        }
                        userItemToAdd = new ShoppingListUserItem(userItemName, userItemDate, userItemQuantity, ifGreenTickMarked,ifSavedForLater);
                        System.out.println("USER ITEM TO ADD: " + userItemName + userItemDate + userItemQuantity);
                        shoppingListUserItemsToReturn.add(userItemToAdd);
                    }
                }

            }catch(Exception e){
                System.out.println("EXCEPTION OCCURED IN @updateShoppingListUserITems");
                e.printStackTrace();
            }
        }

        if (shoppingListUserItemsToReturn.size() > 0) {
            Collections.sort(shoppingListUserItemsToReturn, new Comparator<ShoppingListUserItem>() {
                @Override
                public int compare(final ShoppingListUserItem object1, final ShoppingListUserItem object2) {
                    return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
                }
            });
        }

        ArrayList<ShoppingListUserItem> shoppingListUserItemArrayListCopy = (ArrayList<ShoppingListUserItem>) shoppingListUserItemsToReturn.clone();
        for(ShoppingListUserItem shoppingListUserItem: shoppingListUserItemsToReturn){
            if(shoppingListUserItem.getIfSavedForLater()){
                shoppingListUserItemArrayListCopy.remove(shoppingListUserItemArrayListCopy.indexOf(shoppingListUserItem));
                shoppingListUserItemArrayListCopy.add(0, shoppingListUserItem);
            }
        }
        return shoppingListUserItemArrayListCopy;
    }

    public static Double round(String additionalWeightUnitPriceDetail, String ogMeasurementUnit, String currentMeasureUnit){
        Double ogActualPrice = Double.parseDouble(additionalWeightUnitPriceDetail.replaceAll("[^\\d.]",""));
        Double actualPrice = ogActualPrice / (ItemMeasurementUnits.findRatioBetweenOgMeasurementUnitAndConversionOutcomeUnit(ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(ogMeasurementUnit), ItemMeasurementUnits.returnItemMeasurementUnitClassVarForPriceComparisonUnit(currentMeasureUnit)));
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
                if(actualPrice != 0.0){
                    return actualPrice;
                }
            }

        }
        else{
            return 0.0;
        }
        return 0.0;
    }

    public static Double roundToFirstNonzero(Double ogActualPrice){
        DecimalFormat f = new DecimalFormat("##.00");
        Double actualPrice = Double.parseDouble(f.format(ogActualPrice));
        if(actualPrice != 0.0){
            return actualPrice;
        }
        Integer sigFigs = 2;
        System.out.println("OG ACTUAL PRICE: " + ogActualPrice);
        if (ogActualPrice != 0) {
            while(actualPrice == 0.0){
                sigFigs++;
                StringBuilder formatExpression = new StringBuilder("##.");
                for(int i = 0; i < sigFigs; i++) {
                    formatExpression.append("0");
                }
                DecimalFormat newFormatter = new DecimalFormat(formatExpression.toString());
                actualPrice = Double.parseDouble(newFormatter.format(ogActualPrice));
                System.out.println("ACTUAL PRICE NOW: " + actualPrice);
                if(actualPrice - 0.0 != 0.0){
                    return actualPrice;
                }
            }

        }
        else{
            return 0.0;
        }
        return 0.0;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void saveDetailsOfShoppingListUserItem(String shoppingListUserItemName, String storeName, String date, String quantity, String unitPrice, String measurementUnit, String additionalWeight, String withinPackageItemCount, android.content.Context context) throws ParseException {
        System.out.println("IVE BEEN ACCESSED - saveDetailsOfShoppingListUserItem func");
        System.out.println("ORIGINAL NAME PARAMETER saveDetailsOfShoppingListUserItem func @QueryUtils: " + storeName);
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData);
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < stores.size(); i++){
                JSONObject storeObject = (JSONObject) stores.get(i);
                String store_name = (String) storeObject.get("store_name");
                JSONArray store_user_items = (JSONArray) jsonParser.parse(String.valueOf(storeObject.get("store_user_items")));
                System.out.println("STORE NAME: " + store_name);
                if(store_name.equalsIgnoreCase(storeName)){
                    System.out.println("STORE NAME 2: " + store_name);
                    JSONObject storeUserItemToAdd = new JSONObject();
                    Double total = 0.0;
                    storeUserItemToAdd.put("user_item_store", storeName);
                    storeUserItemToAdd.put("user_item_date", date);
                    storeUserItemToAdd.put("user_item_name", shoppingListUserItemName);
                    storeUserItemToAdd.put("user_item_quantity", quantity);
                    if(withinPackageItemCount != null) {
                        storeUserItemToAdd.put("user_item_within_package_item_count", withinPackageItemCount);
                    } else{
                        storeUserItemToAdd.put("user_item_within_package_item_count", "1");
                    }
                    DecimalFormat df = new DecimalFormat("#.##");
                    if(!quantity.equals("not filled") && !unitPrice.equals("not filled")){
                        try {
                            int parsedInt = Integer.parseInt(quantity) * Integer.parseInt(unitPrice);
                            total = Double.parseDouble(String.valueOf(parsedInt));
                            storeUserItemToAdd.put("user_item_total_amount_paid", String.valueOf(parsedInt));
                        } catch(NumberFormatException e){
                            total = Double.parseDouble(quantity.replaceAll("[a-zA-Z]" , "")) * Double.parseDouble(unitPrice);
                            storeUserItemToAdd.put("user_item_total_amount_paid", String.valueOf(df.format(total)));

                        }
                    } else{
                        storeUserItemToAdd.put("user_item_total_amount_paid", "not enough info given");
                    }
                    storeUserItemToAdd.put("user_item_unit_price", unitPrice + "/" + measurementUnit);
                    if(withinPackageItemCount == null && additionalWeight == null){
                        storeUserItemToAdd.put("user_item_additional_weight_pricing_detail", unitPrice + "/" + quantity.replaceAll("[^a-zA-Z\\s]","").trim());
                    }
                    if(additionalWeight != null && !additionalWeight.isEmpty()){
                        double ogActualPrice = Double.parseDouble(unitPrice) / Double.parseDouble(additionalWeight.replaceAll("[a-zA-Z]", ""));
                        storeUserItemToAdd.put("user_item_additional_weight_pricing_detail",
                                String.valueOf(roundToFirstNonzero(ogActualPrice)
                                        + "/" + additionalWeight.replaceAll("[^a-zA-Z\\s]","").trim()));
                        System.out.println("ADDITIONAL WEIGHT DETAIL: " + additionalWeight);
                        System.out.println("ROUNDED TO FIRST NONZERO: " + String.valueOf(roundToFirstNonzero(ogActualPrice)));
                    }
                    storeUserItemToAdd.put("id", System.currentTimeMillis());

                    System.out.println("STORE USER ITEM TO ADD @saveDetailsOfShoppingListUserItem: " + storeUserItemToAdd.toJSONString());
                    store_user_items.add(storeUserItemToAdd);
                    storeObject.replace("store_user_items", store_user_items);
                    System.out.println("STORE USER ITEMS @saveDetailsOfShoppingListUserItem: " + store_user_items);

                }
            }
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",Constants.json_data_str.toString()).apply();
            System.out.println("JSON DATA STR @saveDetailsOfShoppingListUserItem: " + Constants.json_data_str);

        }catch(Exception e){
            System.out.println("EXCEPTION @saveDetailsOfShoppingListUserItem:");
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setItemGreenTickMarked(String shoppingListUserItemName, String shoppingList, Context context) throws ParseException {
        System.out.println("IVE BEEN ACCESSED - setItemGreenTickMarked func");
        System.out.println("ORIGINAL NAME PARAMETER setItemGreenTickMarked func @QueryUtils: " + shoppingList);
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject shoppingListObject = (JSONObject) shopping_lists.get(i);
                String shopping_list_name = (String) shoppingListObject.get("shopping_list_name");
                JSONArray store_user_items = (JSONArray) jsonParser.parse(String.valueOf(shoppingListObject.get("store_user_items")));
                System.out.println("SHOPPING LIST NAME: " + shopping_list_name);
                if(shopping_list_name.equalsIgnoreCase(shoppingList)){
                    JSONArray shopping_list_user_items = null;
                    try {
                        shopping_list_user_items = (JSONArray) shoppingListObject.get("shopping_list_user_items");
                    } catch (Exception e){
                        shopping_list_user_items = (JSONArray) jsonParser.parse(shoppingListObject.get("shopping_list_user_items").toString());
                    }
                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                        if(strip(shopping_list_user_item.get("shopping_list_item_name").toString()).equalsIgnoreCase(shoppingListUserItemName)){
                            shopping_list_user_item.put("if_green_tick_marked", "true");
                            Constants.json_data_str = jsonObject.toJSONString();
                        }
                    }
                }
            }
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",Constants.json_data_str.toString()).apply();
            System.out.println("JSON DATA STR @saveDetailsOfShoppingListUserItem: " + Constants.json_data_str);

        }catch(Exception e){
            System.out.println("EXCEPTION @saveDetailsOfShoppingListUserItem:");
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setItemNotGreenTickMarked(String shoppingListUserItemName, String shoppingList, Context context) throws ParseException {
        System.out.println("IVE BEEN ACCESSED - setItemGreenTickMarked func");
        System.out.println("ORIGINAL NAME PARAMETER setItemGreenTickMarked func @QueryUtils: " + shoppingList);
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject shoppingListObject = (JSONObject) shopping_lists.get(i);
                String shopping_list_name = (String) shoppingListObject.get("shopping_list_name");
                JSONArray store_user_items = (JSONArray) jsonParser.parse(String.valueOf(shoppingListObject.get("store_user_items")));
                System.out.println("SHOPPING LIST NAME: " + shopping_list_name);
                if(shopping_list_name.equalsIgnoreCase(shoppingList)){
                    JSONArray shopping_list_user_items = null;
                    try {
                        shopping_list_user_items = (JSONArray) shoppingListObject.get("shopping_list_user_items");
                    } catch (Exception e){
                        shopping_list_user_items = (JSONArray) jsonParser.parse(shoppingListObject.get("shopping_list_user_items").toString());
                    }
                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                        if(strip(shopping_list_user_item.get("shopping_list_item_name").toString()).equalsIgnoreCase(shoppingListUserItemName)){
                            shopping_list_user_item.replace("if_green_tick_marked", "false");
                            Constants.json_data_str = jsonObject.toJSONString();
                        }
                    }
                }
            }
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",Constants.json_data_str.toString()).apply();
            System.out.println("JSON DATA STR @saveDetailsOfShoppingListUserItem: " + Constants.json_data_str);

        }catch(Exception e){
            System.out.println("EXCEPTION @saveDetailsOfShoppingListUserItem:");
            e.printStackTrace();
        }
    }

    public static boolean ifShoppingListAlreadyExists(String shoppingListName) throws ParseException {
        System.out.println("IVE BEEN ACCESSED - editStoreName func");
        System.out.println("ORIGINAL NAME PARAMETER ifShoppingListAlreadyExists func @QueryUtils: " + shoppingListName);
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shoppingLists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shoppingLists.size(); i++){
                JSONObject shoppingList = (JSONObject) shoppingLists.get(i);
                String shopping_list_name = (String) shoppingList.get("shopping_list_name");
                System.out.println("STORE NAME: " + shopping_list_name);
                if(strip(shopping_list_name).trim().equalsIgnoreCase(strip(shoppingListName).trim())){
                    System.out.println("IVE BEEN ACCESSED - ifShoppingListAlreadyExists: " + shoppingListName);
                    return true;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean ifStoreAlreadyExists(String storeName) throws ParseException {
        System.out.println("IVE BEEN ACCESSED - editStoreName func");
        System.out.println("ORIGINAL NAME PARAMETER ifStoreAlreadyExists func @QueryUtils: " + storeName);
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < stores.size(); i++){
                JSONObject storeObject = (JSONObject) stores.get(i);
                String store_name = (String) storeObject.get("store_name");
                System.out.println("STORE NAME: " + store_name);
                if(store_name.replaceAll(" ", "").equalsIgnoreCase(storeName.replaceAll(" ", ""))){
                    System.out.println("IVE BEEN ACCESSED - ifStoreAlreadyExists: " + store_name);
                    return true;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void reorderShoppingListItem(String originalShoppingList, String shoppingListToMoveTo, String shoppingListItemName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        JSONObject shoppingListItemObj = new JSONObject();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shoppingLists = (JSONArray) jsonObject.get("shopping_lists");
            // getting item from original shopping list
            for(int i = 0; i < shoppingLists.size(); i++){
                System.out.println("MADE IT @ogSl : " + originalShoppingList);
                JSONObject shoppingList = (JSONObject) shoppingLists.get(i);
                if(shoppingList.get("shopping_list_name").toString().equalsIgnoreCase(originalShoppingList)){
                    System.out.println("MADE IT @ogSl 2 : " + shoppingListItemName);
                    JSONArray shopping_list_user_items = (JSONArray) shoppingList.get("shopping_list_user_items");
                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                        if(shopping_list_user_item.get("shopping_list_item_name").toString().equals(shoppingListItemName)){
                            shoppingListItemObj = shopping_list_user_item;
                            shopping_list_user_items.remove(shopping_list_user_item);
                            Constants.json_data_str = jsonObject.toJSONString();
                        }
                    }
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            // finding shopping list to move it and placing it there
            QueryUtils.addShoppingListItemWithQuantity(shoppingListToMoveTo, shoppingListItemName, shoppingListItemObj.get("shopping_list_item_last_bought").toString(), shoppingListItemObj.get("shopping_list_item_quantity").toString(), context);
            // update json data string value
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void increaseShoppingListItemQuantity(String shoppingListName, String shoppingListItemName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        ArrayList<ShoppingList> shoppingListArray = new ArrayList<>();
        StoreUserItem userItem = new StoreUserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shoppingLists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shoppingLists.size(); i++){
                System.out.println("MADE IT @increaseShoppingListItemQuantity : " + shoppingListName);
                JSONObject shoppingList = (JSONObject) shoppingLists.get(i);
                if(shoppingList.get("shopping_list_name").toString().equalsIgnoreCase(shoppingListName)){
                    System.out.println("MADE IT @increaseShoppingListItemQuantity 2 : " + shoppingListItemName);
                    JSONArray shopping_list_user_items = (JSONArray) shoppingList.get("shopping_list_user_items");

                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONArray jsonArray = (JSONArray) shoppingList.get("shopping_list_user_items");
                        JSONObject item2 = (JSONObject) jsonArray.get(i2);
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                        try {
                            if (strip(item2.get("shopping_list_item_name").toString().toLowerCase()).equals(strip(shoppingListItemName.toLowerCase()))) {
                                System.out.println("RUNNING FOR 2 DUP:" + shoppingListItemName);
                                Integer quantityToPut = Integer.parseInt(shopping_list_user_item.get("shopping_list_item_quantity").toString()) + 1;
                                System.out.println("BEFORE: " + shopping_list_user_item);
                                shopping_list_user_item.replace("shopping_list_item_quantity", quantityToPut.toString());
                                PreferenceManager.getDefaultSharedPreferences(context).edit()
                                        .putString("jsonData",jsonObject.toJSONString()).apply();
                                System.out.println("AFTER: " + shopping_list_user_item);
                                System.out.println("I REACHED @addNewStore 3: " + jsonObject);
                                Constants.json_data_str = jsonObject.toJSONString();
                            }
                        } catch(Exception e){
                            if(strip(shopping_list_user_item.get("shopping_list_item_name").toString().toLowerCase()).equals(shoppingListItemName.toLowerCase())){
                                System.out.println("RUNNING FOR 2 DUP:" + shoppingListItemName);
                                Integer quantityToPut = Integer.parseInt(shopping_list_user_item.get("shopping_list_item_quantity").toString()) + 1;
                                System.out.println("BEFORE: " + shopping_list_user_item);
                                shopping_list_user_item.replace("shopping_list_item_quantity", quantityToPut.toString());
                                PreferenceManager.getDefaultSharedPreferences(context).edit()
                                        .putString("jsonData",jsonObject.toJSONString()).apply();
                                System.out.println("AFTER: " + shopping_list_user_item);
                                System.out.println("I REACHED @addNewStore 3: " + jsonObject);
                                Constants.json_data_str = jsonObject.toJSONString();
                            }
                        }
                    }
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            // update json data string value
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void decreaseShoppingListItemQuantity(String shoppingListName, String shoppingListItemName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        ArrayList<ShoppingList> shoppingListArray = new ArrayList<>();
        StoreUserItem userItem = new StoreUserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shoppingLists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shoppingLists.size(); i++){
                System.out.println("MADE IT @addShoppingListItem : " + shoppingListItemName);
                JSONObject shoppingList = (JSONObject) shoppingLists.get(i);
                if(shoppingList.get("shopping_list_name").toString().equalsIgnoreCase(shoppingListName)){
                    System.out.println("MADE IT @addShoppingListItem : " + shoppingListItemName);
                    JSONArray shopping_list_user_items = (JSONArray) shoppingList.get("shopping_list_user_items");
                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                        if(strip(shopping_list_user_item.get("shopping_list_item_name").toString()).equalsIgnoreCase(shoppingListItemName)){
                            Integer quantityToPut = Integer.parseInt(shopping_list_user_item.get("shopping_list_item_quantity").toString()) - 1;
                            System.out.println("BEFORE: " + shopping_list_user_item);
                            shopping_list_user_item.replace("shopping_list_item_quantity", quantityToPut.toString());
                            System.out.println("AFTER: " + shopping_list_user_item);
                            System.out.println("I REACHED @addNewStore 3: " + jsonObject);
                            Constants.json_data_str = jsonObject.toJSONString();
                        }
                    }
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            // update json data string value
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean addShoppingListItem(String shoppingListName, String shoppingListItemName, String lastBoughtDate, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        ArrayList<ShoppingList> shoppingListArray = new ArrayList<>();
        StoreUserItem userItem = new StoreUserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            System.out.println("jsonObject @addshoppingListUserItem: " + jsonObject);
            JSONArray shoppingLists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shoppingLists.size(); i++){
                System.out.println("MADE IT @addShoppingListItem : " + shoppingListItemName);
                JSONObject shoppingList = (JSONObject) shoppingLists.get(i);
                if(shoppingList.get("shopping_list_name").toString().equalsIgnoreCase(shoppingListName)){
                    System.out.println("MADE IT @addShoppingListItem : " + shoppingListItemName);
                    JSONArray shopping_list_user_items_og = null;
                    try {
                         shopping_list_user_items_og = (JSONArray) shoppingList.get("shopping_list_user_items");
                    }
                    catch(Exception e){
                        System.out.println("EXCEPTION occured!!");
                        shopping_list_user_items_og = (JSONArray) jsonParser.parse(shoppingList.get("shopping_list_user_items").toString());

                    }
                    JSONArray shopping_list_user_items = (JSONArray) jsonParser.parse(shoppingList.get("shopping_list_user_items").toString());
                    for(int i2 = 0; i2 < shopping_list_user_items_og.size(); i2++){
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items_og.get(i2);
                        System.out.println("LOGGINGS");
                        System.out.println(strip(shopping_list_user_item.get("shopping_list_item_name").toString().toLowerCase()) + ": " + (shoppingListItemName.toLowerCase()));
                        JSONArray jsonArray = (JSONArray) shoppingList.get("shopping_list_user_items");
                        JSONObject item2 = (JSONObject) jsonArray.get(i2);
                        try {
                            if (strip(item2.get("shopping_list_item_name").toString().toLowerCase()).equals(strip(shoppingListItemName.toLowerCase()))) {
                                System.out.println("RUNNING FOR 2 DUP:" + shoppingListItemName);
                                Integer quantityToPut = Integer.parseInt(shopping_list_user_item.get("shopping_list_item_quantity").toString()) + 1;
                                System.out.println("BEFORE: " + shopping_list_user_item);
                                shopping_list_user_item.replace("shopping_list_item_quantity", quantityToPut.toString());
                                PreferenceManager.getDefaultSharedPreferences(context).edit()
                                        .putString("jsonData",jsonObject.toJSONString()).apply();
                                System.out.println("AFTER: " + shopping_list_user_item);
                                System.out.println("I REACHED @addNewStore 3: " + jsonObject);
                                Constants.json_data_str = jsonObject.toJSONString();
                                return false;
                            }
                        } catch(Exception e){
                            if(strip(shopping_list_user_item.get("shopping_list_item_name").toString().toLowerCase()).equals(shoppingListItemName.toLowerCase())){
                                System.out.println("RUNNING FOR 2 DUP:" + shoppingListItemName);
                                Integer quantityToPut = Integer.parseInt(shopping_list_user_item.get("shopping_list_item_quantity").toString()) + 1;
                                System.out.println("BEFORE: " + shopping_list_user_item);
                                shopping_list_user_item.replace("shopping_list_item_quantity", quantityToPut.toString());
                                PreferenceManager.getDefaultSharedPreferences(context).edit()
                                        .putString("jsonData",jsonObject.toJSONString()).apply();
                                System.out.println("AFTER: " + shopping_list_user_item);
                                System.out.println("I REACHED @addNewStore 3: " + jsonObject);
                                Constants.json_data_str = jsonObject.toJSONString();
                                return false;
                            }
                        }
                    }
                    System.out.println("RUNNING FOR 2 NOT DUP: " + shoppingListItemName);
                    JSONObject shoppingListUserItemToAdd = new JSONObject();
                    shoppingListUserItemToAdd.put("shopping_list_item_name", shoppingListItemName);
                    if(lastBoughtDate != null){
                        shoppingListUserItemToAdd.put("shopping_list_item_last_bought", lastBoughtDate);
                    } else{
                        shoppingListUserItemToAdd.put("shopping_list_item_last_bought", "");
                    }
                    shoppingListUserItemToAdd.put("shopping_list_item_quantity", "1");
                    shoppingListUserItemToAdd.put("if_green_tick_marked", "false");
                    shopping_list_user_items.add(shoppingListUserItemToAdd);
                    System.out.println("I REACHED @addNewStore 2445 : " + shopping_list_user_items);
                    shoppingList.replace("shopping_list_user_items",shopping_list_user_items);
                }
            }
            // adding item to general item master
            JSONArray generalItemMaster = (JSONArray) jsonObject.get("general_items_master");
            JSONObject generalItemMasterToAdd = new JSONObject();

            boolean ifItemAlreadyExists = false;
            for(int i = 0; i < generalItemMaster.size(); i++){
                JSONObject generalItemMasterItem = (JSONObject) generalItemMaster.get(i);
                if(generalItemMasterItem.get("general_item_name").toString().replaceAll("\\s", "").equalsIgnoreCase(shoppingListItemName.replaceAll("\\s", ""))){ // going through to check if the item we want to add to general item master is already there
                    ifItemAlreadyExists = true;
                    break;
                }
            }
            if(!ifItemAlreadyExists){
                generalItemMasterToAdd.put("general_item_name", shoppingListItemName);
                generalItemMaster.add(generalItemMasterToAdd);
            }
            System.out.println("GENERAL ITEM MASTER: " + generalItemMaster.toJSONString());
            jsonObject.replace("general_item_master", generalItemMaster);
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toJSONString()).apply();
            Constants.json_data_str =PreferenceManager.
                    getDefaultSharedPreferences(context).getString("jsonData", "");
            System.out.println("JSON DATA PRINTLN: " + PreferenceManager.
                    getDefaultSharedPreferences(context).getString("jsonData", ""));
            // update json data string value
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean addShoppingListItemWithQuantity(String shoppingListName, String shoppingListItemName, String lastBoughtDate, String quantity, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shoppingLists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shoppingLists.size(); i++){
                System.out.println("MADE IT @addShoppingListItemWithQuantity : " + shoppingListItemName);
                JSONObject shoppingList = (JSONObject) shoppingLists.get(i);
                if(shoppingList.get("shopping_list_name").toString().equals(shoppingListName)){
                    System.out.println("MADE IT @addShoppingListItemWithQuantity : " + shoppingListName);
                    JSONArray shopping_list_user_items = null;
                    try {
                         shopping_list_user_items = (JSONArray) shoppingList.get("shopping_list_user_items");
                    } catch (Exception e){
                        shopping_list_user_items = (JSONArray) jsonParser.parse(shoppingList.get("shopping_list_user_items").toString());
                    }
                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                        if(strip(shopping_list_user_item.get("shopping_list_item_name").toString()).equalsIgnoreCase(shoppingListItemName)){
                            System.out.println("RUNNING FOR 2 fljlfsjlaksfj: " + shoppingListItemName);
                            Integer quantityToPut = Integer.parseInt(shopping_list_user_item.get("shopping_list_item_quantity").toString()) + Integer.parseInt(quantity);
                            System.out.println("BEFORE: " + shopping_list_user_item);
                            shopping_list_user_item.replace("shopping_list_item_quantity", quantityToPut.toString());
                            System.out.println("AFTER: " + shopping_list_user_item);
                            System.out.println("I REACHED @addNewStore 3: " + jsonObject);
                            Constants.json_data_str = jsonObject.toJSONString();
                            return false;
                        }
                    }
                    System.out.println("RUNNING FOR 2: " + shoppingListItemName);
                    JSONObject shoppingListUserItemToAdd = new JSONObject();
                    shoppingListUserItemToAdd.put("shopping_list_item_name", shoppingListItemName);
                    if(lastBoughtDate != null){
                        shoppingListUserItemToAdd.put("shopping_list_item_last_bought", lastBoughtDate);
                    } else{
                        shoppingListUserItemToAdd.put("shopping_list_item_last_bought", "");
                    }
                    shoppingListUserItemToAdd.put("shopping_list_item_quantity", quantity);
                    shopping_list_user_items.add(shoppingListUserItemToAdd);
                    System.out.println("SHOPPING LIST USER ITEMS AFTEER ADDING " + shoppingListItemName + " TO " + shoppingListName + ": " + shopping_list_user_items.toJSONString());
                    shoppingList.replace("shopping_list_user_items", shopping_list_user_items);
                }
            }
            // update json data string value
            System.out.println("I REACHED END @addShoppingListItemWithQuantity : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            System.out.println("I REACHED END @addShoppingListItemWithQuantity  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<StoreUserItem> getAllItemMasterItemsAsStoreUserItems() throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );

        ArrayList<StoreUserItem> storeUserItemArrayList = new ArrayList<>();

        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray general_item_master = (JSONArray) jsonParser.parse(jsonObject.get("general_items_master").toString());
            for(int i = 0; i < general_item_master.size(); i++){
                    JSONObject userItemObject = (JSONObject) general_item_master.get(i);
                    String generalItemName = (String) userItemObject.get("general_item_name");
//                    String userItemDate = (String) userItemObject.get("user_item_date");
//                    String userItemName = (String) userItemObject.get("user_item_name");
//                    String userItemQuantity = (String) userItemObject.get("user_item_quantity");
//                    String userItemTotalAmountPaid = String.valueOf(userItemObject.get("user_item_total_amount_paid"));
//                    String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
//                    String userItemAdditionWeightPricingDetail = (String) userItemObject.get("user_item_additional_weight_pricing_detail");
                    StoreUserItem userItemToAdd = new StoreUserItem();
//                    if(userItemAdditionWeightPricingDetail != null){ // if the store user item json object has an additional weight pricing detail then use different constructor and save the detail
//                        userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice, userItemAdditionWeightPricingDetail);
//                    }else{
                        userItemToAdd = new StoreUserItem(generalItemName);
//                    }
                    storeUserItemArrayList.add(userItemToAdd);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return storeUserItemArrayList;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<RepItem> getRepItems() throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );

        ArrayList<RepItem> repItemArrayList = new ArrayList<>();

        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray general_item_master = (JSONArray) jsonParser.parse(jsonObject.get("general_items_master").toString());
            for(int i = 0; i < general_item_master.size(); i++){
                JSONObject userItemObject = (JSONObject) general_item_master.get(i);
                String generalItemName = (String) userItemObject.get("general_item_name");
                RepItem repItemToAdd = new RepItem(generalItemName);
                repItemArrayList.add(repItemToAdd);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if (repItemArrayList.size() > 0) {
            Collections.sort(repItemArrayList, new Comparator<RepItem>() {
                @Override
                public int compare(final RepItem object1, final RepItem object2) {
                    return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
                }
            });
        }
        return repItemArrayList;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<RepItem> editRepItem(String oldName, String newName, Context context) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );

        ArrayList<RepItem> storeUserItemArrayList = new ArrayList<>();

        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray general_item_master = (JSONArray) jsonParser.parse(jsonObject.get("general_items_master").toString());
            for(int i = 0; i < general_item_master.size(); i++){
                JSONObject userItemObject = (JSONObject) general_item_master.get(i);
                String generalItemName = (String) userItemObject.get("general_item_name");
                if(generalItemName.equals(oldName)){
                    System.out.println("REPLACING " + oldName + " WITH " + newName);
                    JSONObject generalItemObjectToAdd = new JSONObject();
                    generalItemObjectToAdd.put("general_item_name", newName);
                    general_item_master.remove(userItemObject);
                    general_item_master.add(i, generalItemObjectToAdd);
                }
            }
            System.out.println("POST EDIT USER JSON OBJECT: " + general_item_master);
            jsonObject.replace("general_items_master", general_item_master);
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();


        }catch(Exception e){
            e.printStackTrace();
        }
        return storeUserItemArrayList;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<RepItem> deleteRepItem(String repItemName, Context context) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        System.out.println("RUNNING DELETE ITEM");

        ArrayList<RepItem> storeUserItemArrayList = new ArrayList<>();

        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray general_item_master = (JSONArray) jsonParser.parse(jsonObject.get("general_items_master").toString());
            for(int i = 0; i < general_item_master.size(); i++){
                JSONObject userItemObject = (JSONObject) general_item_master.get(i);
                String generalItemName = (String) userItemObject.get("general_item_name");
                if(generalItemName.equals(repItemName)){
                    System.out.println("REMOVING " + generalItemName + " FROM GENERAL ITEM MASTER");
                    general_item_master.remove(userItemObject);
                }
            }
            jsonObject.replace("general_items_master", general_item_master);
            System.out.println("POST REMOVE GENERAL ITEM MASTER: " + general_item_master.toJSONString());
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();

        }catch(Exception e){
            e.printStackTrace();
        }
        return storeUserItemArrayList;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean addRepItem(String repItemName, Context context) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );

        JSONObject jsonObject = (JSONObject) object;

        try{
            // adding item to general item master
            JSONArray generalItemMaster = (JSONArray) jsonObject.get("general_items_master");
            JSONObject generalItemMasterToAdd = new JSONObject();

            boolean ifItemAlreadyExists = false;
            for(int i = 0; i < generalItemMaster.size(); i++){
                JSONObject generalItemMasterItem = (JSONObject) generalItemMaster.get(i);
                if(generalItemMasterItem.get("general_item_name").toString().replaceAll(" ", "").equalsIgnoreCase(repItemName.replaceAll(" ", ""))){ // going through to check if the item we want to add to general item master is already there
                    ifItemAlreadyExists = true;
                    return false;
                }
            }
            if(!ifItemAlreadyExists){
                generalItemMasterToAdd.put("general_item_name", repItemName);
                generalItemMaster.add(generalItemMasterToAdd);
            }
            jsonObject.replace("general_items_master", generalItemMaster);
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            return true;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    @RequiresApi
    public static void deleteShoppingListUserItem(String shoppingListUserItemName, String shoppingListName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject shoppingList = (JSONObject) shopping_lists.get(i);
                if(shoppingList.get("shopping_list_name").equals(shoppingListName)) {
                    System.out.println("MADE iT DELETE FUNC 1: " + shoppingList.get("shopping_list_name"));
                    JSONArray shopping_list_user_items = (JSONArray)shoppingList.get("shopping_list_user_items");
                    for (int i3 = 0; i3 < shopping_list_user_items.size(); i3++) {
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i3);
                        if (shopping_list_user_item.get("shopping_list_item_name").equals(shoppingListUserItemName)) {
                            System.out.println("MADE iT DELETE FUNC: " + shopping_list_user_item.get("shopping_list_item_name"));
                            shopping_list_user_items.remove(shopping_list_user_item);
                            System.out.println("SHOPPING LIST NOW: "  + shoppingList.toJSONString());
                        }
                    }
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            System.out.println("JSON OBJ: "+ jsonObject.toJSONString());
            // update json data string value
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @RequiresApi
    public static void deleteStoreListItem(String itemId, String storeName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < stores.size(); i++){
                JSONObject store = (JSONObject) stores.get(i);
                if(store.get("store_name").equals(storeName)) {
                    JSONArray store_user_items = (JSONArray)store.get("store_user_items");
                    for (int i3 = 0; i3 < store_user_items.size(); i3++) {
                        JSONObject store_user_item = (JSONObject) store_user_items.get(i3);
                        if (Objects.equals(String.valueOf(store_user_item.get("id")), itemId)) {
                            store_user_items.remove(store_user_item);
                            System.out.println("SHOPPING LIST NOW: "  + store.toJSONString());
                        }
                    }
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            System.out.println("JSON OBJ: "+ jsonObject.toJSONString());
            // update json data string value
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<StoreUserItem> getHistoryOfShoppingListItem(String shoppingListUserItemName) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        System.out.println(shoppingListUserItemName + "@getWhenShoppingListUserWhenLastBought");
        ArrayList<StoreUserItem> storeUserItemsHistoryOfShoppingListItem = new ArrayList<>();

        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < stores.size(); i++){
                JSONObject store = (JSONObject) stores.get(i);
                JSONArray store_user_items = (JSONArray) jsonParser.parse(store.get("store_user_items").toString());
                System.out.println("STORE USER ITEMS: " + store_user_items);
                for(int i2 = 0; i2 < store_user_items.size(); i2++){
                    System.out.println("SIZE: " + store_user_items.size());
                    JSONObject userItemObject = (JSONObject) store_user_items.get(i2);
                    StoreUserItem userItemToAdd = new StoreUserItem();
                    System.out.println("USER ITEM OBJECT: " + strip(userItemObject.get("user_item_name").toString()));
                    if(strip(userItemObject.get("user_item_name").toString()).equalsIgnoreCase(strip(shoppingListUserItemName))) {
                        String userItemName = (String) userItemObject.get("user_item_name");
                        String userItemDate = (String) userItemObject.get("user_item_date");
                        String userItemQuantity = (String) userItemObject.get("user_item_quantity");
                        String userItemStore = (String) userItemObject.get("user_item_store");
                        String userItemTotalAmountPaid = String.valueOf(userItemObject.get("user_item_total_amount_paid"));
                        String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
                        String withinPackageItemCount = (String) userItemObject.get("within_package_item_count");
                        String userItemAdditionWeightPricingDetail = (String) userItemObject.get("user_item_additional_weight_pricing_detail");
                        System.out.println("ADDITIONAL WEIGHT DETAIL: " + userItemAdditionWeightPricingDetail);
                        String id = String.valueOf(userItemObject.get(("id")));
                        if(userItemAdditionWeightPricingDetail != null){ // if the store user item json object has an additional weight pricing detail then use different constructor and save the detail
                            System.out.println("MADE ITTTTTTTTTTTTTT: " + userItemName + ": " + userItemAdditionWeightPricingDetail);
                            userItemToAdd = new StoreUserItem(userItemStore, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice, withinPackageItemCount, userItemAdditionWeightPricingDetail, id);

                        }else{
                            userItemToAdd = new StoreUserItem(userItemStore, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice, withinPackageItemCount, id);
                        }
                        storeUserItemsHistoryOfShoppingListItem.add(userItemToAdd);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return storeUserItemsHistoryOfShoppingListItem;


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ShoppingList> ifShoppingListItemExistsInOtherShoppingLists(String shoppingListUserItemName) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        System.out.println(shoppingListUserItemName + "@ifShoppingListItemExistsInOtherShoppingLists");
        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();

        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject shopping_list = (JSONObject) shopping_lists.get(i);
                JSONArray shopping_list_user_items = null;
                try {
                     shopping_list_user_items = (JSONArray) shopping_list.get("shopping_list_user_items");
                } catch(Exception e){
                    shopping_list_user_items = (JSONArray) jsonParser.parse(shopping_list.get("shopping_list_user_items").toString());
                }
                for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                    System.out.println("SIZE: " + shopping_list_user_items.size());
                    JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                    ShoppingList shoppingListToAdd = new ShoppingList();
                    System.out.println("USER ITEM OBJECT: " + strip(shopping_list_user_item.get("shopping_list_item_name").toString()));
                    if(strip(shopping_list_user_item.get("shopping_list_item_name").toString()).equalsIgnoreCase(strip(shoppingListUserItemName))) {
                        String shopping_list_name = (String) shopping_list.get("shopping_list_name");
                        System.out.println("ADDING: " + shopping_list_name + "@ifShoppingListItemExistsInOtherShoppingLists");
                        shoppingListToAdd = new ShoppingList(shopping_list_name);
                        shoppingLists.add(shoppingListToAdd);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if(shoppingLists.size() - 1 == 0) {
            System.out.println("RETURNING 2: " + shoppingLists.size() + " FOR " + shoppingListUserItemName);
            return null;
        } else{
            System.out.println("RETURNING: " + shoppingLists.size() + " FOR " + shoppingListUserItemName);
            return shoppingLists;
        }

    }

    // TODO: create getStoreUserItems() and getShoppingListUsersItems() and if storeAlreadyExists(), shoppingListAlreadyExists(), and if storeUserItemAlreadyExists() and if shoppingListUserItem() already exist
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getWhenShoppingListUserItemLastBought(String shoppingListUserItemName) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        System.out.println(shoppingListUserItemName + "@getWhenShoppingListUserWhenLastBought");
        ArrayList<ShoppingListUserItem> toSortArrayList = new ArrayList<>();

        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < stores.size(); i++){
                JSONObject store = (JSONObject) stores.get(i);
                JSONArray store_user_items = (JSONArray) jsonParser.parse(store.get("store_user_items").toString());
                System.out.println("STORE USER ITEMS: " + store_user_items);
                for(int i2 = 0; i2 < store_user_items.size(); i2++){
                    System.out.println("SIZE: " + store_user_items.size());
                    JSONObject userItemObject = (JSONObject) store_user_items.get(i2);
                    ShoppingListUserItem userItemToAdd = new ShoppingListUserItem();
                    System.out.println("USER ITEM OBJECT: " + strip(userItemObject.get("user_item_name").toString()));
                    if(strip(userItemObject.get("user_item_name").toString()).equalsIgnoreCase(strip(shoppingListUserItemName))) {
                        String userItemName = (String) userItemObject.get("user_item_name");
                        String userItemDate = (String) userItemObject.get("user_item_date");
                        String userItemQuantity = (String) userItemObject.get("user_item_quantity");
                        boolean ifGreenTickMarked = false;
                        try {
                            ifGreenTickMarked = Boolean.parseBoolean(userItemObject.get("if_green_tick_marked").toString());
                        } catch(Exception e){
                            ifGreenTickMarked = false;
                        }
                        boolean ifSavedForLater = false;
                        try {
                            ifSavedForLater = Boolean.parseBoolean(userItemObject.get("if_saved_for_later").toString());
                        } catch(Exception e){
                            ifSavedForLater = false;
                        }
                        userItemToAdd = new ShoppingListUserItem(userItemName, userItemDate, userItemQuantity, ifGreenTickMarked, ifSavedForLater);
                        System.out.println("ADDING " + userItemToAdd.getName() + " FOR " + shoppingListUserItemName);
                        toSortArrayList.add(userItemToAdd);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("MY LIF SUCKS" + shoppingListUserItemName + ":" + toSortArrayList.size());
        if(!toSortArrayList.isEmpty()) {
            ArrayList<Date> lastBoughtDates = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
            toSortArrayList.forEach(item -> {
                try {

                    lastBoughtDates.add(simpleDateFormat.parse(item.getLastBought()));
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("MY LIF SUCKS2" + shoppingListUserItemName + ":" + simpleDateFormat.format(Collections.max(lastBoughtDates)).toString());
            return simpleDateFormat.format(Collections.max(lastBoughtDates)).toString();
        } else{
            return "not previously bought";
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<StoreUserItem> getStoreUserItems(String storeName) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );

        ArrayList<StoreUserItem> storeUserItemArrayList = new ArrayList<>();

        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < stores.size(); i++){
                JSONObject store = (JSONObject) stores.get(i);
                if(store.get("store_name").equals(storeName)){
                    JSONArray store_user_items = (JSONArray) jsonParser.parse(store.get("store_user_items").toString());
                    for(int i2 = 0; i2 < store_user_items.size(); i2++){
                        JSONObject userItemObject = (JSONObject) store_user_items.get(i2);
                        String userItemStoreName = (String) userItemObject.get("user_item_store");
                        String userItemDate = (String) userItemObject.get("user_item_date");
                        String userItemName = (String) userItemObject.get("user_item_name");
                        String userItemQuantity = (String) userItemObject.get("user_item_quantity");
                        String userItemTotalAmountPaid = String.valueOf(userItemObject.get("user_item_total_amount_paid"));
                        String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
                        String withinPackageItemCount = (String) userItemObject.get("user_item_within_package_item_count");

                        String userItemAdditionWeightPricingDetail = (String) userItemObject.get("user_item_additional_weight_pricing_detail");
                        String id = String.valueOf(userItemObject.get("id"));
                        StoreUserItem userItemToAdd = new StoreUserItem();
                        if(withinPackageItemCount == null){
                            // weight based detail
                            userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice, id);

                        } else { // package based detail
                            if(userItemAdditionWeightPricingDetail != null){ // if the store user item json object has an additional weight pricing detail then use different constructor and save the detail
                                System.out.println("MADE IT @getStoreUserItems: " + userItemName + ": " + userItemAdditionWeightPricingDetail);
                                userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice, withinPackageItemCount, userItemAdditionWeightPricingDetail, id);
                            }else{
                                // weight based detail
                                userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice, withinPackageItemCount, id);
                            }

                        }
                        storeUserItemArrayList.add(userItemToAdd);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        if (storeUserItemArrayList.size() > 0) {
            Collections.sort(storeUserItemArrayList, new Comparator<StoreUserItem>() {
                @Override
                public int compare(final StoreUserItem object1, final StoreUserItem object2) {
                    return object1.getItemName().toLowerCase().compareTo(object2.getItemName().toLowerCase());
                }
            });
        }
        return storeUserItemArrayList;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ShoppingListUserItem> getShoppingListUserItems(String shoppingListName) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = new JSONParser().parse(jsonData
        );

        ArrayList<ShoppingListUserItem> ShoppingListUserItemArraylist = new ArrayList<>();
        System.out.println("CALLED AT @getShoppingListUserItems");
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject shopping_list = (JSONObject) shopping_lists.get(i);
                if(shopping_list.get("shopping_list_name").equals(shoppingListName)){
                    JSONArray shopping_list_user_items = (JSONArray) jsonParser.parse(shopping_list.get("shopping_list_user_items").toString());
                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONObject ShoppingListUserItemObject = (JSONObject) shopping_list_user_items.get(i2);
                        String shopping_list_item_name = (String) ShoppingListUserItemObject.get("shopping_list_item_name");
                        String shopping_list_item_last_bought = (String) ShoppingListUserItemObject.get("shopping_list_item_last_bought");
                        String shopping_list_item_quantity = (String) ShoppingListUserItemObject.get("shopping_list_item_quantity");
                        boolean ifGreenTickMarked = false;
                        try {
                            ifGreenTickMarked = Boolean.parseBoolean(ShoppingListUserItemObject.get("if_green_tick_marked").toString());
                        } catch(Exception e){
                            ifGreenTickMarked = false;
                        }
                        boolean ifSavedForLater = false;
                        try {
                            ifSavedForLater = Boolean.parseBoolean(ShoppingListUserItemObject.get("if_saved_for_later").toString());
                        } catch(Exception e){
                            ifSavedForLater = false;
                        }
                        System.out.println("ADDING @getShoppingListUserItems: " + shopping_list_item_name + " " + shopping_list_item_last_bought + " " + shopping_list_item_quantity);
                        ShoppingListUserItem shoppingListUserItemToAdd = new ShoppingListUserItem(shopping_list_item_name, shopping_list_item_last_bought, shopping_list_item_quantity, ifGreenTickMarked, ifSavedForLater);
                        ShoppingListUserItemArraylist.add(shoppingListUserItemToAdd);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("GET SHOPPING LIST ITEMS FOR " + shoppingListName);
        for(ShoppingListUserItem shoppingListUserItem: ShoppingListUserItemArraylist){
            System.out.println("ITEM NAME: " + shoppingListUserItem.getName());
        }
        if (ShoppingListUserItemArraylist.size() > 0) {
            Collections.sort(ShoppingListUserItemArraylist, new Comparator<ShoppingListUserItem>() {
                @Override
                public int compare(final ShoppingListUserItem object1, final ShoppingListUserItem object2) {
                    return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
                }
            });
        }
        ArrayList<ShoppingListUserItem> shoppingListUserItemArrayListCopy = (ArrayList<ShoppingListUserItem>) ShoppingListUserItemArraylist.clone();
        for(ShoppingListUserItem shoppingListUserItem: ShoppingListUserItemArraylist){
            if(shoppingListUserItem.getIfSavedForLater()){
                shoppingListUserItemArrayListCopy.remove(shoppingListUserItemArrayListCopy.indexOf(shoppingListUserItem));
                shoppingListUserItemArrayListCopy.add(0, shoppingListUserItem);
            }
        }

        return shoppingListUserItemArrayListCopy;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Store> getStores(Context context) throws IOException, ParseException {

        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        ArrayList<Store> storeArrayList = new ArrayList<>();
        StoreUserItem userItem = new StoreUserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray userItemsList = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < userItemsList.size(); i++){
                JSONObject storeObject = (JSONObject) userItemsList.get(i);
                ArrayList<StoreUserItem> userItemArrayList = new ArrayList<>();
                String store_name = (String) storeObject.get("store_name");
                System.out.println("STORE USER ITEMS 3 FOR " + store_name + ": " + storeObject.get("store_user_items"));
                JSONArray store_user_items = (JSONArray) new JSONParser().parse(storeObject.get("store_user_items").toString());
                for(int i2 = 0; i2 < store_user_items.size(); i2++){
                    JSONObject userItemObject = (JSONObject) store_user_items.get(i2);
                    String userItemStoreName = (String) userItemObject.get("user_item_store");
                    String userItemDate = (String) userItemObject.get("user_item_date");
                    String userItemName = (String) userItemObject.get("user_item_name");
                    String userItemQuantity = (String) userItemObject.get("user_item_quantity");
                    String userItemTotalAmountPaid = String.valueOf(userItemObject.get("user_item_total_amount_paid"));
                    String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
                    String withinPackageItemCount = (String) userItemObject.get("within_package_item_count");
                    String userItemAdditionWeightPricingDetail = (String) userItemObject.get("user_item_additional_weight_pricing_detail");
                    String id = String.valueOf(userItemObject.get("id"));
                    StoreUserItem userItemToAdd = new StoreUserItem();
                    if(userItemAdditionWeightPricingDetail != null){ // if the store user item json object has an additional weight pricing detail then use different constructor and save the detail
                        System.out.println("MADE ITTTTTTTTTTTTTT: " + userItemName + ": " + userItemAdditionWeightPricingDetail);
                        userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice, withinPackageItemCount, userItemAdditionWeightPricingDetail, id);

                    }else{
                        userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice, withinPackageItemCount, id);
                    }
                    userItemArrayList.add(userItemToAdd);
                }
                Store storeToAdd = new Store(store_name, store_user_items);
                storeArrayList.add(storeToAdd);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if (storeArrayList.size() > 0) {
            Collections.sort(storeArrayList, new Comparator<Store>() {
                @Override
                public int compare(final Store object1, final Store object2) {
                    return object1.getStoreName().toLowerCase().compareTo(object2.getStoreName().toLowerCase());
                }
            });
        }
        return refactorStoresOrder(storeArrayList, Constants.storeBeingShoppedIn, context);
    }


    public static ArrayList<Store> refactorStoresOrder(ArrayList<Store> stores, String storeName, Context context) {

        int position = 0;
        Store specifiedStore = null;
        for(Store store: stores){
            if(PreferenceManager.getDefaultSharedPreferences(context).getString("selectedStore", "").equalsIgnoreCase(store.getStoreName())){
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ShoppingList> getShoppingLists() throws IOException, ParseException {


        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        ArrayList<ShoppingList> shoppingListArray = new ArrayList<>();
        StoreUserItem userItem = new StoreUserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray userItemsList = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < userItemsList.size(); i++){
                JSONObject storeObject = (JSONObject) userItemsList.get(i);
                String shopping_list_name = (String) storeObject.get("shopping_list_name");
                JSONArray shopping_list_user_items = (JSONArray) jsonParser.parse(storeObject.get("shopping_list_user_items").toString());
                ArrayList<ShoppingListUserItem> ShoppingListUserItemArraylist = new ArrayList<ShoppingListUserItem>();
                for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                    JSONObject userItemObject = (JSONObject) shopping_list_user_items.get(i2);
                    String shopping_list_item_name = (String) userItemObject.get("shopping_list_item_name");
                    String shopping_list_item_last_bought = (String) userItemObject.get("shopping_list_item_last_bought");
                    String shopping_list_item_quantity = (String) userItemObject.get("shopping_list_item_quantity");
                    boolean ifGreenTickMarked = false;
                    try {
                         ifGreenTickMarked = Boolean.parseBoolean(userItemObject.get("if_green_tick_marked").toString());
                    } catch(Exception e){
                        ifGreenTickMarked = false;
                    }
                    boolean ifSavedForLater = false;
                    try {
                        ifSavedForLater = Boolean.parseBoolean(userItemObject.get("if_saved_for_later").toString());
                    } catch(Exception e){
                        ifSavedForLater = false;
                    }
                    ShoppingListUserItem ShoppingListUserItemToAdd = new ShoppingListUserItem(shopping_list_item_name, shopping_list_item_last_bought, shopping_list_item_quantity, ifGreenTickMarked, ifSavedForLater);
                    ShoppingListUserItemArraylist.add(ShoppingListUserItemToAdd);
                }
                ShoppingList shoppingListToAdd = new ShoppingList(shopping_list_name, ShoppingListUserItemArraylist);
                shoppingListArray.add(shoppingListToAdd);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if (shoppingListArray.size() > 0) {
            Collections.sort(shoppingListArray, new Comparator<ShoppingList>() {
                @Override
                public int compare(final ShoppingList object1, final ShoppingList object2) {
                    return object1.getName().toLowerCase().compareTo(object2.getName().toLowerCase());
                }
            });
        }
        return shoppingListArray;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void editStoreName(String originalName, String replacementName, android.content.Context context) throws IOException, ParseException {
        System.out.println("IVE BEEN ACCESSED - editStoreName func");
        System.out.println("ORIGINAL NAME PARAMETER editStoreName func @QueryUtils: " + originalName);
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData
        );
        ArrayList<ShoppingList> shoppingListArray = new ArrayList<>();
        StoreUserItem userItem = new StoreUserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < stores.size(); i++){
                JSONObject storeObject = (JSONObject) stores.get(i);
                String store_name = (String) storeObject.get("store_name");
                System.out.println("STORE NAME: " + store_name);
                if(store_name.equals(originalName)){
                    System.out.println("IVE BEEN ACCESSED - editStoreName func replacement");
                    storeObject.replace("store_name", replacementName);
                }
                JSONArray store_user_items = (JSONArray) jsonParser.parse(storeObject.get("store_user_items").toString());
//                JSONArray store_user_items_to_be_replaced = new JSONArray();
                for(int i2 = 0; i2 < store_user_items.size(); i2++){
                    JSONObject store_user_item = (JSONObject) store_user_items.get(i2);
                    System.out.println("REPLACING: " + store_user_item.get("user_item_store") + " WITH " + replacementName);
                    store_user_item.replace("user_item_store", replacementName);
//                    store_user_items_to_be_replaced.add(store_user_items);
                    System.out.println("STORE USER ITEM: " + store_user_item);
                }
//                store_user_items = store_user_items_to_be_replaced;
                storeObject.replace("store_user_items", store_user_items);
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            System.out.println("STORE OBJECT: " + stores);
            // update json data string value
            System.out.println("IVE BEEN ACCESSED - editStoreName func updation");
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ShoppingList> editShoppingListName(String originalName, String replacementName, android.content.Context context) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        ArrayList<ShoppingList> shoppingListArray = new ArrayList<>();
        StoreUserItem userItem = new StoreUserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject storeObject = (JSONObject) shopping_lists.get(i);
                ArrayList<StoreUserItem> userItemArrayList = new ArrayList<>();
                String shopping_list_name = (String) storeObject.get("shopping_list_name");
                if(shopping_list_name.equals(originalName)){
                    storeObject.replace("shopping_list_name", replacementName);
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            // update json data string value
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
        return shoppingListArray;

    }

    public static void addNewStore(String storeName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        ArrayList<ShoppingList> shoppingListArray = new ArrayList<>();
        StoreUserItem userItem = new StoreUserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            JSONObject storeJsonObjectToAdd = new JSONObject();
            storeJsonObjectToAdd.put("store_name", storeName);
            storeJsonObjectToAdd.put("store_user_items", "[]");
            stores.add(storeJsonObjectToAdd);
            // update json data string value
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void addShoppingList(String shoppingListName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            JSONObject shoppingListJsonObjectToAdd = new JSONObject();
            shoppingListJsonObjectToAdd.put("shopping_list_name", shoppingListName);
            shoppingListJsonObjectToAdd.put("shopping_list_user_items", "[]");
            shopping_lists.add(shoppingListJsonObjectToAdd);
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            // update json data string value
            System.out.println("IM BEING ACCESSED @addNewShoppingList Query Utils finalJSONObject: " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void deleteStore(String storeName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData);
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray stores = (JSONArray) jsonObject.get("stores");
            for(int i = 0; i < stores.size(); i++){
                JSONObject store = (JSONObject) stores.get(i);
                if(store.get("store_name").equals(storeName)){
                    System.out.println("MADE IT DELETE: " + storeName);
                    stores.remove(store);
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            System.out.println("TO UPDATE JSON OBJECT @deleteStore: " + jsonObject);
            // update json data string value
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void deleteShoppingList(String shoppingListName, android.content.Context context) throws ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject shoppingList = (JSONObject) shopping_lists.get(i);
                if(shoppingList.get("shopping_list_name").equals(shoppingListName)){
                    shopping_lists.remove(shoppingList);
                }
            }
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putString("jsonData",jsonObject.toString()).apply();
            // update json data string value
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        File file = new File("practice.txt");
//        File file = new File(filePath);
        ArrayList<StoreUserItem> userItemArrayList = new ArrayList<>();
//        Scanner scanner = new Scanner(file);
//        file.setWritable(true);
//        file.setReadable(true);
//        file.canRead();
        System.out.println("EXISTS: " + file.exists());
    }

    public static void setShoppingListItemToSavedForLater(String name, String shoppingList, Context applicationContext) throws ParseException {
        System.out.println("IVE BEEN ACCESSED - setShoppingListItemToSavedForLater func");
        System.out.println("ORIGINAL NAME PARAMETER setShoppingListItemToSavedForLater func @QueryUtils: " + shoppingList);
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData);
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject shoppingListObject = (JSONObject) shopping_lists.get(i);
                String shopping_list_name = (String) shoppingListObject.get("shopping_list_name");
                JSONArray store_user_items = (JSONArray) jsonParser.parse(String.valueOf(shoppingListObject.get("store_user_items")));
                System.out.println("SHOPPING LIST NAME: " + shopping_list_name);
                if(shopping_list_name.equalsIgnoreCase(shoppingList)){
                    JSONArray shopping_list_user_items = null;
                    try {
                        shopping_list_user_items = (JSONArray) shoppingListObject.get("shopping_list_user_items");
                    } catch (Exception e){
                        shopping_list_user_items = (JSONArray) jsonParser.parse(shoppingListObject.get("shopping_list_user_items").toString());
                    }
                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                        if(strip(shopping_list_user_item.get("shopping_list_item_name").toString()).equalsIgnoreCase(name)){
                            shopping_list_user_item.put("if_saved_for_later", "true");
                            Constants.json_data_str = jsonObject.toJSONString();
                        }
                    }
                }
            }
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                    .putString("jsonData",Constants.json_data_str.toString()).apply();
            System.out.println("JSON DATA STR @saveDetailsOfShoppingListUserItem: " + Constants.json_data_str);

        }catch(Exception e){
            System.out.println("EXCEPTION @saveDetailsOfShoppingListUserItem:");
            e.printStackTrace();
        }
    }


    public static void setShoppingListItemToNotSavedForLater(String name, String shoppingList, Context applicationContext) throws ParseException {
        System.out.println("IVE BEEN ACCESSED - setShoppingListItemToNotSavedForLater func");
        System.out.println("ORIGINAL NAME PARAMETER setShoppingListItemToNotSavedForLater func @QueryUtils: " + shoppingList);
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = jsonParser.parse(jsonData);
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shopping_lists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shopping_lists.size(); i++){
                JSONObject shoppingListObject = (JSONObject) shopping_lists.get(i);
                String shopping_list_name = (String) shoppingListObject.get("shopping_list_name");
                JSONArray store_user_items = (JSONArray) jsonParser.parse(String.valueOf(shoppingListObject.get("store_user_items")));
                System.out.println("SHOPPING LIST NAME: " + shopping_list_name);
                if(shopping_list_name.equalsIgnoreCase(shoppingList)){
                    JSONArray shopping_list_user_items = null;
                    try {
                        shopping_list_user_items = (JSONArray) shoppingListObject.get("shopping_list_user_items");
                    } catch (Exception e){
                        shopping_list_user_items = (JSONArray) jsonParser.parse(shoppingListObject.get("shopping_list_user_items").toString());
                    }
                    for(int i2 = 0; i2 < shopping_list_user_items.size(); i2++){
                        JSONObject shopping_list_user_item = (JSONObject) shopping_list_user_items.get(i2);
                        if(strip(shopping_list_user_item.get("shopping_list_item_name").toString()).equalsIgnoreCase(name)){
                            shopping_list_user_item.put("if_saved_for_later", "false");
                            Constants.json_data_str = jsonObject.toJSONString();
                        }
                    }
                }
            }
            Constants.json_data_str = jsonObject.toJSONString();
            PreferenceManager.getDefaultSharedPreferences(applicationContext).edit()
                    .putString("jsonData",Constants.json_data_str.toString()).apply();
            System.out.println("JSON DATA STR @saveDetailsOfShoppingListUserItem: " + Constants.json_data_str);

        }catch(Exception e){
            System.out.println("EXCEPTION @saveDetailsOfShoppingListUserItem:");
            e.printStackTrace();
        }
    }

}
