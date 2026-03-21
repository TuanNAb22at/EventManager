package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Location {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "contact")
    public String contact;

    @ColumnInfo(name = "latitude")
    public double latitude;

    @ColumnInfo(name = "longitude")
    public double longitude;

    // Empty constructor for Room
    public Location() {
    }

    // Full constructor
    public Location(int id, String name, String address, String contact, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Constructor without id (for insert)
    public Location(String name, String address, String contact, double latitude, double longitude) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
