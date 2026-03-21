package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(
    tableName = "event_vendors",
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
    public int eventId;
    public int vendorId;

    @ColumnInfo(name = "contractNote")
    public String contractNote;

    public EventVendor(int eventId, int vendorId, String contractNote) {
        this.eventId = eventId;
        this.vendorId = vendorId;
        this.contractNote = contractNote;
    }
}
