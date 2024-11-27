package com.example.tastytracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

        Button backButton = findViewById(R.id.backButton);
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
