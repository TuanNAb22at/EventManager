package com.example.eventmanager.ui.vendor;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.FilterAdapter;
import com.example.eventmanager.adapter.VendorAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityVendorListBinding;
import com.example.eventmanager.model.Vendor;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VendorListActivity extends AppCompatActivity implements VendorAdapter.OnVendorActionListener {
    
    private ActivityVendorListBinding binding;
    private VendorAdapter adapter;
    private FilterAdapter filterAdapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final int REQUEST_ADD_VENDOR = 101;
    private static final int REQUEST_EDIT_VENDOR = 102;
    private SessionManager sessionManager;
    private String currentFilterType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVendorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        setupRecyclerView();
        setupFilters();
        setupSearch();

        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.fabAddSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVendorActivity.class);
            startActivityForResult(intent, REQUEST_ADD_VENDOR);
        });

        binding.btnSeeAll.setOnClickListener(v -> {
            currentFilterType = "";
            if (filterAdapter != null) filterAdapter.setSelectedFilter("");
            binding.etSearch.setText("");
            loadVendors();
        });

        loadVendors();
        loadFilters();
    }

    private void setupRecyclerView() {
        adapter = new VendorAdapter(new ArrayList<>());
        adapter.setOnVendorActionListener(this);
        binding.rvSuppliers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSuppliers.setAdapter(adapter);
    }

    private void setupFilters() {
        filterAdapter = new FilterAdapter(new ArrayList<>(), filter -> {
            if (currentFilterType.equals(filter)) {
                currentFilterType = ""; // Toggle off
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
                // Staff can see all vendors
                vendors = AppDatabase.getInstance(this).vendorDao()
                        .searchAndFilterAllVendors(query, currentFilterType);
            } else {
                // Organizer only sees their own created vendors
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ADD_VENDOR || requestCode == REQUEST_EDIT_VENDOR) && resultCode == RESULT_OK) {
            loadVendors();
            loadFilters();
            String message = (requestCode == REQUEST_ADD_VENDOR) ? "Đã thêm nhà cung cấp" : "Đã cập nhật thông tin";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEdit(Vendor vendor) {
        Intent intent = new Intent(this, EditVendorActivity.class);
        intent.putExtra("vendor_id", vendor.getId());
        startActivityForResult(intent, REQUEST_EDIT_VENDOR);
    }

    @Override
    public void onDelete(Vendor vendor) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa nhà cung cấp?")
                .setMessage("Bạn có chắc chắn muốn xóa nhà cung cấp \"" + vendor.getName() + "\"? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    executorService.execute(() -> {
                        AppDatabase.getInstance(this).vendorDao().deleteVendor(vendor);
                        loadVendors();
                        loadFilters();
                        runOnUiThread(() -> Toast.makeText(this, "Đã xóa nhà cung cấp", Toast.LENGTH_SHORT).show());
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onItemClick(Vendor vendor) {
        onEdit(vendor);
    }
}
