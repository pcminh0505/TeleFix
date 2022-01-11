package com.example.telefixmain.Adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.R;

public class VendorItemViewHolder extends RecyclerView.ViewHolder {
    TextView vendorName, vendorDistance;
    View mView;


    public VendorItemViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        // initialize vendor information (for maintenance)
        vendorName = itemView.findViewById(R.id.tv_vendor_name);
        vendorDistance = itemView.findViewById(R.id.tv_vendor_distance);
    }
}
