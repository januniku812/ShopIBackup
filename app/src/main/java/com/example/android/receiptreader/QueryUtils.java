package com.example.android.receiptreader;


import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class QueryUtils  {
    public static String strip(String string){
        return string.replaceAll(" ","");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void reorderShoppingListItem(String originalShoppingList, String shoppingListToMoveTo, String shoppingListItemName) throws ParseException {
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
            // finding shopping list to move it and placing it there
            QueryUtils.addShoppingListItemWithQuantity(shoppingListToMoveTo, shoppingListItemName, shoppingListItemObj.get("shopping_list_item_last_bought").toString(), shoppingListItemObj.get("shopping_list_item_quantity").toString());
            // update json data string value
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void increaseShoppingListItemQuantity(String shoppingListName, String shoppingListItemName) throws ParseException {
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
                            Integer quantityToPut = Integer.parseInt(shopping_list_user_item.get("shopping_list_item_quantity").toString()) + 1;
                            System.out.println("BEFORE: " + shopping_list_user_item);
                            shopping_list_user_item.replace("shopping_list_item_quantity", quantityToPut.toString());
                            System.out.println("AFTER: " + shopping_list_user_item);
                            System.out.println("I REACHED @addNewStore 3: " + jsonObject);
                            Constants.json_data_str = jsonObject.toJSONString();
                        }
                    }
                }
            }
            // update json data string value
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void decreaseShoppingListItemQuantity(String shoppingListName, String shoppingListItemName) throws ParseException {
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
            // update json data string value
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean addShoppingListItem(String shoppingListName, String shoppingListItemName, String lastBoughtDate) throws ParseException {
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
                            System.out.println("RUNNING FOR 2: " + shoppingListItemName);
                            Integer quantityToPut = Integer.parseInt(shopping_list_user_item.get("shopping_list_item_quantity").toString()) + 1;
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
                    shoppingListUserItemToAdd.put("shopping_list_item_quantity", "1");
                    shopping_list_user_items.add(shoppingListUserItemToAdd);
                }
            }
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
    public static boolean addShoppingListItemWithQuantity(String shoppingListName, String shoppingListItemName, String lastBoughtDate, String quantity) throws ParseException {
        String jsonData = Constants.json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray shoppingLists = (JSONArray) jsonObject.get("shopping_lists");
            for(int i = 0; i < shoppingLists.size(); i++){
                System.out.println("MADE IT @addShoppingListItem : " + shoppingListItemName);
                JSONObject shoppingList = (JSONObject) shoppingLists.get(i);
                if(shoppingList.get("shopping_list_name").toString().equals(shoppingListName)){
                    System.out.println("MADE IT @addShoppingListItem : " + shoppingListName);
                    JSONArray shopping_list_user_items = (JSONArray) shoppingList.get("shopping_list_user_items");
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
                }
            }
            // update json data string value
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<StoreUserItem> getAllStoreUserItems() throws IOException, ParseException {
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
                    JSONArray store_user_items = (JSONArray) jsonParser.parse(store.get("store_user_items").toString());
                    for(int i2 = 0; i2 < store_user_items.size(); i2++){
                        JSONObject userItemObject = (JSONObject) store_user_items.get(i2);
                        String userItemStoreName = (String) userItemObject.get("user_item_store");
                        String userItemDate = (String) userItemObject.get("user_item_date");
                        String userItemName = (String) userItemObject.get("user_item_name");
                        String userItemQuantity = (String) userItemObject.get("user_item_quantity");
                        String userItemTotalAmountPaid = (String) userItemObject.get("user_item_total_amount_paid");
                        String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
                        StoreUserItem userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice);
                        storeUserItemArrayList.add(userItemToAdd);
                    }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return storeUserItemArrayList;

    }

    @RequiresApi
    public static void deleteShoppingListUserItem(String shoppingListUserItemName, String shoppingListName) throws ParseException {
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
                        }
                    }
                }
            }
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
                        String userItemTotalAmountPaid = (String) userItemObject.get("user_item_total_amount_paid");
                        String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
                        userItemToAdd = new StoreUserItem(userItemStore, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice);
                        System.out.println("ADDING " + userItemToAdd.getItemName() + " FOR " + shoppingListUserItemName);
                        storeUserItemsHistoryOfShoppingListItem.add(userItemToAdd);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        if(!storeUserItemsHistoryOfShoppingListItem.isEmpty()) {
            return storeUserItemsHistoryOfShoppingListItem;
        } else{
            return null;
        }

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
                JSONArray shopping_list_user_items = (JSONArray) shopping_list.get("shopping_list_user_items");
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
        if(shoppingLists.size() == 0) {
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
                            userItemToAdd = new ShoppingListUserItem(userItemName, userItemDate, userItemQuantity);
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
                        String userItemTotalAmountPaid = (String) userItemObject.get("user_item_total_amount_paid");
                        String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
                        StoreUserItem userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice);
                        storeUserItemArrayList.add(userItemToAdd);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return storeUserItemArrayList;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ShoppingListUserItem> getShoppingListUsersItems(String shoppingListName) throws IOException, ParseException {
        String jsonData = Constants.json_data_str;
        JSONParser jsonParser = new JSONParser();
        Object object = new JSONParser().parse(jsonData
        );

        ArrayList<ShoppingListUserItem> ShoppingListUserItemArraylist = new ArrayList<>();

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
                        ShoppingListUserItem shoppingListUserItemToAdd = new ShoppingListUserItem(shopping_list_item_name, shopping_list_item_last_bought, shopping_list_item_quantity);
                        ShoppingListUserItemArraylist.add(shoppingListUserItemToAdd);
                    }
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return ShoppingListUserItemArraylist;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Store> getStores() throws IOException, ParseException {

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
                    String userItemTotalAmountPaid = (String) userItemObject.get("user_item_total_amount_paid");
                    String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
                    StoreUserItem userItemToAdd = new StoreUserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice);
                    userItemArrayList.add(userItemToAdd);
                }
                Store storeToAdd = new Store(store_name, store_user_items);
                storeArrayList.add(storeToAdd);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return storeArrayList;

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
                    ShoppingListUserItem ShoppingListUserItemToAdd = new ShoppingListUserItem(shopping_list_item_name, shopping_list_item_last_bought, shopping_list_item_quantity);
                    ShoppingListUserItemArraylist.add(ShoppingListUserItemToAdd);
                }
                ShoppingList shoppingListToAdd = new ShoppingList(shopping_list_name, ShoppingListUserItemArraylist);
                shoppingListArray.add(shoppingListToAdd);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return shoppingListArray;

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void editStoreName(String originalName, String replacementName) throws IOException, ParseException {
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
            System.out.println("STORE OBJECT: " + stores);
            // update json data string value
            System.out.println("IVE BEEN ACCESSED - editStoreName func updation");
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<ShoppingList> editShoppingListName(String originalName, String replacementName) throws IOException, ParseException {
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
            // update json data string value
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
        return shoppingListArray;

    }

    public static void addNewStore(String storeName) throws ParseException {
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
            System.out.println("I REACHED @addNewStore : " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();
            System.out.println("I REACHED @addNewStore  2: " + Constants.json_data_str);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void addShoppingList(String shoppingListName) throws ParseException {
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
            // update json data string value
            System.out.println("IM BEING ACCESSED @addNewShoppingList Query Utils finalJSONObject: " + jsonObject);
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void deleteStore(String storeName) throws ParseException {
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
            System.out.println("TO UPDATE JSON OBJECT @deleteStore: " + jsonObject);
            // update json data string value
            Constants.json_data_str = jsonObject.toJSONString();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void deleteShoppingList(String shoppingListName) throws ParseException {
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

}
