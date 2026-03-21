package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
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
            childColumns = "assignedTo",
            onDelete = ForeignKey.SET_NULL
        )
    }
)
public class Task {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "eventId", index = true)
    public int eventId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "dueDate")
    public String dueDate;

    @ColumnInfo(name = "assignedTo", index = true)
    public Integer assignedTo; // Changed to Integer to allow null

    @ColumnInfo(name = "priority")
    public int priority;

    @ColumnInfo(name = "note")
    public String note;

    @ColumnInfo(name = "status")
    public String status;

    // Empty constructor for Room
    public Task() {
    }

    // Full constructor
    public Task(int id, int eventId, String title, String dueDate, Integer assignedTo, int priority, String note, String status) {
        this.id = id;
        this.eventId = eventId;
        this.title = title;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.note = note;
        this.status = status;
    }

    // Constructor without id (for insert)
    public Task(int eventId, String title, String dueDate, Integer assignedTo, int priority, String note, String status) {
        this.eventId = eventId;
        this.title = title;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
        this.priority = priority;
        this.note = note;
        this.status = status;
    }
}
