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
import java.util.ArrayList;
import java.util.List;

public class SelectVendorAdapter extends RecyclerView.Adapter<SelectVendorAdapter.ViewHolder> {
    private List<Vendor> vendors;
    private List<Vendor> selectedVendors = new ArrayList<>();
    private List<Integer> preSelectedVendorIds = new ArrayList<>();
    private OnVendorSelectionChangedListener listener;

    public interface OnVendorSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    public SelectVendorAdapter(List<Vendor> vendors, OnVendorSelectionChangedListener listener) {
        this.vendors = vendors;
        this.listener = listener;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
        notifyDataSetChanged();
    }

    public void setSelectedVendors(List<Vendor> selected) {
        this.selectedVendors = new ArrayList<>(selected);
        this.preSelectedVendorIds = new ArrayList<>();
        for (Vendor v : selected) {
            preSelectedVendorIds.add(v.getId());
        }
        notifyDataSetChanged();
    }

    public List<Vendor> getSelectedVendors() {
        return selectedVendors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_supplier, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);
        holder.tvName.setText(vendor.getName());
        holder.tvPhone.setText(vendor.getPhone() != null ? vendor.getPhone() : "Chưa có SĐT");
        holder.tvCategory.setText(vendor.getServiceType());

        int typeColor = VendorAdapter.getColorForType(vendor.getServiceType());
        holder.cardCategory.setCardBackgroundColor(ColorStateList.valueOf(VendorAdapter.adjustAlpha(typeColor, 0.15f)));
        holder.tvCategory.setTextColor(typeColor);

        // Kiểm tra xem vendor này đã được chọn chưa (bao gồm cả những cái đã có sẵn)
        boolean isSelected = false;
        for (Vendor v : selectedVendors) {
            if (v.getId() == vendor.getId()) {
                isSelected = true;
                break;
            }
        }

        // Nếu là nhà cung cấp đã có sẵn từ trước, có thể làm mờ hoặc vô hiệu hóa nếu muốn
        // Ở đây mình cứ cho phép chọn/bỏ chọn bình thường theo yêu cầu của bạn
        if (isSelected) {
            holder.cardVendor.setStrokeColor(Color.parseColor("#4F46E5")); // primary_blue
            holder.ivCheckbox.setImageResource(R.drawable.ic_checkbox_selected);
            holder.cardVendor.setCardBackgroundColor(Color.parseColor("#F8FAFC")); 
        } else {
            holder.cardVendor.setStrokeColor(Color.TRANSPARENT);
            holder.ivCheckbox.setImageResource(R.drawable.ic_checkbox_unselected);
            holder.cardVendor.setCardBackgroundColor(Color.WHITE);
        }

        View.OnClickListener clickListener = v -> {
            boolean alreadySelected = false;
            int selectedIndex = -1;
            for (int i = 0; i < selectedVendors.size(); i++) {
                if (selectedVendors.get(i).getId() == vendor.getId()) {
                    alreadySelected = true;
                    selectedIndex = i;
                    break;
                }
            }

            if (alreadySelected) {
                selectedVendors.remove(selectedIndex);
            } else {
                selectedVendors.add(vendor);
            }
            notifyItemChanged(position);
            if (listener != null) listener.onSelectionChanged(selectedVendors.size());
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.ivCheckbox.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return vendors != null ? vendors.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvCategory;
        MaterialCardView cardCategory, cardVendor;
        ImageView ivCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSupplierName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            cardVendor = itemView.findViewById(R.id.cardVendor);
            ivCheckbox = itemView.findViewById(R.id.ivCheckbox);
        }
    }
}
