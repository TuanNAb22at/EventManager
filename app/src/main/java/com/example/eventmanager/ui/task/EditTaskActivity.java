package com.example.eventmanager.ui.task;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityEditTaskBinding;
import com.example.eventmanager.model.Event;
import com.example.eventmanager.model.Task;
import com.example.eventmanager.model.TaskAssignee;
import com.example.eventmanager.model.User;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditTaskActivity extends AppCompatActivity {

    private ActivityEditTaskBinding binding;
    private int taskId;
    private Task currentTask;
    private Event currentEvent;
    private List<User> staffList;
    private User selectedStaff;
    private final Calendar calendar = Calendar.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        taskId = getIntent().getIntExtra("TASK_ID", -1);
        if (taskId == -1) {
            finish();
            return;
        }

        setupToolbar();
        loadTaskAndAssignees();
        setupDateTimePicker();
        setupUpdateButtons();
        setupPriorityToggle();
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupPriorityToggle() {
        binding.toggleGroupPriority.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                updatePriorityColors(checkedId);
            }
        });
    }

    private void updatePriorityColors(int checkedId) {
        int activeColor = ContextCompat.getColor(this, R.color.primary_blue);
        int inactiveColor = Color.parseColor("#94A3B8");

        // Reset all
        binding.btnPriorityLow.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        binding.btnPriorityLow.setTextColor(inactiveColor);
        binding.btnPriorityMedium.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        binding.btnPriorityMedium.setTextColor(inactiveColor);
        binding.btnPriorityHigh.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        binding.btnPriorityHigh.setTextColor(inactiveColor);

        // Set selected
        if (checkedId == R.id.btnPriorityLow) {
            binding.btnPriorityLow.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            binding.btnPriorityLow.setTextColor(activeColor);
        } else if (checkedId == R.id.btnPriorityMedium) {
            binding.btnPriorityMedium.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            binding.btnPriorityMedium.setTextColor(activeColor);
        } else if (checkedId == R.id.btnPriorityHigh) {
            binding.btnPriorityHigh.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            binding.btnPriorityHigh.setTextColor(activeColor);
        }
    }

    private void loadTaskAndAssignees() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            currentTask = db.taskDao().getTaskById(taskId);
            if (currentTask != null) {
                currentEvent = db.eventDao().getEventByIdSync(currentTask.getEventId());
            }
            staffList = db.userDao().getAllStaffs();
            List<User> currentAssignees = db.taskAssigneeDao().getAssigneesForTask(taskId);
            
            if (currentTask != null) {
                runOnUiThread(() -> {
                    binding.etTaskName.setText(currentTask.getTitle());
                    binding.tvDeadline.setText(currentTask.getDueDate());
                    binding.etNotes.setText(currentTask.getNote());
                    setPriorityToggle(currentTask.getPriority());
                    
                    if (!currentAssignees.isEmpty()) {
                        selectedStaff = currentAssignees.get(0);
                        binding.tvSelectedAssigneeName.setText(selectedStaff.getFullName());
                    } else {
                        binding.tvSelectedAssigneeName.setText("Chọn người làm");
                    }

                    binding.layoutAssignee.setOnClickListener(v -> showSingleSelectDialog());
                });
            }
        });
    }

    private void showSingleSelectDialog() {
        if (staffList == null || staffList.isEmpty()) return;

        String[] staffNames = new String[staffList.size()];
        int checkedItem = -1;

        for (int i = 0; i < staffList.size(); i++) {
            staffNames[i] = staffList.get(i).getFullName();
            if (selectedStaff != null && selectedStaff.getId() == staffList.get(i).getId()) {
                checkedItem = i;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Chọn người thực hiện")
                .setSingleChoiceItems(staffNames, checkedItem, (dialog, which) -> {
                    selectedStaff = staffList.get(which);
                    binding.tvSelectedAssigneeName.setText(selectedStaff.getFullName());
                    dialog.dismiss();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void setPriorityToggle(int priority) {
        int buttonId;
        if (priority == 0) buttonId = R.id.btnPriorityLow;
        else if (priority == 2) buttonId = R.id.btnPriorityHigh;
        else buttonId = R.id.btnPriorityMedium;
        
        binding.toggleGroupPriority.check(buttonId);
        updatePriorityColors(buttonId);
    }

    private void setupDateTimePicker() {
        binding.tvDeadline.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    binding.tvDeadline.setText(dateFormat.format(calendar.getTime()));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupUpdateButtons() {
        binding.btnUpdateChanges.setOnClickListener(v -> updateTask());
    }

    private boolean validateData(String title, String deadline) {
        if (title.isEmpty()) {
            binding.etTaskName.setError("Tên công việc không được để trống");
            binding.etTaskName.requestFocus();
            return false;
        }

        if (title.length() < 3) {
            binding.etTaskName.setError("Tên công việc tối thiểu 3 ký tự");
            binding.etTaskName.requestFocus();
            return false;
        }

        if (title.length() > 100) {
            binding.etTaskName.setError("Tên công việc tối đa 100 ký tự");
            binding.etTaskName.requestFocus();
            return false;
        }

        if (deadline.isEmpty() || deadline.equals("Chọn ngày và giờ")) {
            Toast.makeText(this, "Vui lòng chọn hạn chót", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Rule về hạn chót cho Sửa: Cho phép giữ nguyên hạn chót cũ nếu nó đã qua, 
        // nhưng nếu đổi thì phải chọn ngày tương lai hoặc bằng ngày cũ.
        // Ở đây áp dụng rule chung cho đơn giản: đổi là phải chọn ngày >= hiện tại.
        try {
            Date deadlineDate = dateFormat.parse(deadline);
            Date now = new Date();
            
            // Nếu deadline mới khác deadline cũ thì mới kiểm tra rule ngày hiện tại
            if (!deadline.equals(currentTask.getDueDate()) && deadlineDate.before(now)) {
                Toast.makeText(this, "Hạn chót mới không được nhỏ hơn thời điểm hiện tại", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (currentEvent != null && currentEvent.getEndAt() != null && !currentEvent.getEndAt().isEmpty()) {
                SimpleDateFormat eventDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                Date eventEndDate = eventDateFormat.parse(currentEvent.getEndAt());
                if (deadlineDate.after(eventEndDate)) {
                    Toast.makeText(this, "Hạn chót không được vượt quá thời gian kết thúc sự kiện (" + currentEvent.getEndAt() + ")", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (selectedStaff == null) {
            Toast.makeText(this, "Vui lòng chọn người thực hiện", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.toggleGroupPriority.getCheckedButtonId() == -1) {
            Toast.makeText(this, "Vui lòng chọn độ ưu tiên", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void updateTask() {
        String title = binding.etTaskName.getText().toString().trim();
        String deadline = binding.tvDeadline.getText().toString().trim();
        String note = binding.etNotes.getText().toString().trim();

        if (!validateData(title, deadline)) {
            return;
        }

        currentTask.setTitle(title);
        currentTask.setDueDate(deadline);
        currentTask.setNote(note);
        
        int priority = 1;
        int checkedId = binding.toggleGroupPriority.getCheckedButtonId();
        if (checkedId == R.id.btnPriorityLow) priority = 0;
        else if (checkedId == R.id.btnPriorityHigh) priority = 2;
        currentTask.setPriority(priority);

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            db.taskDao().updateTask(currentTask);
            
            db.taskAssigneeDao().deleteByTaskId(taskId);
            if (selectedStaff != null) {
                db.taskAssigneeDao().insert(new TaskAssignee(taskId, selectedStaff.getId()));
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã cập nhật công việc", Toast.LENGTH_SHORT).show();
                finish();
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
