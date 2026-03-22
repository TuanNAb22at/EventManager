package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "event_vendor",
    primaryKeys = {"eventId", "vendorId"},
    foreignKeys = {
        @ForeignKey(
            entity = Event.class,
            parentColumns = "id",
            childColumns = "eventId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Vendor.class,
            parentColumns = "id",
            childColumns = "vendorId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("eventId"), @Index("vendorId")}
)
public class EventVendor {
    private int eventId;
    private int vendorId;
    private String contractNote;
    
    // Audit fields (Lỗi 10)
    private long createdAt;
    private long updatedAt;

    public EventVendor(int eventId, int vendorId, String contractNote) {
        this.eventId = eventId;
        this.vendorId = vendorId;
        this.contractNote = contractNote;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    // Getters and Setters (Lỗi 9)
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }
    public String getContractNote() { return contractNote; }
    public void setContractNote(String contractNote) { this.contractNote = contractNote; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
