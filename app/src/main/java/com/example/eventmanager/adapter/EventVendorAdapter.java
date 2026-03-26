package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Vendor;
import java.util.List;

public class EventVendorAdapter extends RecyclerView.Adapter<EventVendorAdapter.ViewHolder> {
    private List<Vendor> vendors;
    private OnEventVendorActionListener listener;

    public interface OnEventVendorActionListener {
        void onRemove(Vendor vendor);
        void onManage(Vendor vendor);
    }

    public EventVendorAdapter(List<Vendor> vendors, OnEventVendorActionListener listener) {
        this.vendors = vendors;
        this.listener = listener;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_vendor_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);
        holder.tvVendorName.setText(vendor.getName());
        holder.tvServiceType.setText(vendor.getServiceType());
        
        // Simplified status for now
        holder.tvStatus.setText("ĐÃ XÁC NHẬN");

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(vendor);
        });

        holder.btnManage.setOnClickListener(v -> {
            if (listener != null) listener.onManage(vendor);
        });
    }

    @Override
    public int getItemCount() {
        return vendors != null ? vendors.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvServiceType, tvStatus;
        ImageView ivVendor;
        View btnManage;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.tvVendorName);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivVendor = itemView.findViewById(R.id.ivVendor);
            btnManage = itemView.findViewById(R.id.btnManage);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
