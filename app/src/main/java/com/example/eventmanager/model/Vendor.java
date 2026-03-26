package com.example.eventmanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "vendor",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "createdBy",
        onDelete = ForeignKey.SET_NULL // Sửa lỗi 4: Tránh xóa vendor khi user bị xóa
    ),
    indices = {@Index(value = {"email"}, unique = true)}
)
public class Vendor {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String phone;
    private String email;
    private String serviceType;
    private String note;

    @ColumnInfo(index = true)
    private Integer createdBy;
    
    private long createdAt;
    private long updatedAt;

    public Vendor() {
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getServiceType() { return serviceType; }
    public void setServiceType(String serviceType) { this.serviceType = serviceType; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
