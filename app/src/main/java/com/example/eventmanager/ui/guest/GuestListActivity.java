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
import com.example.eventmanager.utils.SessionManager;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GuestListActivity extends AppCompatActivity {

    private ActivityGuestListBinding binding;
    private GuestAdapter adapter;
    private int eventId;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private List<Guest> invitedGuests = new ArrayList<>();
    private List<Guest> allSystemGuests = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuestListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        if (eventId == -1) {
            finish();
            return;
        }

        setupToolbar();
        setupRecyclerView();
        setupTabs();
        loadData();

        binding.fabAddGuest.setOnClickListener(v -> showAddGuestDialog());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new GuestAdapter(new ArrayList<>(), guest -> {
            Intent intent = new Intent(this, GuestDetailActivity.class);
            intent.putExtra("GUEST_ID", guest.getId());
            intent.putExtra("CURRENT_EVENT_ID", eventId);
            startActivity(intent);
        });
        binding.rvGuests.setLayoutManager(new LinearLayoutManager(this));
        binding.rvGuests.setAdapter(adapter);
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // Hiện nút + khi ở tab "Tất cả khách" cho cả Quản lý và Nhân viên
                binding.fabAddGuest.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                updateListBasedOnTab(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        // Mặc định ẩn nút + vì ban đầu ở tab 0
        binding.fabAddGuest.setVisibility(View.GONE);
    }

    private void loadData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            // Lấy danh sách khách mời cho sự kiện này
            invitedGuests = db.eventGuestDao().getGuestsByEventId(eventId);
            // Gán nhãn "Đã mời" cho hiển thị
            for (Guest g : invitedGuests) {
                g.setStatus("Đã mời");
            }

            // Lấy toàn bộ khách trong hệ thống
            allSystemGuests = db.guestDao().getAllGuests();
            // Đối với tab Tất cả, chúng ta có thể kiểm tra xem ai đã được mời vào sự kiện này chưa
            for (Guest g : allSystemGuests) {
                boolean isInvited = false;
                for (Guest invited : invitedGuests) {
                    if (invited.getId() == g.getId()) {
                        isInvited = true;
                        break;
                    }
                }
                g.setStatus(isInvited ? "Đã mời" : "Chưa mời");
            }
            
            runOnUiThread(() -> {
                updateListBasedOnTab(binding.tabLayout.getSelectedTabPosition());
            });
        });
    }

    private void updateListBasedOnTab(int position) {
        List<Guest> listToShow = (position == 0) ? invitedGuests : allSystemGuests;
        
        if (listToShow.isEmpty()) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText(position == 0 ? "Chưa có khách mời nào cho sự kiện này" : "Hệ thống chưa có khách hàng nào");
        } else {
            binding.tvEmptyState.setVisibility(View.GONE);
        }
        adapter.setGuests(listToShow);
    }

    private void showAddGuestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Thêm khách hàng mới");

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

            saveGuestToSystem(name, email, phone);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void saveGuestToSystem(String name, String email, String phone) {
        executorService.execute(() -> {
            Guest guest = new Guest();
            guest.setEventId(null);
            guest.setName(name);
            guest.setEmail(email);
            guest.setPhone(phone);
            guest.setStatus("Chưa mời");

            AppDatabase.getInstance(this).guestDao().insertGuest(guest);
            loadData();
            runOnUiThread(() -> Toast.makeText(this, "Đã thêm khách hàng vào hệ thống", Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
