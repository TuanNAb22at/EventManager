package com.example.eventmanager.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.TaskOverviewAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityTaskOverviewBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TaskOverviewActivity extends AppCompatActivity {

    private ActivityTaskOverviewBinding binding;
    private TaskOverviewAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;
    private List<Event> fullEventsWithTasks = new ArrayList<>();
    private Map<Integer, Integer> fullTaskCounts = new HashMap<>();
    private String currentSearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        setupRecyclerView();
        setupSearch();
        
        binding.btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void setupRecyclerView() {
        adapter = new TaskOverviewAdapter(new ArrayList<>(), new HashMap<>(), event -> {
            Intent intent = new Intent(this, TaskListActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            intent.putExtra("EVENT_NAME", event.getName());
            startActivity(intent);
        });
        binding.rvEventTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvEventTasks.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchQuery = s.toString().toLowerCase().trim();
                applyFilter();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadData() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            int userId = sessionManager.getUserId();
            List<Event> allEvents;
            
            if (sessionManager.isStaff()) {
                allEvents = db.eventDao().getAllEventsSync();
            } else {
                allEvents = db.eventDao().getEventsByUserIdSync(userId);
            }

            List<Event> eventsWithTasks = new ArrayList<>();
            Map<Integer, Integer> taskCounts = new HashMap<>();

            for (Event event : allEvents) {
                int count;
                if (sessionManager.isStaff()) {
                    count = db.taskDao().getPendingTaskCountForUserByEventSync(userId, event.getId());
                } else {
                    count = db.taskDao().getPendingTaskCountByEventSync(event.getId());
                }

                if (count > 0) {
                    eventsWithTasks.add(event);
                    taskCounts.put(event.getId(), count);
                }
            }

            fullEventsWithTasks = eventsWithTasks;
            fullTaskCounts = taskCounts;

            runOnUiThread(this::applyFilter);
        });
    }

    private void applyFilter() {
        List<Event> filteredEvents = fullEventsWithTasks.stream()
                .filter(event -> event.getName().toLowerCase().contains(currentSearchQuery))
                .collect(Collectors.toList());

        adapter.setData(filteredEvents, fullTaskCounts);
        binding.tvEmptyState.setVisibility(filteredEvents.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
