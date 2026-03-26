package com.example.eventmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Budget;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetPlanAdapter extends RecyclerView.Adapter<BudgetPlanAdapter.ViewHolder> {

    private final List<Budget> budgets;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Budget budget);
    }

    public BudgetPlanAdapter(List<Budget> budgets, OnItemClickListener listener) {
        this.budgets = budgets;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget_plan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Budget budget = budgets.get(position);
        
        // Viết hoa chữ cái đầu cho tiêu đề
        String title = budget.getTitle();
        if (title != null && !title.isEmpty()) {
            title = title.substring(0, 1).toUpperCase() + title.substring(1);
        }
        holder.tvBudgetTitle.setText(title);
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvAmount.setText(formatter.format(budget.getAmount()));
        
        // Viết hoa chữ cái đầu cho danh mục
        String category = budget.getCategory();
        if (category != null && !category.isEmpty()) {
            category = category.substring(0, 1).toUpperCase() + category.substring(1);
        }
        holder.tvBudgetInfo.setText(String.format("Danh mục: %s", category));

        // Map icons and colors based on category
        setupCategoryStyle(holder, budget.getCategory());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(budget));
    }

    private void setupCategoryStyle(ViewHolder holder, String category) {
        Context context = holder.itemView.getContext();
        int iconRes;
        int colorRes;
        int bgColorRes;

        if (category == null) category = "khác";
        String normalizedCategory = category.toLowerCase().trim();

        switch (normalizedCategory) {
            case "đồ ăn":
                iconRes = R.drawable.ic_food;
                colorRes = android.R.color.holo_red_dark;
                bgColorRes = android.R.color.holo_red_light;
                break;
            case "trang trí":
                iconRes = R.drawable.ic_decor;
                colorRes = android.R.color.holo_orange_dark;
                bgColorRes = android.R.color.holo_orange_light;
                break;
            case "âm nhạc":
                iconRes = R.drawable.ic_music;
                colorRes = android.R.color.holo_purple;
                bgColorRes = R.color.primary_blue;
                break;
            case "quà tặng":
                iconRes = R.drawable.img_gift;
                colorRes = android.R.color.holo_green_dark;
                bgColorRes = android.R.color.holo_green_light;
                break;
            case "địa điểm":
                iconRes = R.drawable.ic_location;
                colorRes = R.color.primary_blue;
                bgColorRes = android.R.color.holo_blue_bright;
                break;
            case "khác":
            default:
                iconRes = R.drawable.ic_category;
                colorRes = android.R.color.darker_gray;
                bgColorRes = R.color.light_gray_bg;
                break;
        }

        holder.ivCategoryIcon.setImageResource(iconRes);
        holder.ivCategoryIcon.setColorFilter(ContextCompat.getColor(context, colorRes));
        holder.cvIconBackground.setCardBackgroundColor(ContextCompat.getColor(context, bgColorRes));
        holder.cvIconBackground.setAlpha(0.6f);
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public void setBudgets(List<Budget> newBudgets) {
        this.budgets.clear();
        this.budgets.addAll(newBudgets);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBudgetTitle, tvBudgetInfo, tvAmount;
        ImageView ivCategoryIcon;
        CardView cvIconBackground;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBudgetTitle = itemView.findViewById(R.id.tvBudgetTitle);
            tvBudgetInfo = itemView.findViewById(R.id.tvBudgetInfo);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            cvIconBackground = itemView.findViewById(R.id.cvIconBackground);
        }
    }
}
