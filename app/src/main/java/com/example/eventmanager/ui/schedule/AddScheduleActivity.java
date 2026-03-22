package com.example.eventmanager.ui.schedule;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.R;
import com.example.eventmanager.model.Schedule;
import com.example.eventmanager.repository.ScheduleRepository;
import com.example.eventmanager.utils.SessionManager;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText timeEditText;
    private EditText descriptionEditText;
    private Spinner statusSpinner;
    private Button saveButton;
    private ScheduleRepository scheduleRepository;
    private SessionManager sessionManager;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        eventId = getIntent().getIntExtra("eventId", -1);
        if (eventId == -1) {
            Toast.makeText(this, "Invalid event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        scheduleRepository = new ScheduleRepository(getApplication());
        sessionManager = new SessionManager(this);

        titleEditText = findViewById(R.id.titleEditText);
        timeEditText = findViewById(R.id.timeEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        statusSpinner = findViewById(R.id.statusSpinner);
        saveButton = findViewById(R.id.saveButton);

        timeEditText.setOnClickListener(v -> showTimePicker());

        saveButton.setOnClickListener(v -> saveSchedule());
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
            (view, hourOfDay, minute1) -> {
                String time = String.format("%02d:%02d", hourOfDay, minute1);
                timeEditText.setText(time);
            }, hour, minute, true);
        timePickerDialog.show();
    }

    private void saveSchedule() {
        String title = titleEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String status = statusSpinner.getSelectedItem().toString();

        if (title.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Schedule schedule = new Schedule();
        schedule.setEventId(eventId);
        schedule.setTitle(title);
        schedule.setTime(time);
        schedule.setDescription(description);
        schedule.setStatus(status);
        schedule.setCreatedBy(sessionManager.getUserId());

        scheduleRepository.insert(schedule);
        Toast.makeText(this, "Schedule added", Toast.LENGTH_SHORT).show();
        finish();
    }
}
