package com.example.android.receiptreader;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class ShoppingListUserItemsActivity extends AppCompatActivity {
    ShoppingListUserItemAdapter shoppingListUserItemAdapter;
    TextView resultsForshoppingListUserItemsView;
    ListView shoppingListUserItemsListView;
    int quantityMicrophoneState = 0;
    int unitPriceMicrophoneState = 0;
    ArrayList<ShoppingListUserItem> shoppingListUserItems;
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
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    try {
                        QueryUtils.deleteShoppingListUserItem(originalName, shoppingListName);
                        shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
                        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems);
                        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                    } catch (IOException e) {
                        e.printStackTrace();
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
                                                                        QueryUtils.reorderShoppingListItem(shoppingListName, shoppingListToMoveTo, originalName);
                                                                        shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
                                                                        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems);
                                                                        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                                                    } catch (ParseException | IOException e) {
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


    private void speakWithVoiceDialog(String shoppingListUserItemName) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder
                        (ShoppingListUserItemsActivity.this, R.style.AlertDialogCustom);
        View view = LayoutInflater.from(ShoppingListUserItemsActivity.this).inflate(
                R.layout.voice_input_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        } else {
            builder.setView(view);
            final androidx.appcompat.app.AlertDialog alertDialog = builder.create();
            Button cancel_button = view.findViewById(R.id.cancel_button);
            Button enter_button = view.findViewById(R.id.enterButton);
            EditText quantityEditText = view.findViewById(R.id.quantity_edit_text);
            EditText unitPriceEditText = view.findViewById(R.id.unit_price_edit_text);
            ImageView quantityMicrophone = view.findViewById(R.id.quantity_microphone);
            ImageView unitPriceMicrophone = view.findViewById(R.id.unit_price_microphone);
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            enter_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, EnglishWordsToNumbers.replaceNumbers(String.valueOf(quantityEditText.getText())), unitPriceEditText.getText());


                }
            });
            SpeechRecognizer quantitySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            SpeechRecognizer unitPriceSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            final Intent unitPriceRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            // when the users click on the textviews next to the edit texts for hold details, listening starts
            quantityMicrophone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(quantityMicrophoneState == 0){
                        quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                        quantitySpeechRecognizer.startListening(speechRecognizerIntent);
                        quantityMicrophoneState = 1;
                    }
                    else if(quantityMicrophoneState == 1){
                        quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                        quantitySpeechRecognizer.stopListening();
                        quantityMicrophoneState = 0;

                    }
                }
            });

            quantitySpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

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

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> data = bundle.getStringArrayList(quantitySpeechRecognizer.RESULTS_RECOGNITION);
                    quantityEditText.setText(data.get(0));
                }

                @Override
                public void onPartialResults(Bundle bundle) {
                    ArrayList<String> data = bundle.getStringArrayList(quantitySpeechRecognizer.RESULTS_RECOGNITION);
                    quantityEditText.setText(data.get(0));

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });

            unitPriceMicrophone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(unitPriceMicrophoneState == 0){
                        unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
                        unitPriceSpeechRecognizer.startListening(unitPriceRecognizerIntent);
                        unitPriceMicrophoneState = 1;
                    }
                    else if(unitPriceMicrophoneState == 1){
                        unitPriceMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                        unitPriceSpeechRecognizer.stopListening();
                        unitPriceMicrophoneState = 0;

                    }

                }
            });

            unitPriceSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {

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

                }

                @Override
                public void onError(int i) {

                }

                @Override
                public void onResults(Bundle bundle) {
                    ArrayList<String> data = bundle.getStringArrayList(unitPriceSpeechRecognizer.RESULTS_RECOGNITION);
                    unitPriceEditText.setText(data.get(0));

                }

                @Override
                public void onPartialResults(Bundle bundle) {
                    ArrayList<String> data = bundle.getStringArrayList(unitPriceSpeechRecognizer.RESULTS_RECOGNITION);
                    unitPriceEditText.setText(data.get(0));

                }

                @Override
                public void onEvent(int i, Bundle bundle) {

                }
            });

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, getString(R.string.audio_permission_granted), Toast.LENGTH_SHORT);
            }else{
                Toast.makeText(this, getString(R.string.cant_use_this_feature), Toast.LENGTH_SHORT);

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shopping_list_items_layout);
        String shoppingListName = getIntent().getStringExtra("shoppingListName");
        shoppingListUserItemsListView = findViewById(R.id.shopping_list_user_items_list_view);
        resultsForshoppingListUserItemsView = findViewById(R.id.results_for_user_item_text);
        SearchView searchView = findViewById(R.id.search_bar);
        Toolbar toolBar = findViewById(R.id.my_toolbar);
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(shoppingListName);
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
            }
        });
        ImageButton add_item_button = findViewById(R.id.add_image_button);
        add_item_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShoppingListUserItemsActivity.this, AddShoppingListUserItemActivity.class);
                intent.putExtra("shoppingListName", shoppingListName);
                startActivity(intent);
            }
        });
        shoppingListUserItems = null;
        try {
            shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(this, shoppingListUserItems);
        shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);

        hideSoftKeyboard(this);
        searchView.setIconified(false);

        ArrayList<ShoppingListUserItem> finalshoppingListUserItems = shoppingListUserItems;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                                              @Override
                                              public boolean onQueryTextSubmit(String query) {
                                                  Integer searchQueryLength = query.length();
                                                  ArrayList<ShoppingListUserItem> newshoppingListUserItemList = new ArrayList<>();
                                                  ShoppingListUserItemAdapter shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), newshoppingListUserItemList);
                                                  for(int i = 0; i < finalshoppingListUserItems.size(); i++){
                                                      ShoppingListUserItem shoppingListUserItem =  finalshoppingListUserItems.get(i);
                                                      try{
                                                          if(shoppingListUserItem.getName().substring(0,searchQueryLength).equalsIgnoreCase(query)){
                                                              newshoppingListUserItemList.add(shoppingListUserItem);
                                                          }
                                                      }
                                                      catch (StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                      }

                                                  }
                                                  shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                                  return false;

                                              }

                                              @Override
                                              public boolean onQueryTextChange(String newText) {
                                                  Integer searchQueryLength = newText.length();
                                                  ArrayList<ShoppingListUserItem> newMainGodList = new ArrayList<ShoppingListUserItem>();
                                                  ShoppingListUserItemAdapter shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(),newMainGodList);
                                                  for(int i = 0; i < finalshoppingListUserItems.size(); i++){
                                                      ShoppingListUserItem shoppingListUserItem = (ShoppingListUserItem) finalshoppingListUserItems.get(i);
                                                      try{
                                                          if(shoppingListUserItem.getName().substring(0, searchQueryLength).equalsIgnoreCase(newText)){
                                                              newMainGodList.add(shoppingListUserItem);
                                                          }
                                                      }
                                                      catch(StringIndexOutOfBoundsException exception){
//                        catching the StringIndexOutOfBounds exception when the user uses line/cross texting
                                                      }
                                                  }
                                                  // if user has deleted all their text
                                                  if (newText.isEmpty()) {
                                                      resultsForshoppingListUserItemsView.setVisibility(View.GONE);
                                                  }
                                                  else {
                                                      resultsForshoppingListUserItemsView.setText("Results for " + newText);
                                                      resultsForshoppingListUserItemsView.setVisibility(View.VISIBLE);
                                                  }
                                                  shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
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
        shoppingListUserItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShoppingListUserItem shoppingListUserItem = (ShoppingListUserItem) shoppingListUserItemAdapter.getItem(i);
                String shoppingListUserItemName = shoppingListUserItem.getName();
                View selectedStoreView = shoppingListUserItemAdapter.getView(i, view, adapterView);
                ImageView historyButton = (ImageView) selectedStoreView.findViewById(R.id.history_button_sl_item);
                ImageView duplicateIndicator = (ImageView) selectedStoreView.findViewById(R.id.duplicate_indicator);
                ImageView microphoneButton = (ImageView) selectedStoreView.findViewById(R.id.record_details_button);
                ImageView reorderButton = (ImageView) selectedStoreView.findViewById(R.id.reorder_item_button);
                ImageView deleteButton = (ImageView) selectedStoreView.findViewById(R.id.delete_item_button);
                ImageView increaseQuantityButton = (ImageView) selectedStoreView.findViewById(R.id.quantity_add_button);
                ImageView decreasedQuantityButton = (ImageView) selectedStoreView.findViewById(R.id.quantity_minus_button);
                ArrayList<ShoppingList> shoppingListsContainingSl = shoppingListUserItem.getOtherShoppingListsExistingIn();
                if(shoppingListsContainingSl.size() > 1) {
                    duplicateIndicator.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(ShoppingListUserItemsActivity.this, ShoppingListsHistoryActivity.class);
                            Bundle args = new Bundle();
                            args.putString("shoppingListUserItemName", shoppingListUserItemName);
                            args.putString("shoppingListName", shoppingListName);
                            if(getIntent().getStringExtra("originalNavPathSLUTIShoppingList") != null){
                                System.out.println("ORIGINALNAVPATH NAME: " + getIntent().getStringExtra("originalNavPathSLUTIShoppingList") );
                                intent.putExtra("originalNavPathSLUTIShoppingList", getIntent().getStringExtra("originalNavPathSLUTIShoppingList"));
                            }else {
                                System.out.println("ORIGINALNAVPATH NAME REAL START: " + shoppingListName);
                                intent.putExtra("originalNavPathSLUTIShoppingList", shoppingListName);

                            }args.putSerializable("shoppingListsContainingSlItem", shoppingListsContainingSl);
                            intent.putExtra("BUNDLE", args);
                            startActivity(intent);
                        }
                    });
                }

                ArrayList<StoreUserItem> storeUserItemsHistory = shoppingListUserItem.getStoreUserItemsHistory();

                if(storeUserItemsHistory != null){
                    System.out.println(shoppingListUserItemName + " HAS  HISTORY");
                    historyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ArrayList<StoreUserItem> storeUserItemsHistory = shoppingListUserItem.getStoreUserItemsHistory();
                            Intent intent = new Intent(ShoppingListUserItemsActivity.this, ShoppingListUserItemHistoryActivity.class);
                            Bundle args = new Bundle();
                            args.putString("classComingFrom", "ShoppingListUserItemsActivity");
                            args.putString("title", shoppingListUserItemName);
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
                            startActivity(intent);
                        }
                    });
                }

                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            showDialog(getString(R.string.delete_shopping_list_item), shoppingListUserItemName, shoppingListName, JSONEditCodes.DELETE_SHOPPING_LIST_ITEM);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });

                reorderButton.setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        try {
                            showDialog(getString(R.string.reorder_shopping_list), shoppingListUserItemName, shoppingListName, JSONEditCodes.REORDER_SHOPPING_LIST_ITEM);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });

                increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
                    // increment quantity and update the adapter
                    @Override
                    public void onClick(View view) {
                        try {
                            QueryUtils.increaseShoppingListItemQuantity(shoppingListName, shoppingListUserItemName);
                            shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems);
                            shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                        } catch (ParseException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                decreasedQuantityButton.setOnClickListener(new View.OnClickListener() {
                    // increment quantity and update the adapter
                    @Override
                    public void onClick(View view) {
                        try {
                            QueryUtils.decreaseShoppingListItemQuantity(shoppingListName, shoppingListUserItemName);
                            shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems);
                            shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                        } catch (ParseException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                microphoneButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        speakWithVoiceDialog(shoppingListUserItemName);
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
}
