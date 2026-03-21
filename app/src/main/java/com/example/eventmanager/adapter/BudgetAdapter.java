package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Budget;
import java.util.List;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {
    private List<Budget> budgets;
    private OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(Budget budget);
    }

    public BudgetAdapter(List<Budget> budgets, OnItemClickListener onItemClick) {
        this.budgets = budgets;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = budgets.get(position);
        holder.totalTextView.setText(String.valueOf(budget.amount));
        holder.noteTextView.setText(budget.note);
        holder.itemView.setOnClickListener(v -> onItemClick.onItemClick(budget));
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView totalTextView;
        TextView noteTextView;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            totalTextView = itemView.findViewById(R.id.budgetTotalTextView);
            noteTextView = itemView.findViewById(R.id.budgetNoteTextView);
        }
    }
}
