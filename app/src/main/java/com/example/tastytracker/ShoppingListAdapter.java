package com.example.tastytracker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import android.widget.CheckBox;

//Class to help properly display the shopping list
public class ShoppingListAdapter extends ArrayAdapter<shoppingListItem> {
    private Context mContext;
    private ArrayList<shoppingListItem> mList;
    private int householdID;

    public ShoppingListAdapter(Context context, ArrayList<shoppingListItem> list, int householdID) {
        super(context, R.layout.inventory_list_layout, list);
        mContext = context;
        mList = list;
        this.householdID = householdID;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.shopping_list_layout, parent, false);

        CheckBox shoppedCheckBox = view.findViewById(R.id.shoppedCheckBox);
        TextView itemNameTextView = view.findViewById(R.id.itemNameTextView);
        TextView itemQuantityTextView = view.findViewById(R.id.itemQuantityTextView);
        TextView itemUnitTextView = view.findViewById(R.id.itemUnitTextView);
        ImageButton editButton = view.findViewById(R.id.editButton);

        if (UserSession.getInstance().getPermissions().equals("MWOP")){
            shoppedCheckBox.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.INVISIBLE);
        }

        shoppingListItem currentItem = mList.get(position);
        itemNameTextView.setText(currentItem.getName());
        itemQuantityTextView.setText(String.valueOf(currentItem.getQuantity()));
        itemUnitTextView.setText(currentItem.getUnit());

        //If the user presses edit, move to the edit item activity
        editButton.setOnClickListener(v -> {
            resetAllShoppedValues(householdID);
            Intent intent = new Intent(mContext, EditItemActivity.class);
            intent.putExtra("MODE", "EDIT");
            intent.putExtra("RETURN", "SHOPPING");
            intent.putExtra("TOADD", "SHOPPING");
            intent.putExtra("HOUSEHOLD_ID", householdID);
            intent.putExtra("ITEM_NAME", currentItem.getName());
            intent.putExtra("ITEM_QUANTITY", currentItem.getQuantity());
            intent.putExtra("ITEM_UNIT", currentItem.getUnit());
            mContext.startActivity(intent);
        });

        //If the user checks a box, flip the shopped value of that item
        shoppedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            foodDBAdapter dbAdapter = new foodDBAdapter(mContext);
            dbAdapter.open(householdID);
            dbAdapter.flipShopped(householdID, currentItem.getName());
            dbAdapter.close();
        });

        return view;
    }

    public void resetAllShoppedValues(int householdID){
        //Make sure user didn't click checkboxes, reset all shopped values to false
        foodDBAdapter dbAdapter = new foodDBAdapter(mContext);
        dbAdapter.open(householdID);
        dbAdapter.resetShoppedItems(householdID);
        dbAdapter.close();
    }

}
