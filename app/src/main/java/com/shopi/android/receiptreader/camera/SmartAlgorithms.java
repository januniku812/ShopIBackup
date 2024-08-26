package com.shopi.android.receiptreader.camera;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;

import com.shopi.android.receiptreader.Constants;
import com.googlecode.tesseract.android.TessBaseAPI;
import java.util.ArrayList;
import java.util.Date;

public class SmartAlgorithms {
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    public static String replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(String str){
        return str.replaceAll("\\$", "").replaceAll(" ", "").replaceAll("[^\\d.]", "").replaceAll(";","");
    }
    public static boolean isInt(String str) {
        System.out.println("IS INT TESTING:****" + str + "*****");
        try {
            Integer.parseInt(str.replace("\\s",""));
            return true;
        } catch (Exception e) {
//            System.out.println(str + " ISSUE:  " + e);
            return false;
        }
    }
    public static String getNameOfSplitUpLine(String str){
        return str.substring(0, str.indexOf("$"));
    }
    public static void indiaBazaarSmartAlgorithm(String filePath) throws IOException {
        ArrayList<String> user_items = new ArrayList<String>();
        TessBaseAPI tesseract = new TessBaseAPI();
        tesseract.setImage(new File(filePath));
        tesseract.init("com\\googlecode\\tesseract", "eng");

        String text = tesseract.getUTF8Text();

        // path of your image file
        System.out.print("INDIA RECEIPT: " + text);

        BufferedReader br = new BufferedReader(new StringReader(text));
        int lineCounter = 0;
        int arrayCounter = 0;
        String line = null;
        ArrayList<String> splitUpLines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            String lineToLowerCase = line.toLowerCase();
            String[] lineSplitUp = line.split(" ");
            for(int i = 0; i < lineSplitUp.length; i ++){
                String lineSplitUpNoParan = lineSplitUp[i].replaceAll("\\{", "");
                if(!(isInt(lineSplitUpNoParan)) && (lineSplitUp[i].length() == 1) && !(lineSplitUpNoParan.equals("@")) ){
                    System.out.println("FOUND A BAD ONE: " + lineSplitUpNoParan + " FROM LINE: " + line);
                    if(lineSplitUpNoParan.equals("*")) {
                        System.out.println("MADE IT *: " + line+ " - " + line.replaceAll("\\*", ""));
                        line = line.replaceAll(("\\*"), "");
                    }
                    else{
                        line = line.replace((lineSplitUpNoParan), "");
                    }
                    System.out.println("WHAT WE MADED AFTERWORDS: : " +  line);
                }
            }
//            if(lineToLowerCase.contains("\"[^\\\\d.]\"ach")){
            line = line.replaceAll("\"[^\\\\d.]\"ach", "Each");
//            }
            if(lineToLowerCase.contains("ach") && !(String.valueOf(lineToLowerCase.charAt(lineToLowerCase.indexOf("ach")-1)).equals("e"))){
                System.out.println("LINE BEFORE: " + line);
                line = line.substring(0, lineToLowerCase.indexOf("ach")-1) + "E" + line.substring(lineToLowerCase.indexOf("ach"));
                System.out.println("LINE AFTER: " + line);
            }
            if(!line.toLowerCase().contains("ach") && line.contains("$") && (line.replace("\\s","").split(" ").length == 1)){ // for single lines like 7.99 being alone through incorrect readings
                // replacing the last line with the current line as well
                int lastSplitUpLinesLineIndex = splitUpLines.size()-1;
                System.out.println("WHAT WE SET:  " + ( splitUpLines.get(lastSplitUpLinesLineIndex) + line));
                splitUpLines.set(lastSplitUpLinesLineIndex, splitUpLines.get(lastSplitUpLinesLineIndex) + line);
            }
            else {
                System.out.println("WAHT WE ADDED: " + line.toUpperCase().replaceAll("\\{", "("));
                splitUpLines.add(line.toUpperCase().replaceAll("\\{", "("));
            }

        }
        // checking for and deleting blank lines && voided entry lines
        for (int i = 0; i < splitUpLines.size(); i++) {
            if (splitUpLines.get(i).isEmpty() || StringUtils.isBlank(splitUpLines.get(i))) {
                splitUpLines.remove(splitUpLines.get(i));
            }
            else if(splitUpLines.get(i).toLowerCase().replace("\\s","").contains("you saved") || splitUpLines.get(i).toLowerCase().replace("\\s","").contains("saved") ){
                System.out.println("WHAT WE DELETED: " + splitUpLines.get(i));
                splitUpLines.remove(splitUpLines.get(i));
            }
        }

        Integer  pos_containing_line = null;
        System.out.println("SPLIT UP LINES: " + splitUpLines);
        String pos_amount_line_str = null;
        Integer st_containing_line = null;
        String st_amount_line_str = null;
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy ");
        Date dateObj = new Date();
        String date = formatter.format(dateObj); // by default, we will make the user's receipt date the current day just in case the scanner can't find it
        System.out.println("INITIAL DATE: "+ date);

        // looking for important parameters and places in the receipt to find our items data
        for (int i = 0; i < splitUpLines.size(); i++) {
            boolean foundInProperDateLine = false;
            System.out.println("SPLIT UP LINE " + i + ": " + splitUpLines.get(i));
            if (splitUpLines.get(i).toLowerCase().contains("pos") || splitUpLines.get(i).toLowerCase().contains("terminal"))  {
                System.out.println("FOUND ITTT: " + splitUpLines.get(i));
                if (st_containing_line == null) {
                    pos_containing_line = i;
                    pos_amount_line_str = splitUpLines.get(i);
                }
                System.out.println("POS LINE CHECKING: " + pos_amount_line_str);
            }// pos line in india bazaar receipts is always two lines before the start of the user items
            if (splitUpLines.get(i).toLowerCase().contains("subtotal") || splitUpLines.get(i).toLowerCase().contains("sub total") || splitUpLines.get(i).toLowerCase().contains("total") ) {
                if (st_containing_line == null) {
                    st_containing_line = i;
                    st_amount_line_str = splitUpLines.get(i);
                }
                System.out.println("SUBTOTAL LINE CHECKING: " + st_amount_line_str);
            }
            // india bazaar gives a proper line where date and time of purchase are recorded and the following ifs statements are for the proper date line and sheer search of a date containing if can't find the proper one
            if(splitUpLines.get(i).toLowerCase().contains("date/time")){
                System.out.println("MADE IT DATE CALCULATION PROPER DATE LINE: " + splitUpLines.get(i));
                String[] splitUpLineSplitUp = splitUpLines.get(i).replaceAll("' ", "").split(" ");
                date = splitUpLineSplitUp[1];
                foundInProperDateLine = true;
                System.out.println("DATE PROPER DATE LINE: " + date);
            }
            if (!foundInProperDateLine && StringUtils.countMatches(splitUpLines.get(i), "/") == 2 && isInt(splitUpLines.get(i).split("/")[0].replace("\\s",""))) {
                System.out.println("MADE IT DATE CALCULATION(NOT PROPER LINE): " + splitUpLines.get(i));
                String[] splitUpLineSplitUp = splitUpLines.get(i).replaceAll("' ", "").split(" ");
                String splitUpLinesDateLineFirstPart = splitUpLineSplitUp[0];
                if (StringUtils.countMatches(splitUpLinesDateLineFirstPart, "/") == 2) {
                    date = splitUpLinesDateLineFirstPart.substring(0, 8);
                    System.out.println("DATE:  " + date);
                } else {
                    for (int i2 = 0; i2 < splitUpLineSplitUp.length; i2++) {
                        if (splitUpLineSplitUp[i2].length() == 8 && (StringUtils.countMatches(splitUpLineSplitUp[i2], "/") == 2)) {
                            date = splitUpLineSplitUp[i];
                            System.out.println("DATE 2:  " + date);
                        }
                    }
                }
            }
        }

        System.out.println("FINAL DATE: " + date);

        System.out.println("ST LINE: " + st_containing_line + " pod containing:  " + pos_amount_line_str);
        for (int i = 0; i < splitUpLines.size(); i++) {
            if (i > (pos_containing_line + 1) && (i < st_containing_line)) {
//                System.out.println("WHAT IS BEING ADDED : " + splitUpLines.get(i));
                user_items.add(splitUpLines.get(i));

            }
        }
        System.out.println("******************************FINAL OUTPUT ****************************************************");

        String tab = "                ";
        for(int i = 0; i < user_items.size(); i++){
            String user_item = user_items.get(i);
            String user_item_lower_case = user_item.toLowerCase();
            String name = "";
            String india_bazaar_print_out_base = "INDIA BAZAAR"+ tab + date;
            double quantity;
            double unit_price;
            double total;
            if(!user_item.contains("$") && !(user_item_lower_case.contains("each"))){ // items that have there price on another line
                int nextLineIndex = user_items.indexOf(user_item) +1;
                String nextLine = user_items.get(nextLineIndex);
                String nextLineLowerCase = nextLine.toLowerCase();
                if(user_item_lower_case.contains(" ea")){ // for lines like LEMON ea
                    name = user_item.substring(0, user_item_lower_case.indexOf(" ea"));
                    if((nextLine.trim().split(" "))[0].toLowerCase().contains("each")){ /// if the next line starts with (each) or each means it has a quantity of one
                        quantity = 1.0;
                        String str_unit_price = replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(nextLine.substring(nextLineLowerCase.indexOf("each")+5)); // + 5 because sometimes it will be (Each) unit_price and sometimes Each unit_price
                        unit_price = Double.parseDouble(str_unit_price);
                        if(!str_unit_price.contains(".")){
                            unit_price = unit_price / 100;
                        }
                    }
                    else {
                        quantity = Double.parseDouble(nextLine.substring(0, nextLineLowerCase.indexOf("each")).replace("\\s",""));
                        String str_unit_price = replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(nextLine.substring( nextLine.indexOf("@")+1, nextLineLowerCase.indexOf("/each")).replaceAll("\\$", ""));
                        unit_price = Double.parseDouble(str_unit_price);
                        if(!str_unit_price.contains(".")){
                            unit_price = unit_price / 100;
                        }
                    }
                    total = quantity * unit_price;
                    try {
                        JSONObject previousJsonObj = new JSONObject(Constants.USER_ITEMS_JSON_FILE_PATH);
                        JSONArray array = previousJsonObj.getJSONArray("user_items");
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("user_item_store", "INDIA BAZAAR");
                        jsonObj.put("user_item_date", date);
                        jsonObj.put("user_item_name", name);
                        jsonObj.put("user_item_quantity", quantity);
                        jsonObj.put("user_item_total_amount_paid", round(total,2));
                        jsonObj.put("user_item_unit_price", unit_price);
                        array.put(jsonObj);
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                    System.out.println(india_bazaar_print_out_base + tab +  name + tab + quantity + tab + round(total,2) + tab + unit_price);
                }
                else if(user_item_lower_case.contains("/lb")){
                    //for lines like SMALL CHILLI /LB 0.17 Oound @ 1.49/Pound $0.25
                    name = user_item.substring(0, user_item_lower_case.indexOf("/lb"));
                    String stringQuan = nextLine.substring(0, nextLine.toLowerCase().indexOf("pound"));
                    String[] stringQuanSplitUp = stringQuan.split(" ");
                    if(stringQuanSplitUp.length >= 2){
                        stringQuan = stringQuanSplitUp[stringQuanSplitUp.length -1];
                    }
                    quantity  = Double.parseDouble(replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(stringQuan));
                    if(!stringQuan.contains(".")){
                        quantity  = quantity / 100;
                    }
                    unit_price = Double.parseDouble(replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(nextLine.substring( nextLine.indexOf("@")+1, nextLine.toLowerCase().indexOf("/pound"))));
                    total = quantity * unit_price;
                    try {
                        JSONObject previousJsonObj = new JSONObject(Constants.USER_ITEMS_JSON_FILE_PATH);
                        JSONArray array = previousJsonObj.getJSONArray("user_items");
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("user_item_store", "INDIA BAZAAR");
                        jsonObj.put("user_item_date", date);
                        jsonObj.put("user_item_name", name);
                        jsonObj.put("user_item_quantity", quantity);
                        jsonObj.put("user_item_total_amount_paid", round(total,2));
                        jsonObj.put("user_item_unit_price", unit_price);
                        array.put(jsonObj);
                    } catch (JSONException exception) {
                        exception.printStackTrace();
                    }
                    System.out.println(india_bazaar_print_out_base + tab +  name + tab + quantity + tab +  round(total,2) + tab + unit_price);
                }
                else {
                    if(nextLineLowerCase.contains("(each)")) {//for lines like ANDHRA GONGURA PICKLE (next line) MOTHER'S RECIPE 300G (Each) $2.99
                        name = user_item +  " "  + nextLine.substring(0, nextLine.toLowerCase().indexOf("(each)"));
//                        System.out.println("USER ITEM CASE 3: " + user_item + " - NAME: " + name);
                        quantity = 1.0; // quantity always one
                        String str_unit_price = replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(nextLine.substring(nextLineLowerCase.indexOf("each)")+5).replace("$", ""));
                        System.out.println("LINE: " +  nextLine);
                        unit_price = Double.parseDouble(str_unit_price);
                        if(!str_unit_price.contains(".")){
                            unit_price  = unit_price / 100;
                        }
                        total = unit_price;
                        try {
                            JSONObject previousJsonObj = new JSONObject(Constants.USER_ITEMS_JSON_FILE_PATH);
                            JSONArray array = previousJsonObj.getJSONArray("user_items");
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("user_item_store", "INDIA BAZAAR");
                            jsonObj.put("user_item_date", date);
                            jsonObj.put("user_item_name", name);
                            jsonObj.put("user_item_quantity", quantity);
                            jsonObj.put("user_item_total_amount_paid", round(total,2));
                            jsonObj.put("user_item_unit_price", unit_price);
                            array.put(jsonObj);
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                        System.out.println(india_bazaar_print_out_base + tab +  name + tab + quantity + tab + total + tab + unit_price);
                    }
                    else if(nextLineLowerCase.contains("each") && !(nextLineLowerCase.contains("(each)"))){ //  for lines like GUVAR VADILAL 312G - NAME: GUVAR VADILAL 312G(next line) 2 Each @ $2.49/Each $4 98
                        name = user_item;
                        try {
                            quantity = Double.parseDouble(nextLine.substring(0, nextLineLowerCase.indexOf("each")));
                        } catch (Exception exception){
                            String[] nextLineSplitUp =  nextLine.substring(0, nextLineLowerCase.indexOf("each")).split(" ");
//                             System.out.println("NEXT LINE: " + nextLine);
                            quantity =Double.parseDouble(nextLineSplitUp[nextLineSplitUp.length - 1]);
                        }
//                         System.out.println("LINE 2: " + nextLine);
                        String str_unit_price;
                        try {
                            str_unit_price = replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(nextLine.substring(nextLine.indexOf("@") + 1, nextLineLowerCase.indexOf("/each")));
                        } catch (Exception exception){
                            str_unit_price = replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(nextLine.substring(nextLine.indexOf("@") + 1, nextLineLowerCase.indexOf("/")));
                        }
                        unit_price = Double.parseDouble(str_unit_price);
                        if(!str_unit_price.contains(".")){
                            unit_price  = unit_price / 100;
                        }
                        total = quantity * unit_price;
                        // TODO replace the println statements with the appending of new json objects to the user_items_json.json file
                        try {
                            JSONObject previousJsonObj = new JSONObject(Constants.USER_ITEMS_JSON_FILE_PATH);
                            JSONArray array = previousJsonObj.getJSONArray("user_items");
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("user_item_store", "INDIA BAZAAR");
                            jsonObj.put("user_item_date", date);
                            jsonObj.put("user_item_name", name);
                            jsonObj.put("user_item_quantity", quantity);
                            jsonObj.put("user_item_total_amount_paid", round(total,2));
                            jsonObj.put("user_item_unit_price", unit_price);
                            array.put(jsonObj);
                        } catch (JSONException exception) {
                            exception.printStackTrace();
                        }
                        System.out.println(india_bazaar_print_out_base + tab +  name + tab + quantity + tab +  round(total,2) + tab + unit_price);
                    }
                }
                user_items.set(nextLineIndex, nextLine + " INDEPENDENT PRICE LINE");
            }
            else if (user_item.contains("$") && !(user_item.contains("@")) && !(user_item.contains("INDEPENDENT PRICE LINE"))){ // item lines that have their price within them that aren't the corresponding price lines themselves of items split in 2 lines
                String after_each_price_str = replaceDollarSignSpacesNonNumericCharactersAndOtherUnwantedCharacters(user_item.substring(user_item_lower_case.indexOf("each)") + 5));
                unit_price = Double.parseDouble(after_each_price_str);
                if(!after_each_price_str.contains(".")){
                    unit_price = unit_price / 100;
                }
                quantity = 1.0;
                total = unit_price;
                if(user_item_lower_case.contains("(each)") && !(user_item_lower_case.contains(" ea "))){
                    name  = user_item.substring(0, user_item_lower_case.indexOf("(each)"));
//                     System.out.println("USER ITEM CASE 5: " + user_item + " - NAME: " + name);

                }
                else if(user_item_lower_case.contains(" ea") && (user_item_lower_case.contains("(each)"))) {
                    name = user_item.substring(0, user_item_lower_case.indexOf(" ea (each)"));
//                     System.out.println("USER ITEM CASE 6: " + user_item + " - NAME: " + name);
                }
                else{
                    if(!user_item.contains("@")) {
                        System.out.println("DIFFERENT KIND OF LINE****: " + user_item);
                    }
                }
                try {
                    JSONObject previousJsonObj = new JSONObject(Constants.USER_ITEMS_JSON_FILE_PATH);
                    JSONArray array = previousJsonObj.getJSONArray("user_items");
                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("user_item_store", "INDIA BAZAAR");
                    jsonObj.put("user_item_date", date);
                    jsonObj.put("user_item_name", name);
                    jsonObj.put("user_item_quantity", quantity);
                    jsonObj.put("user_item_total_amount_paid", round(total,2));
                    jsonObj.put("user_item_unit_price", unit_price);
                    array.put(jsonObj);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }
                // logging
                System.out.println(india_bazaar_print_out_base + tab +  name + tab + quantity + tab +  round(total,2) + tab + unit_price);

            }
        }
        System.out.println("***********************************************************************************************");
    }

}
