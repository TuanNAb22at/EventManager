package com.example.eventmanager.ui.budget;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.EventAdapter;
import com.example.eventmanager.adapter.FilterAdapter;
import com.example.eventmanager.databinding.ActivityBudgetEventBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.utils.SessionManager;
import com.example.eventmanager.viewmodel.EventViewModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BudgetEventActivity extends AppCompatActivity {
    private ActivityBudgetEventBinding binding;
    private EventAdapter adapter;
    private FilterAdapter filterAdapter;
    private EventViewModel eventViewModel;
    private List<Event> allEvents = new ArrayList<>();
    private SessionManager sessionManager;
    private String currentSearchQuery = "";
    private String selectedCategory = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBudgetEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        setupToolbar();
        setupSearch();
        setupFilters();
        setupRecyclerView();
        observeEvents();
        observeEventTypes();

        binding.fabAddBudget.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectEventBudgetActivity.class);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupSearch() {
        binding.etSearchEvent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                filterAndDisplayEvents();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilters() {
        List<String> categories = new ArrayList<>();
        categories.add("Tất cả");

        filterAdapter = new FilterAdapter(categories, category -> {
            selectedCategory = category;
            filterAdapter.setSelectedFilter(category);
            filterAndDisplayEvents();
        });
        filterAdapter.setSelectedFilter("Tất cả");
        
        binding.rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvFilters.setAdapter(filterAdapter);
    }

    private void setupRecyclerView() {
        adapter = new EventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(this, BudgetPlanActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            intent.putExtra("EVENT_NAME", event.getName());
            intent.putExtra("TOTAL_BUDGET", event.getTotalBudget());
            startActivity(intent);
        });
        binding.rvEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEvents.setAdapter(adapter);
    }

    private void observeEvents() {
        int userId = sessionManager.getUserId();
        String role = sessionManager.getUserRole();

        if (SessionManager.ROLE_ORGANIZER.equals(role)) {
            eventViewModel.getAllEvents().observe(this, events -> {
                if (events != null) {
                    allEvents = events;
                    filterAndDisplayEvents();
                }
            });
        } else {
            eventViewModel.getMyEvents(userId).observe(this, events -> {
                if (events != null) {
                    allEvents = events;
                    filterAndDisplayEvents();
                }
            });
        }
    }

    private void observeEventTypes() {
        eventViewModel.getAllEventTypes().observe(this, types -> {
            if (types != null) {
                List<String> categories = new ArrayList<>();
                categories.add("Tất cả");
                categories.addAll(types);
                filterAdapter.setFilters(categories);
            }
        });
    }

    private void filterAndDisplayEvents() {
        List<Event> filteredList = allEvents.stream()
            .filter(event -> {
                // Chỉ hiện những sự kiện ĐÃ có ngân sách (> 0)
                boolean hasBudget = event.getTotalBudget() > 0;
                if (!hasBudget) return false;

                // Lọc theo tên
                boolean matchesSearch = event.getName().toLowerCase().contains(currentSearchQuery);
                
                // Lọc theo loại sự kiện
                boolean matchesCategory = selectedCategory.equals("Tất cả") || 
                                          (event.getEventType() != null && event.getEventType().equalsIgnoreCase(selectedCategory));
                
                return matchesSearch && matchesCategory;
            })
            .collect(Collectors.toList());
        
        adapter.setEvents(filteredList);
        binding.tvEmptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
