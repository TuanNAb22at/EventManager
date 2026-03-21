package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "createdBy",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index(value = {"email"}, unique = true)}
)
public class Vendor {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "serviceType")
    public String serviceType;

    @ColumnInfo(name = "createdBy", index = true)
    public int createdBy;

    // Empty constructor for Room
    public Vendor() {
    }

    // Full constructor
    public Vendor(int id, String name, String phone, String email, String serviceType, int createdBy) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.serviceType = serviceType;
        this.createdBy = createdBy;
    }

    // Constructor without id (for insert)
    public Vendor(String name, String phone, String email, String serviceType, int createdBy) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.serviceType = serviceType;
        this.createdBy = createdBy;
    }
}
