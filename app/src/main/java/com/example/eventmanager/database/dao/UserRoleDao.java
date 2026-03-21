package com.example.eventmanager.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.eventmanager.model.UserRole;
import java.util.List;

@Dao
public interface UserRoleDao {
    @Insert
    void insertUserRole(UserRole userRole);

    @Query("SELECT roleId FROM user_roles WHERE userId = :userId")
    List<Integer> getRolesForUser(int userId);
}
