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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

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

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
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
        if (sessionManager.isStaff()) {
            eventViewModel.getAllEvents().observe(this, events -> {
                if (events != null) {
                    allMyEvents = events;
                    filterAndDisplayEvents();
                }
            });
        } else {
            eventViewModel.getMyEvents(sessionManager.getUserId()).observe(this, events -> {
                if (events != null) {
                    allMyEvents = events;
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

    private boolean isEventPassed(Event event) {
        if ("Đã kết thúc".equals(event.getStatus())) return true;
        if (event.getEndAt() != null && !event.getEndAt().isEmpty()) {
            try {
                Date endDate = dateTimeFormat.parse(event.getEndAt());
                return endDate != null && endDate.before(new Date());
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }

    private void filterAndDisplayEvents() {
        int tabPosition = binding.tabLayout.getSelectedTabPosition();
        
        List<Event> filteredList = allMyEvents.stream()
            .filter(event -> {
                boolean passed = isEventPassed(event);
                boolean matchesTab = (tabPosition == 0) ? !passed : passed;
                boolean matchesSearch = event.getName().toLowerCase().contains(currentSearchQuery);
                boolean matchesCategory = selectedCategory.equals("Tất cả") || 
                                          (event.getEventType() != null && event.getEventType().equalsIgnoreCase(selectedCategory));
                return matchesTab && matchesSearch && matchesCategory;
            })
            .sorted((e1, e2) -> {
                try {
                    Date d1 = dateTimeFormat.parse(e1.getStartAt());
                    Date d2 = dateTimeFormat.parse(e2.getStartAt());
                    if (d1 == null || d2 == null) return 0;
                    return d1.compareTo(d2);
                } catch (ParseException e) {
                    return 0;
                }
            })
            .collect(Collectors.toList());
        
        adapter.setEvents(filteredList);
        binding.tvEmptyState.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
