package com.example.eventmanager.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.eventmanager.R;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityTaskDetailBinding;
import com.example.eventmanager.model.Task;
import com.example.eventmanager.model.User;
import com.example.eventmanager.utils.SessionManager;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskDetailActivity extends AppCompatActivity {

    private ActivityTaskDetailBinding binding;
    private int taskId;
    private Task currentTask;
    private SessionManager sessionManager;
    private boolean isAssignedToMe = false;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        taskId = getIntent().getIntExtra("TASK_ID", -1);

        if (taskId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin công việc", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskDetails();
    }

    private void setupToolbar() {
        binding.btnBack.setOnClickListener(v -> finish());

        // Role-based UI control for Edit button
        if (sessionManager.canManageAll()) {
            binding.btnEdit.setVisibility(View.VISIBLE);
            binding.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditTaskActivity.class);
                intent.putExtra("TASK_ID", taskId);
                startActivity(intent);
            });
        } else {
            binding.btnEdit.setVisibility(View.GONE);
        }
    }

    private void loadTaskDetails() {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            currentTask = db.taskDao().getTaskById(taskId);
            
            if (currentTask != null) {
                List<User> assignees = db.taskAssigneeDao().getAssigneesForTask(taskId);

                // Check if staff is assigned to this task
                isAssignedToMe = false;
                for (User user : assignees) {
                    if (user.getId() == sessionManager.getUserId()) {
                        isAssignedToMe = true;
                        break;
                    }
                }

                runOnUiThread(() -> displayTaskInfo(currentTask, assignees));
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }

    private void displayTaskInfo(Task task, List<User> assignees) {
        binding.tvTaskTitle.setText(task.getTitle());
        binding.tvDueDate.setText(task.getDueDate());
        binding.tvNotes.setText(task.getNote() != null && !task.getNote().isEmpty() ? task.getNote() : "Không có ghi chú");
        
        if (assignees != null && !assignees.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < assignees.size(); i++) {
                sb.append(assignees.get(i).getFullName());
                if (i < assignees.size() - 1) sb.append(", ");
            }
            binding.tvAssigneeName.setText(sb.toString());
        } else {
            binding.tvAssigneeName.setText("Chưa phân công");
        }

        // Setup Priority Tag
        setupPriorityTag(task.getPriority());

        // Setup Status Button
        updateStatusButton(task.getStatus());

        // Role-based logic for Status Button
        if (sessionManager.canManageAll() || (sessionManager.isStaff() && isAssignedToMe)) {
            binding.btnToggleStatus.setVisibility(View.VISIBLE);
            binding.btnToggleStatus.setOnClickListener(v -> toggleTaskStatus());
        } else {
            binding.btnToggleStatus.setVisibility(View.GONE);
        }

        // Role-based logic for Delete and Share
        if (sessionManager.canManageAll()) {
            binding.btnDelete.setVisibility(View.VISIBLE);
            binding.btnDelete.setOnClickListener(v -> showDeleteConfirmDialog());
        } else {
            binding.btnDelete.setVisibility(View.GONE);
        }

        binding.btnShare.setOnClickListener(v -> Toast.makeText(this, "Đã chia sẻ", Toast.LENGTH_SHORT).show());
    }

    private void setupPriorityTag(int priority) {
        String label;
        int colorRes;
        int bgRes;

        switch (priority) {
            case 2: // High
                label = "CAO";
                colorRes = android.R.color.holo_red_dark;
                bgRes = R.drawable.bg_priority_high;
                break;
            case 0: // Low
                label = "THẤP";
                colorRes = android.R.color.holo_green_dark;
                bgRes = R.drawable.bg_priority_low;
                break;
            default: // Medium
                label = "TRUNG BÌNH";
                colorRes = android.R.color.holo_orange_dark;
                bgRes = R.drawable.bg_priority_medium;
                break;
        }

        binding.tvPriorityTag.setText(label);
        binding.tvPriorityTag.setTextColor(ContextCompat.getColor(this, colorRes));
        binding.tvPriorityTag.setBackgroundResource(bgRes);
    }

    private void updateStatusButton(String status) {
        if ("DONE".equals(status)) {
            binding.btnToggleStatus.setText("Mở lại công việc");
            binding.btnToggleStatus.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.darker_gray));
        } else {
            binding.btnToggleStatus.setText("Đánh dấu hoàn thành");
            binding.btnToggleStatus.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.primary_blue));
        }
    }

    private void toggleTaskStatus() {
        if (sessionManager.isStaff() && "DONE".equals(currentTask.getStatus())) {
             Toast.makeText(this, "Chỉ Quản lý mới có thể mở lại công việc", Toast.LENGTH_SHORT).show();
             return;
        }

        String newStatus = "DONE".equals(currentTask.getStatus()) ? "TODO" : "DONE";
        currentTask.setStatus(newStatus);
        
        executorService.execute(() -> {
            AppDatabase.getInstance(this).taskDao().updateTask(currentTask);
            runOnUiThread(() -> {
                updateStatusButton(newStatus);
                Toast.makeText(this, "Đã cập nhật trạng thái", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void showDeleteConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa công việc này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteTask();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteTask() {
        executorService.execute(() -> {
            AppDatabase.getInstance(this).taskDao().deleteTask(currentTask);
            runOnUiThread(() -> {
                Toast.makeText(this, "Đã xóa công việc", Toast.LENGTH_SHORT).show();
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
