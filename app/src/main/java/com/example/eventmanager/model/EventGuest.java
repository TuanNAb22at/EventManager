package com.example.eventmanager.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "event_guest",
    primaryKeys = {"eventId", "guestId"},
    foreignKeys = {
        @ForeignKey(
            entity = Event.class,
            parentColumns = "id",
            childColumns = "eventId",
            onDelete = ForeignKey.CASCADE
        ),
        @ForeignKey(
            entity = Guest.class,
            parentColumns = "id",
            childColumns = "guestId",
            onDelete = ForeignKey.CASCADE
        )
    },
    indices = {@Index("guestId"), @Index("eventId")}
)
public class EventGuest {
    private int eventId;
    private int guestId;
    private String status; // e.g., "INVITED", "CONFIRMED", "CANCELLED"

    public EventGuest(int eventId, int guestId, String status) {
        this.eventId = eventId;
        this.guestId = guestId;
        this.status = status;
    }

    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
