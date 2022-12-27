package com.example.android.receiptreader;


import android.content.res.Resources;
import android.os.Build;
import android.os.FileUtils;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class QueryUtils  {
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<UserItem> getUserItems() throws IOException, ParseException {

        String jsonData = Constants.user_items_json_data_str;
        Object object = new JSONParser().parse(jsonData
        );
//                 Object object = new JSONParser().parse(new String(Files.readAllBytes(Paths.get(filePath))));
//        Object object = new JSONParser().parse("{\n" +
//                "  \"user_items\": [\n" +
//                "    {\n" +
//                "      \"user_item_store\": \"WALMART\",\n" +
//                "      \"user_item_date\":\"12/7/2022\",\n" +
//                "      \"user_item_name\": \"HRSPRAY\",\n" +
//                "      \"user_item_quantity\": \"2\",\n" +
//                "      \"user_item_total_amount_paid\": \"5.00\",\n" +
//                "      \"user_item_unit_price\": \"2.50\"\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}");
        ArrayList<UserItem> userItemArrayList = new ArrayList<>();

        UserItem userItem = new UserItem();
        try{
            JSONObject jsonObject = (JSONObject) object;
            JSONArray userItemsList = (JSONArray) jsonObject.get("user_items");
            for(int i = 0; i < userItemsList.size(); i++){
                JSONObject userItemObject = (JSONObject) userItemsList.get(i);
                String userItemStoreName = (String) userItemObject.get("user_item_store");
                String userItemDate = (String) userItemObject.get("user_item_date");
                String userItemName = (String) userItemObject.get("user_item_name");
                String userItemQuantity = (String) userItemObject.get("user_item_quantity");
                String userItemTotalAmountPaid = (String) userItemObject.get("user_item_total_amount_paid");
                String userItemUnitPrice = (String) userItemObject.get("user_item_unit_price");
                UserItem userItemToAdd = new UserItem(userItemStoreName, userItemDate, userItemName, userItemQuantity, userItemTotalAmountPaid, userItemUnitPrice);
                userItemArrayList.add(userItemToAdd);

            }

        }catch(Exception e){
            e.printStackTrace();
        }
         return userItemArrayList;

    }




    public static void main(String[] args) {
        File file = new File("practice.txt");
//        File file = new File(filePath);
        ArrayList<UserItem> userItemArrayList = new ArrayList<>();
//        Scanner scanner = new Scanner(file);
//        file.setWritable(true);
//        file.setReadable(true);
//        file.canRead();
        System.out.println("EXISTS: " + file.exists());
    }
}
