package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Vendor;
import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {
    private List<Vendor> vendors;
    private OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(Vendor vendor);
    }

    public VendorAdapter(List<Vendor> vendors, OnItemClickListener onItemClick) {
        this.vendors = vendors;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);
        holder.nameTextView.setText(vendor.name);
        holder.emailTextView.setText(vendor.email);
        holder.serviceTypeTextView.setText(vendor.serviceType);
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClick(vendor));
    }

    @Override
    public int getItemCount() {
        return vendors.size();
    }

    public static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView emailTextView;
        TextView serviceTypeTextView;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.vendorNameTextView);
            emailTextView = itemView.findViewById(R.id.vendorEmailTextView);
            serviceTypeTextView = itemView.findViewById(R.id.vendorServiceTypeTextView);
        }
    }
}
