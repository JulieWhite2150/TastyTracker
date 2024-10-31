package com.example.tastytracker;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity {
    private ListView listView;
    private CustomAdapter adapter;
    private trackerDBAdapter db;
    private String username;
    private int householdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        TextView upperText = findViewById(R.id.upper_text);
        username = getIntent().getStringExtra("USERNAME");
        upperText.setText("Welcome " + username + "!");

        TextView lowerText = findViewById(R.id.lower_text);
        householdID = getIntent().getIntExtra("HOUSEHOLD_ID", -1);
        lowerText.setText("Inventory for household " + householdID);

        listView = findViewById(R.id.listView);

        Button backToInitialButton = findViewById(R.id.backToInitialButton);
        backToInitialButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, InitialActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, EditItemActivity.class);
            intent.putExtra("HOUSEHOLD_ID", householdID);
            startActivity(intent);
        });

        Button shoppingListButton = findViewById(R.id.shoppingListButton);
        shoppingListButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, ShoppingActivity.class);
            intent.putExtra("HOUSEHOLD_ID", householdID);
            startActivity(intent);
        });

        new LoadList().execute(householdID);
    }

    private class LoadList extends AsyncTask<Integer, Void, ArrayList<foodItem>> {
        @Override
        protected ArrayList<foodItem> doInBackground(Integer... params) {
            if (params.length == 0) {
                return new ArrayList<>(); // Return empty list if no householdID is passed
            }

            int householdID = params[0];
            db = new trackerDBAdapter(InventoryActivity.this);
            db.open(householdID);
            return db.getAllItems(householdID);
        }

        @Override
        protected void onPostExecute(ArrayList<foodItem> inventoryList) {
            super.onPostExecute(inventoryList);
            adapter = new CustomAdapter(InventoryActivity.this, inventoryList, householdID);
            listView.setAdapter(adapter);
            db.close();
        }
    }
}

