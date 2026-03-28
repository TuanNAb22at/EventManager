package com.example.eventmanager.ui.event;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.FilterAdapter;
import com.example.eventmanager.adapter.SelectVendorAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivitySelectVendorBinding;
import com.example.eventmanager.model.EventVendor;
import com.example.eventmanager.model.Vendor;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SelectVendorActivity extends AppCompatActivity implements SelectVendorAdapter.OnVendorSelectionChangedListener {

    private ActivitySelectVendorBinding binding;
    private int eventId;
    private String eventName;
    private SelectVendorAdapter adapter;
    private FilterAdapter filterAdapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;
    private String currentFilterType = "";
    private List<Vendor> alreadySelectedVendors = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectVendorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        eventName = getIntent().getStringExtra("EVENT_NAME");

        if (eventId == -1) {
            finish();
            return;
        }

        if (eventName != null) {
            binding.tvEventName.setText("Cho sự kiện: " + eventName);
        } else {
            loadEventName();
        }
        
        setupRecyclerView();
        setupFilters();
        setupSearch();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnConfirm.setOnClickListener(v -> saveSelections());

        loadAlreadySelectedVendors();
        loadFilters();
    }

    private void loadEventName() {
        AppDatabase.getInstance(this).eventDao().getEventById(eventId).observe(this, event -> {
            if (event != null) {
                eventName = event.getName();
                binding.tvEventName.setText("Cho sự kiện: " + eventName);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new SelectVendorAdapter(new ArrayList<>(), this);
        binding.rvSuppliers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSuppliers.setAdapter(adapter);
    }

    private void setupFilters() {
        filterAdapter = new FilterAdapter(new ArrayList<>(), filter -> {
            if (currentFilterType.equals(filter)) {
                currentFilterType = "";
            } else {
                currentFilterType = filter;
            }
            filterAdapter.setSelectedFilter(currentFilterType);
            loadVendors();
        });
        binding.rvFilters.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvFilters.setAdapter(filterAdapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadVendors();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAlreadySelectedVendors() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // Lấy danh sách vendor đã được gán cho event này từ trước
            List<Vendor> vendors = db.vendorDao().getVendorsByEventIdSync(eventId);
            runOnUiThread(() -> {
                alreadySelectedVendors = vendors;
                adapter.setSelectedVendors(vendors);
                updateSelectedCount(vendors.size());
                loadVendors(); // Load danh sách tổng sau khi đã biết cái nào được chọn
            });
        });
    }

    private void loadFilters() {
        executorService.execute(() -> {
            List<String> types = AppDatabase.getInstance(this).vendorDao().getDistinctServiceTypes();
            runOnUiThread(() -> {
                if (filterAdapter != null) {
                    filterAdapter.setFilters(types);
                }
            });
        });
    }

    private void loadVendors() {
        String query = "%" + binding.etSearch.getText().toString().trim() + "%";
        int userId = sessionManager.getUserId();

        executorService.execute(() -> {
            List<Vendor> vendors;
            if (sessionManager.isStaff()) {
                // Staff can see all vendors to select
                vendors = AppDatabase.getInstance(this).vendorDao()
                        .searchAndFilterAllVendors(query, currentFilterType);
            } else {
                // Organizer only sees their own created vendors to select
                vendors = AppDatabase.getInstance(this).vendorDao()
                        .searchAndFilterVendors(userId, query, currentFilterType);
            }

            runOnUiThread(() -> {
                adapter.setVendors(vendors);
                if (vendors.isEmpty()) {
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyState.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    public void onSelectionChanged(int count) {
        updateSelectedCount(count);
    }

    private void updateSelectedCount(int count) {
        binding.tvSelectedCount.setText(count + " Nhà cung cấp đã chọn");
    }

    private void saveSelections() {
        List<Vendor> currentSelected = adapter.getSelectedVendors();

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            
            // 1. Xóa các liên kết cũ của sự kiện này
            db.eventVendorDao().deleteByEventId(eventId);
            
            // 2. Thêm các liên kết mới dựa trên danh sách hiện tại
            for (Vendor vendor : currentSelected) {
                EventVendor ev = new EventVendor(eventId, vendor.getId(), "");
                db.eventVendorDao().insert(ev);
            }
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã cập nhật danh sách nhà cung cấp", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
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
