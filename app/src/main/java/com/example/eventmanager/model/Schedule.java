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
        )
    }
)
public class Schedule {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "eventId", index = true)
    public int eventId;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "time")
    public String time;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "status")
    public String status;

    // Empty constructor for Room
    public Schedule() {
    }

    // Full constructor
    public Schedule(int id, int eventId, String title, String time, String description, String status) {
        this.id = id;
        this.eventId = eventId;
        this.title = title;
        this.time = time;
        this.description = description;
        this.status = status;
    }

    // Constructor without id (for insert)
    public Schedule(int eventId, String title, String time, String description, String status) {
        this.eventId = eventId;
        this.title = title;
        this.time = time;
        this.description = description;
        this.status = status;
    }
}
