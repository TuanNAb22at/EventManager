package com.example.eventmanager.ui.budget;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.EventAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivitySelectEventBudgetBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.utils.SessionManager;
import com.example.eventmanager.viewmodel.EventViewModel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class SelectEventBudgetActivity extends AppCompatActivity {
    private ActivitySelectEventBudgetBinding binding;
    private EventAdapter adapter;
    private EventViewModel eventViewModel;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final DecimalFormat decimalFormat = new DecimalFormat("#,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectEventBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        binding.btnBack.setOnClickListener(v -> finish());

        setupRecyclerView();
        observeEvents();
    }

    private void setupRecyclerView() {
        adapter = new EventAdapter(new ArrayList<>(), event -> {
            showAddBudgetDialog(event);
        });
        binding.rvEventsToSelect.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEventsToSelect.setAdapter(adapter);
    }

    private void observeEvents() {
        int userId = sessionManager.getUserId();
        String role = sessionManager.getUserRole();

        if (SessionManager.ROLE_ORGANIZER.equals(role)) {
            eventViewModel.getAllEvents().observe(this, this::filterAndDisplayEvents);
        } else {
            eventViewModel.getMyEvents(userId).observe(this, this::filterAndDisplayEvents);
        }
    }

    private void filterAndDisplayEvents(List<Event> events) {
        if (events == null) return;
        List<Event> filteredList = events.stream()
                .filter(e -> "Đang lên kế hoạch".equals(e.getStatus()) && e.getTotalBudget() <= 0)
                .collect(Collectors.toList());
        adapter.setEvents(filteredList);
        binding.tvEmptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddBudgetDialog(Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_budget_event, null);
        builder.setView(dialogView);

        View layoutSelectEvent = dialogView.findViewById(R.id.layoutSelectEvent);
        if (layoutSelectEvent != null) layoutSelectEvent.setVisibility(View.GONE);

        EditText etBudget = dialogView.findViewById(R.id.etTotalBudget);
        etBudget.requestFocus();

        // Thêm định dạng tiền tệ khi nhập
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String budgetStr = etBudget.getText().toString().replaceAll("[^\\d]", "");
            if (!budgetStr.isEmpty()) {
                try {
                    double budget = Double.parseDouble(budgetStr);
                    updateEventBudget(event, budget);
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Ngân sách không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void updateEventBudget(Event event, double budget) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            event.setTotalBudget(budget);
            db.eventDao().updateEvent(event);
            runOnUiThread(() -> {
                Intent intent = new Intent(this, BudgetPlanActivity.class);
                intent.putExtra("EVENT_ID", event.getId());
                intent.putExtra("EVENT_NAME", event.getName());
                intent.putExtra("TOTAL_BUDGET", budget);
                startActivity(intent);
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
