package com.example.android.receiptreader;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class RepItemsActivity extends AppCompatActivity {
    RepItemAdapter repItemAdapter;
    ListView repItemsListView;
    Boolean occupied;
    ArrayList<RepItem> repItems;
    String searchViewFilter = "";
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  ArrayList<RepItem> filter(ArrayList<RepItem> repItems, String query){
        Integer searchQueryLength = query.length();
        ArrayList<RepItem> newRepItemList = new ArrayList<>();
        repItemAdapter = new RepItemAdapter(getApplicationContext(), newRepItemList);
        for(int i = 0; i < repItems.size(); i++){
            RepItem repItem1 =  repItems.get(i);
            try{
                if(repItem1.getName().substring(0,searchQueryLength).equalsIgnoreCase(query)){
                    newRepItemList.add(repItem1);
                }
                else if(query.length() >= 3 && repItem1.getName().toLowerCase().contains(query.toLowerCase())){
                    newRepItemList.add(repItem1);
                }
            }
            catch (StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
            }

        }
        return newRepItemList;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void update(){
        int index = repItemsListView.getFirstVisiblePosition();
        View v = repItemsListView.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        System.out.println("RUNNING REP ITEMS BEFORE: " + repItems.size());
        for (RepItem item: repItems) {
            System.out.println(item.getName());
        }
        try {
            if(!searchViewFilter.isEmpty()){
                repItems = filter(QueryUtils.getRepItems(), searchViewFilter);
            } else{
                repItems = QueryUtils.getRepItems();
            }
        } catch (Exception e) {
            System.out.println("RUNNING ONCHANGED  EXCEPTION");
            e.printStackTrace();
        }
        System.out.println("RUNNING ONCHANGED SHOPPING LIST ITEMS AFTER: " + repItems.size());
        for (RepItem item: repItems
        ) {
            System.out.println(item.getName());
        }
        repItemAdapter = new RepItemAdapter(getApplicationContext(), repItems);
        resetAdapter();

        repItemsListView.setSelectionFromTop(index, top);
    }

    public void resetAdapter(){
        repItemsListView.setAdapter(repItemAdapter);
    }

    public void errorDialog(String message){
        if(!occupied) {
            occupied = true;
            androidx.appcompat.app.AlertDialog.Builder errorBuilder =
                    new androidx.appcompat.app.AlertDialog.Builder
                            (RepItemsActivity.this, R.style.AlertDialogCustom);
            View errorView = LayoutInflater.from(RepItemsActivity.this).inflate(
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

    public void showDialog(String title, int jsonEditCode, @Nullable String repItemName) {
        occupied = true;
        final int finalJsonEditCode = jsonEditCode;
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (RepItemsActivity.this, R.style.AlertDialogCustom);
        View view = LayoutInflater.from(RepItemsActivity.this).inflate(
                R.layout.custom_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText(title);
        ImageView icon = (ImageView) view.findViewById(R.id.imageIcon);
        TextView editText = view.findViewById(R.id.custom_dialog_edit_text);
        Button enterButton = (Button) view.findViewById(R.id.enterButton);
        if(jsonEditCode == JSONEditCodes.DELETE_REP_ITEM){
            editText.setVisibility(View.INVISIBLE);
            icon.setMinimumHeight(50);
            icon.setMinimumWidth(50);
            enterButton.setText(R.string.yes);
            icon.setImageResource(R.drawable.delete_foreground); // making the pop up icon a trash can since by default it is the edit icon
            TextView delete_text =  (TextView) view.findViewById(R.id.delete_text);
            delete_text.setVisibility(View.VISIBLE);
            delete_text.setText(String.format("Are you sure you want to delete %s, this action is permanent and cannot be undone", repItemName));
        }
        else if(finalJsonEditCode == JSONEditCodes.EDIT_REP_ITEM_NAME){
            icon.setMinimumHeight(25);
            icon.setMinimumWidth(25);
            editText.setText(repItemName);
            icon.setImageResource(R.drawable.ic_baseline_edit_24); // making the pop up icon a edit symbol
            editText.setVisibility(View.VISIBLE);
            enterButton.setText(getString(R.string.enter));
            view.findViewById(R.id.delete_text).setVisibility(View.INVISIBLE); // making sure the are you sure you want to delete text is not going to show up from previous possible uses

        }
        else if(finalJsonEditCode == JSONEditCodes.ADD_REP_ITEM){
            icon.setMinimumHeight(25);
            icon.setMinimumWidth(25);
            icon.setImageResource(R.drawable.ic_baseline_add_24); // making the pop up icon a edit symbol
            editText.setVisibility(View.VISIBLE);
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
                if(finalJsonEditCode == JSONEditCodes.EDIT_REP_ITEM_NAME){
                    try {
                        System.out.println("EDIT TEXT VAL SUBMIT MOMENT: "+ editText.getText());
                        QueryUtils.editRepItem(repItemName, editText.getText().toString(), getApplicationContext());
                        alertDialog.dismiss();
                        occupied = false;
                        errorDialog(getString(R.string.edit_item_name_alert_message));
                        update();
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(finalJsonEditCode == JSONEditCodes.ADD_REP_ITEM){
                    try {
                        System.out.println("REP ITEMS ARRAY LIST: " + repItems.toArray().toString());
                        boolean worked = QueryUtils.addRepItem(editText.getText().toString(), getApplicationContext());
                        if(!worked){
                            Toast.makeText(getApplicationContext(), String.format(getString(R.string.rep_item_already_exists), editText.getText().toString()), Toast.LENGTH_SHORT).show();
                        }
                        update();
                        alertDialog.dismiss();
                        occupied = false;
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(finalJsonEditCode == JSONEditCodes.DELETE_REP_ITEM){
                    try {
                        QueryUtils.deleteRepItem(repItemName, getApplicationContext());
                        update();
                        alertDialog.dismiss();
                        occupied = false;
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                alertDialog.dismiss();


            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rep_items_layout);
        try {
            repItems = QueryUtils.getRepItems();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        ImageButton add_item_button = (ImageButton) findViewById(R.id.add_image_button_ril);
        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(getString(R.string.add_rep_item), JSONEditCodes.ADD_REP_ITEM, null);

            }
        });
        SearchView searchView = findViewById(R.id.search_bar);
        Toolbar toolBar = findViewById(R.id.my_toolbar);
        repItemsListView = findViewById(R.id.rep_items_list_view);
        repItemAdapter = new RepItemAdapter(getApplicationContext(), repItems);
        repItemsListView.setAdapter(repItemAdapter);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RepItemsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        repItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RepItem repItem = (RepItem) repItemAdapter.getItem(i);
                String repItemName = repItem.getName();
                View selectedStoreView = repItemAdapter.getView(i, view, adapterView);
                ImageView editNameButton = (ImageView) selectedStoreView.findViewById(R.id.edit_name_button);
                ImageView deleteButton = (ImageView) selectedStoreView.findViewById(R.id.delete_item_button);
                ImageView purchaseHistoryButton = (ImageView) selectedStoreView.findViewById(R.id.item_rep_purchase_history);


                editNameButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(getString(R.string.edit_rep_item), JSONEditCodes.EDIT_REP_ITEM_NAME, repItemName);
                    }
                });

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(String.format(getString(R.string.delete_rep_item), repItemName), JSONEditCodes.DELETE_REP_ITEM, repItemName);


                    }
                });

                purchaseHistoryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<StoreUserItem> storeUserItemsHistory = new ArrayList<>();
                        try {
                            storeUserItemsHistory = QueryUtils.getHistoryOfShoppingListItem(repItemName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if(storeUserItemsHistory != null) {
                            Intent intent = new Intent(getApplicationContext(), ShoppingListUserItemHistoryActivity.class);
                            Bundle intentBundle = new Bundle();
                            intentBundle.putString("classComingFrom", "RepItemsActivity");
                            intentBundle.putString("title", repItemName);
                            intentBundle.putSerializable("storeUserItemsHistory", storeUserItemsHistory);
                            intent.putExtra("BUNDLE", intentBundle);
                            startActivity(intent);
                        } else{
                            Toast.makeText(getApplicationContext(), String.format(getString(R.string.item_has_no_history), repItemName), Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }




        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                    @Override
                                                    public boolean onQueryTextSubmit(String query) {
                                                        searchViewFilter = query; // updating global search view filter just in case user edits, deletes, or adds in item while running a filter in the search view
                                                        try {
                                                            repItemAdapter = new RepItemAdapter(getApplicationContext(), filter(QueryUtils.getRepItems(), query));
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        repItemsListView.setAdapter(repItemAdapter);
                                                        return false;

                                                    }

                                                    @Override
                                                    public boolean onQueryTextChange(String newText) {
                                                        searchViewFilter = newText; // updating global search view filter just in case user edits, deletes, or adds in item while running a filter in the search view
                                                        try {
                                                            repItemAdapter = new RepItemAdapter(getApplicationContext(), filter(QueryUtils.getRepItems(), newText));
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        repItemsListView.setAdapter(repItemAdapter);
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
}
