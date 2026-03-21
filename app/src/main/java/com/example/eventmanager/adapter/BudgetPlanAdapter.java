package com.example.eventmanager.adapter;

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

    private List<Budget> budgets;
    private OnItemClickListener listener;

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
        holder.tvBudgetTitle.setText(budget.title);
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvAmount.setText(formatter.format(budget.amount));
        holder.tvBudgetInfo.setText("Danh mục: " + budget.category);

        // Map icons and colors based on category
        setupCategoryStyle(holder, budget.category);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(budget));
    }

    private void setupCategoryStyle(ViewHolder holder, String category) {
        int iconRes = R.drawable.ic_category;
        int colorRes = R.color.primary_blue;
        int bgColorRes = android.R.color.holo_blue_light;

        if (category == null) category = "Khác";

        switch (category) {
            case "Trang trí":
                iconRes = R.drawable.ic_decor;
                colorRes = android.R.color.holo_orange_dark;
                bgColorRes = android.R.color.holo_orange_light;
                break;
            case "Ăn uống":
                iconRes = R.drawable.ic_food;
                colorRes = android.R.color.holo_red_dark;
                bgColorRes = android.R.color.holo_red_light;
                break;
            case "Âm thanh ánh sáng":
                iconRes = R.drawable.ic_logo_e;
                colorRes = android.R.color.holo_purple;
                bgColorRes = android.R.color.holo_blue_bright;
                break;
            case "Quà tặng":
                iconRes = R.drawable.img_gift;
                colorRes = android.R.color.holo_green_dark;
                bgColorRes = android.R.color.holo_green_light;
                break;
        }

        holder.ivCategoryIcon.setImageResource(iconRes);
        holder.ivCategoryIcon.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(), colorRes));
        holder.cvIconBackground.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), bgColorRes));
        holder.cvIconBackground.setAlpha(0.2f); // Làm nhẹ màu nền
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
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
