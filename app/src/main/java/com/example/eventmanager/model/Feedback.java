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
            childColumns = "userId",
            onDelete = ForeignKey.CASCADE
        )
    }
)
public class Feedback {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "eventId", index = true)
    public int eventId;

    @ColumnInfo(name = "userId", index = true)
    public int userId;

    @ColumnInfo(name = "rating")
    public int rating; // 1-5 stars

    @ColumnInfo(name = "comments")
    public String comments;

    @ColumnInfo(name = "dateSubmitted")
    public String dateSubmitted;

    // Empty constructor for Room
    public Feedback() {
    }

    // Full constructor
    public Feedback(int id, int eventId, int userId, int rating, String comments, String dateSubmitted) {
        this.id = id;
        this.eventId = eventId;
        this.userId = userId;
        this.rating = rating;
        this.comments = comments;
        this.dateSubmitted = dateSubmitted;
    }

    // Constructor without id (for insert)
    public Feedback(int eventId, int userId, int rating, String comments, String dateSubmitted) {
        this.eventId = eventId;
        this.userId = userId;
        this.rating = rating;
        this.comments = comments;
        this.dateSubmitted = dateSubmitted;
    }
}
