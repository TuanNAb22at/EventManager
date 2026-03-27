package com.example.eventmanager.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "task_assignee",
    primaryKeys = {"taskId", "userId"},
    foreignKeys = {
        @ForeignKey(
            entity = Task.class,
            parentColumns = "id",
            childColumns = "taskId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("userId")}
)
public class TaskAssignee {
    private int taskId;
    private int userId;

    public TaskAssignee(int taskId, int userId) {
        this.taskId = taskId;
        this.userId = userId;
    }

    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
