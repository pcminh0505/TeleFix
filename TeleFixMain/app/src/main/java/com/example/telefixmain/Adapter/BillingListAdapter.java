package com.example.telefixmain.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.telefixmain.Model.Booking.Billing;
import com.example.telefixmain.R;
import com.example.telefixmain.Util.DatabaseHandler;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class BillingListAdapter extends RecyclerView.Adapter<BillingItemViewHolder> {
    private Context issueBillingActivity;
    private ArrayList<Billing> currentBilling;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // init pricelist
    private HashMap<String, String> inspectionPriceContainer = new HashMap<>();
    private HashMap<String, String> repairPriceContainer = new HashMap<>();

    public BillingListAdapter(Context issueBillingActivity, ArrayList<Billing> currentBilling) {
        this.issueBillingActivity = issueBillingActivity;
        this.currentBilling = currentBilling;
        getPriceList();
    }

    @NonNull
    @Override
    public BillingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_billing, parent, false);
        return new BillingItemViewHolder(itemView);
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull BillingItemViewHolder holder, int position) {
        String currentItem = currentBilling.get(position).getItem();
        int currentQuantity = currentBilling.get(position).getQuantity();
        int currentPrice = 0;

        if (inspectionPriceContainer.containsKey(currentItem)) {
            currentPrice = currentQuantity * Integer.parseInt(Objects.requireNonNull(inspectionPriceContainer.get(currentItem)));
        } else {
            currentPrice = currentQuantity * Integer.parseInt(Objects.requireNonNull(repairPriceContainer.get(currentItem)));
        }

        holder.item.setText(currentItem);
        holder.quantity.setText(Integer.toString(currentQuantity));
        holder.total.setText(String.format("%,d",currentPrice) + ",000 VND");
    }

    @Override
    public int getItemCount() {
        if (currentBilling != null) {
            return currentBilling.size();
        }
        return 0;
    }

    private void getPriceList() {
        // Mock default one
        String currentVendorId = "01";

        DatabaseHandler.getVendorPriceListById(
            db,
            currentVendorId,
            inspectionPriceContainer,
            repairPriceContainer, () -> {});
    }
}
