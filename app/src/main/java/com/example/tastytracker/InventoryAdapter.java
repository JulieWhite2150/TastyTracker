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

//Class to create a list adapter to properly display the household inventory
public class InventoryAdapter extends ArrayAdapter<foodItem> {
    private Context mContext;
    private ArrayList<foodItem> mList;
    private int householdID;

    //Constructor
    public InventoryAdapter(Context context, ArrayList<foodItem> list, int householdID) {
        super(context, R.layout.inventory_list_layout, list);
        mContext = context;
        mList = list;
        this.householdID = householdID;
    }

    //Set the ListView
    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.inventory_list_layout, parent, false);

        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemQuantityTextView = view.findViewById(R.id.itemQuantityTextView);
        TextView itemUnitTextView = view.findViewById(R.id.itemUnitTextView);
        Button editButton = view.findViewById(R.id.editButton);
        Button shopButton = view.findViewById(R.id.shopButton);

        foodItem currentItem = mList.get(position);
        itemNameTextView.setText(currentItem.getName());
        itemQuantityTextView.setText(String.valueOf(currentItem.getQuantity()));
        itemUnitTextView.setText(currentItem.getUnit());

        //Take user to Edit item activity where they can add item to shoppping list
        shopButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditItemActivity.class);
            intent.putExtra("MODE", "ADD");
            intent.putExtra("RETURN", "INVENTORY");
            intent.putExtra("TOADD", "SHOPPING");
            intent.putExtra("HOUSEHOLD_ID", householdID);
            intent.putExtra("ITEM_NAME", currentItem.getName());
            intent.putExtra("ITEM_QUANTITY", currentItem.getQuantity());
            intent.putExtra("ITEM_UNIT", currentItem.getUnit());
            intent.putExtra("PERMISSIONS", UserSession.getInstance().getPermissions());
            mContext.startActivity(intent);
        });

        //Take user to edit item activity where they can edit a specific item in the inventory
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EditItemActivity.class);
            intent.putExtra("MODE", "EDIT");
            intent.putExtra("RETURN", "INVENTORY");
            intent.putExtra("TOADD", "INVENTORY");
            intent.putExtra("HOUSEHOLD_ID", householdID);
            intent.putExtra("ITEM_NAME", currentItem.getName());
            intent.putExtra("ITEM_QUANTITY", currentItem.getQuantity());
            intent.putExtra("ITEM_UNIT", currentItem.getUnit());
            mContext.startActivity(intent);
        });

        return view;
    }
}
