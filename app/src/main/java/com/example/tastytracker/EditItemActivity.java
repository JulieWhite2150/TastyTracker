package com.example.tastytracker;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;

public class EditItemActivity extends AppCompatActivity {
    private EditText itemNameEditText;
    private EditText itemQuantityEditText;
    private EditText itemUnitEditText;
    private int householdID;
    private String mode, returnLocation, locationToAdd;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Intent intent = getIntent();
        mode = intent.getStringExtra("MODE");
        returnLocation = intent.getStringExtra("RETURN");
        locationToAdd = intent.getStringExtra("TOADD");

        TextView title = findViewById(R.id.title);

        itemNameEditText = findViewById(R.id.itemNameEditText);
        itemQuantityEditText = findViewById(R.id.itemQuantityEditText);
        itemUnitEditText = findViewById(R.id.itemUnitEditText);
        Button saveButton = findViewById(R.id.saveButton);
        Button cancelButton = findViewById(R.id.cancelButton);
        deleteButton = findViewById(R.id.deleteButton);

        //Series of if/else to set the proper instruction text/button visibility depending on user context
        if (mode.equals("ADD")){
            deleteButton.setVisibility(TextView.GONE); //user can't delete item they haven't added
            title.setText("Enter information on the item to add to the ");
            if (locationToAdd.equals("INVENTORY")){
                title.append("inventory.");
            }
            else{
                title.append("shopping list.");
                if (returnLocation.equals("INVENTORY")){
                    itemNameEditText.setEnabled(false);
                    itemUnitEditText.setEnabled(false);
                }
            }
        }
        else{
            itemNameEditText.setEnabled(false);
            title.setText("Edit information on the item in the ");
            if (locationToAdd.equals("INVENTORY")){
                title.append("inventory.");
            }
            else{
                title.append("shopping list.");
            }
        }


        householdID = getIntent().getIntExtra("HOUSEHOLD_ID", -1);

        String itemName = getIntent().getStringExtra("ITEM_NAME");
        double itemQuantity = getIntent().getDoubleExtra("ITEM_QUANTITY", 0.0);
        String itemUnit = getIntent().getStringExtra("ITEM_UNIT");

        //Set text in the EditTexts to be the current items for editing purposes
        if (itemName != null) {
            itemNameEditText.setText(itemName);
            itemQuantityEditText.setText(String.valueOf(itemQuantity));
            itemUnitEditText.setText(itemUnit);
        }

        //Cancel button moves the user back to the activity they were in before coming to edit
        cancelButton.setOnClickListener(v -> {
            returnToLocation(returnLocation);
        });

        //Delete will delete an item from a table and then return user to previous activity
        deleteButton.setOnClickListener(v -> {
            foodDBAdapter dbAdapter = new foodDBAdapter(this);
            dbAdapter.open(householdID);
            dbAdapter.deleteItem(householdID, itemName, locationToAdd);
            dbAdapter.close();
            returnToLocation(returnLocation);
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

            if (roundedQuant <= 0.0){
                quantityCannotBeZeroAlert();
                return;
            }
            //if the user is adding to inventory from inventory activity
            if (mode.equals("ADD")){
                addItem(itemNameInput, roundedQuant, itemUnitInput, locationToAdd, returnLocation);
            }
            //if the user is editing inventory from inventory activity
            else{
                editItem(itemNameInput, roundedQuant, itemUnitInput, locationToAdd, returnLocation);
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
            returnToLocation(returnLocation);
        }
        else{
            itemAlreadyExistsAlert();
        }
        dbAdapter.close();
    }

    //This method calls the db adapter method to edit item given a location
    private void editItem(String itemName, double itemQuantity, String unit, String locationToAdd, String returnLocation){
        foodDBAdapter dbAdapter = new foodDBAdapter(this);
        dbAdapter.open(householdID);
        dbAdapter.updateItem(householdID, itemName, itemQuantity, unit, locationToAdd);
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

    //Tell the user that the quantity of an item cannot be 0, ack is needed so AlertDialog used
    private void quantityCannotBeZeroAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantity of Items Cannot be 0")
                .setMessage("If you need to delete an item, use the delete button within the edit screen.")
                .setPositiveButton("OK", null)
                .create()
                .show();
    }

    //Given a return location, return the user there.
    public void returnToLocation(String returnLocation){
        Intent intentLeave;
        if (returnLocation.equals("INVENTORY")){
            intentLeave = new Intent(EditItemActivity.this, InventoryActivity.class);
        }
        else{
            intentLeave = new Intent(EditItemActivity.this, ShoppingActivity.class);
        }
        intentLeave.putExtra("HOUSEHOLD_ID", householdID);
        startActivity(intentLeave);
        finish();
    }
}



