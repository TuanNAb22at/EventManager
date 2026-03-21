package com.example.eventmanager.ui.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityAddEventBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.utils.SessionManager;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEventActivity extends AppCompatActivity {
    
    private ActivityAddEventBinding binding;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setupToolbar();
        setupDateTimePickers();

        binding.btnCreateEvent.setOnClickListener(v -> saveEvent());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupDateTimePickers() {
        binding.tvSelectDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                binding.tvSelectDate.setText(dateFormatter.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.tvStartTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);
                binding.tvStartTime.setText(timeFormatter.format(time.getTime()));
            }, 8, 0, true).show();
        });

        binding.tvEndTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                Calendar time = Calendar.getInstance();
                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                time.set(Calendar.MINUTE, minute);
                binding.tvEndTime.setText(timeFormatter.format(time.getTime()));
            }, 17, 0, true).show();
        });
    }

    private void saveEvent() {
        String name = binding.etEventName.getText().toString().trim();
        String date = binding.tvSelectDate.getText().toString().trim();
        String startTime = binding.tvStartTime.getText().toString().trim();
        String endTime = binding.tvEndTime.getText().toString().trim();
        String locationName = binding.etLocation.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etEventName.setError("Vui lòng nhập tên sự kiện");
            return;
        }
        if (date.isEmpty() || date.equals("Chọn ngày")) {
            Toast.makeText(this, "Vui lòng chọn ngày diễn ra", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(this);
                int userId = sessionManager.getUserId();

                // 1. Xử lý Location trước
                Integer locationId = null;
                if (!locationName.isEmpty()) {
                    Location loc = new Location(locationName, locationName, "", 0.0, 0.0);
                    long newLocId = db.locationDao().insertLocation(loc);
                    locationId = (int) newLocId;
                }

                // 2. Tạo và Lưu Event
                Event event = new Event(
                    name,
                    description,
                    "Chung",
                    date + " " + startTime,
                    date + " " + endTime,
                    locationId,
                    userId,
                    "Đang diễn ra",
                    50000000.0
                );

                db.eventDao().insertEvent(event);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Thêm sự kiện thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi lưu sự kiện: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
