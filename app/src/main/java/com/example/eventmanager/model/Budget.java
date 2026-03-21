package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(
    tableName = "budget",
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

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "category")
    public String category; // Ví dụ: Thuê mặt bằng, Ăn uống, Trang trí...

    @ColumnInfo(name = "date")
    public Date date;

    @ColumnInfo(name = "note")
    public String note;

    public Budget() {
    }

    public Budget(int eventId, String title, double amount, String category, Date date, String note) {
        this.eventId = eventId;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }
}
