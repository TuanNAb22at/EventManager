package com.example.eventmanager.ui.event;

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
import com.example.eventmanager.databinding.ActivityMyEventBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.utils.SessionManager;
import com.example.eventmanager.viewmodel.EventViewModel;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MyEventActivity extends AppCompatActivity {

    private ActivityMyEventBinding binding;
    private EventViewModel eventViewModel;
    private EventAdapter adapter;
    private FilterAdapter filterAdapter;
    private SessionManager sessionManager;
    private List<Event> allMyEvents = new ArrayList<>();
    private String currentSearchQuery = "";
    private String selectedCategory = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        setupToolbar();
        setupSearch();
        setupFilters();
        setupRecyclerView();
        setupTabs();
        observeEvents();
        observeEventTypes();
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
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
            Intent intent = new Intent(MyEventActivity.this, EventDetailActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            startActivity(intent);
        });
        binding.rvMyEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMyEvents.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterAndDisplayEvents();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void observeEvents() {
        eventViewModel.getMyEvents(sessionManager.getUserId()).observe(this, events -> {
            if (events != null) {
                allMyEvents = events;
                filterAndDisplayEvents();
            }
        });
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
        int tabPosition = binding.tabLayout.getSelectedTabPosition();
        
        List<Event> filteredList = allMyEvents.stream()
            .filter(event -> {
                // Lọc theo Tab (Sắp tới / Đã qua)
                boolean matchesTab = (tabPosition == 0) 
                    ? !"Đã kết thúc".equals(event.getStatus()) 
                    : "Đã kết thúc".equals(event.getStatus());
                
                // Lọc theo từ khóa tìm kiếm
                boolean matchesSearch = event.getName().toLowerCase().contains(currentSearchQuery);
                
                // Lọc theo loại sự kiện (Category)
                boolean matchesCategory = selectedCategory.equals("Tất cả") || 
                                          (event.getEventType() != null && event.getEventType().equalsIgnoreCase(selectedCategory));
                
                return matchesTab && matchesSearch && matchesCategory;
            })
            .collect(Collectors.toList());
        
        adapter.setEvents(filteredList);
        binding.tvEmptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
