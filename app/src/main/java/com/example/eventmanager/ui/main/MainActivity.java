package com.example.eventmanager.ui.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.FilterAdapter;
import com.example.eventmanager.adapter.UpcomingEventAdapter;
import com.example.eventmanager.databinding.ActivityMainBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.ui.auth.ChangePasswordActivity;
import com.example.eventmanager.ui.auth.LoginActivity;
import com.example.eventmanager.ui.budget.BudgetEventActivity;
import com.example.eventmanager.ui.event.AddEventActivity;
import com.example.eventmanager.ui.event.EventDetailActivity;
import com.example.eventmanager.ui.event.MyEventActivity;
import com.example.eventmanager.ui.location.VenueListActivity;
import com.example.eventmanager.ui.profile.ProfileActivity;
import com.example.eventmanager.ui.profile.UserListActivity;
import com.example.eventmanager.ui.vendor.VendorListActivity;
import com.example.eventmanager.utils.SessionManager;
import com.example.eventmanager.viewmodel.EventViewModel;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;
    private EventViewModel eventViewModel;
    private UpcomingEventAdapter adapter;
    private FilterAdapter filterAdapter;
    private List<Event> allEvents = new ArrayList<>();
    private String currentSearchQuery = "";
    private String selectedCategory = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        initUI();
        setupSearch();
        setupFilters();
        setupRecyclerView();
        setupWindowInsets();
        setupDrawer();
        setupBottomNavigation();
        setupFab();
        observeEvents();
        observeEventTypes();
        checkUserPermissions();
    }

    private void checkUserPermissions() {
        if (sessionManager.isStaff()) {
            binding.fabAdd.setVisibility(View.GONE);

            // Ẩn menu Ngân sách và Placeholder ở thanh điều hướng dưới
            Menu menu = binding.bottomNavigationView.getMenu();
            MenuItem budgetItem = menu.findItem(R.id.nav_budget);
            if (budgetItem != null) {
                budgetItem.setVisible(false);
            }
            
            MenuItem placeholderItem = menu.findItem(R.id.nav_placeholder);
            if (placeholderItem != null) {
                placeholderItem.setVisible(false);
            }

            // Ẩn menu Quản lý tài khoản trong Drawer
            Menu drawerMenu = binding.navigationView.getMenu();
            MenuItem manageAccountItem = drawerMenu.findItem(R.id.nav_manage_accounts);
            if (manageAccountItem != null) {
                manageAccountItem.setVisible(false);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void initUI() {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
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
        adapter = new UpcomingEventAdapter(event -> {
            Intent intent = new Intent(MainActivity.this, EventDetailActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            startActivity(intent);
        });
        binding.rvUpcomingEvents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvUpcomingEvents.setAdapter(adapter);
    }

    private void observeEvents() {
        eventViewModel.getAllEvents().observe(this, events -> {
            if (events != null) {
                allEvents = events;
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
        List<Event> filteredList = allEvents.stream()
            .filter(event -> {
                // Lọc theo tên
                boolean matchesSearch = event.getName().toLowerCase().contains(currentSearchQuery);
                
                // Lọc theo loại sự kiện
                boolean matchesCategory = selectedCategory.equals("Tất cả") || 
                                          (event.getEventType() != null && event.getEventType().equalsIgnoreCase(selectedCategory));
                
                return matchesSearch && matchesCategory;
            })
            .collect(Collectors.toList());
        
        adapter.setEvents(filteredList);
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            if (sessionManager.canManageAll()) {
                startActivity(new Intent(MainActivity.this, AddEventActivity.class));
            } else {
                Snackbar.make(binding.getRoot(), "Bạn không có quyền thực hiện chức năng này!", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(ContextCompat.getColor(this, android.R.color.holo_red_dark))
                        .show();
            }
        });
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            binding.headerLayout.setPadding(
                binding.headerLayout.getPaddingLeft(),
                systemBars.top, 
                binding.headerLayout.getPaddingRight(),
                binding.headerLayout.getPaddingBottom()
            );

            binding.bottomNavContainer.setPadding(0, 0, 0, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void setupDrawer() {
        binding.btnMenu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        View headerView = binding.navigationView.getHeaderView(0);
        TextView tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
        TextView tvHeaderRole = headerView.findViewById(R.id.tvHeaderRole);
        
        if (tvHeaderName != null) tvHeaderName.setText(sessionManager.getUsername());
        if (tvHeaderRole != null) tvHeaderRole.setText(getRoleDisplayName(sessionManager.getUserRole()));

        binding.navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else if (id == R.id.nav_manage_accounts) {
                startActivity(new Intent(this, UserListActivity.class));
            } else if (id == R.id.nav_change_password) {
                startActivity(new Intent(this, ChangePasswordActivity.class));
            } else if (id == R.id.nav_logout) {
                sessionManager.logoutUser();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private String getRoleDisplayName(String role) {
        if (role == null) return "Người dùng";
        switch (role) {
            case SessionManager.ROLE_ORGANIZER: return "Người tổ chức";
            case SessionManager.ROLE_STAFF: return "Nhân viên";
            default: return "Người dùng";
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_supplier) {
                startActivity(new Intent(MainActivity.this, VendorListActivity.class));
                return true;
            } else if (itemId == R.id.nav_budget) {
                if (sessionManager.isStaff()) {
                    Toast.makeText(this, "Bạn không có quyền xem mục này", Toast.LENGTH_SHORT).show();
                    return false;
                }
                startActivity(new Intent(MainActivity.this, BudgetEventActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile_menu) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_event) {
                startActivity(new Intent(MainActivity.this, MyEventActivity.class));
                return true;
            }
            return true;
        });
    }
}
