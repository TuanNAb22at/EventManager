package com.example.eventmanager.ui.budget;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.BudgetPlanAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityBudgetPlanBinding;
import com.example.eventmanager.model.Budget;
import com.example.eventmanager.model.Event;
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

        // Cho phép chỉnh sửa tổng ngân sách khi nhấn vào số tiền
        binding.tvTotalBudget.setOnClickListener(v -> showEditBudgetDialog());
    }

    private void setupRecyclerView() {
        adapter = new BudgetPlanAdapter(new ArrayList<>(), budget -> {
            // Xem chi tiết hoặc xóa nếu cần
        });
        binding.rvBudgets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBudgets.setAdapter(adapter);
    }

    private void showEditBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chỉnh sửa tổng ngân sách");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf((long)totalBudget));
        builder.setView(input);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String newBudgetStr = input.getText().toString();
            if (!newBudgetStr.isEmpty()) {
                try {
                    double newBudget = Double.parseDouble(newBudgetStr);
                    updateTotalBudget(newBudget);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void updateTotalBudget(double newBudget) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Event event = db.eventDao().getEventById(eventId);
            if (event != null) {
                event.setTotalBudget(newBudget);
                db.eventDao().updateEvent(event);
                totalBudget = newBudget;
                loadBudgetData(); // Tải lại để cập nhật UI
            }
        });
    }

    private void loadBudgetData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Budget> budgets = db.budgetDao().getBudgetsByEventId(eventId);
            
            double spent = 0;
            for (Budget b : budgets) {
                spent += b.getAmount();
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
                    binding.tvRemaining.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
                } else {
                    binding.tvRemaining.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
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
