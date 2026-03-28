package com.example.eventmanager.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.EventVendorAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityEventVendorListBinding;
import com.example.eventmanager.model.Vendor;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EventVendorListActivity extends AppCompatActivity implements EventVendorAdapter.OnEventVendorActionListener {

    private ActivityEventVendorListBinding binding;
    private int eventId;
    private String eventName;
    private EventVendorAdapter adapter;
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final int REQUEST_SELECT_VENDOR = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventVendorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        eventName = getIntent().getStringExtra("EVENT_NAME");

        if (eventId == -1) {
            finish();
            return;
        }

        if (eventName != null) {
            binding.tvEventName.setText(eventName);
        } else {
            loadEventName();
        }

        setupRecyclerView();
        observeVendors();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.fabAddVendor.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectVendorActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            intent.putExtra("EVENT_NAME", eventName);
            startActivityForResult(intent, REQUEST_SELECT_VENDOR);
        });
    }

    private void loadEventName() {
        AppDatabase.getInstance(this).eventDao().getEventById(eventId).observe(this, event -> {
            if (event != null) {
                eventName = event.getName();
                binding.tvEventName.setText(eventName);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new EventVendorAdapter(new ArrayList<>(), this);
        binding.rvEventVendors.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEventVendors.setAdapter(adapter);
    }

    private void observeVendors() {
        AppDatabase.getInstance(this).eventVendorDao().getVendorsForEvent(eventId).observe(this, vendors -> {
            if (vendors != null) {
                adapter.setVendors(vendors);
                binding.tvTotalVendors.setText(String.valueOf(vendors.size()));
            }
        });
    }

    @Override
    public void onRemove(Vendor vendor) {
        new AlertDialog.Builder(this)
                .setTitle("Gỡ bỏ nhà cung cấp")
                .setMessage("Bạn có chắc chắn muốn gỡ nhà cung cấp \"" + vendor.getName() + "\" khỏi sự kiện này?")
                .setPositiveButton("Gỡ bỏ", (dialog, which) -> {
                    executorService.execute(() -> {
                        AppDatabase.getInstance(this).eventVendorDao().deleteById(eventId, vendor.getId());
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_VENDOR && resultCode == RESULT_OK) {
            Toast.makeText(this, "Đã thêm nhà cung cấp vào sự kiện", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
