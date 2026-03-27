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
import com.example.eventmanager.ui.task.TaskListActivity;
import com.example.eventmanager.ui.location.VenueListActivity;
import com.example.eventmanager.ui.guest.GuestListActivity;
import com.example.eventmanager.utils.SessionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventDetailActivity extends AppCompatActivity {

    private ActivityEventDetailBinding binding;
    private int eventId;
    private SessionManager sessionManager;
    private Event currentEvent;
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
        observeGuestCount();
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

    private void observeGuestCount() {
        AppDatabase.getInstance(this).guestDao().getGuestCountByEventId(eventId).observe(this, count -> {
            if (count != null) {
                binding.tvAttendees.setText(count + " Tham gia");
            }
        });
    }

    private void displayEventDetails(Event event) {
        binding.tvEventTitle.setText(event.getName());
        binding.tvEventDate.setText(event.getStartAt());
        
        // Hiển thị mô tả (description)
        if (event.getDescription() != null && !event.getDescription().isEmpty()) {
            binding.tvAboutContent.setText(event.getDescription());
        } else {
            binding.tvAboutContent.setText("Chưa có mô tả cho sự kiện này.");
        }
        
        // Hiển thị địa điểm
        if (event.getLocationId() != null) {
            executorService.execute(() -> {
                Location location = AppDatabase.getInstance(this).locationDao().getLocationByIdSync(event.getLocationId());
                if (location != null) {
                    runOnUiThread(() -> {
                        binding.tvEventLocation.setText(location.getName());
                        binding.tvEventAddress.setText(location.getAddress());
                    });
                }
            });
        } else {
            binding.tvEventLocation.setText("Địa điểm chưa xác định");
            binding.tvEventAddress.setText("Vui lòng cập nhật địa điểm");
        }
        
        // Hiển thị ảnh banner từ bannerUri
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
        binding.btnInvite.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestListActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            if (currentEvent != null) intent.putExtra("EVENT_NAME", currentEvent.getName());
            startActivity(intent);
        });

        binding.btnTasks.setOnClickListener(v -> {
            Intent intent = new Intent(this, TaskListActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            if (currentEvent != null) intent.putExtra("EVENT_NAME", currentEvent.getName());
            startActivity(intent);
        });

        binding.btnSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(this, EventVendorListActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            if (currentEvent != null) intent.putExtra("EVENT_NAME", currentEvent.getName());
            startActivity(intent);
        });

        binding.btnLocation.setOnClickListener(v -> {
            Intent intent = new Intent(this, VenueListActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            if (currentEvent != null) intent.putExtra("EVENT_NAME", currentEvent.getName());
            startActivity(intent);
        });

        binding.btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
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
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
