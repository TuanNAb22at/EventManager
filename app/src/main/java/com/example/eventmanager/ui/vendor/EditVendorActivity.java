package com.example.eventmanager.ui.vendor;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityEditVendorBinding;
import com.example.eventmanager.model.Vendor;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditVendorActivity extends AppCompatActivity {

    private ActivityEditVendorBinding binding;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<String> existingServiceTypes = new ArrayList<>();
    private Vendor currentVendor;
    private int vendorId;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditVendorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        vendorId = getIntent().getIntExtra("vendor_id", -1);
        if (vendorId == -1) {
            Toast.makeText(this, "Không tìm thấy nhà cung cấp", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnCancel.setOnClickListener(v -> finish());

        loadVendorData();
        loadServiceTypes();

        binding.btnUpdateVendor.setOnClickListener(v -> updateVendor());
    }

    private void loadVendorData() {
        executorService.execute(() -> {
            currentVendor = AppDatabase.getInstance(this).vendorDao().getVendorById(vendorId);
            if (currentVendor != null) {
                runOnUiThread(() -> {
                    binding.etVendorName.setText(currentVendor.getName());
                    binding.etVendorPhone.setText(currentVendor.getPhone());
                    binding.etVendorEmail.setText(currentVendor.getEmail());
                    binding.actvServiceType.setText(currentVendor.getServiceType());
                    binding.etVendorNote.setText(currentVendor.getNote());
                });
            }
        });
    }

    private void loadServiceTypes() {
        executorService.execute(() -> {
            List<String> types = AppDatabase.getInstance(this).vendorDao().getDistinctServiceTypes();
            if (types.isEmpty()) {
                types.add("Ẩm thực (Catering)");
                types.add("Trang trí (Decor)");
                types.add("Âm nhạc (Music)");
                types.add("Quay phim/Chụp ảnh");
                types.add("Địa điểm (Venue)");
            }
            existingServiceTypes = types;

            runOnUiThread(() -> {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        existingServiceTypes
                );
                binding.actvServiceType.setAdapter(adapter);
                binding.actvServiceType.setOnClickListener(v -> binding.actvServiceType.showDropDown());
            });
        });
    }

    private boolean validateInput(String name, String phone, String email, String serviceType) {
        boolean isValid = true;

        if (name.isEmpty()) {
            binding.etVendorName.setError("Vui lòng nhập tên nhà cung cấp");
            isValid = false;
        } else {
            binding.etVendorName.setError(null);
        }

        if (serviceType.isEmpty()) {
            binding.actvServiceType.setError("Vui lòng chọn hoặc nhập loại dịch vụ");
            isValid = false;
        } else {
            binding.actvServiceType.setError(null);
        }

        if (!phone.isEmpty() && !Patterns.PHONE.matcher(phone).matches()) {
            binding.etVendorPhone.setError("Số điện thoại không hợp lệ");
            isValid = false;
        } else {
            binding.etVendorPhone.setError(null);
        }

        if (!email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etVendorEmail.setError("Email không hợp lệ");
            isValid = false;
        } else {
            binding.etVendorEmail.setError(null);
        }

        return isValid;
    }

    private void updateVendor() {
        String name = binding.etVendorName.getText().toString().trim();
        String phone = binding.etVendorPhone.getText().toString().trim();
        String email = binding.etVendorEmail.getText().toString().trim();
        String serviceType = binding.actvServiceType.getText().toString().trim();
        String note = binding.etVendorNote.getText().toString().trim();

        if (!validateInput(name, phone, email, serviceType)) {
            return;
        }

        executorService.execute(() -> {
            if (currentVendor != null) {
                currentVendor.setName(name);
                currentVendor.setPhone(phone);
                currentVendor.setEmail(email);
                currentVendor.setServiceType(serviceType);
                currentVendor.setNote(note);
                currentVendor.setUpdatedAt(System.currentTimeMillis());

                AppDatabase.getInstance(this).vendorDao().updateVendor(currentVendor);
                runOnUiThread(() -> {
                    setResult(RESULT_OK);
                    finish();
                });
            }
        });
    }
}
