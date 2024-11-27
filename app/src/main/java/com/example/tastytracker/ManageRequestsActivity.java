package com.example.tastytracker;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ManageRequestsActivity extends AppCompatActivity {

    private int householdID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);

        TextView titleTextView = findViewById(R.id.titleTextView);
        titleTextView.append(String.valueOf(householdID));

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(ManageRequestsActivity.this, ManageHouseholdActivity.class);
            startActivity(intent);
            finish();
        });

        // Set household ID
        householdID = UserSession.getInstance().getHouseholdID();

        // Retrieve requests from the database
        foodDBAdapter db = new foodDBAdapter(this);
        db.open(householdID);
        ArrayList<foodItem> requestedItems = db.getInventoryOrRequestItems(householdID, "_Requests");
        db.close();

        // Set up the RecyclerView with the adapter
        RequestsListAdapter adapter = new RequestsListAdapter(this, requestedItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up swipe gestures
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No moving items
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (position >= 0 && position < requestedItems.size()) {
                    foodItem item = requestedItems.get(position);

                    if (direction == ItemTouchHelper.RIGHT) {
                        approveRequest(item);
                    } else if (direction == ItemTouchHelper.LEFT) {
                        removeRequest(item);
                    }

                    requestedItems.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Paint paint = new Paint();

                if (dX > 0) { // Swiping to the right (approve - green)
                    paint.setColor(ContextCompat.getColor(ManageRequestsActivity.this, android.R.color.holo_green_dark));
                    c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(),
                            dX, (float) itemView.getBottom(), paint);

                } else if (dX < 0) { // Swiping to the left (deny - red)
                    paint.setColor(ContextCompat.getColor(ManageRequestsActivity.this, android.R.color.holo_red_light));
                    c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), paint);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void approveRequest(foodItem item) {
        foodDBAdapter db = new foodDBAdapter(this);
        db.open(householdID);
        db.insertItem(householdID, item.getName(), item.getQuantity(), item.getUnit(), false, "SHOPPING");
        db.close();
        removeRequest(item);
    }

    private void removeRequest(foodItem item) {
        foodDBAdapter db = new foodDBAdapter(this);
        db.open(householdID);
        db.deleteItem(householdID, item.getName(), "REQUEST");
        db.close();
    }
}
