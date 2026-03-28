package com.example.eventmanager.ui.task;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.eventmanager.adapter.TaskAdapter;
import com.example.eventmanager.database.AppDatabase;
import com.example.eventmanager.databinding.ActivityTaskListBinding;
import com.example.eventmanager.model.Task;
import com.example.eventmanager.model.User;
import com.example.eventmanager.utils.SessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class TaskListActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    private ActivityTaskListBinding binding;
    private TaskAdapter adapter;
    private int eventId;
    private String eventName;
    private List<Task> allTasks = new ArrayList<>();
    private SessionManager sessionManager;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        eventId = getIntent().getIntExtra("EVENT_ID", -1);
        eventName = getIntent().getStringExtra("EVENT_NAME");

        if (eventId == -1) {
            finish();
            return;
        }

        setupUI();
        observeTasks();
        
        if (eventName == null) {
            loadEventName();
        }
    }

    private void loadEventName() {
        AppDatabase.getInstance(this).eventDao().getEventById(eventId).observe(this, event -> {
            if (event != null) {
                eventName = event.getName();
                binding.tvEventTag.setText(eventName);
            }
        });
    }

    private void setupUI() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvEventTag.setText(eventName != null ? eventName : "Lập kế hoạch");

        adapter = new TaskAdapter(new ArrayList<>(), this);
        if (sessionManager.isStaff()) {
            adapter.setReadOnly(true);
        }
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTasks.setAdapter(adapter);

        if (sessionManager.canManageAll()) {
            binding.fabAddTask.setVisibility(View.VISIBLE);
            binding.fabAddTask.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddTaskActivity.class);
                intent.putExtra("EVENT_ID", eventId);
                startActivity(intent);
            });
        } else {
            binding.fabAddTask.setVisibility(View.GONE);
        }

        setupSearchAndFilters();
    }

    private void setupSearchAndFilters() {
        binding.etSearchTasks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilters();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.chipGroupStatus.setOnCheckedChangeListener((group, checkedId) -> applyFilters());
        binding.chipGroupPriority.setOnCheckedChangeListener((group, checkedId) -> applyFilters());
    }

    private void observeTasks() {
        AppDatabase.getInstance(this).taskDao().getTasksByEventId(eventId).observe(this, tasks -> {
            if (tasks != null) {
                allTasks = tasks;
                updateOverallProgress(tasks);
                applyFilters();
            }
        });
    }

    private void updateOverallProgress(List<Task> tasks) {
        int total = tasks.size();
        long done = tasks.stream().filter(t -> "DONE".equals(t.getStatus())).count();
        
        binding.tvTaskCount.setText(done + "/" + total + " công việc hoàn thành");
        if (total > 0) {
            int percent = (int) ((done * 100) / total);
            binding.tvProgressPercent.setText(percent + "%");
            binding.progressBar.setProgress(percent);
        } else {
            binding.tvProgressPercent.setText("0%");
            binding.progressBar.setProgress(0);
        }
    }

    private void applyFilters() {
        String query = binding.etSearchTasks.getText().toString().toLowerCase().trim();

        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<Task> filtered = allTasks.stream().filter(task -> {
                // Phân quyền cho Staff: Chỉ thấy task được giao cho mình thông qua bảng TaskAssignee
                if (sessionManager.isStaff()) {
                    List<User> assignees = db.taskAssigneeDao().getAssigneesForTask(task.getId());
                    boolean isMine = assignees.stream().anyMatch(u -> u.getId() == sessionManager.getUserId());
                    if (!isMine) {
                        return false;
                    }
                }

                boolean matchesSearch = task.getTitle().toLowerCase().contains(query);
                
                boolean matchesStatus = true;
                int checkedStatusId = binding.chipGroupStatus.getCheckedChipId();
                if (checkedStatusId == binding.chipToDo.getId()) matchesStatus = !"DONE".equals(task.getStatus());
                else if (checkedStatusId == binding.chipDone.getId()) matchesStatus = "DONE".equals(task.getStatus());

                boolean matchesPriority = true;
                int checkedPriorityId = binding.chipGroupPriority.getCheckedChipId();
                if (checkedPriorityId == binding.chipHigh.getId()) matchesPriority = task.getPriority() == 2;
                else if (checkedPriorityId == binding.chipMedium.getId()) matchesPriority = task.getPriority() == 1;
                else if (checkedPriorityId == binding.chipLow.getId()) matchesPriority = task.getPriority() == 0;

                return matchesSearch && matchesStatus && matchesPriority;
            }).collect(Collectors.toList());

            runOnUiThread(() -> adapter.setTasks(filtered));
        });
    }

    @Override
    public void onItemClick(Task task) {
        Intent intent = new Intent(this, TaskDetailActivity.class);
        intent.putExtra("TASK_ID", task.getId());
        startActivity(intent);
    }

    @Override
    public void onStatusChanged(Task task, boolean isDone) {
        executorService.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            List<User> assignees = db.taskAssigneeDao().getAssigneesForTask(task.getId());
            boolean isMine = assignees.stream().anyMatch(u -> u.getId() == sessionManager.getUserId());

            runOnUiThread(() -> {
                if (sessionManager.isStaff()) {
                    if (!isMine) {
                        Toast.makeText(this, "Bạn không thể cập nhật công việc của người khác", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    if (!isDone) {
                        Toast.makeText(this, "Chỉ Quản lý mới có thể mở lại công việc", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    // Hành động xác nhận cho nhân viên
                    new AlertDialog.Builder(this)
                            .setTitle("Xác nhận hoàn thành")
                            .setMessage("Bạn có chắc chắn muốn đánh dấu công việc này là đã hoàn thành?")
                            .setPositiveButton("Xác nhận", (dialog, which) -> {
                                task.setStatus("DONE");
                                updateTaskStatus(task);
                            })
                            .setNegativeButton("Hủy", (dialog, which) -> adapter.notifyDataSetChanged())
                            .setCancelable(false)
                            .show();
                } else {
                    task.setStatus(isDone ? "DONE" : "TODO");
                    updateTaskStatus(task);
                }
            });
        });
    }

    private void updateTaskStatus(Task task) {
        executorService.execute(() -> {
            AppDatabase DB = AppDatabase.getInstance(this);
            DB.taskDao().updateTask(task);
        });
    }

    @Override
    public void onEditClick(Task task) {
        if (sessionManager.canManageAll()) {
            Intent intent = new Intent(this, EditTaskActivity.class);
            intent.putExtra("TASK_ID", task.getId());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Bạn không có quyền chỉnh sửa công việc", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeleteClick(Task task) {
        if (sessionManager.canManageAll()) {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa công việc '" + task.getTitle() + "' không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        executorService.execute(() -> {
                            AppDatabase.getInstance(this).taskDao().deleteTask(task);
                            runOnUiThread(() -> Toast.makeText(this, "Đã xóa công việc", Toast.LENGTH_SHORT).show());
                        });
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        } else {
            Toast.makeText(this, "Bạn không có quyền xóa công việc", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
