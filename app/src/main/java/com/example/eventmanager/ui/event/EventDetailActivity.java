package com.example.eventmanager.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityEventDetailBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.ui.guest.GuestListActivity;
import com.example.eventmanager.ui.location.VenueDetailActivity;
import com.example.eventmanager.ui.location.VenueListActivity;
import com.example.eventmanager.utils.SessionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventDetailActivity extends AppCompatActivity {

    private ActivityEventDetailBinding binding;
    private int eventId;
    private SessionManager sessionManager;
    private Event currentEvent;
    private Location currentLocation;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);

        if (eventId == -1) {
            finish();
            return;
        }

        setupToolbar();
        observeEvent();
        setupButtons();
        updateGuestCount();
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditEventActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            startActivity(intent);
        });
    }

    private void observeEvent() {
        AppDatabase.getInstance(this).eventDao().getEventById(eventId).observe(this, event -> {
            if (event != null) {
                currentEvent = event;
                displayEventDetails(event);
            }
        });
    }

    private void displayEventDetails(Event event) {
        binding.tvEventTitle.setText(event.getName());
        binding.tvEventDate.setText(event.getStartAt());
        
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            binding.tvAboutContent.setText(event.getDescription());
        } else {
            binding.tvAboutContent.setText("Chưa có mô tả cho sự kiện này.");
        }
        
        if (event.getLocationId() != null) {
            executorService.execute(() -> {
                Location location = AppDatabase.getInstance(this).locationDao().getLocationByIdSync(event.getLocationId());
                currentLocation = location;
                if (location != null) {
                    runOnUiThread(() -> {
                        binding.tvEventLocation.setText(location.getName());
                        binding.tvEventAddress.setText(location.getAddress());
                    });
                }
            });
        } else {
            currentLocation = null;
            binding.tvEventLocation.setText("Địa điểm chưa xác định");
            binding.tvEventAddress.setText("Vui lòng cập nhật địa điểm");
        }
        
        if (event.getBannerUri() != null && !event.getBannerUri().isEmpty()) {
            Glide.with(this)
                .load(event.getBannerUri())
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(binding.ivBanner);
        } else {
            binding.ivBanner.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    private void setupButtons() {
        // Nút Mời (Invite)
        binding.btnInvite.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestListActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            startActivity(intent);
        });

        // Cả nút màu cam và phần khung địa điểm đều mở Chi tiết địa điểm ở chế độ "Sự kiện"
        View.OnClickListener openVenueDetail = v -> {
            if (currentLocation != null) {
                Intent intent = new Intent(this, VenueDetailActivity.class);
                intent.putExtra("LOCATION_DATA", currentLocation);
                intent.putExtra(VenueDetailActivity.EXTRA_EVENT_CONTEXT, true); // Chế độ có nút Thay đổi
                intent.putExtra(VenueDetailActivity.EXTRA_EVENT_ID, eventId);
                startActivity(intent);
            } else {
                // Nếu chưa có địa điểm thì mở thẳng danh sách chọn
                Intent intent = new Intent(this, VenueListActivity.class);
                intent.putExtra(VenueListActivity.EXTRA_SELECT_MODE, true);
                startActivity(intent);
            }
        };

        binding.btnLocation.setOnClickListener(openVenueDetail);
        View locationContainer = (View) binding.tvEventLocation.getParent();
        locationContainer.setOnClickListener(openVenueDetail);

        binding.btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
    }

    private void updateGuestCount() {
        executorService.execute(() -> {
            int count = AppDatabase.getInstance(this).guestDao().getGuestCountByEventId(eventId);
            runOnUiThread(() -> {
                binding.tvAttendees.setText("+" + count + " Going");
            });
        });
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sự kiện")
                .setMessage("Bạn có chắc chắn muốn xóa sự kiện này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteEvent())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteEvent() {
        if (currentEvent != null) {
            executorService.execute(() -> {
                AppDatabase.getInstance(this).eventDao().deleteEvent(currentEvent);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã xóa sự kiện", Toast.LENGTH_SHORT).show();
                    finish();
                });
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGuestCount(); // Cập nhật lại số lượng khách khi quay lại từ màn hình danh sách khách
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
