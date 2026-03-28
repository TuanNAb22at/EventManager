package com.example.eventmanager.ui.event;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityEditEventBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.ui.location.VenueListActivity;
import com.example.eventmanager.utils.SessionManager;
import com.example.eventmanager.viewmodel.EventViewModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditEventActivity extends AppCompatActivity {

    private ActivityEditEventBinding binding;
    private int eventId;
    private Event currentEvent;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private EventViewModel eventViewModel;
    private SessionManager sessionManager;
    private String selectedBannerUri = null;
    private Integer selectedLocationId = null;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int SELECT_LOCATION_REQUEST = 2;

    private static final String[] STATUS_OPTIONS = {
            "Đang lên kế hoạch",
            "Đang diễn ra",
            "Đã kết thúc",
            "Đã hủy"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        if (eventId == -1) {
            finish();
            return;
        }

        sessionManager = new SessionManager(this);
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);

        setupToolbar();
        setupDateTimePickers();
        setupImagePicker();
        setupSpinners();
        setupLocationPicker();
        observeEventTypes();
        loadEventData();

        binding.btnUpdateEvent.setOnClickListener(v -> updateEvent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLocationSuggestions();
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, STATUS_OPTIONS);
        binding.actvStatus.setAdapter(statusAdapter);
        binding.actvStatus.setOnClickListener(v -> binding.actvStatus.showDropDown());
    }

    private void observeEventTypes() {
        eventViewModel.getAllEventTypes().observe(this, types -> {
            List<String> eventTypes = new ArrayList<>();
            if (types != null && !types.isEmpty()) {
                eventTypes.addAll(types);
            } else {
                eventTypes.add("Đám cưới (Wedding)");
                eventTypes.add("Sinh nhật (Birthday)");
                eventTypes.add("Hội nghị/Sự kiện doanh nghiệp");
                eventTypes.add("Lễ kỷ niệm");
                eventTypes.add("Tiệc tối (Gala Dinner)");
                eventTypes.add("Buổi hòa nhạc/Show diễn");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    eventTypes
            );
            binding.actvEventType.setAdapter(adapter);
            binding.actvEventType.setOnClickListener(v -> binding.actvEventType.showDropDown());
        });
    }

    private void setupImagePicker() {
        View.OnClickListener listener = v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        };
        binding.btnChooseImage.setOnClickListener(listener);
        binding.btnChangeImage.setOnClickListener(listener);
    }

    private void setupLocationPicker() {
        binding.btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, VenueListActivity.class);
            intent.putExtra("SELECT_MODE", true);
            startActivityForResult(intent, SELECT_LOCATION_REQUEST);
        });
        
        loadLocationSuggestions();
    }

    private void loadLocationSuggestions() {
        executorService.execute(() -> {
            List<Location> locations = AppDatabase.getInstance(this).locationDao().getAllLocationsSync();
            List<String> locationNames = new ArrayList<>();
            for (Location loc : locations) {
                locationNames.add(loc.getName());
            }

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        locationNames
                );
                binding.etLocation.setAdapter(adapter);
                binding.etLocation.setOnItemClickListener((parent, view, position, id) -> {
                    String selectedName = (String) parent.getItemAtPosition(position);
                    for (Location loc : locations) {
                        if (loc.getName().equals(selectedName)) {
                            selectedLocationId = loc.getId();
                            break;
                        }
                    }
                });
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    selectedBannerUri = imageUri.toString();
                    binding.ivBannerPreview.setImageURI(imageUri);
                    binding.uploadPlaceholder.setVisibility(View.GONE);
                    binding.ivBannerPreview.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    Toast.makeText(this, "Lỗi chọn ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == SELECT_LOCATION_REQUEST && resultCode == RESULT_OK && data != null) {
            int locId = data.getIntExtra("LOCATION_ID", -1);
            String locationName = data.getStringExtra("LOCATION_NAME");
            if (locId != -1) {
                selectedLocationId = locId;
                binding.etLocation.setText(locationName);
            }
        }
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

    private void loadEventData() {
        AppDatabase.getInstance(this).eventDao().getEventById(eventId).observe(this, event -> {
            if (event != null && currentEvent == null) {
                currentEvent = event;
                populateViews(event);
            }
        });
    }

    private void populateViews(Event event) {
        binding.etEventName.setText(event.getName());
        binding.actvEventType.setText(event.getEventType(), false);
        binding.actvStatus.setText(event.getStatus(), false);
        binding.etDescription.setText(event.getDescription());
        
        if (event.getStartAt() != null && event.getStartAt().contains(" ")) {
            String[] parts = event.getStartAt().split(" ");
            binding.tvSelectDate.setText(parts[0]);
            binding.tvStartTime.setText(parts[1]);
        }

        if (event.getEndAt() != null && event.getEndAt().contains(" ")) {
            binding.tvEndTime.setText(event.getEndAt().split(" ")[1]);
        }

        if (event.getBannerUri() != null && !event.getBannerUri().isEmpty()) {
            selectedBannerUri = event.getBannerUri();
            Glide.with(this).load(selectedBannerUri).into(binding.ivBannerPreview);
            binding.uploadPlaceholder.setVisibility(View.GONE);
            binding.ivBannerPreview.setVisibility(View.VISIBLE);
        }

        selectedLocationId = event.getLocationId();
        if (selectedLocationId != null) {
            executorService.execute(() -> {
                Location location = AppDatabase.getInstance(this).locationDao().getLocationByIdSync(selectedLocationId);
                if (location != null) {
                    runOnUiThread(() -> binding.etLocation.setText(location.getName()));
                }
            });
        }
    }

    private void updateEvent() {
        String name = binding.etEventName.getText().toString().trim();
        String eventType = binding.actvEventType.getText().toString().trim();
        String status = binding.actvStatus.getText().toString().trim();
        String date = binding.tvSelectDate.getText().toString().trim();
        String startTime = binding.tvStartTime.getText().toString().trim();
        String endTime = binding.tvEndTime.getText().toString().trim();
        String locationName = binding.etLocation.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etEventName.setError("Vui lòng nhập tên sự kiện");
            binding.etEventName.requestFocus();
            return;
        }
        if (eventType.isEmpty()) {
            binding.actvEventType.setError("Vui lòng chọn loại sự kiện");
            binding.actvEventType.requestFocus();
            return;
        }
        if (date.isEmpty() || date.equals("Chọn ngày")) {
            Toast.makeText(this, "Vui lòng chọn ngày diễn ra", Toast.LENGTH_SHORT).show();
            return;
        }
        if (startTime.isEmpty() || startTime.equals("Giờ bắt đầu")) {
            Toast.makeText(this, "Vui lòng chọn giờ bắt đầu", Toast.LENGTH_SHORT).show();
            return;
        }
        if (endTime.isEmpty() || endTime.equals("Giờ kết thúc")) {
            Toast.makeText(this, "Vui lòng chọn giờ kết thúc", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate start time < end time
        try {
            Date start = timeFormatter.parse(startTime);
            Date end = timeFormatter.parse(endTime);
            if (start != null && end != null && !end.after(start)) {
                Toast.makeText(this, "Giờ kết thúc phải sau giờ bắt đầu", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            Toast.makeText(this, "Định dạng thời gian không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (locationName.isEmpty()) {
            binding.etLocation.setError("Vui lòng chọn hoặc nhập địa điểm");
            binding.etLocation.requestFocus();
            return;
        }

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int userId = sessionManager.getUserId();

            // Handle location update
            Integer locationId = selectedLocationId;
            if (!locationName.isEmpty()) {
                if (locationId == null) {
                    Location loc = new Location();
                    loc.setName(locationName);
                    loc.setAddress(locationName);
                    loc.setCreatedBy(userId);
                    locationId = (int) db.locationDao().insertLocation(loc);
                } else {
                    Location loc = db.locationDao().getLocationByIdSync(locationId);
                    if (loc != null) {
                        loc.setName(locationName);
                        loc.setAddress(locationName);
                        db.locationDao().updateLocation(loc);
                    }
                }
            }

            currentEvent.setName(name);
            currentEvent.setEventType(eventType);
            currentEvent.setStatus(status);
            currentEvent.setStartAt(date + " " + startTime);
            currentEvent.setEndAt(date + " " + endTime);
            currentEvent.setLocationId(locationId);
            currentEvent.setDescription(description);
            currentEvent.setBannerUri(selectedBannerUri);
            currentEvent.setUpdatedAt(System.currentTimeMillis());

            db.eventDao().updateEvent(currentEvent);

            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
