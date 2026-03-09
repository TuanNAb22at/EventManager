package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Vendor {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "email", index = true)
    public String email;

    @ColumnInfo(name = "serviceType")
    public String serviceType;

    // Empty constructor for Room
    public Vendor() {
    }

    // Full constructor
    public Vendor(int id, String name, String phone, String email, String serviceType) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.serviceType = serviceType;
    }

    // Constructor without id (for insert)
    public Vendor(String name, String phone, String email, String serviceType) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.serviceType = serviceType;
    }
}
