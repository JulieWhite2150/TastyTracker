package com.example.tastytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RequestsListAdapter extends RecyclerView.Adapter<RequestsListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<foodItem> mList;

    public RequestsListAdapter(Context context, ArrayList<foodItem> list) {
        this.mContext = context;
        this.mList = list;
    }

    // ViewHolder class to hold the layout for each item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameTextView;
        TextView itemQuantityTextView;
        TextView itemUnitTextView;
        TextView whereToAddTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTextView = itemView.findViewById(R.id.itemNameTextView);
            itemQuantityTextView = itemView.findViewById(R.id.itemQuantityTextView);
            itemUnitTextView = itemView.findViewById(R.id.itemUnitTextView);
            whereToAddTextView = itemView.findViewById(R.id.whereToAddTextView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.requests_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        foodItem request = mList.get(position);
        holder.itemNameTextView.setText(request.getName());
        holder.itemQuantityTextView.setText(String.valueOf(request.getQuantity()));
        holder.itemUnitTextView.setText(request.getUnit());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

