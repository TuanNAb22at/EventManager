package com.example.eventmanager.ui.guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityGuestDetailBinding;
import com.example.eventmanager.model.Guest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuestDetailActivity extends AppCompatActivity {

    private ActivityGuestDetailBinding binding;
    private int guestId;
    private int currentEventId;
    private Guest currentGuest;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        guestId = getIntent().getIntExtra("GUEST_ID", -1);
        currentEventId = getIntent().getIntExtra("CURRENT_EVENT_ID", -1);
        
        if (guestId == -1) {
            finish();
            return;
        }

        setupToolbar();
        loadGuestData();
        setupButtons();
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void loadGuestData() {
        executorService.execute(() -> {
            currentGuest = AppDatabase.getInstance(this).guestDao().getGuestById(guestId);
            if (currentGuest != null) {
                runOnUiThread(() -> {
                    binding.tvGuestNameTop.setText(currentGuest.getName());
                    binding.tvGuestEmail.setText(currentGuest.getEmail());
                    binding.tvGuestPhone.setText(currentGuest.getPhone());
                    binding.tvGuestStatus.setText(currentGuest.getStatus());
                    
                    // Logic hiển thị nút Mời hoặc Hủy mời
                    if (currentGuest.getEventId() == null) {
                        binding.btnInviteToEvent.setVisibility(View.VISIBLE);
                        binding.btnCancelInvitation.setVisibility(View.GONE);
                    } else if (currentGuest.getEventId() == currentEventId) {
                        binding.btnInviteToEvent.setVisibility(View.GONE);
                        binding.btnCancelInvitation.setVisibility(View.VISIBLE);
                    } else {
                        // Đã mời vào sự kiện khác
                        binding.btnInviteToEvent.setVisibility(View.VISIBLE);
                        binding.btnInviteToEvent.setText("Chuyển sang sự kiện này");
                        binding.btnCancelInvitation.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void setupButtons() {
        binding.btnInviteToEvent.setOnClickListener(v -> inviteGuestToEvent());
        binding.btnCancelInvitation.setOnClickListener(v -> cancelInvitation());

        binding.btnEditGuest.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditGuestActivity.class);
            intent.putExtra("GUEST_ID", guestId);
            startActivity(intent);
        });

        binding.btnDeleteGuest.setOnClickListener(v -> showDeleteConfirmDialog());
    }

    private void inviteGuestToEvent() {
        if (currentEventId == -1) return;
        
        executorService.execute(() -> {
            if (currentGuest != null) {
                currentGuest.setEventId(currentEventId);
                currentGuest.setStatus("Đã mời");
                currentGuest.setUpdatedAt(System.currentTimeMillis());
                
                AppDatabase.getInstance(this).guestDao().updateGuest(currentGuest);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã mời khách vào sự kiện", Toast.LENGTH_SHORT).show();
                    loadGuestData();
                });
            }
        });
    }

    private void cancelInvitation() {
        executorService.execute(() -> {
            if (currentGuest != null) {
                currentGuest.setEventId(null);
                currentGuest.setStatus("Chưa mời");
                currentGuest.setUpdatedAt(System.currentTimeMillis());
                
                AppDatabase.getInstance(this).guestDao().updateGuest(currentGuest);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã hủy lời mời", Toast.LENGTH_SHORT).show();
                    loadGuestData();
                });
            }
        });
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa khách mời")
                .setMessage("Bạn có chắc chắn muốn xóa khách mời này khỏi hệ thống?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteGuest())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteGuest() {
        executorService.execute(() -> {
            if (currentGuest != null) {
                AppDatabase.getInstance(this).guestDao().deleteGuest(currentGuest);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Đã xóa khách mời", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGuestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
