package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Vendor;
import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {
    private List<Vendor> vendors;
    private OnVendorActionListener actionListener;

    public interface OnVendorActionListener {
        void onEdit(Vendor vendor);
        void onDelete(Vendor vendor);
        void onItemClick(Vendor vendor);
    }

    public VendorAdapter(List<Vendor> vendors, OnVendorActionListener actionListener) {
        this.vendors = vendors;
        this.actionListener = actionListener;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier_card, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);
        
        holder.tvSupplierName.setText(vendor.name);
        holder.tvPhone.setText(vendor.phone);
        holder.tvEmail.setText(vendor.email);
        holder.tvCategory.setText(vendor.serviceType);
        
        // Ghi chú - Tạm thời hiển thị service type nếu không có field note
        holder.tvNote.setText("Dịch vụ: " + vendor.serviceType);

        holder.btnEdit.setOnClickListener(v -> actionListener.onEdit(vendor));
        holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(vendor));
        holder.itemView.setOnClickListener(v -> actionListener.onItemClick(vendor));
    }

    @Override
    public int getItemCount() {
        return vendors != null ? vendors.size() : 0;
    }

    public static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView tvSupplierName, tvPhone, tvEmail, tvCategory, tvNote;
        ImageView btnEdit, btnDelete;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSupplierName = itemView.findViewById(R.id.tvSupplierName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
