package com.example.tastytracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ManageRequestsActivity extends AppCompatActivity {

    int householdID = UserSession.getInstance().getHouseholdID();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);
        TextView titleTextView = findViewById(R.id.titleTextView);
        ListView listView = findViewById(R.id.listView);

        titleTextView.setText("Manage Requests for Household #" + householdID);

        //Open up db and fill array list with users to set as what will be shown in listview
        foodDBAdapter db = new foodDBAdapter(this);
        db.open(householdID);
        ArrayList<foodItem> requestedItems = db.getInventoryOrRequestItems(householdID, "_Requests");
        db.close();

        RequestsListAdapter adapter = new RequestsListAdapter(this, requestedItems, householdID);
        listView.setAdapter(adapter);

        Button backButton = findViewById(R.id.backButton);

        //Button to return the user to the inventory activity
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManageRequestsActivity.this, ManageHouseholdActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
