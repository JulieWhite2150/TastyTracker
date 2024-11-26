package com.example.tastytracker;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

//Class to help properly display the list of users in the household
public class HouseholdListAdapter extends ArrayAdapter<User> {
    private Context mContext;
    private ArrayList<User> mList;
    private int householdID;

    public HouseholdListAdapter(Context context, ArrayList<User> list, int householdID) {
        super(context, R.layout.household_list_layout, list);
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
            convertView = inflater.inflate(R.layout.household_list_layout, parent, false);
        }

        // Get the user at the current position
        User user = mList.get(position);

        TextView usernameTextView = convertView.findViewById(R.id.usernameTextView);
        TextView permissionsTextView = convertView.findViewById(R.id.permissionsTextView);
        SwitchMaterial permissionsSwitch = convertView.findViewById(R.id.permissionsSwitch);

        usernameTextView.setText(user.getUsername());
        permissionsTextView.setText(user.getPermissions());

        if (user.getPermissions().equals("HH")){
            permissionsSwitch.setVisibility(View.INVISIBLE);
        }
        else if (user.getPermissions().equals("MWP")){
            permissionsSwitch.setChecked(true);
        }

        permissionsSwitch.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            String newPermissions = "";
            userInfoDBAdapter db = new userInfoDBAdapter(this.getContext());
            db.open();
            if (isChecked){
                newPermissions = "MWP";
            }
            else{
                newPermissions = "MWOP";
            }
            user.changePermissions(newPermissions);
            permissionsTextView.setText(newPermissions);
            db.updateUserPermissions(user.getUsername(), user.getPermissions());
            db.close();
        }));

        return convertView;
    }

}
