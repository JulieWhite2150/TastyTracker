package com.example.tastytracker;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;

public class EditItemActivity extends AppCompatActivity {
    private EditText itemNameEditText;
    private EditText itemQuantityEditText;
    private EditText itemUnitEditText;
    private int householdID;
    private String locationToAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        locationToAdd = getIntent().getStringExtra("LOCATION");
        TextView title = findViewById(R.id.title);

        //Series of if/else to set the proper instruction text depending on user context
        if (locationToAdd.equals("add_inventory")){
            title.setText("Enter information on the item to add to the inventory");
        }
        else if (locationToAdd.equals("edit_inventory")){
            title.setText("Edit information on the item in the inventory");
        }
        else if (locationToAdd.equals("shopping_inventory") || locationToAdd.equals("add_shopping")) {
            title.setText("Enter information on the item to add to the shopping list");
        }
        else{
            title.setText("Edit information on the item in the shopping list");
        }

        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemQuantityEditText = findViewById(R.id.itemQuantityEditText);
        itemUnitEditText = findViewById(R.id.itemUnitEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);

        householdID = getIntent().getIntExtra("HOUSEHOLD_ID", -1);

        String itemName = getIntent().getStringExtra("ITEM_NAME");
        double itemQuantity = getIntent().getDoubleExtra("ITEM_QUANTITY", 0.0);
        String itemUnit = getIntent().getStringExtra("ITEM_UNIT");

        if (itemName != null) {
            itemNameEditText.setText(itemName);
            itemQuantityEditText.setText(String.valueOf(itemQuantity));
            itemUnitEditText.setText(itemUnit);
        }

        //Cancel button moves the user back to the activity they were in before coming to edit
        cancelButton.setOnClickListener(v -> {
            if (locationToAdd.equals("add_inventory") || locationToAdd.equals("edit_inventory") || locationToAdd.equals("shopping_inventory")){
                Intent intent = new Intent(EditItemActivity.this, InventoryActivity.class);
                intent.putExtra("HOUSEHOLD_ID", householdID);
                startActivity(intent);
                finish();
            }
            else{
                Intent intent = new Intent(EditItemActivity.this, ShoppingActivity.class);
                intent.putExtra("HOUSEHOLD_ID", householdID);
                startActivity(intent);
                finish();
            }
        });

        //Either edits or inserts the food item into the db depending on user context.
        saveButton.setOnClickListener(v -> {
            String itemNameInput = itemNameEditText.getText().toString().trim();
            String itemQuantityInput = itemQuantityEditText.getText().toString().trim();
            String itemUnitInput = itemUnitEditText.getText().toString().trim();

            if (itemNameInput.isEmpty() || itemQuantityInput.isEmpty() || itemUnitInput.isEmpty()) {
                emptyFieldAlert();
                return;
            }

            //Makes sure that the entered quantity is rounded to two decimal places
            double roundedQuant = Math.round(Double.parseDouble(itemQuantityInput)*100.0)/100.0;

            //if the user is adding to inventory from inventory activity
            if (locationToAdd.equals("add_inventory")){
                addItem(itemNameInput, roundedQuant, itemUnitInput, "INVENTORY", "INVENTORY");
            }
            //if the user is editing inventory from inventory activity
            else if(locationToAdd.equals("edit_inventory")) {
                editItem(itemNameInput, roundedQuant, itemUnitInput, "INVENTORY");
                Intent intent = new Intent(EditItemActivity.this, InventoryActivity.class);
                intent.putExtra("HOUSEHOLD_ID", householdID);
                startActivity(intent);
                finish();
            }
            //if the user is adding to shopping list from inventory activity
            else if (locationToAdd.equals("shopping_inventory")){
                addItem(itemNameInput, roundedQuant, itemUnitInput, "SHOPPING", "INVENTORY");
            }
            //if the user is editing shopping list from shopping activity
            else if (locationToAdd.equals("edit_shopping")){
                editItem(itemNameInput, roundedQuant, itemUnitInput, "SHOPPING");
                Intent intent = new Intent(EditItemActivity.this, ShoppingActivity.class);
                intent.putExtra("HOUSEHOLD_ID", householdID);
                startActivity(intent);
                finish();
            }
            //if the user is adding to shopping list from shopping activity
            else{
                addItem(itemNameInput, roundedQuant, itemUnitInput, "SHOPPING", "SHOPPING");
            }
        });
    }

    //This method calls the db adapter method insert item given a location
    private void addItem(String itemName, double itemQuantity, String unit, String addLocation, String returnLocation){
        foodDBAdapter dbAdapter = new foodDBAdapter(this);
        dbAdapter.open(householdID);
        double quantity = dbAdapter.getItemQuant(householdID, itemName, addLocation);
        if (quantity == 0.0){
            dbAdapter.insertItem(householdID, itemName, itemQuantity, unit, false, addLocation);

            if (returnLocation.equals("INVENTORY")) {
                Intent intent = new Intent(EditItemActivity.this, InventoryActivity.class);
                intent.putExtra("HOUSEHOLD_ID", householdID);
                startActivity(intent);
                finish();
            }
            else{
                Intent intent = new Intent(EditItemActivity.this, ShoppingActivity.class);
                intent.putExtra("HOUSEHOLD_ID", householdID);
                startActivity(intent);
                finish();
            }
        }
        else{
            itemAlreadyExistsAlert();
        }
        dbAdapter.close();
    }

    //This method calls the db adapter method to edit item given a location
    private void editItem(String itemName, double itemQuantity, String unit, String location){
        foodDBAdapter dbAdapter = new foodDBAdapter(this);
        dbAdapter.open(householdID);
        dbAdapter.updateItem(householdID, itemName, itemQuantity, unit, location);
        dbAdapter.close();
    }

    //Tell the user that household id entered does not exist, ack is needed so AlertDialog used
    private void emptyFieldAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("At Least One Field Is Empty")
                .setMessage("You must enter information for item name, quantity, and unit.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //Tell the user that the item they are entering is already in table, ack is needed so AlertDialog used
    private void itemAlreadyExistsAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Item Already Exists")
                .setMessage("The item you are trying to add is already in the list. Please use the edit option instead.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

}



