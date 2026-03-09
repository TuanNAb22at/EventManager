package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    foreignKeys = @ForeignKey(
        entity = Event.class,
        parentColumns = "id",
        childColumns = "eventId",
        onDelete = ForeignKey.CASCADE
    )
)
public class Guest {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "eventId", index = true)
    public int eventId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "status")
    public String status;

    // Empty constructor for Room
    public Guest() {
    }

    // Full constructor
    public Guest(int id, int eventId, String name, String email, String phone, String status) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    // Constructor without id (for insert)
    public Guest(int eventId, String name, String email, String phone, String status) {
        this.eventId = eventId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }
}
