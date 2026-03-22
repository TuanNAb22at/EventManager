package com.example.eventmanager.model;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "role", indices = {@Index(value = {"roleName"}, unique = true)})
public class Role {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String roleName; // ORGANIZER, STAFF, etc.

    public Role(String roleName) {
        this.roleName = roleName;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
}
