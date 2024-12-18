package com.example.tastytracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.util.Log;

import java.util.ArrayList;
import android.widget.Button;
import android.widget.TextView;

public class ShoppingActivity extends AppCompatActivity {
    private ListView listView;
    private foodDBAdapter dbAdapter;
    private int householdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping);

        listView = findViewById(R.id.listView);
        householdID = getIntent().getIntExtra("HOUSEHOLD_ID", -1);

        loadShoppingListItems(); //Load the shopping list

        ImageButton addItem = findViewById(R.id.addButton);
        ImageButton markAsShopped = findViewById(R.id.markAsShoppedButton);
        ImageButton backToInventory = findViewById(R.id.backButton);
        ImageButton question = findViewById(R.id.question);

        if (UserSession.getInstance().getPermissions().equals("MWOP")){
            markAsShopped.setVisibility(View.INVISIBLE);
            question.setVisibility(View.INVISIBLE);
        }

        TextView introText = findViewById(R.id.intro_text);
        introText.setText("Shopping List for Household #" + householdID);

        //Button to navigate user back to inventory activity
        backToInventory.setOnClickListener(v -> {
            resetAllShoppedValues(householdID);

            //Navigate back to Inventory
            Intent intent = new Intent(ShoppingActivity.this, InventoryActivity.class);
            intent.putExtra("HOUSEHOLD_ID", householdID);
            startActivity(intent);
            finish();
        });

        //Button to navigate user to the edit item activity
        addItem.setOnClickListener(v -> {
            Intent intent = new Intent(ShoppingActivity.this, EditItemActivity.class);
            intent.putExtra("HOUSEHOLD_ID", householdID);
            intent.putExtra("MODE", "ADD");
            intent.putExtra("RETURN", "SHOPPING");
            intent.putExtra("TOADD", "SHOPPING");
            startActivity(intent);
            finish();
        });

        /*
         * Button to allow user to mark all the items that have their respective checkbox checked as shopped.
         * When this happens, the db needs to take the checked items and either add them to the inventory
         * or add their quantity to the already existing inventory item, and then remove the checked
         * items from the shopping list.
         */
        //When
        markAsShopped.setOnClickListener(view -> {
            Log.d("MarkAsShopped", "I was clicked!");
            dbAdapter = new foodDBAdapter(this);
            dbAdapter.open(householdID);

            ArrayList<shoppingListItem> shoppedItems = dbAdapter.getShoppedItems(householdID);
            for (int i = 0; i < shoppedItems.size(); i++){
                String shoppedName = shoppedItems.get(i).getName();
                String shoppedUnit = shoppedItems.get(i).getUnit();
                double shoppedQuant = shoppedItems.get(i).getQuantity();

                double quantityInInventory = dbAdapter.getItemQuant(householdID, shoppedName, "INVENTORY");

                //If quantity in inventory = 0, then the item is not in inventory and needs to be added
                if (quantityInInventory == 0.0){
                    dbAdapter.insertItem(householdID, shoppedName, shoppedQuant, shoppedUnit, false, "INVENTORY");
                }
                //Else item exists in inventory and current quantity needs to be added to shopped quantity
                else{
                    double totalQuant = Math.round((shoppedQuant + quantityInInventory)*100.0)/100.0;
                    dbAdapter.updateItem(householdID, shoppedName, totalQuant, shoppedUnit, "INVENTORY");
                }
                //remove item from the shopping list
                dbAdapter.deleteItem(householdID, shoppedName, "SHOPPING");
            }
            dbAdapter.close();
            loadShoppingListItems(); //reload the list
        });

        ImageButton questionButton = findViewById(R.id.question);
        questionButton.setOnClickListener(v -> {
            // Create and display the AlertDialog
            new AlertDialog.Builder(this)
                    .setTitle("Mark as Shopped")
                    .setMessage("Use the checkboxes to select the items you have purchased. \n\nOnce you've selected the items, press the shopping cart button to move them from your shopping list to your inventory. \n\nThis will help you easily update your shopping list and inventory!")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    //Method to load the shopping list and set the listView to the shopping list adapter.
    private void loadShoppingListItems() {
        dbAdapter = new foodDBAdapter(this);
        dbAdapter.open(householdID);

        ArrayList<shoppingListItem> shoppingListItems = dbAdapter.getShoppingListItems(householdID);
        ShoppingListAdapter adapter = new ShoppingListAdapter(this, shoppingListItems, householdID);

        listView.setAdapter(adapter);
        dbAdapter.close();
    }

    public void resetAllShoppedValues(int householdID){
        //Make sure user didn't click checkboxes, reset all shopped values to false
        dbAdapter = new foodDBAdapter(this);
        dbAdapter.open(householdID);
        dbAdapter.resetShoppedItems(householdID);
        dbAdapter.close();
    }
}


