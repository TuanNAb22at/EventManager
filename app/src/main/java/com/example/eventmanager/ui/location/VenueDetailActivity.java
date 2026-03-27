package com.example.eventmanager.ui.location;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityVenueDetailBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.viewmodel.LocationViewModel;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VenueDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_CONTEXT = "EVENT_CONTEXT";
    public static final String EXTRA_EVENT_ID = "EVENT_ID";
    private static final int SELECT_LOCATION_REQUEST = 300;

    private ActivityVenueDetailBinding binding;
    private Location location;
    private LocationViewModel viewModel;
    private boolean isEventContext = false;
    private int eventId = -1;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVenueDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        location = (Location) getIntent().getSerializableExtra("LOCATION_DATA");
        isEventContext = getIntent().getBooleanExtra(EXTRA_EVENT_CONTEXT, false);
        eventId = getIntent().getIntExtra(EXTRA_EVENT_ID, -1);
        
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
        if (isEventContext) {
            // Chế độ xem từ sự kiện: Hiển thị 1 nút "Thay đổi địa điểm" rộng
            binding.btnEdit.setText("Thay đổi địa điểm");
            binding.btnEdit.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.primary_blue)));
            
            android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) binding.btnEdit.getLayoutParams();
            params.width = android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
            params.setMarginEnd(0);
            binding.btnEdit.setLayoutParams(params);
            
            binding.btnDelete.setVisibility(View.GONE);

            binding.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, VenueListActivity.class);
                intent.putExtra(VenueListActivity.EXTRA_SELECT_MODE, true);
                startActivityForResult(intent, SELECT_LOCATION_REQUEST);
            });
        } else {
            // Chế độ quản lý bình thường (được gọi từ nút Chi tiết trong danh sách)
            binding.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditVenueActivity.class);
                intent.putExtra("LOCATION_DATA", location);
                startActivity(intent);
            });

            binding.btnDelete.setOnClickListener(v -> {
                new androidx.appcompat.app.AlertDialog.Builder(this)
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_LOCATION_REQUEST && resultCode == RESULT_OK && data != null) {
            Location selectedLocation = (Location) data.getSerializableExtra(VenueListActivity.EXTRA_SELECTED_LOCATION);
            if (selectedLocation != null && eventId != -1) {
                updateEventLocation(selectedLocation);
            }
        }
    }

    private void updateEventLocation(Location selectedLocation) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            Event event = db.eventDao().getEventByIdSync(eventId);
            if (event != null) {
                event.setLocationId(selectedLocation.getId());
                db.eventDao().updateEvent(event);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã thay đổi địa điểm sự kiện", Toast.LENGTH_SHORT).show();
                    this.location = selectedLocation;
                    displayDetails();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (location != null && !isEventContext) {
            viewModel.getLocationById(location.getId()).observe(this, updatedLocation -> {
                if (updatedLocation != null) {
                    location = updatedLocation;
                    displayDetails();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
