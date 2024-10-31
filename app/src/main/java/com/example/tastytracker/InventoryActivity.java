package com.example.tastytracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class InventoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        TextView text = findViewById(R.id.invtext);

        // Retrieve the householdID from the Intent
        int householdID = getIntent().getIntExtra("HOUSEHOLD_ID", -1);

        // Display the household ID in the TextView
        text.setText("Inventory for household " + householdID);
    }
}
