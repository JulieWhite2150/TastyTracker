package com.example.tastytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

//Class to help properly display the list of users in the household
public class RequestsListAdapter extends ArrayAdapter<foodItem> {
    private Context mContext;
    private ArrayList<foodItem> mList;
    private int householdID;

    public RequestsListAdapter(Context context, ArrayList<foodItem> list, int householdID) {
        super(context, R.layout.requests_list_layout, list);
        mContext = context;
        mList = list;
        this.householdID = householdID;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        // Inflate view only if convertView is null
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.requests_list_layout, parent, false);
        }

        // Get the request at the current position
        foodItem request = mList.get(position);
        TextView itemNameTextView = convertView.findViewById(R.id.itemNameTextView);
        TextView itemQuantityTextView = convertView.findViewById(R.id.itemQuantityTextView);
        TextView itemUnitTextView = convertView.findViewById(R.id.itemUnitTextView);
        TextView whereToAddTextView = convertView.findViewById(R.id.whereToAddTextView);

        itemNameTextView.setText(request.getName());
        itemQuantityTextView.setText(String.valueOf(request.getQuantity()));
        itemUnitTextView.setText(request.getUnit());
        whereToAddTextView.setText("Ya booty");

        return convertView;
    }

}
