package com.example.eventmanager.ui.guest;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.R;
import com.example.eventmanager.adapter.GuestAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityGuestListBinding;
import com.example.eventmanager.model.Guest;
import com.example.eventmanager.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;
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
    private String currentSearchQuery = "";
    private String filterStatus = "ALL"; // ALL, INVITED, NOT_INVITED

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
        setupSearch();
        setupFilters();
        setupRecyclerView();
        setupTabs();
        loadData();

        binding.fabAddGuest.setOnClickListener(v -> showAddGuestDialog());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().trim();
                updateListBasedOnTab(binding.tabLayout.getSelectedTabPosition());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilters() {
        binding.chipAll.setOnClickListener(v -> {
            filterStatus = "ALL";
            updateFilterUI();
            updateListBasedOnTab(binding.tabLayout.getSelectedTabPosition());
        });

        binding.chipInvited.setOnClickListener(v -> {
            filterStatus = "INVITED";
            updateFilterUI();
            updateListBasedOnTab(binding.tabLayout.getSelectedTabPosition());
        });

        binding.chipNotInvited.setOnClickListener(v -> {
            filterStatus = "NOT_INVITED";
            updateFilterUI();
            updateListBasedOnTab(binding.tabLayout.getSelectedTabPosition());
        });
    }

    private void updateFilterUI() {
        resetChipStyle(binding.chipAll, binding.tvAll);
        resetChipStyle(binding.chipInvited, binding.tvInvited);
        resetChipStyle(binding.chipNotInvited, binding.tvNotInvited);

        if (filterStatus.equals("ALL")) {
            setActiveChipStyle(binding.chipAll, binding.tvAll);
        } else if (filterStatus.equals("INVITED")) {
            setActiveChipStyle(binding.chipInvited, binding.tvInvited);
        } else if (filterStatus.equals("NOT_INVITED")) {
            setActiveChipStyle(binding.chipNotInvited, binding.tvNotInvited);
        }
    }

    private void setActiveChipStyle(MaterialCardView card, TextView text) {
        card.setCardBackgroundColor(ContextCompat.getColor(this, R.color.primary_blue));
        card.setStrokeWidth(0);
        text.setTextColor(Color.WHITE);
    }

    private void resetChipStyle(MaterialCardView card, TextView text) {
        card.setCardBackgroundColor(Color.WHITE);
        card.setStrokeWidth(1);
        card.setStrokeColor(Color.parseColor("#E2E8F0"));
        text.setTextColor(Color.parseColor("#475569"));
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
                binding.fabAddGuest.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                binding.filterContainer.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                updateListBasedOnTab(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        
        binding.fabAddGuest.setVisibility(View.GONE);
        binding.filterContainer.setVisibility(View.GONE);
    }

    private void loadData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            invitedGuests = db.eventGuestDao().getGuestsByEventId(eventId);
            for (Guest g : invitedGuests) {
                g.setStatus("Đã mời");
            }

            allSystemGuests = db.guestDao().getAllGuests();
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
        List<Guest> sourceList = (position == 0) ? invitedGuests : allSystemGuests;
        List<Guest> listToShow = new ArrayList<>();

        String query = currentSearchQuery.toLowerCase();
        
        for (Guest guest : sourceList) {
            boolean matchesSearch = query.isEmpty() || guest.getName().toLowerCase().contains(query);
            boolean matchesFilter = true;

            if (position == 1) { // Chỉ lọc status ở tab "Tất cả khách"
                if (filterStatus.equals("INVITED")) {
                    matchesFilter = guest.getStatus().equals("Đã mời");
                } else if (filterStatus.equals("NOT_INVITED")) {
                    matchesFilter = guest.getStatus().equals("Chưa mời");
                }
            }

            if (matchesSearch && matchesFilter) {
                listToShow.add(guest);
            }
        }
        
        if (listToShow.isEmpty()) {
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            if (currentSearchQuery.isEmpty()) {
                if (position == 1 && !filterStatus.equals("ALL")) {
                    binding.tvEmptyState.setText("Không có khách hàng nào ở trạng thái này");
                } else {
                    binding.tvEmptyState.setText(position == 0 ? "Chưa có khách mời nào cho sự kiện này" : "Hệ thống chưa có khách hàng nào");
                }
            } else {
                binding.tvEmptyState.setText("Không tìm thấy khách hàng nào khớp với \"" + currentSearchQuery + "\"");
            }
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
