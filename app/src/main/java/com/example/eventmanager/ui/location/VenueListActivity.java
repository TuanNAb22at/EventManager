package com.example.eventmanager.ui.location;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.VenueAdapter;
import com.example.eventmanager.databinding.ActivityVenueListBinding;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.viewmodel.LocationViewModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VenueListActivity extends AppCompatActivity {

    public static final String EXTRA_SELECT_MODE = "SELECT_MODE";
    public static final String EXTRA_SELECTED_LOCATION = "SELECTED_LOCATION";

    private ActivityVenueListBinding binding;
    private VenueAdapter adapter;
    private LocationViewModel viewModel;
    private boolean isSelectMode = false;
    
    private List<Location> fullList = new ArrayList<>();
    private String currentSearchQuery = "";
    private boolean isSortLowToHigh = false;
    private boolean isFilterCapacity500 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isSelectMode = getIntent().getBooleanExtra(EXTRA_SELECT_MODE, false);
        if (isSelectMode) {
            binding.toolbar.setTitle("Chọn địa điểm");
        }

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupSearchAndFilters();
        observeLocations();
        
        binding.fabAddVenue.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVenueActivity.class);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new VenueAdapter(new VenueAdapter.OnVenueClickListener() {
            @Override
            public void onVenueClick(Location location) {
                if (isSelectMode) {
                    showConfirmSelectionDialog(location);
                } else {
                    openDetail(location);
                }
            }

            @Override
            public void onDetailClick(Location location) {
                openDetail(location);
            }
        });
        binding.rvVenues.setLayoutManager(new LinearLayoutManager(this));
        binding.rvVenues.setAdapter(adapter);
    }

    private void setupSearchAndFilters() {
        // Tìm kiếm tự động khi gõ
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Lọc giá thấp đến cao
        binding.btnSortPrice.setOnClickListener(v -> {
            isSortLowToHigh = !isSortLowToHigh;
            updateFilterButtonState(binding.btnSortPrice, isSortLowToHigh);
            applyFilters();
        });

        // Lọc sức chứa 500+
        binding.btnFilterCapacity.setOnClickListener(v -> {
            isFilterCapacity500 = !isFilterCapacity500;
            updateFilterButtonState(binding.btnFilterCapacity, isFilterCapacity500);
            applyFilters();
        });
    }

    private void updateFilterButtonState(android.widget.Button button, boolean isActive) {
        if (button instanceof com.google.android.material.button.MaterialButton) {
            com.google.android.material.button.MaterialButton materialButton = (com.google.android.material.button.MaterialButton) button;
            if (isActive) {
                materialButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(com.example.eventmanager.R.color.primary_blue)));
                materialButton.setTextColor(getResources().getColor(android.R.color.white));
                materialButton.setStrokeWidth(0);
            } else {
                materialButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
                materialButton.setTextColor(getResources().getColor(android.R.color.black));
                materialButton.setStrokeWidth(1);
            }
        } else {
            // Fallback for regular Button if it's not a MaterialButton for some reason
            if (isActive) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(com.example.eventmanager.R.color.primary_blue)));
                button.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.TRANSPARENT));
                button.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    private void applyFilters() {
        List<Location> filteredList = new ArrayList<>(fullList);

        // 1. Lọc theo tên/địa chỉ
        if (!currentSearchQuery.isEmpty()) {
            filteredList = filteredList.stream()
                    .filter(l -> l.getName().toLowerCase().contains(currentSearchQuery) || 
                                 l.getAddress().toLowerCase().contains(currentSearchQuery))
                    .collect(Collectors.toList());
        }

        // 2. Lọc theo sức chứa
        if (isFilterCapacity500) {
            filteredList = filteredList.stream()
                    .filter(l -> l.getCapacity() >= 500)
                    .collect(Collectors.toList());
        }

        // 3. Sắp xếp theo giá
        if (isSortLowToHigh) {
            Collections.sort(filteredList, (l1, l2) -> Double.compare(l1.getPrice(), l2.getPrice()));
        } else {
            // Mặc định hoặc quay lại trạng thái cũ, LiveData sẽ trả về danh sách gốc
        }

        adapter.setLocations(filteredList);
        binding.tvVenueCount.setText(filteredList.size() + " địa điểm tại Việt Nam");
    }

    private void showConfirmSelectionDialog(Location location) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận chọn")
                .setMessage("Bạn có muốn chọn địa điểm \"" + location.getName() + "\" cho sự kiện này không?")
                .setPositiveButton("Chọn", (dialog, which) -> {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_SELECTED_LOCATION, location);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void openDetail(Location location) {
        Intent intent = new Intent(this, VenueDetailActivity.class);
        intent.putExtra("LOCATION_DATA", location);
        intent.putExtra(VenueDetailActivity.EXTRA_EVENT_CONTEXT, false); 
        startActivity(intent);
    }

    private void observeLocations() {
        viewModel.getAllLocations().observe(this, locations -> {
            if (locations != null) {
                fullList = locations;
                applyFilters();
            }
        });
    }
}
