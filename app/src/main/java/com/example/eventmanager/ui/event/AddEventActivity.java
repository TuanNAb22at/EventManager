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
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityAddEventBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.ui.location.VenueListActivity;
import com.example.eventmanager.utils.SessionManager;
import com.example.eventmanager.viewmodel.LocationViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
    private LocationViewModel locationViewModel;
    private String selectedBannerUri = null;
    private Integer selectedLocationId = null;
    private List<Location> allLocations = new ArrayList<>();
    
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int SELECT_LOCATION_REQUEST = 2;

    private static final String[] EVENT_TYPES = {
        "Đám cưới (Wedding)",
        "Sinh nhật (Birthday)",
        "Hội nghị/Sự kiện doanh nghiệp",
        "Lễ kỷ niệm",
        "Tiệc tối (Gala Dinner)",
        "Buổi hòa nhạc/Show diễn",
        "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        setupToolbar();
        setupDateTimePickers();
        setupImagePicker();
        setupEventTypeSpinner();
        setupLocationAutocomplete();

        binding.btnCreateEvent.setOnClickListener(v -> saveEvent());
        
        binding.btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, VenueListActivity.class);
            intent.putExtra(VenueListActivity.EXTRA_SELECT_MODE, true);
            startActivityForResult(intent, SELECT_LOCATION_REQUEST);
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
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

    private void setupLocationAutocomplete() {
        locationViewModel.getAllLocations().observe(this, locations -> {
            if (locations != null) {
                allLocations = locations;
                List<String> locationNames = new ArrayList<>();
                for (Location loc : locations) {
                    locationNames.add(loc.getName());
                }
                
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        locationNames
                );
                binding.actvLocation.setAdapter(adapter);
            }
        });

        binding.actvLocation.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            updateSelectedLocationByName(selectedName);
        });
    }

    private void updateSelectedLocationByName(String name) {
        for (Location loc : allLocations) {
            if (loc.getName().equals(name)) {
                selectedLocationId = loc.getId();
                binding.actvLocation.setText(loc.getName(), false);
                break;
            }
        }
    }

    private void setupImagePicker() {
        binding.btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
    }

    private void setupLocationPicker() {
        binding.btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocationMapActivity.class);
            startActivityForResult(intent, PICK_LOCATION_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    try {
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        selectedBannerUri = imageUri.toString();
                        binding.ivBannerPreview.setImageURI(imageUri);
                        binding.ivBannerPreview.setVisibility(View.VISIBLE);
                        binding.uploadPlaceholder.setVisibility(View.GONE);
                    } catch (Exception e) {
                        Toast.makeText(this, "Không thể chọn ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == SELECT_LOCATION_REQUEST) {
                Location selectedLocation = (Location) data.getSerializableExtra(VenueListActivity.EXTRA_SELECTED_LOCATION);
                if (selectedLocation != null) {
                    selectedLocationId = selectedLocation.getId();
                    binding.actvLocation.setText(selectedLocation.getName(), false);
                }
            }
        } else if (requestCode == PICK_LOCATION_REQUEST && resultCode == RESULT_OK && data != null) {
            String locationName = data.getStringExtra("LOCATION_NAME");
            if (locationName != null) {
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

    private void saveEvent() {
        String name = binding.etEventName.getText().toString().trim();
        String eventType = binding.actvEventType.getText().toString().trim();
        String date = binding.tvSelectDate.getText().toString().trim();
        String startTime = binding.tvStartTime.getText().toString().trim();
        String endTime = binding.tvEndTime.getText().toString().trim();
        String locationInput = binding.actvLocation.getText().toString().trim();
        String guestsStr = binding.etTotalGuests.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etEventName.setError("Vui lòng nhập tên sự kiện");
            return;
        }
        if (eventType.isEmpty()) {
            binding.actvEventType.setError("Vui lòng chọn hoặc nhập loại sự kiện");
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

                // Kiểm tra lại nếu người dùng nhập tên mà không chọn từ gợi ý
                Integer locationId = selectedLocationId;
                if (locationId == null && !locationInput.isEmpty()) {
                    for (Location loc : allLocations) {
                        if (loc.getName().equalsIgnoreCase(locationInput)) {
                            locationId = loc.getId();
                            break;
                        }
                    }
                }

                // Tạo và Lưu Event
                Event event = new Event();
                event.setName(name);
                event.setEventType(eventType);
                event.setDescription(description);
                event.setStartAt(date + " " + startTime);
                event.setEndAt(date + " " + endTime);
                event.setLocationId(locationId);
                event.setCreatedBy(userId);
                event.setBannerUri(selectedBannerUri);
                event.setTotalGuests(0);
                event.setStatus("Đang lên kế hoạch");
                event.setTotalBudget(0.0);

                db.eventDao().insertEvent(event);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Tạo sự kiện thành công!", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
