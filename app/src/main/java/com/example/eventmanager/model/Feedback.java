package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "feedback",
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
            onDelete = ForeignKey.SET_NULL
        )
    }
)
public class Feedback {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(index = true)
    private int eventId;

    @ColumnInfo(index = true)
    private Integer userId; // Có thể null nếu khách không đăng nhập (Lỗi 6)

    private int rating;
    private String comments;
    private String dateSubmitted;
    
    // Audit fields (Lỗi 10)
    private long createdAt;
    private long updatedAt;

    public Feedback() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public Feedback(int eventId, Integer userId, int rating, String comments, String dateSubmitted) {
        this();
        this.eventId = eventId;
        this.userId = userId;
        this.rating = rating;
        this.comments = comments;
        this.dateSubmitted = dateSubmitted;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public String getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(String dateSubmitted) { this.dateSubmitted = dateSubmitted; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
