package com.example.eventmanager.ui.location;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.eventmanager.R;
import com.example.eventmanager.databinding.ActivityEditVenueBinding;
import com.example.eventmanager.model.Location;
import com.example.eventmanager.viewmodel.LocationViewModel;

public class EditVenueActivity extends AppCompatActivity {

    private ActivityEditVenueBinding binding;
    private Location location;
    private LocationViewModel viewModel;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        // Cấp quyền đọc lâu dài cho Uri (nếu cần thiết để hiển thị sau này từ Database)
                        getContentResolver().takePersistableUriPermission(selectedImageUri, 
                                Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        
                        Glide.with(this)
                                .load(selectedImageUri)
                                .centerCrop()
                                .into(binding.ivVenueImage);
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditVenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        location = (Location) getIntent().getSerializableExtra("LOCATION_DATA");
        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        if (location != null) {
            populateData();
        }

        setupToolbar();
        setupSaveButton();
        
        binding.fabChangeImage.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void populateData() {
        binding.etName.setText(location.getName());
        binding.etAddress.setText(location.getAddress());
        binding.etCapacity.setText(String.valueOf(location.getCapacity()));
        binding.etArea.setText(String.valueOf(location.getArea()));
        binding.etPrice.setText(String.valueOf((long)location.getPrice()));
        binding.etDescription.setText(location.getDescription());
        binding.switchPremium.setChecked(location.isPremium());

        if (location.getImageUrl() != null && !location.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(location.getImageUrl())
                    .placeholder(R.drawable.ic_event_placeholder)
                    .error(R.drawable.ic_event_placeholder)
                    .centerCrop()
                    .into(binding.ivVenueImage);
        }
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();

            if (name.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            location.setName(name);
            location.setAddress(address);
            location.setCapacity(Integer.parseInt(binding.etCapacity.getText().toString().isEmpty() ? "0" : binding.etCapacity.getText().toString()));
            location.setArea(Double.parseDouble(binding.etArea.getText().toString().isEmpty() ? "0" : binding.etArea.getText().toString()));
            location.setPrice(Double.parseDouble(binding.etPrice.getText().toString().isEmpty() ? "0" : binding.etPrice.getText().toString()));
            location.setDescription(binding.etDescription.getText().toString().trim());
            location.setPremium(binding.switchPremium.isChecked());
            location.setUpdatedAt(System.currentTimeMillis());
            
            if (selectedImageUri != null) {
                location.setImageUrl(selectedImageUri.toString());
            }

            viewModel.update(location);
            Toast.makeText(this, "Đã cập nhật thông tin địa điểm", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
