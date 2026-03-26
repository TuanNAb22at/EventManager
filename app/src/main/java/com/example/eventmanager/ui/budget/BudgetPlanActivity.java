package com.example.eventmanager.ui.budget;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.BudgetPlanAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityBudgetPlanBinding;
import com.example.eventmanager.model.Budget;
import com.example.eventmanager.model.Event;
import com.google.android.material.textfield.TextInputEditText;
import java.text.DecimalFormat;
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
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

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

        binding.tvTotalBudget.setOnClickListener(v -> showEditBudgetDialog());
    }

    private void setupRecyclerView() {
        adapter = new BudgetPlanAdapter(new ArrayList<>(), budget -> {
            // Chỉnh sửa chi phí khi nhấn vào item
            Intent intent = new Intent(this, AddBudgetItemActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            intent.putExtra("BUDGET_ID", budget.getId());
            startActivity(intent);
        });
        binding.rvBudgets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBudgets.setAdapter(adapter);
    }

    private void showEditBudgetDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_budget, null);
        TextInputEditText etBudget = dialogView.findViewById(R.id.etBudgetAmount);

        etBudget.setText(decimalFormat.format(totalBudget));

        etBudget.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    etBudget.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = decimalFormat.format(parsed);
                        current = formatted;
                        etBudget.setText(formatted);
                        etBudget.setSelection(formatted.length());
                    } else {
                        current = "";
                        etBudget.setText("");
                    }

                    etBudget.addTextChangedListener(this);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String cleanString = etBudget.getText().toString().replaceAll("[^\\d]", "");
                    if (!cleanString.isEmpty()) {
                        try {
                            double newBudget = Double.parseDouble(cleanString);
                            updateTotalBudget(newBudget);
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void updateTotalBudget(double newBudget) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Event event = db.eventDao().getEventByIdSync(eventId);
            if (event != null) {
                event.setTotalBudget(newBudget);
                db.eventDao().updateEvent(event);
                totalBudget = newBudget;
                loadBudgetData();
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
