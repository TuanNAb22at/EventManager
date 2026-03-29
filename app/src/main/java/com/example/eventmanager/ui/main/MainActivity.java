package com.example.eventmanager.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.FilterAdapter;
import com.example.eventmanager.adapter.UpcomingEventAdapter;
import com.example.eventmanager.databinding.ActivityMainBinding;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.User;
import com.example.eventmanager.ui.auth.ChangePasswordActivity;
import com.example.eventmanager.ui.auth.LoginActivity;
import com.example.eventmanager.ui.budget.BudgetEventActivity;
import com.example.eventmanager.ui.event.AddEventActivity;
import com.example.eventmanager.ui.event.EventDetailActivity;
import com.example.eventmanager.ui.event.MyEventActivity;
import com.example.eventmanager.ui.location.VenueListActivity;
import com.example.eventmanager.ui.profile.ProfileActivity;
import com.example.eventmanager.ui.profile.UserListActivity;
import com.example.eventmanager.ui.task.TaskOverviewActivity;
import com.example.eventmanager.ui.vendor.VendorListActivity;
import com.example.eventmanager.utils.SessionManager;
import com.example.eventmanager.viewmodel.EventViewModel;
import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.snackbar.Snackbar;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        setupLocationSelection();
        
        // Mặc định lấy vị trí tự động khi mở app
        getCurrentLocation();
        
        binding.btnDashboardAction.setOnClickListener(v -> {
            startActivity(new Intent(this, TaskOverviewActivity.class));
        });
    }

    private void setupLocationSelection() {
        binding.locationContainer.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add(0, 1, 0, "Vị trí hiện tại (GPS)");
            popupMenu.getMenu().add(0, 2, 1, "Hà Nội, Việt Nam");
            
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    getCurrentLocation();
                } else if (item.getItemId() == 2) {
                    binding.tvCurrentLocation.setText("Hà Nội, Việt Nam");
                }
                return true;
            });
            popupMenu.show();
        });
    }

    private void getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        CurrentLocationRequest locationRequest = new CurrentLocationRequest.Builder()
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        fusedLocationClient.getCurrentLocation(locationRequest, null).addOnSuccessListener(this, location -> {
            if (location != null) {
                updateLocationUI(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void updateLocationUI(double lat, double lon) {
        executorService.execute(() -> {
            Geocoder geocoder = new Geocoder(this, new Locale("vi", "VN"));
            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String locationName = address.getLocality();
                    if (locationName == null) locationName = address.getSubAdminArea();
                    if (locationName == null) locationName = address.getAdminArea();
                    
                    String countryName = address.getCountryName();
                    String fullLocation = (locationName != null ? locationName : "") + 
                                         (locationName != null && countryName != null ? ", " : "") + 
                                         (countryName != null ? countryName : "");
                    
                    runOnUiThread(() -> {
                        if (!fullLocation.isEmpty()) {
                            binding.tvCurrentLocation.setText(fullLocation);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private void checkUserPermissions() {
        if (sessionManager.isStaff()) {
            binding.fabAdd.setVisibility(View.GONE);

            Menu menu = binding.bottomNavigationView.getMenu();
            MenuItem budgetItem = menu.findItem(R.id.nav_budget);
            if (budgetItem != null) {
                budgetItem.setVisible(false);
            }
            
            MenuItem placeholderItem = menu.findItem(R.id.nav_placeholder);
            if (placeholderItem != null) {
                placeholderItem.setVisible(false);
            }

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
        updateHeaderInfo();
        updateDashboardInfo();
    }

    private void updateDashboardInfo() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int pendingCount;
            if (sessionManager.isStaff()) {
                pendingCount = db.taskDao().getPendingTaskCountForUserSync(sessionManager.getUserId());
            } else {
                pendingCount = db.taskDao().getPendingTaskCountAllSync();
            }

            runOnUiThread(() -> {
                if (pendingCount > 0) {
                    binding.tvDashboardSubtitle.setText("Bạn có " + pendingCount + " công việc đang chờ xử lý.");
                } else {
                    binding.tvDashboardSubtitle.setText("Tuyệt vời! Bạn đã hoàn thành tất cả công việc.");
                }
            });
        });
    }

    private void updateHeaderInfo() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            User user = db.userDao().getUserById(sessionManager.getUserId());
            if (user != null) {
                runOnUiThread(() -> {
                    View headerView = binding.navigationView.getHeaderView(0);
                    TextView tvHeaderName = headerView.findViewById(R.id.tvHeaderName);
                    TextView tvHeaderRole = headerView.findViewById(R.id.tvHeaderRole);
                    ImageView ivUserAvatar = headerView.findViewById(R.id.ivUserAvatar);

                    if (tvHeaderName != null) tvHeaderName.setText(user.getFullName());
                    if (tvHeaderRole != null) tvHeaderRole.setText(getRoleDisplayName(sessionManager.getUserRole()));
                    if (ivUserAvatar != null) {
                        if (user.getAvatarUri() != null && !user.getAvatarUri().isEmpty()) {
                            Glide.with(this)
                                .load(user.getAvatarUri())
                                .placeholder(R.drawable.ic_user)
                                .circleCrop()
                                .into(ivUserAvatar);
                        } else {
                            ivUserAvatar.setImageResource(R.drawable.ic_user);
                        }
                    }
                });
            }
        });
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
        List<Event> filteredList = allEvents.stream()
            .filter(event -> {
                // Chỉ hiển thị sự kiện CHƯA kết thúc ở mục Sự kiện sắp tới
                if (isEventPassed(event)) return false;

                boolean matchesSearch = event.getName().toLowerCase().contains(currentSearchQuery);
                boolean matchesCategory = selectedCategory.equals("Tất cả") || 
                                          (event.getEventType() != null && event.getEventType().equalsIgnoreCase(selectedCategory));
                return matchesSearch && matchesCategory;
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

        updateHeaderInfo();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
