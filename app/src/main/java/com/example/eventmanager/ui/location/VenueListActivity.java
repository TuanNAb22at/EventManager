package com.example.eventmanager.ui.location;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.VenueAdapter;
import com.example.eventmanager.databinding.ActivityVenueListBinding;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.viewmodel.LocationViewModel;
import com.google.android.material.card.MaterialCardView;
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
            binding.tvTitle.setText("Chọn địa điểm");
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
        binding.btnBack.setOnClickListener(v -> finish());
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
        binding.cardSortPrice.setOnClickListener(v -> {
            isSortLowToHigh = !isSortLowToHigh;
            updateFilterCardState(binding.cardSortPrice, binding.tvSortPriceLabel, isSortLowToHigh);
            applyFilters();
        });

        // Lọc sức chứa 500+
        binding.cardFilterCapacity.setOnClickListener(v -> {
            isFilterCapacity500 = !isFilterCapacity500;
            updateFilterCardState(binding.cardFilterCapacity, binding.tvFilterCapacityLabel, isFilterCapacity500);
            applyFilters();
        });
    }

    private void updateFilterCardState(MaterialCardView card, TextView label, boolean isActive) {
        if (isActive) {
            card.setCardBackgroundColor(ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
            label.setTextColor(Color.WHITE);
            card.setStrokeWidth(0);
            card.setCardElevation(4f);
        } else {
            card.setCardBackgroundColor(ColorStateList.valueOf(Color.WHITE));
            label.setTextColor(Color.parseColor("#475569"));
            card.setStrokeWidth(1);
            card.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E2E8F0")));
            card.setCardElevation(0f);
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
