package com.example.eventmanager.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.GuestAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityGuestListBinding;
import com.example.eventmanager.model.Guest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuestListActivity extends AppCompatActivity {

    private ActivityGuestListBinding binding;
    private GuestAdapter adapter;
    private int eventId;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        if (eventId == -1) {
            finish();
            return;
        }

        setupToolbar();
        setupRecyclerView();
        loadGuests();

        binding.fabAddGuest.setOnClickListener(v -> showAddGuestDialog());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new GuestAdapter(new ArrayList<>(), guest -> {
            // Option to edit or delete guest can be added here
        });
        binding.rvGuests.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGuests.setAdapter(adapter);
    }

    private void loadGuests() {
        executorService.execute(() -> {
            List<Guest> guests = AppDatabase.getInstance(this).guestDao().getGuestsByEventId(eventId);
            runOnUiThread(() -> {
                if (guests.isEmpty()) {
                    binding.tvEmptyState.setVisibility(View.VISIBLE);
                } else {
                    binding.tvEmptyState.setVisibility(View.GONE);
                }
                adapter.setGuests(guests);
            });
        });
    }

    private void showAddGuestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm khách mời mới");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_guest, null);
        EditText etName = view.findViewById(R.id.etGuestName);
        EditText etEmail = view.findViewById(R.id.etGuestEmail);
        EditText etPhone = view.findViewById(R.id.etGuestPhone);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = etName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                return;
            }

            saveGuest(name, email, phone);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void saveGuest(String name, String email, String phone) {
        executorService.execute(() -> {
            Guest guest = new Guest();
            guest.setEventId(eventId);
            guest.setName(name);
            guest.setEmail(email);
            guest.setPhone(phone);
            guest.setStatus("Chờ xác nhận");

            AppDatabase.getInstance(this).guestDao().insertGuest(guest);
            loadGuests();
            runOnUiThread(() -> Toast.makeText(this, "Đã thêm khách mời", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
