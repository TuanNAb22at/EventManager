package com.example.eventmanager.ui.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.VenueAdapter;
import com.example.eventmanager.databinding.ActivityVenueListBinding;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.viewmodel.LocationViewModel;
import java.util.ArrayList;
import java.util.List;

public class VenueListActivity extends AppCompatActivity {

    private ActivityVenueListBinding binding;
    private VenueAdapter adapter;
    private LocationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        setupToolbar();
        setupRecyclerView();
        observeLocations();
        
        binding.fabAddVenue.setOnClickListener(v -> {
            // Placeholder: Add a dummy location for testing
            Location dummy = new Location();
            dummy.setName("Khách sạn New World");
            dummy.setAddress("76 Lê Lai, Quận 1, TP.HCM");
            dummy.setPrice(50000000);
            dummy.setCapacity(500);
            dummy.setArea(1000);
            dummy.setPremium(true);
            viewModel.insert(dummy);
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new VenueAdapter(location -> {
            Intent intent = new Intent(this, VenueDetailActivity.class);
            intent.putExtra("LOCATION_DATA", location);
            startActivity(intent);
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
