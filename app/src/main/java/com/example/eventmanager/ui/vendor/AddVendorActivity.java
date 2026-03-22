package com.example.eventmanager.ui.vendor;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityAddVendorBinding;
import com.example.eventmanager.model.Vendor;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddVendorActivity extends AppCompatActivity {
    
    private ActivityAddVendorBinding binding;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<String> existingServiceTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVendorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnCancel.setOnClickListener(v -> finish());

        loadServiceTypes();

        binding.btnSaveVendor.setOnClickListener(v -> saveVendor());
    }

    private void loadServiceTypes() {
        executorService.execute(() -> {
            // Lấy danh sách các loại dịch vụ đã tồn tại trong Database
            List<String> types = AppDatabase.getInstance(this).vendorDao().getDistinctServiceTypes();
            
            // Thêm một số loại mặc định nếu database trống
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
                // Cho phép hiển thị danh sách khi click vào
                binding.actvServiceType.setOnClickListener(v -> binding.actvServiceType.showDropDown());
            });
        });
    }

    private void saveVendor() {
        String name = binding.etVendorName.getText().toString().trim();
        String phone = binding.etVendorPhone.getText().toString().trim();
        String email = binding.etVendorEmail.getText().toString().trim();
        String serviceType = binding.actvServiceType.getText().toString().trim();

        if (name.isEmpty()) {
            binding.etVendorName.setError("Vui lòng nhập tên");
            return;
        }
        
        if (serviceType.isEmpty()) {
            binding.actvServiceType.setError("Vui lòng chọn hoặc nhập loại dịch vụ");
            return;
        }

        int currentUserId = sessionManager.getUserId();

        executorService.execute(() -> {
            Vendor vendor = new Vendor();
            vendor.setName(name);
            vendor.setPhone(phone);
            vendor.setEmail(email);
            vendor.setServiceType(serviceType);
            vendor.setCreatedBy(currentUserId);

            AppDatabase.getInstance(this).vendorDao().insertVendor(vendor);
            runOnUiThread(() -> {
                setResult(RESULT_OK);
                finish();
            });
        });
    }
}
