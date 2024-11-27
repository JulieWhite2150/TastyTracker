package com.example.tastytracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ManageHouseholdActivity extends AppCompatActivity {

    int householdID = UserSession.getInstance().getHouseholdID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_household);

        TextView titleTextView = findViewById(R.id.titleTextView);
        ListView listView = findViewById(R.id.listView);

        titleTextView.setText("Manage Household #" + householdID);

        //Open up db and fill array list with users to set as what will be shown in listview
        userInfoDBAdapter db = new userInfoDBAdapter(this);
        db.open();
        ArrayList<User> users = db.getUsersInHousehold(householdID);
        db.close();
        HouseholdListAdapter adapter = new HouseholdListAdapter(this, users, householdID);
        listView.setAdapter(adapter);

        foodDBAdapter foodDB = new foodDBAdapter(this);
        foodDB.open(householdID);
        updateNotificationBubble(foodDB.getNumberOfRequests(householdID));
        foodDB.close();

        ImageButton backButton = findViewById(R.id.backButton);
        Button requestsButton = findViewById(R.id.RequestsButton);

        //Button to return the user to the inventory activity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageHouseholdActivity.this, InventoryActivity.class);
            startActivity(intent);
            finish();
        });

        //Button to return the user to the inventory activity
        requestsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageHouseholdActivity.this, ManageRequestsActivity.class);
            startActivity(intent);
            finish();
        });

        ImageButton questionButton = findViewById(R.id.question);
        questionButton.setOnClickListener(v -> {
            // Create and display the AlertDialog
            new AlertDialog.Builder(this)
                    .setTitle("Permissions Toggle")
                    .setMessage("Use the toggle switch to grant or revoke permissions for a user.\n\nWhen the switch is ON, the user has permissions to manage household inventory or shopping lists.\n\nWhen the switch is OFF, the user has limited access and will need to request to add items to the shopping list.")
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void updateNotificationBubble(int numRequests) {
        TextView notificationBubble = findViewById(R.id.notificationBubble);
        if (numRequests > 0) {
            notificationBubble.setVisibility(View.VISIBLE);
            notificationBubble.setText(String.valueOf(numRequests));
        } else {
            notificationBubble.setVisibility(View.GONE);
        }
    }
}
