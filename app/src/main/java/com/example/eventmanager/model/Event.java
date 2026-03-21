package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "events",
    foreignKeys = {
        @ForeignKey(
            entity = Location.class,
            parentColumns = "id",
            childColumns = "locationId",
            onDelete = ForeignKey.SET_NULL
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

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "eventType")
    public String eventType;

    @ColumnInfo(name = "startAt")
    public String startAt;

    @ColumnInfo(name = "endAt")
    public String endAt;

    @ColumnInfo(name = "locationId", index = true)
    public Integer locationId; // Changed to Integer to allow null

    @ColumnInfo(name = "createdBy", index = true)
    public int createdBy;

    @ColumnInfo(name = "status")
    public String status; // DRAFT, PREPARING, ONGOING, FINISHED, CANCELLED

    // Empty constructor for Room
    public Event() {
    }

    // Full constructor
    public Event(int id, String name, String description, String eventType, String startAt, String endAt, Integer locationId, int createdBy, String status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.eventType = eventType;
        this.startAt = startAt;
        this.endAt = endAt;
        this.locationId = locationId;
        this.createdBy = createdBy;
        this.status = status;
    }

    // Constructor without id (for insert)
    public Event(String name, String description, String eventType, String startAt, String endAt, Integer locationId, int createdBy, String status) {
        this.name = name;
        this.description = description;
        this.eventType = eventType;
        this.startAt = startAt;
        this.endAt = endAt;
        this.locationId = locationId;
        this.createdBy = createdBy;
        this.status = status;
    }
}
