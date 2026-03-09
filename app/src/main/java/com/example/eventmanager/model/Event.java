package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    foreignKeys = {
        @ForeignKey(
            entity = Location.class,
            parentColumns = "id",
            childColumns = "locationId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = User.class,
            parentColumns = "id",
            childColumns = "createdBy",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class Event {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "startTime")
    public String startTime;

    @ColumnInfo(name = "endTime")
    public String endTime;

    @ColumnInfo(name = "locationId", index = true)
    public int locationId;

    @ColumnInfo(name = "createdBy", index = true)
    public int createdBy;

    @ColumnInfo(name = "status")
    public String status;

    // Empty constructor for Room
    public Event() {
    }

    // Full constructor
    public Event(int id, String name, String date, String startTime, String endTime, int locationId, int createdBy, String status) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationId = locationId;
        this.createdBy = createdBy;
        this.status = status;
    }

    // Constructor without id (for insert)
    public Event(String name, String date, String startTime, String endTime, int locationId, int createdBy, String status) {
        this.name = name;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.locationId = locationId;
        this.createdBy = createdBy;
        this.status = status;
    }
}
