package com.example.eventmanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.model.Task;
import com.example.eventmanager.model.User;
import com.example.eventmanager.database.AppDatabase;
import java.util.List;
import java.util.concurrent.Executors;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private OnTaskActionListener listener;
    private boolean isReadOnly = false;

    public interface OnTaskActionListener {
        void onItemClick(Task task);
        void onStatusChanged(Task task, boolean isDone);
        void onEditClick(Task task);
        void onDeleteClick(Task task);
    }

    public TaskAdapter(List<Task> tasks, OnTaskActionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    public void setReadOnly(boolean readOnly) {
        this.isReadOnly = readOnly;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.dueDateTextView.setText(task.getDueDate());
        
        boolean isDone = "DONE".equals(task.getStatus());
        holder.statusCheckBox.setChecked(isDone);
        
        // Style for completed task
        if (isDone) {
            holder.titleTextView.setAlpha(0.5f);
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.titleTextView.setAlpha(1.0f);
            holder.titleTextView.setPaintFlags(holder.titleTextView.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Display priority
        switch (task.getPriority()) {
            case 0: // Low
                holder.priorityTextView.setText("LOW");
                holder.priorityTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.primary_blue));
                holder.priorityLayout.setBackgroundResource(R.drawable.bg_priority_low);
                break;
            case 1: // Medium
                holder.priorityTextView.setText("MEDIUM");
                holder.priorityTextView.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
                holder.priorityLayout.setBackgroundResource(R.drawable.bg_priority_medium);
                break;
            case 2: // High
                holder.priorityTextView.setText("HIGH");
                holder.priorityTextView.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.red_tag));
                holder.priorityLayout.setBackgroundResource(R.drawable.bg_priority_high);
                break;
        }

        // Load Assignee
        if (task.getAssignedTo() != null) {
            holder.assigneeLayout.setVisibility(View.VISIBLE);
            Executors.newSingleThreadExecutor().execute(() -> {
                User user = AppDatabase.getInstance(holder.itemView.getContext()).userDao().getUserById(task.getAssignedTo());
                if (user != null) {
                    holder.itemView.post(() -> holder.assigneeNameTextView.setText(user.getFullName()));
                }
            });
        } else {
            holder.assigneeLayout.setVisibility(View.GONE);
        }

        if (isReadOnly) {
            holder.editButton.setVisibility(View.GONE);
            holder.deleteButton.setVisibility(View.GONE);
        } else {
            holder.editButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.editButton.setOnClickListener(v -> listener.onEditClick(task));
            holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(task));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(task));
        holder.statusCheckBox.setOnClickListener(v -> listener.onStatusChanged(task, holder.statusCheckBox.isChecked()));
    }

    @Override
    public int getItemCount() {
        return tasks == null ? 0 : tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dueDateTextView, priorityTextView, assigneeNameTextView;
        CheckBox statusCheckBox;
        View priorityLayout, assigneeLayout;
        ImageView editButton, deleteButton;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvTaskTitle);
            dueDateTextView = itemView.findViewById(R.id.tvDueDate);
            statusCheckBox = itemView.findViewById(R.id.cbTaskStatus);
            priorityTextView = itemView.findViewById(R.id.tvPriority);
            priorityLayout = itemView.findViewById(R.id.layoutPriority);
            assigneeNameTextView = itemView.findViewById(R.id.tvAssigneeName);
            assigneeLayout = itemView.findViewById(R.id.layoutAssignee);
            editButton = itemView.findViewById(R.id.btnEditTask);
            deleteButton = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}
