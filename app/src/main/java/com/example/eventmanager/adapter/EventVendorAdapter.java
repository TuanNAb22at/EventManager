package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Vendor;
import java.util.List;

public class EventVendorAdapter extends RecyclerView.Adapter<EventVendorAdapter.ViewHolder> {
    private List<Vendor> vendors;
    private OnEventVendorActionListener listener;
    private boolean isReadOnly = false;

    public interface OnEventVendorActionListener {
        void onRemove(Vendor vendor);
    }

    public EventVendorAdapter(List<Vendor> vendors, OnEventVendorActionListener listener) {
        this.vendors = vendors;
        this.listener = listener;
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        notifyDataSetChanged();
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
        holder.tvServiceType.setText(vendor.getServiceType() != null ? vendor.getServiceType().toUpperCase() : "DỊCH VỤ");
        holder.tvVendorPhone.setText(vendor.getPhone() != null ? vendor.getPhone() : "Chưa có SĐT");
        holder.tvVendorEmail.setText(vendor.getEmail() != null ? vendor.getEmail() : "Chưa có Email");
        
        if (vendor.getNote() != null && !vendor.getNote().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText("\"" + vendor.getNote() + "\"");
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        // Simplified status for now
        holder.tvStatus.setText("ĐÃ XÁC NHẬN");

        if (isReadOnly) {
            holder.btnRemove.setVisibility(View.GONE);
        } else {
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnRemove.setOnClickListener(v -> {
                if (listener != null) listener.onRemove(vendor);
            });
        }
    }

    @Override
    public int getItemCount() {
        return vendors != null ? vendors.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvServiceType, tvStatus, tvVendorPhone, tvVendorEmail, tvNote;
        ImageButton btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.tvVendorName);
            tvServiceType = itemView.findViewById(R.id.tvServiceType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvVendorPhone = itemView.findViewById(R.id.tvVendorPhone);
            tvVendorEmail = itemView.findViewById(R.id.tvVendorEmail);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
