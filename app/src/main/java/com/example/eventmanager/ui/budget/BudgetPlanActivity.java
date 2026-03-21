package com.example.eventmanager.ui.budget;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.BudgetPlanAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityBudgetPlanBinding;
import com.example.eventmanager.model.Budget;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetPlanActivity extends AppCompatActivity {
    private ActivityBudgetPlanBinding binding;
    private BudgetPlanAdapter adapter;
    private int eventId;
    private String eventName;
    private double totalBudget;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBudgetPlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        eventName = getIntent().getStringExtra("EVENT_NAME");
        totalBudget = getIntent().getDoubleExtra("TOTAL_BUDGET", 0);

        binding.tvToolbarTitle.setText("Ngân sách: " + eventName);
        
        setupRecyclerView();
        
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.fabAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBudgetItemActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new BudgetPlanAdapter(new ArrayList<>(), budget -> {
            // Xem chi tiết hoặc xóa nếu cần
        });
        binding.rvBudgets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBudgets.setAdapter(adapter);
    }

    private void loadBudgetData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Budget> budgets = db.budgetDao().getBudgetsByEventId(eventId);
            
            double spent = 0;
            for (Budget b : budgets) {
                spent += b.amount;
            }
            
            final double finalSpent = spent;
            final double remaining = totalBudget - finalSpent;
            final int percent = totalBudget > 0 ? (int) ((finalSpent / totalBudget) * 100) : 0;

            runOnUiThread(() -> {
                adapter.setBudgets(budgets);
                
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                binding.tvTotalBudget.setText(formatter.format(totalBudget));
                binding.tvSpent.setText(formatter.format(finalSpent));
                binding.tvRemaining.setText(formatter.format(remaining));
                
                binding.progressIndicator.setProgress(Math.min(percent, 100));
                binding.tvProgressPercent.setText(percent + "%");
                
                if (remaining < 0) {
                    binding.tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    binding.tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBudgetData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
