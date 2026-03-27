package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "task",
    foreignKeys = {
        @ForeignKey(
            entity = Event.class,
            parentColumns = "id",
            childColumns = "eventId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "createdBy",
            onDelete = ForeignKey.SET_NULL
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "assignedTo",
            onDelete = ForeignKey.SET_NULL
        )
    }
)
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(index = true)
    private int eventId;

    private String title;
    private String dueDate;

    private int priority; // 0: Low, 1: Medium, 2: High
    private String note;
    private String status; // "TODO", "DONE" (Overall status)

    @ColumnInfo(index = true)
    private Integer createdBy;

    @ColumnInfo(index = true)
    private Integer assignedTo;
    
    private long createdAt;
    private long updatedAt;

    public Task() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.status = "TODO";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public Integer getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Integer assignedTo) { this.assignedTo = assignedTo; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
