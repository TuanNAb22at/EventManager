package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "event",
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
            onDelete = ForeignKey.SET_NULL // Sửa lỗi 2: Không xóa event khi xóa user
        )
    }
)
public class Event {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private String eventType;
    private String startAt;
    private String endAt;
    
    @ColumnInfo(index = true)
    private Integer locationId;
    
    @ColumnInfo(index = true)
    private Integer createdBy; // Chuyển sang Integer để có thể NULL (SET_NULL)

    private String status;
    private double totalBudget;
    
    // Audit fields (Lỗi 10)
    private long createdAt;
    private long updatedAt;

    public Event() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getStartAt() { return startAt; }
    public void setStartAt(String startAt) { this.startAt = startAt; }
    public String getEndAt() { return endAt; }
    public void setEndAt(String endAt) { this.endAt = endAt; }
    public Integer getLocationId() { return locationId; }
    public void setLocationId(Integer locationId) { this.locationId = locationId; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getTotalBudget() { return totalBudget; }
    public void setTotalBudget(double totalBudget) { this.totalBudget = totalBudget; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
