package com.example.eventmanager.ui.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.databinding.ActivityVenueDetailBinding;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.viewmodel.LocationViewModel;
import java.text.NumberFormat;
import java.util.Locale;

public class VenueDetailActivity extends AppCompatActivity {

    private ActivityVenueDetailBinding binding;
    private Location location;
    private LocationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        location = (Location) getIntent().getSerializableExtra("LOCATION_DATA");
        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        if (location != null) {
            displayDetails();
        }

        setupToolbar();
        setupButtons();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void displayDetails() {
        binding.tvVenueName.setText(location.getName());
        binding.tvAddress.setText(location.getAddress());
        binding.tvRating.setText(String.format(Locale.getDefault(), "%.1f", location.getRating()));
        binding.tvCapacity.setText("Lên đến " + location.getCapacity());
        binding.tvArea.setText(location.getArea() + " m²");
        
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        binding.tvPrice.setText("Từ " + formatter.format(location.getPrice()));
        
        if (location.getDescription() != null && !location.getDescription().isEmpty()) {
            binding.tvDescription.setText(location.getDescription());
        }

        if (location.getImageUrl() != null && !location.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(location.getImageUrl())
                    .placeholder(R.drawable.ic_event_placeholder)
                    .error(R.drawable.ic_event_placeholder)
                    .centerCrop()
                    .into(binding.ivVenueImage);
        } else {
            binding.ivVenueImage.setImageResource(R.drawable.ic_event_placeholder);
        }
    }

    private void setupButtons() {
        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditVenueActivity.class);
            intent.putExtra("LOCATION_DATA", location);
            startActivity(intent);
        });

        binding.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Xóa địa điểm")
                .setMessage("Bạn có chắc chắn muốn xóa địa điểm này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    viewModel.delete(location);
                    Toast.makeText(this, "Đã xóa địa điểm", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from edit
        if (location != null) {
            viewModel.getLocationById(location.getId()).observe(this, updatedLocation -> {
                if (updatedLocation != null) {
                    location = updatedLocation;
                    displayDetails();
                }
            });
        }
    }
}
