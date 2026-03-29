package com.example.eventmanager.ui.task;

import android.content.Intent;
import android.os.Bundle;
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

public class TaskOverviewActivity extends AppCompatActivity {

    private ActivityTaskOverviewBinding binding;
    private TaskOverviewAdapter adapter;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskOverviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        setupRecyclerView();
        
        binding.toolbar.setNavigationOnClickListener(v -> finish());
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

            runOnUiThread(() -> {
                adapter.setData(eventsWithTasks, taskCounts);
                binding.tvEmptyState.setVisibility(eventsWithTasks.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
