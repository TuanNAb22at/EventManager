package com.example.eventmanager.ui.schedule;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.adapter.ScheduleAdapter;
import com.example.eventmanager.model.Schedule;
import com.example.eventmanager.viewmodel.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

public class ScheduleListActivity extends AppCompatActivity implements ScheduleAdapter.OnItemClickListener {
    private RecyclerView recyclerView;
    private ScheduleAdapter adapter;
    private ScheduleViewModel viewModel;
    private Button addButton;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        eventId = getIntent().getIntExtra("eventId", -1);
        if (eventId == -1) {
            finish();
            return;
        }

        recyclerView = findViewById(R.id.scheduleRecyclerView);
        addButton = findViewById(R.id.addScheduleButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ScheduleAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ScheduleViewModel.class);
        viewModel.getSchedulesForEvent(eventId).observe(this, this::updateSchedules);

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddScheduleActivity.class);
            intent.putExtra("eventId", eventId);
            startActivity(intent);
        });
    }

    private void updateSchedules(List<Schedule> schedules) {
        adapter.updateSchedules(schedules);
    }

    @Override
    public void onItemClick(Schedule schedule) {
        // Handle item click, e.g., open detail or edit
    }
}
