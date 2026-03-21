package com.example.eventmanager.ui.budget;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.BudgetEventAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityBudgetEventBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetEventActivity extends AppCompatActivity {
    private ActivityBudgetEventBinding binding;
    private BudgetEventAdapter adapter;
    private List<Event> allEvents = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBudgetEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        setupRecyclerView();
        loadEvents();

        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.etSearchEvent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new BudgetEventAdapter(new ArrayList<>(), event -> {
            Intent intent = new Intent(this, BudgetPlanActivity.class);
            intent.putExtra("EVENT_ID", event.id);
            intent.putExtra("EVENT_NAME", event.name);
            intent.putExtra("TOTAL_BUDGET", event.totalBudget);
            startActivity(intent);
        });
        binding.rvEvents.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEvents.setAdapter(adapter);
    }

    private void loadEvents() {
        binding.rvEvents.setVisibility(View.GONE);
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int userId = sessionManager.getUserId();
            String role = sessionManager.getUserRole();

            List<Event> events;
            // Nếu là ORGANIZER thì xem được tất cả, STAFF thì chỉ xem được event mình tạo
            if (SessionManager.ROLE_ORGANIZER.equals(role)) {
                events = db.eventDao().getAllEvents();
            } else {
                events = db.eventDao().getEventsByUserId(userId);
            }

            allEvents = events != null ? events : new ArrayList<>();

            runOnUiThread(() -> {
                adapter.setEvents(allEvents);
                binding.rvEvents.setVisibility(View.VISIBLE);

                // Hiển thị thông báo nếu không có dữ liệu
                if (allEvents.isEmpty()) {
                    // Bạn có thể thêm một TextView "No Data" vào layout và hiển thị ở đây
                }
            });
        });
    }

    private void filterEvents(String query) {
        List<Event> filtered = new ArrayList<>();
        for (Event event : allEvents) {
            if (event.name != null && event.name.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(event);
            }
        }
        adapter.setEvents(filtered);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
