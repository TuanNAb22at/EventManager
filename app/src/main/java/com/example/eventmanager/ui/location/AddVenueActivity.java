package com.example.eventmanager.ui.location;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class AddVenueActivity extends AppCompatActivity {

    private ActivityEditVenueBinding binding;
    private LocationViewModel viewModel;
    private Uri selectedImageUri;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
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

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

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
        binding.toolbar.setTitle("Thêm địa điểm mới");
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (binding.etName.getText().toString().trim().isEmpty()) {
            binding.tilName.setError("Vui lòng nhập tên địa điểm");
            isValid = false;
        } else {
            binding.tilName.setError(null);
        }

        if (binding.etAddress.getText().toString().trim().isEmpty()) {
            binding.tilAddress.setError("Vui lòng nhập địa chỉ");
            isValid = false;
        } else {
            binding.tilAddress.setError(null);
        }

        if (binding.etPrice.getText().toString().trim().isEmpty()) {
            binding.tilPrice.setError("Vui lòng nhập giá thuê");
            isValid = false;
        } else {
            binding.tilPrice.setError(null);
        }

        return isValid;
    }

    private void setupSaveButton() {
        binding.btnSave.setText("Thêm địa điểm");
        binding.btnSave.setOnClickListener(v -> {
            if (!validateInputs()) return;

            String name = binding.etName.getText().toString().trim();
            String address = binding.etAddress.getText().toString().trim();
            String capacityStr = binding.etCapacity.getText().toString().trim();
            String areaStr = binding.etArea.getText().toString().trim();
            String priceStr = binding.etPrice.getText().toString().trim();

            Location location = new Location();
            location.setName(name);
            location.setAddress(address);
            
            try {
                location.setCapacity(capacityStr.isEmpty() ? 0 : Integer.parseInt(capacityStr));
                location.setArea(areaStr.isEmpty() ? 0.0 : Double.parseDouble(areaStr));
                location.setPrice(priceStr.isEmpty() ? 0.0 : Double.parseDouble(priceStr));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Vui lòng nhập số hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            location.setDescription(binding.etDescription.getText().toString().trim());
            location.setPremium(binding.switchPremium.isChecked());
            
            if (selectedImageUri != null) {
                location.setImageUrl(selectedImageUri.toString());
            }

            viewModel.insert(location);
            Toast.makeText(this, "Đã thêm địa điểm mới", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
