package com.example.eventmanager.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.eventmanager.model.Role;
import java.util.List;

@Dao
public interface RoleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertRole(Role role);

    @Query("SELECT * FROM roles WHERE roleName = :roleName LIMIT 1")
    Role getRoleByName(String roleName);

    @Query("SELECT COUNT(*) FROM roles")
    int getRoleCount();

    @Query("SELECT * FROM roles")
    List<Role> getAllRoles();
}
