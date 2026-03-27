package com.example.eventmanager.ui.location;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.VenueAdapter;
import com.example.eventmanager.databinding.ActivityVenueListBinding;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.viewmodel.LocationViewModel;

public class VenueListActivity extends AppCompatActivity {

    public static final String EXTRA_SELECT_MODE = "SELECT_MODE";
    public static final String EXTRA_SELECTED_LOCATION = "SELECTED_LOCATION";

    private ActivityVenueListBinding binding;
    private VenueAdapter adapter;
    private LocationViewModel viewModel;
    private boolean isSelectMode = false;

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
        adapter = new VenueAdapter(location -> {
            if (isSelectMode) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(EXTRA_SELECTED_LOCATION, location);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Intent intent = new Intent(this, VenueDetailActivity.class);
                intent.putExtra("LOCATION_DATA", location);
                startActivity(intent);
            }
        });
        binding.rvVenues.setLayoutManager(new LinearLayoutManager(this));
        binding.rvVenues.setAdapter(adapter);
    }

    private void observeLocations() {
        viewModel.getAllLocations().observe(this, locations -> {
            if (locations != null) {
                adapter.setLocations(locations);
                binding.tvVenueCount.setText(locations.size() + " địa điểm tại Việt Nam");
            }
        });
    }
}
