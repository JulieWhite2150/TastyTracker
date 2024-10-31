package com.example.tastytracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

public class EditItemActivity extends AppCompatActivity {
    private EditText itemNameEditText;
    private EditText itemQuantityEditText;
    private EditText itemUnitEditText;
    private Button saveButton;
    private int householdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemQuantityEditText = findViewById(R.id.itemQuantityEditText);
        itemUnitEditText = findViewById(R.id.itemUnitEditText); // New EditText for unit
        saveButton = findViewById(R.id.saveButton);

        householdID = getIntent().getIntExtra("HOUSEHOLD_ID", -1);

        String itemName = getIntent().getStringExtra("ITEM_NAME");
        int itemQuantity = getIntent().getIntExtra("ITEM_QUANTITY", 0);
        String itemUnit = getIntent().getStringExtra("ITEM_UNIT"); // Get unit from Intent

        if (itemName != null) {
            itemNameEditText.setText(itemName);
            itemQuantityEditText.setText(String.valueOf(itemQuantity));
            itemUnitEditText.setText(itemUnit); // Set unit if editing
        }

        saveButton.setOnClickListener(v -> {
            String itemNameInput = itemNameEditText.getText().toString().trim();
            String itemQuantityInput = itemQuantityEditText.getText().toString().trim();
            String itemUnitInput = itemUnitEditText.getText().toString().trim();

            if (itemNameInput.isEmpty() || itemQuantityInput.isEmpty() || itemUnitInput.isEmpty()) {
                Toast.makeText(EditItemActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            saveOrUpdateItem(itemNameInput, Integer.parseInt(itemQuantityInput), itemUnitInput);

            Intent intent = new Intent(EditItemActivity.this, InventoryActivity.class);
            intent.putExtra("HOUSEHOLD_ID", householdID);
            startActivity(intent);
            finish();
        });
    }

    private void saveOrUpdateItem(String itemName, int itemQuantity, String unit) {
        trackerDBAdapter dbAdapter = new trackerDBAdapter(this);
        dbAdapter.open(householdID);

        if (dbAdapter.itemExists(householdID, itemName)) {
            dbAdapter.updateCurrentItem(householdID, itemName, itemQuantity, unit);
        } else {
            dbAdapter.insertCurrentItem(householdID, itemName, itemQuantity, unit);
        }

        dbAdapter.printCurrentItems(householdID);
        dbAdapter.close();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("ITEM_NAME", itemName);
        resultIntent.putExtra("ITEM_QUANTITY", itemQuantity);
        resultIntent.putExtra("ITEM_UNIT", unit);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}



