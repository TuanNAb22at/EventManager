package com.example.eventmanager.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Vendor;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class VendorAdapter extends RecyclerView.Adapter<VendorAdapter.VendorViewHolder> {
    private List<Vendor> vendors;
    private OnVendorActionListener actionListener;
    private boolean isReadOnly = false;

    public static final String[] VENDOR_COLORS = {
        "#FF7043", "#FFB74D", "#26A69A", "#5C6BC0", "#66BB6A",
        "#AB47BC", "#26C6DA", "#EC407A", "#78909C", "#8D6E63"
    };

    public interface OnVendorActionListener {
        void onEdit(Vendor vendor);
        void onDelete(Vendor vendor);
        void onItemClick(Vendor vendor);
    }

    public void setOnVendorActionListener(OnVendorActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        notifyDataSetChanged();
    }

    public VendorAdapter(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
        notifyDataSetChanged();
    }

    public static int getColorForType(String type) {
        if (type == null || type.isEmpty()) return Color.GRAY;
        int index = Math.abs(type.hashCode()) % VENDOR_COLORS.length;
        return Color.parseColor(VENDOR_COLORS[index]);
    }

    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
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
        
        holder.tvSupplierName.setText(vendor.getName());
        holder.tvPhone.setText(vendor.getPhone() != null && !vendor.getPhone().isEmpty() ? vendor.getPhone() : "Chưa có SĐT");
        holder.tvEmail.setText(vendor.getEmail() != null && !vendor.getEmail().isEmpty() ? vendor.getEmail() : "Chưa có Email");
        
        String serviceType = vendor.getServiceType();
        holder.tvCategory.setText(serviceType != null ? serviceType.toUpperCase() : "N/A");
        
        int typeColor = getColorForType(serviceType);
        holder.cardCategory.setCardBackgroundColor(ColorStateList.valueOf(adjustAlpha(typeColor, 0.15f)));
        holder.tvCategory.setTextColor(typeColor);

        if (vendor.getNote() != null && !vendor.getNote().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText("\"" + vendor.getNote() + "\"");
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        if (isReadOnly) {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            if (actionListener != null) {
                holder.btnEdit.setOnClickListener(v -> actionListener.onEdit(vendor));
                holder.btnDelete.setOnClickListener(v -> actionListener.onDelete(vendor));
            }
        }

        if (actionListener != null) {
            holder.itemView.setOnClickListener(v -> actionListener.onItemClick(vendor));
        }
    }

    @Override
    public int getItemCount() {
        return vendors != null ? vendors.size() : 0;
    }

    public static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView tvSupplierName, tvPhone, tvEmail, tvCategory, tvNote;
        ImageView btnEdit, btnDelete;
        MaterialCardView cardCategory;

        public VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSupplierName = itemView.findViewById(R.id.tvSupplierName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvNote = itemView.findViewById(R.id.tvNote);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            cardCategory = itemView.findViewById(R.id.cardCategory);
        }
    }
}
