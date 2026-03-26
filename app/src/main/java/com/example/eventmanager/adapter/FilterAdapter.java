package com.example.eventmanager.adapter;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {
    private List<String> filters;
    private OnFilterClickListener listener;
    private String selectedFilter = "";

    public interface OnFilterClickListener {
        void onFilterClick(String filter);
    }

    public FilterAdapter(List<String> filters, OnFilterClickListener listener) {
        this.filters = filters;
        this.listener = listener;
    }

    public void setFilters(List<String> filters) {
        this.filters = filters;
        notifyDataSetChanged();
    }

    public void setSelectedFilter(String filter) {
        this.selectedFilter = filter;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_chip, parent, false);
        return new FilterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        String filter = filters.get(position);
        // Đồng bộ chữ in hoa giống như trên thẻ nhà cung cấp
        holder.tvFilterName.setText(filter.toUpperCase());

        int baseColor = VendorAdapter.getColorForType(filter);

        if (filter.equals(selectedFilter)) {
            // Khi được chọn: Nền màu đậm, chữ trắng để nổi bật
            holder.cardFilter.setCardBackgroundColor(ColorStateList.valueOf(baseColor));
            holder.cardFilter.setStrokeWidth(0);
            holder.tvFilterName.setTextColor(Color.WHITE);
            holder.cardFilter.setCardElevation(4f);
        } else {
            // Khi chưa chọn: Nền trắng, viền và chữ cùng màu để dễ nhìn trên nền xanh
            holder.cardFilter.setCardBackgroundColor(ColorStateList.valueOf(Color.WHITE));
            holder.cardFilter.setStrokeWidth(3);
            holder.cardFilter.setStrokeColor(ColorStateList.valueOf(VendorAdapter.adjustAlpha(baseColor, 0.4f)));
            holder.tvFilterName.setTextColor(baseColor);
            holder.cardFilter.setCardElevation(0f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterClick(filter);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filters != null ? filters.size() : 0;
    }

    public static class FilterViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardFilter;
        TextView tvFilterName;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFilter = itemView.findViewById(R.id.cardFilter);
            tvFilterName = itemView.findViewById(R.id.tvFilterName);
        }
    }
}
