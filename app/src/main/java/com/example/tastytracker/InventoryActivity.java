package com.example.tastytracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

//Activity to display the inventory and allow user to edit their households inventory
public class InventoryActivity extends AppCompatActivity {
    private ListView listView;
    private foodDBAdapter db;
    private final String username = UserSession.getInstance().getUsername();
    private int householdID;
    Button backToInitialButton, addButton, shoppingListButton, settingsButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        TextView upperText = findViewById(R.id.upper_text);
        upperText.setText("Welcome " + username + "!");

        TextView lowerText = findViewById(R.id.lower_text);
        householdID = UserSession.getInstance().getHouseholdID();
        lowerText.setText("Inventory for household " + householdID);

        settingsButton = findViewById(R.id.settingsButton);

        //Restrict users without HH permissions from being able to access the settings
        if (!UserSession.getInstance().getPermissions().equals("HH")){
            settingsButton.setVisibility(TextView.GONE);
        }

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, ManageHouseholdActivity.class);
            startActivity(intent);
            finish();
        });

        listView = findViewById(R.id.listView);

        //Button to return the user to the initial (register/login) activity
        backToInitialButton = findViewById(R.id.backToInitialButton);
        backToInitialButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, InitialActivity.class);
            startActivity(intent);
            finish();
        });

        //Button to allow user to add an item to the inventory, moves user to the edit item activity
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, EditItemActivity.class);
            intent.putExtra("MODE", "ADD");
            intent.putExtra("RETURN", "INVENTORY");
            intent.putExtra("TOADD", "INVENTORY");
            intent.putExtra("HOUSEHOLD_ID", householdID);
            startActivity(intent);
        });

        //Button to allow user to add item in inventory to the shopping list, moves user to edit item activity
        shoppingListButton = findViewById(R.id.shoppingListButton);
        shoppingListButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, ShoppingActivity.class);
            intent.putExtra("HOUSEHOLD_ID", householdID);
            startActivity(intent);
        });

        //Load the list of current inventory
        new LoadList().execute(householdID);
    }

    //Loads the current inventory using the list adapter and db adapter
    private class LoadList extends AsyncTask<Integer, Void, ArrayList<foodItem>> {
        @Override
        protected ArrayList<foodItem> doInBackground(Integer... params) {
            if (params.length == 0) {
                return new ArrayList<>();
            }

            int householdID = params[0];
            db = new foodDBAdapter(InventoryActivity.this);
            db.open(householdID);
            return db.getInventoryOrRequestItems(householdID, "_inventoryItems");
        }

        @Override
        protected void onPostExecute(ArrayList<foodItem> inventoryList) {
            super.onPostExecute(inventoryList);
            InventoryAdapter adapter = new InventoryAdapter(InventoryActivity.this, inventoryList, householdID);
            listView.setAdapter(adapter);
            db.close();
        }
    }

}

