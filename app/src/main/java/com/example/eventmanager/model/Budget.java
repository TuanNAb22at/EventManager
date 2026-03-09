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
public class Budget {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "eventId", index = true)
    public int eventId;

    @ColumnInfo(name = "total")
    public double total;

    @ColumnInfo(name = "note")
    public String note;

    // Empty constructor for Room
    public Budget() {
    }

    // Full constructor
    public Budget(int id, int eventId, double total, String note) {
        this.id = id;
        this.eventId = eventId;
        this.total = total;
        this.note = note;
    }

    // Constructor without id (for insert)
    public Budget(int eventId, double total, String note) {
        this.eventId = eventId;
        this.total = total;
        this.note = note;
    }
}
