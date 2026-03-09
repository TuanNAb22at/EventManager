package com.example.eventmanager.ui.vendor;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.VendorAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityVendorListBinding;
import com.example.eventmanager.model.Vendor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VendorListActivity extends AppCompatActivity implements VendorAdapter.OnVendorActionListener {
    
    private ActivityVendorListBinding binding;
    private VendorAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final int REQUEST_ADD_VENDOR = 101;
    private static final int REQUEST_EDIT_VENDOR = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVendorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        loadVendors();

        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.fabAddSupplier.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddVendorActivity.class);
            startActivityForResult(intent, REQUEST_ADD_VENDOR);
        });
    }

    private void setupRecyclerView() {
        adapter = new VendorAdapter(new ArrayList<>(), this);
        binding.rvSuppliers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSuppliers.setAdapter(adapter);
    }

    private void loadVendors() {
        executorService.execute(() -> {
            List<Vendor> vendors = AppDatabase.getInstance(this).vendorDao().getAllVendors();
            runOnUiThread(() -> adapter.setVendors(vendors));
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ADD_VENDOR || requestCode == REQUEST_EDIT_VENDOR) && resultCode == RESULT_OK) {
            loadVendors();
            String message = (requestCode == REQUEST_ADD_VENDOR) ? "Đã thêm nhà cung cấp" : "Đã cập nhật thông tin";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onEdit(Vendor vendor) {
        // Sau này bạn có thể triển khai EditVendorActivity
        Toast.makeText(this, "Tính năng sửa đang được hoàn thiện", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDelete(Vendor vendor) {
        executorService.execute(() -> {
            AppDatabase.getInstance(this).vendorDao().deleteVendor(vendor);
            loadVendors();
            runOnUiThread(() -> Toast.makeText(this, "Đã xóa nhà cung cấp", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public void onItemClick(Vendor vendor) {
        // Xem chi tiết
    }
}
