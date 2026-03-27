package com.example.eventmanager.ui.guest;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityEditGuestBinding;
import com.example.eventmanager.model.Guest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditGuestActivity extends AppCompatActivity {

    private ActivityEditGuestBinding binding;
    private int guestId;
    private Guest currentGuest;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditGuestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        guestId = getIntent().getIntExtra("GUEST_ID", -1);
        if (guestId == -1) {
            finish();
            return;
        }

        setupToolbar();
        setupStatusSpinner();
        loadGuestData();

        binding.btnSave.setOnClickListener(v -> saveChanges());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupStatusSpinner() {
        String[] statuses = {"Chờ xác nhận", "Đã xác nhận", "Đã tham gia", "Đã hủy"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        binding.spinnerStatus.setAdapter(adapter);
    }

    private void loadGuestData() {
        executorService.execute(() -> {
            currentGuest = AppDatabase.getInstance(this).guestDao().getGuestById(guestId);
            if (currentGuest != null) {
                runOnUiThread(() -> {
                    binding.etName.setText(currentGuest.getName());
                    binding.etEmail.setText(currentGuest.getEmail());
                    binding.etPhone.setText(currentGuest.getPhone());
                    binding.spinnerStatus.setText(currentGuest.getStatus(), false);
                });
            }
        });
    }

    private void saveChanges() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String status = binding.spinnerStatus.getText().toString();

        if (name.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
            return;
        }

        executorService.execute(() -> {
            if (currentGuest != null) {
                currentGuest.setName(name);
                currentGuest.setEmail(email);
                currentGuest.setPhone(phone);
                currentGuest.setStatus(status);
                currentGuest.setUpdatedAt(System.currentTimeMillis());

                AppDatabase.getInstance(this).guestDao().updateGuest(currentGuest);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
                    finish();
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
