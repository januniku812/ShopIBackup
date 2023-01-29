package com.example.android.receiptreader;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateTimePatternGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import static android.os.Build.VERSION_CODES.O;

public class ShoppingListUserItemsActivity extends AppCompatActivity {
    ShoppingListUserItemAdapter shoppingListUserItemAdapter;
    TextView resultsForshoppingListUserItemsView;
    ListView shoppingListUserItemsListView;
    String shoppingListName;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("@onactivityresult: " + requestCode);
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
        TextView choseStoreTextView = view.findViewById(R.id.chose_a_store_text_view);
        ListView choseStoreListView = view.findViewById(R.id.chose_stores_list_view);
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        view.setMinimumWidth(width/2);
        choseStoreTextView.setVisibility(View.GONE);
        choseStoreListView.setVisibility(View.GONE); // by default the listview that will be populated should be gone
        builder.setView(view);
            Button cancel_button = view.findViewById(R.id.cancel_button);
            Button enter_button = view.findViewById(R.id.enterButton);
            EditText quantityEditText = view.findViewById(R.id.quantity_edit_text);
            EditText unitPriceEditText = view.findViewById(R.id.unit_price_edit_text);
            ImageView quantityMicrophone = view.findViewById(R.id.quantity_microphone);
            ImageView unitPriceMicrophone = view.findViewById(R.id.unit_price_microphone);

        SpeechRecognizer quantitySpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        SpeechRecognizer unitPriceSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent quantitySpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        quantitySpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        quantitySpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS , 800);
        quantitySpeechRecognizerIntent .putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        final Intent unitPriceRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        unitPriceRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS , 800);
        // when the users click on the textviews next to the edit texts for hold details, listening starts
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
                    System.out.println("@onResults - quantity: " + data.get(0));
                    if (data.get(0).matches(".*[a-z].*")) {
                        System.out.println("A_Z CONTIANING ONE: " + data.get(0));
                        if (EnglishWordsToNumbers.replaceNumbers(data.get(0)).equals("000")) {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT);
                        } else {
                            quantityEditText.setText(EnglishWordsToNumbers.replaceNumbers(data.get(0)));
                        }
                    } else {
                        quantityEditText.setText(data.get(0));

                    }
                }
                catch (Exception e){
                    quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_speech_detected), Toast.LENGTH_SHORT);
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
                    if (data.get(0).matches(".*[a-z].*")) {
                        if (EnglishWordsToNumbers.replaceNumbers(data.get(0)).equals("000")) {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT);
                        } else {
                            quantityEditText.setText(EnglishWordsToNumbers.replaceNumbers(data.get(0)));
                        }
                    } else {
                        quantityEditText.setText(data.get(0));

                    }
                } catch (Exception e){

                }

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    if (data.get(0).matches(".*[a-z].*")) {
                        if (EnglishWordsToNumbers.replaceNumbers(data.get(0)).equals("000")) {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT);
                        } else {
                            quantityEditText.setText(EnglishWordsToNumbers.replaceNumbers(data.get(0)));
                        }
                    } else {
                        quantityEditText.setText(data.get(0));

                    }
                } catch (Exception e){

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
                try {
                    if (data.get(0).matches(".*[a-z].*")) {
                        if (EnglishWordsToNumbers.replaceNumbers(data.get(0)).equals("000")) {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT);
                        } else {
                            unitPriceEditText.setText(EnglishWordsToNumbers.replaceNumbers(data.get(0)));
                        }
                    } else {
                        unitPriceEditText.setText(data.get(0));

                    }
                }
                catch (Exception e){
                    quantityMicrophone.setColorFilter(ContextCompat.getColor(ShoppingListUserItemsActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.SRC_IN);
                    Toast.makeText(ShoppingListUserItemsActivity.this, getString(R.string.no_speech_detected), Toast.LENGTH_SHORT);
                    final Runnable stopListeningRunnable = new Runnable() {
                        @Override
                        public void run() {
                            unitPriceSpeechRecognizer.stopListening();
                            System.out.println("MADE IT");
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
                    if (data.get(0).matches(".*[a-z].*")) {
                        if (EnglishWordsToNumbers.replaceNumbers(data.get(0)).equals("000")) {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT);
                        } else {
                            unitPriceEditText.setText(EnglishWordsToNumbers.replaceNumbers(data.get(0)));
                        }
                    } else {
                        unitPriceEditText.setText(data.get(0));

                    }
                } catch (Exception e){

                }

            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(unitPriceSpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    if (data.get(0).matches(".*[a-z].*")) {
                        if (EnglishWordsToNumbers.replaceNumbers(data.get(0)).equals("000")) {
                            Toast.makeText(ShoppingListUserItemsActivity.this, String.format(getString(R.string.couldnt_rec_value), data.get(0)), Toast.LENGTH_SHORT);
                        } else {
                            unitPriceEditText.setText(EnglishWordsToNumbers.replaceNumbers(data.get(0)));
                        }
                    } else {
                        unitPriceEditText.setText(data.get(0));

                    }
                } catch (Exception e){

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
                    String quantityToPass = quantityEditText.getText().toString();
                    System.out.println("quantityToPass: "+ quantityToPass);
                    String unitPriceToPass = unitPriceEditText.getText().toString();
                    if(quantityToPass.isEmpty()){
                        quantityToPass = "not filled";
                    }
                    if(unitPriceToPass.isEmpty()) {
                        unitPriceToPass = "not filled";
                    }
                    Long date=System.currentTimeMillis();
                    SimpleDateFormat dateFormat =new SimpleDateFormat("MM/dd/yyyy");
                    String dateStr = dateFormat.format(date);
                    if(!ifStoreProvided){
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
                                Store selectedStore =  simpleStoreListAdapter.getItem(i);
                                try {
                                    System.out.println("CALLED 1");
                                    QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, selectedStore.getStoreName(), dateStr,
                                            quantityEditText.getText().toString(), // getting the quantity text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss
                                            unitPriceEditText.getText().toString()); // getting the unit price text input again just in case they changed it before selecting a store for the json func to occur and alert dialog to dismiss

                                    shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
                                    shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems);
                                    shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                                    alertDialog.dismiss();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                    else {
                        try {
                            QueryUtils.saveDetailsOfShoppingListUserItem(shoppingListUserItemName, Constants.storeBeingShoppedIn, dateStr, quantityToPass, unitPriceToPass);

                            shoppingListUserItems = QueryUtils.getShoppingListUsersItems(shoppingListName);
                            shoppingListUserItemAdapter = new ShoppingListUserItemAdapter(getApplicationContext(), shoppingListUserItems);
                            shoppingListUserItemsListView.setAdapter(shoppingListUserItemAdapter);
                            alertDialog.dismiss();
                        } catch (Exception e) {
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
                        try {
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
}
