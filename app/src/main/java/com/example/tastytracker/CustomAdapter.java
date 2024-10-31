package com.example.tastytracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<foodItem> {
    private Context mContext;
    private ArrayList<foodItem> mList;
    private int householdID;

    public CustomAdapter(Context context, ArrayList<foodItem> list, int householdID) {
        super(context, R.layout.list_item_layout, list);
        mContext = context;
        mList = list;
        this.householdID = householdID;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.list_item_layout, parent, false);

        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemQuantityTextView = view.findViewById(R.id.itemQuantityTextView);
        Button shopButton = view.findViewById(R.id.shopButton);
        Button editButton = view.findViewById(R.id.editButton);

        foodItem currentItem = mList.get(position);
        itemNameTextView.setText(currentItem.getName());
        itemQuantityTextView.setText(String.valueOf(currentItem.getQuantity()));

        /*shopButton.setOnClickListener(v -> {
            trackerDBAdapter dbAdapter = new trackerDBAdapter(mContext);
            dbAdapter.open(householdID); // Use householdID here
            dbAdapter.insertShoppingListItem(householdID, currentItem.getName()); // Use householdID here
            dbAdapter.close();
        });*/

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditItemActivity.class);
            intent.putExtra("HOUSEHOLD_ID", householdID); // Pass householdID instead of username
            intent.putExtra("ITEM_NAME", currentItem.getName());
            intent.putExtra("ITEM_QUANTITY", currentItem.getQuantity());
            mContext.startActivity(intent);
        });

        return view;
    }
}
